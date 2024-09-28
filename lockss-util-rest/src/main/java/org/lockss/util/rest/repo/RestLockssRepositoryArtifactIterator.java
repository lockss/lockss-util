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
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

// Currently reports timing info only when end reached, so never
// reported for abandoned iterators

/**
 * Iterator over Artifacts returned by any of the Artifact list-returning
 * REST endpoints of Lockss Repository Service.  Pages of Artifacts are
 * fetched by a background thread and added to a Queue, to avoid a
 * synchronous wait for each page.  {@link #setPageSizes(List<Integer>)}
 * can be used to cause the initial request(s) to be for small page(s), to
 * reduce startup latency, followed by large pages to efficiently keep the
 * queue filled.
 */
public class RestLockssRepositoryArtifactIterator
  implements Iterator<Artifact> {

  private final static L4JLogger log = L4JLogger.getLogger();

  /** Time the iterator will wait on an empty page queue.  This should be
   * as long as the max time the repo might take to respond, but not so
   * long that a process using the iterator hangs unreasonably if the repo
   * hangs.  Not currently configurable.
   */
  public static final long DEFAULT_QUEUE_GET_TIMEOUT = Constants.MINUTE;

  /** Time the producer thread will wait attempting to add to a full page
   * queue.  This should be very long as there is no way to predict how
   * quickly the iterator will remove items from the queue.  Not currently
   * configurable.
   */
  public static final long DEFAULT_QUEUE_PUT_TIMEOUT = Long.MAX_VALUE;

  /** The page queue length.  Should be long enough to avoid starvation,
   * but the only case where that would be a problem is if the producer
   * can't keep up with the consumer.  If that's the case overall,
   * increasing the queue length won't help.  If the consumer's consumption
   * rate is highly variable over numbers of iterations approaching the
   * queue capacity, increaing the queue length could help decrease
   * starvation during period of rapid consumption.. Not currently
   * configurable.
   */
  public static final int DEFAULT_QUEUE_LENGTH = 3;

  // The iterator for the current page of Artifacts
  private Iterator<Artifact> curIter = null;
  private boolean done = false;

  protected ThreadData tdata = new ThreadData();

  // Infrastructure to invoke Cleaner
  private static final Cleaner cleaner = Cleaner.create();
  private Cleaner.Cleanable cleanable = null;
  private ProducerCleaner pCleaner;

  /**
   * Constructor with default batch size and no Authorization header.
   * 
   * @param restTemplate A RestTemplate with the REST service template.
   * @param builder      An UriComponentsBuilder with the REST service URI
   *                     builder.
   */
  RestLockssRepositoryArtifactIterator(RestTemplate restTemplate,
      UriComponentsBuilder builder) {
    this(restTemplate, builder, null, null);
  }

  /**
   * Constructor with default batch size.
   * 
   * @param restTemplate    A RestTemplate with the REST service template.
   * @param builder         An UriComponentsBuilder with the REST service URI
   *                        builder.
   * @param authHeaderValue A String with the Authorization header to be used
   *                        when calling the REST service.
   */
  public RestLockssRepositoryArtifactIterator(RestTemplate restTemplate,
      UriComponentsBuilder builder, String authHeaderValue) {
    this(restTemplate, builder, authHeaderValue, null);
  }

  /**
   * Constructor with no Authorization header.
   * 
   * @param restTemplate A RestTemplate with the REST service template.
   * @param builder      An UriComponentsBuilder with the REST service URI
   *                     builder.
   * @param limit        An Integer with the number of artifacts to request on
   *                     each REST service request.
   */
  RestLockssRepositoryArtifactIterator(RestTemplate restTemplate,
      UriComponentsBuilder builder, Integer limit) {
    this(restTemplate, builder, null, limit);
  }

  /**
   * Full constructor.
   * 
   * @param restTemplate    A RestTemplate with the REST service template.
   * @param builder         An UriComponentsBuilder with the REST service URI
   *                        builder.
   * @param authHeaderValue A String with the Authorization header to be used
   *                        when calling the REST service.
   * @param limit           An Integer with the number of artifacts to request
   *                        on each REST service request.
   */
  RestLockssRepositoryArtifactIterator(RestTemplate restTemplate,
      UriComponentsBuilder builder, String authHeaderValue, Integer limit) {
    // Validation.
    if (restTemplate == null) {
      throw new IllegalArgumentException(
	  "REST service template cannot be null");
    }

    if (builder == null) {
      throw new IllegalArgumentException(
	  "REST service URI builder cannot be null");
    }

    if (limit != null && limit.intValue() < 1) {
      throw new IllegalArgumentException("Limit must be at least 1");
    }

    // Initialization.
    tdata.restTemplate = restTemplate;

    if (limit != null) {
      builder = builder.replaceQueryParam("limit", limit);
    }

    tdata.builder = builder;
    tdata.authHeaderValue = authHeaderValue;
    startProducerThread();
  }

  /**
   * Allows variable page size requests from the server.  If the list is
   * non-empty, the first element will be the page size of the first
   * request, the second element will be used for the second request, etc.
   * When the list runs out the last element will continue to be used for
   * all successive requests.
   */
  public RestLockssRepositoryArtifactIterator setPageSizes(List<Integer> sizes) {
    if (sizes == null || sizes.isEmpty()) {
      tdata.pageSizes = null;
    } else {
      tdata.pageSizes = new LinkedList(sizes);
    }
    return this;
  }

  private void startProducerThread() {
    PageProducer pp = new PageProducer(tdata);
    Thread th = new Thread(pp);
    th.setName("ArtIter Producer");
    pCleaner = new ProducerCleaner(pp, th);
    cleanable = cleaner.register(this, pCleaner);
    th.start();
  }

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
    if (curIter == null || !curIter.hasNext()) {
      try {
        long start = System.currentTimeMillis();
        IterPage nextIterPage =
          tdata.pageQueue.poll(tdata.queueGetTimeout, TimeUnit.MILLISECONDS);
        tdata.iterWaitTime += System.currentTimeMillis() - start;
        if (nextIterPage == null) {
          throw new IteratorTimeoutException("Nothing added to queue for " +
                                             TimeUtil.timeIntervalToString(tdata.queueGetTimeout));
        }
        if (nextIterPage.error() != null) {
          throw new LockssUncheckedException(nextIterPage.error());
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
        throw new IteratorTimeoutException("Queue.poll interrupted");
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
        long queuePutStart = System.currentTimeMillis();

        if (tdata.terminated) {
          break;
        }
        try {
          tdata.pageQueue.offer(page, tdata.queuePutTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          log.debug("Producer thread interrupted, exiting");
          return;
        }
        tdata.queuePutWaitTime += System.currentTimeMillis() - queuePutStart;
        if (isLastBatch()) {
          try {
            tdata.pageQueue.offer(new IterPage(null, null),
                            tdata.queuePutTimeout, TimeUnit.MILLISECONDS);
          } catch (InterruptedException e) {
            log.debug("Producer thread interrupted putting final page, exiting");
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

    private boolean isLastBatch() {
      return continuationToken == null;
    }

    /**
     * Fills the internal buffer with the next batch of artifacts from the REST
     * service.
     */
    private IterPage getNextPage() {
      setPageSize();
      // Check whether a previous response provided a continuation token.
      if (continuationToken != null) {
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
        log.error("Could not fetch artifacts: Exception caught", e);
        return new IterPage(e, null);
      } catch (LockssRestException e) {
        log.error("Could not fetch artifacts: Exception caught", e);
        return new IterPage(e, null);
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

    /** If a page size list has been provided, set the page size for
     * the next request (query param in builder). */
    void setPageSize() {
      if (tdata.pageSizes != null && !tdata.pageSizes.isEmpty()) {
        tdata.builder = tdata.builder.replaceQueryParam("limit",
                                                        nextPageSize(tdata.pageSizes));
      }
    }

    /** Return the next page size from the pageSizes list */
    int nextPageSize(LinkedList<Integer> lst) {
      return (lst.size() > 1) ? lst.pop() : lst.get(0);
    }

    /** Inform the producer that it should stop */
    void terminate() {
      tdata.terminated = true;
    }
  }

  /** Data (mostly) shared by the iterator and producer.  (The
   * producer thread can't reference the Iterator, else the Cleaner
   * will never run, so at least some of the data must be elsewhere.
   * Easiest to put it all in a single object). */
  static class ThreadData {
    // Queue of received Artifact pages
    BlockingQueue<IterPage> pageQueue =
      new ArrayBlockingQueue<>(DEFAULT_QUEUE_LENGTH);

    // Iterator timeout pulling pages from queue
    long queueGetTimeout = DEFAULT_QUEUE_GET_TIMEOUT;

    // Producer timeout adding pages to queue
    long queuePutTimeout = DEFAULT_QUEUE_PUT_TIMEOUT;

    // List of progressive page sizes to request.  The last one is repeated
    LinkedList<Integer> pageSizes;

    // The REST service URI builder.
    UriComponentsBuilder builder;

    // Auth header
    String authHeaderValue = null;

    // REST service template.
    RestTemplate restTemplate;

    // Stats
    long iterWaitTime = 0;
    long fetchWaitTime = 0;
    long queuePutWaitTime = 0;

    // Flag set by Cleaner to force the thread to terminate.  Here so
    // test class can access it
    boolean terminated = false;
  }

  /** The Queue object, contains either a list of Artifacts, an error for
   * the iterator to throw, or noll, null, indicating no more objects
   */
  record IterPage(Exception error, List<Artifact> artifacts) {};

  /** Exception thrown if {@link #hasNext()} waits for a queue item to
   * appear for longer than {@value #DEFAULT_QUEUE_GET_TIMEOUT} ms
   */
  public static class IteratorTimeoutException extends RuntimeException {
    public IteratorTimeoutException(String msg) {
      super(msg);
    }
  }

  /** Cleaner runs when Iterator is discarded, signals producer thread
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
