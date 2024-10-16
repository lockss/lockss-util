/*

Copyright (c) 2019-2024 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package org.lockss.util.rest.repo;

import java.lang.ref.*;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lockss.util.*;
import org.lockss.util.time.*;
import org.lockss.util.rest.repo.model.Artifact;
import org.lockss.util.rest.repo.model.ArtifactPageInfo;
import org.lockss.log.L4JLogger;
import org.lockss.util.rest.RestUtil;
import org.lockss.util.rest.exception.*;
import org.apache.commons.lang3.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Iterator over Artifacts returned by any of the Artifact list-returning
 * REST endpoints of Lockss Repository Service.  Pages of Artifacts are
 * fetched by a background thread and added to a Queue, to avoid a
 * synchronous wait for each page.  The initial page sizes can be
 * controlled, to provide faster startup - see {@link #Params}.
 */
public class RestLockssRepositoryArtifactIterator
  implements Iterator<Artifact> {

  private final static L4JLogger log = L4JLogger.getLogger();

  /** Default time the iterator will wait for the next page of
   * Artifacts to show up on the queue.  This should be as long as the
   * max time the repo might take to respond, but not so long that a
   * process using the iterator hangs unreasonably if the repo hangs.
   */
  public static final long DEFAULT_QUEUE_GET_TIMEOUT = 30 * Constants.MINUTE;

  /** Time the producer thread will wait attempting to add to a full page
   * queue.  This should be very long as there is no way to predict how
   * quickly the iterator will remove items from the queue.  Not currently
   * configurable.
   */
  public static final long DEFAULT_QUEUE_PUT_TIMEOUT = Long.MAX_VALUE;

  /** Default page queue length.  Should be long enough to avoid starvation,
   * but the only case where that would be a problem is if the producer
   * can't keep up with the consumer.  If that's the case overall,
   * increasing the queue length won't help.  If the consumer's consumption
   * rate is highly variable over numbers of iterations approaching the
   * queue capacity, increasing the queue length could help decrease
   * starvation during periods of rapid consumption.
   */
  public static final int DEFAULT_QUEUE_LENGTH = 2;

  /** Param values used if no Params is supplied to constructor */
  static Params DEFAULT_PARAMS = new Params()
    .setQueueLen(DEFAULT_QUEUE_LENGTH)
    .setQueueGetTimeout(DEFAULT_QUEUE_GET_TIMEOUT);

  // The iterator for the current page of Artifacts
  private Iterator<Artifact> curIter = null;

  private boolean done = false;
  private RuntimeException prevError;

  // Data block shared by iterator and producer thread
  protected ThreadData tdata;

  // Infrastructure to invoke Cleaner
  private static final Cleaner cleaner = Cleaner.create();
  private Cleaner.Cleanable cleanable = null;
  private ProducerCleaner pCleaner;

  /**
   * Constructor with default params size and no Authorization header.
   * 
   * @param restTemplate    A RestTemplate with the REST service template.
   * @param builder         An UriComponentsBuilder initialized with the
   *                        REST endpoint IRU.  Query params will be added
   *                        as needed
   *                        builder.
   */
  RestLockssRepositoryArtifactIterator(RestTemplate restTemplate,
      UriComponentsBuilder builder) {
    this(restTemplate, builder, null);
  }

  /**
   * Constructor with default params size and an Authorization header.
   * 
   * @param restTemplate    A RestTemplate with the REST service template.
   * @param builder         An UriComponentsBuilder initialized with the
   *                        REST endpoint IRU.  Query params will be added
   *                        as needed
   *                        builder.
   * @param authHeaderValue A String with the Authorization header to be used
   *                        when calling the REST service.
   */
  RestLockssRepositoryArtifactIterator(RestTemplate restTemplate,
      UriComponentsBuilder builder, String authHeaderValue) {
    this(restTemplate, builder, authHeaderValue, DEFAULT_PARAMS);
  }

  /**
   * Full constructor.
   * 
   * @param restTemplate    A RestTemplate with the REST service template.
   * @param builder         An UriComponentsBuilder initialized with the
   *                        REST endpoint IRU.  Query params will be added
   *                        as needed
   *                        builder.
   * @param authHeaderValue A String with the Authorization header to be used
   *                        when calling the REST service.
   * @param params          Parameters controlling queue length, page sizes,
   *                        timeouts
   */
  RestLockssRepositoryArtifactIterator(RestTemplate restTemplate,
                                       UriComponentsBuilder builder,
                                       String authHeaderValue,
                                       Params params) {
    // Validation.
    if (restTemplate == null) {
      throw new IllegalArgumentException(
	  "REST service template cannot be null");
    }

    if (builder == null) {
      throw new IllegalArgumentException(
	  "REST service URI builder cannot be null");
    }

    // Initialization.
    tdata = new ThreadData(params);
    tdata.restTemplate = restTemplate;
    tdata.builder = builder;
    tdata.authHeaderValue = authHeaderValue;

    // Start producer thread in constructor so that first request is
    // made as early as possible
    startProducerThread();
  }

  static int threadCounter = 1;

  private void startProducerThread() {
    PageProducer pp = new PageProducer(tdata);
    Thread th = new Thread(pp);
    th.setName("ArtIter Producer " + threadCounter++);
    th.setPriority(Thread.NORM_PRIORITY + 2);
    pCleaner = new ProducerCleaner(pp, th);
    cleanable = cleaner.register(this, pCleaner);
    th.start();
  }

  /** Report the total wait times for this iterator.  Currently called
   * only when end reached.  Stats for abandoned iterators would be
   * misleading anyway */
  private void report() {
    log.debug2("Iter wait: {}, fetch wait: {}, queue put wait {}",
               TimeUtil.timeIntervalToString(tdata.iterWaitTime),
               TimeUtil.timeIntervalToString(tdata.fetchWaitTime),
               TimeUtil.timeIntervalToString(tdata.queuePutWaitTime));
  }

  /**
   * Returns {@code true} if the there are more artifacts in the iteration
   *
   * @return a boolean with {@code true} if there are more artifacts to be
   *         returned, {@code false} otherwise.
   */
  @Override
  public boolean hasNext() throws RuntimeException {
    if (done) return false;
    if (prevError != null) {
      throw new IllegalStateException("Iterator previously threw", prevError);
    }
    if (curIter == null || !curIter.hasNext()) {
      try {
        long start = System.currentTimeMillis();
        IterPage nextIterPage =
          tdata.pageQueue.poll(tdata.queueGetTimeout, TimeUnit.MILLISECONDS);
        tdata.iterWaitTime += System.currentTimeMillis() - start;
        if (nextIterPage == null) {
          prevError = new IteratorTimeoutException("Nothing added to queue for " +
                                                   TimeUtil.timeIntervalToString(tdata.queueGetTimeout));
          throw prevError;
        }
        if (nextIterPage.error() != null) {
          prevError = new LockssUncheckedException(nextIterPage.error());
          throw prevError;
        }
        if (nextIterPage.artifacts() == null ||
            nextIterPage.artifacts().isEmpty()) {
          done = true;
          report();
          return false;
        }
        curIter = nextIterPage.artifacts().iterator();
        log.trace("Stored new iter ({}) from {}", nextIterPage.artifacts().size(),
                  nextIterPage);
      } catch (InterruptedException e) {
        prevError = new IteratorTimeoutException("Queue.poll interrupted");;
        throw prevError;
      }
    }
    return curIter.hasNext();
  }

  /**
   * Provides the next artifact.
   *
   * @return an Artifact with the next artifact.
   * @throws NoSuchElementException if there are no more artifacts to return.
   */
  @Override
  public Artifact next() throws NoSuchElementException {
    if (hasNext()) {
      return curIter.next();
    } else {
      throw new NoSuchElementException();
    }
  }

  /** The producer Runnable.  Must be static, containing no references
   * to the iterator, so that the iterator will become phantom
   * reachable when its client discards it or is aborted, triggering
   * the Cleaner to stop the producer thread.
   */
  private static class PageProducer implements Runnable {
    private ThreadData tdata;
    private String continuationToken = null;

    PageProducer(ThreadData tdata) {
      this.tdata = tdata;
    }

    public void run() {
      log.debug2("Producer thread started");
      while (!tdata.terminated) {
        long fetchStart = System.currentTimeMillis();
        IterPage page = getNextPage();
        tdata.fetchWaitTime += System.currentTimeMillis() - fetchStart;

        if (tdata.terminated) {
          break;
        }
        try {
          long queuePutStart = System.currentTimeMillis();
          tdata.pageQueue.offer(page, tdata.queuePutTimeout, TimeUnit.MILLISECONDS);
          tdata.queuePutWaitTime += System.currentTimeMillis() - queuePutStart;
          // If this is an error page, exit
          if (page.error() != null) {
            return;
          }
        } catch (InterruptedException e) {
          log.debug("Producer thread interrupted, exiting");
          // Try to ensure we put a terminating element in the queue
          forceAddTerminatingQueueItem(e);
          return;
        }
        // Data page added.  If this was the last, add termination item
        if (isLastBatch()) {
          // Extra "end-of-pages" queue item is slightly awkward here
          // but replacing it with a "lastPage" indicator in the last
          // page is even more awkward to handle in hasNext()
          try {
            tdata.pageQueue.offer(new IterPage(null, null),
                            tdata.queuePutTimeout, TimeUnit.MILLISECONDS);
          } catch (InterruptedException e) {
            log.debug("Producer thread interrupted putting final page, exiting");
            forceAddTerminatingQueueItem(e);
            return;
          }
          log.debug2("Producer thread exiting after last page");
          return;
        }
      }
      if (tdata.terminated) {
        log.debug("Producer thread forcibly terminated");
      }
    }

    private void forceAddTerminatingQueueItem(Exception e) {
      tdata.pageQueue.clear();
      try {
        tdata.pageQueue.offer(new IterPage(e, null), 10, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e2) {
        // ignore
      }
    }

    private boolean isLastBatch() {
      return continuationToken == null;
    }

    /**
     * Fills the internal buffer with the next batch of artifacts from the REST
     * service.
     */
    private IterPage getNextPage() {
      setReqPageSize();

      // Check whether a previous response provided a continuation token.
      if (!StringUtils.isEmpty(continuationToken)) {
        // Yes: Incorporate it to the next request.
        tdata.builder.replaceQueryParam("continuationToken", continuationToken);
      }

      // Build the URI to make a request to the REST service.
      URI uri = tdata.builder.build().encode().toUri();

      // Build the HttpEntity to include in the request to the REST service.
      HttpHeaders httpHeaders = new HttpHeaders();
      // Add the Auth header, if any
      if (tdata.authHeaderValue != null) {
        httpHeaders.set("Authorization", tdata.authHeaderValue);
      }
      HttpEntity<Void> httpEntity = new HttpEntity<>(null, httpHeaders);

      ResponseEntity<String> response = null;

      try {
        // Make the request and get the response.

        log.trace("Req next page: {}", uri);

        response = RestUtil.callRestService(tdata.restTemplate, uri, HttpMethod.GET,
                                            httpEntity, String.class, "fillArtifactBuffer");
      } catch (LockssRestHttpException e) {
        if (e.getHttpStatus().equals(HttpStatus.NOT_FOUND)) {
          log.debug("End of Artifacts (404)");
          continuationToken = null;
          return new IterPage(null, null);
        }
        log.error("Could not fetch artifact page: Exception caught", e);
        return new IterPage(e, null);
      } catch (LockssRestException e) {
        log.error("Could not fetch artifact page: Exception caught", e);
        return new IterPage(e, null);
      } catch (Exception e) {
        log.error("Unexpected exception fetching artifact page", e);
        return new IterPage(e, null);
      } catch (Throwable e) {
        log.error("Unexpected exception fetching artifact page", e);
        return new IterPage(new RuntimeException("Throwable", e), null);
      }

      // Determine the response status.
      HttpStatusCode statusCode = response.getStatusCode();
      HttpStatus status = HttpStatus.valueOf(statusCode.value());
      log.trace("status = {}", status);

      // Check whether the response status indicates success.
      if (status.is2xxSuccessful()) {
        // Yes: Initialize the response body parser.
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                         false);

        try {
          // Get the returned artifact page information.
          ArtifactPageInfo api = mapper.readValue((String)response.getBody(),
                                                  ArtifactPageInfo.class);
          log.trace("api = {}", api);

          // Record the continuation token
          continuationToken = api.getPageInfo().getContinuationToken();
          log.trace("continuationToken = {}", continuationToken);

          // Return the artifacts
          log.trace("Fetched {} artifacts",
                    api.getArtifacts() == null ? "no" : api.getArtifacts().size());
          return new IterPage(null, api.getArtifacts());

        } catch (Exception e) {
          continuationToken = null;
          return new IterPage(e, null);
        }
      } else {
        // No: Report the problem.
        String msg = "Could not fetch artifacts: REST service status: " +
          status.toString() + " " + status.getReasonPhrase();
        log.error(msg);
        continuationToken = null;
        return new IterPage(new Exception(msg), null);
      }
    }

    /** Set the next page size from the pageSizes list */
    void setReqPageSize() {
      LinkedList<Integer> lst = tdata.pageSizes;
      if (lst != null && !lst.isEmpty()) {
        int limit = (lst.size() > 1) ? lst.pop() : lst.get(0);
        tdata.builder.replaceQueryParam("limit", limit);
      }
    }

    /** Inform the producer that it should stop */
    void terminate() {
      tdata.terminated = true;
    }
  }

  /** Data (mostly) shared by the iterator and producer.  (The
   * producer thread can't reference the Iterator, else the Iterator's
   * Cleaner will never run.  Not all of thise data needs to be in a
   * common object, but easiest to put it all here). */
  static class ThreadData {
    // Queue of received Artifact pages
    BlockingQueue<IterPage> pageQueue =
      new ArrayBlockingQueue<>(DEFAULT_QUEUE_LENGTH);

    // The REST service URI builder.
    UriComponentsBuilder builder;

    // Auth header
    String authHeaderValue = null;

    // REST service template.
    RestTemplate restTemplate;

    // Iterator params
    LinkedList<Integer> pageSizes;
    int queueLen = DEFAULT_QUEUE_LENGTH;

    // Iterator timeout pulling pages from queue
    long queueGetTimeout = DEFAULT_QUEUE_GET_TIMEOUT;

    // Producer timeout adding pages to queue
    long queuePutTimeout = DEFAULT_QUEUE_PUT_TIMEOUT;


    // Stats
    long iterWaitTime = 0;
    long fetchWaitTime = 0;
    long queuePutWaitTime = 0;

    // Flag set by Cleaner to force the thread to terminate.  Here so
    // test class can access it
    boolean terminated = false;

    private ThreadData(Params params) {
      if (params.pageSizes != null && !params.pageSizes.isEmpty()) {
        this.pageSizes = new LinkedList<>(params.pageSizes);
      }
      if (params.queueLen > 0) {
        this.queueLen = params.queueLen;
      }
      if (params.queueGetTimeout > 0) {
        this.queueGetTimeout = params.queueGetTimeout;
      }
      pageQueue = new ArrayBlockingQueue<>(queueLen);
    }

  }

  /** The Queue object, contains either a list of Artifacts, an error for
   * the iterator to throw, or noll, null, indicating no more objects
   */
  record IterPage(Exception error, List<Artifact> artifacts) {};

  /** Iterator parameters controlling queue length, requested page
   * sizes, timeouts */
  public static class Params {
    /** Initial page sizes to request; the last value in the list is used
     * for all remaining pages. */
    List<Integer> pageSizes;
    /** Length of page queue. */
    int queueLen;
    long queueGetTimeout;

    List<Integer> getPageSizes() {
      return pageSizes;
    }

    int getQueueLen() {
      return queueLen;
    }

    long getQueueGetTimeout() {
      return queueGetTimeout;
    }

    /**
     * Allows variable page size requests from the server.  If the list is
     * non-empty, the first element will be the page size of the first
     * request, the second element will be used for the second request, etc.
     * When the list runs out the last element will continue to be used for
     * all successive requests.
     */
    public Params setPageSizes(List<Integer> pageSizes) {
      this.pageSizes = pageSizes;
      return this;
    }

    /** Set a page size for all pages */
    public Params setPageSize(int size) {
      this.pageSizes = List.of(size);
      return this;
    }

    public Params setQueueLen(int len) {
      this.queueLen = len;
      return this;
    }

    public Params setQueueGetTimeout(long timeout) {
      this.queueGetTimeout = timeout;
      return this;
    }
  }

  /** Exception thrown if {@link #hasNext()} waits for a queue item to
   * appear for longer than {@value #DEFAULT_QUEUE_GET_TIMEOUT} ms
   */
  public static class IteratorTimeoutException extends RuntimeException {
    public IteratorTimeoutException(String msg) {
      super(msg);
    }
  }

  /** Cleaner runs when Iterator becomes GCable, signals producer thread
   * to exit */
  private static class ProducerCleaner implements Runnable {
    private PageProducer pp;
    private Thread th;

    private ProducerCleaner(PageProducer pp, Thread th) {
      this.pp = pp;
      this.th = th;
    }

    public void run() {
      pp.terminate();
      th.interrupt();
    }
  }
}
