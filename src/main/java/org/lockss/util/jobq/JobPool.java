/*
 * Copyright (c) 2018 Board of Trustees of Leland Stanford Jr. University,
 * all rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 * STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Stanford University shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization from Stanford University.
 */

package org.lockss.util.jobq;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;

/**
 * The type Job Queue.
 */
public class JobPool {

  private final AtomicInteger seqGenerator = new AtomicInteger();
  private final Set<Job> currentJobs = new HashSet<>();
  private final PriorityBlockingQueue<Job> pendingQueue
      = new PriorityBlockingQueue<>();
  private final Map<String, Queue<Job>> waitingJobs
      = new HashMap<>();
  private final ResultDispatcher resultDispatcher;
  private final List<JobPoolListener> listeners = new ArrayList<>();
  private JobDispatcher jobDispatcher;
  private ExecutorService executorService;
  private boolean isRunning = false;

  /**
   * Instantiates a new Job pool of a.
   *
   * @param resultDispatcher the delivery
   */
  public JobPool(ResultDispatcher resultDispatcher) {
    if ((resultDispatcher == null)) {
      throw new AssertionError("null not allowed!");
    }
    this.resultDispatcher = resultDispatcher;
  }

  /**
   * Start this task pool. This will create an executor and dispatcher.
   */
  public synchronized void start() {
    stop();

    try {
      executorService = createExecutor();
      jobDispatcher = new JobDispatcher(pendingQueue, executorService, resultDispatcher);
      jobDispatcher.start();
      isRunning = true;
    } catch (Exception e) {
      stop();
    }
  }

  /**
   * Create executor service.
   *
   * @return the executor service
   */
  protected ExecutorService createExecutor() {

    return Executors.newCachedThreadPool();
  }

  /**
   * Stop all the tasks in this Job Pool and clear the queues.
   */
  public synchronized void stop() {
    isRunning = false;
    if (jobDispatcher != null) {
      jobDispatcher.quit();
    }
    if (executorService != null) {
      executorService.shutdownNow();
    }

    synchronized (currentJobs) {
      currentJobs.clear();
      pendingQueue.clear();
      waitingJobs.clear();
      seqGenerator.set(0);
    }
  }

  /**
   * return the size of the current task queue
   *
   * @return the int
   */
  public int size() {
    return currentJobs.size();
  }

  /**
   * return a sequence number for a specific task.
   * @return the next number in the sequence.
   */
  private int getSequenceNumber() {
    return seqGenerator.incrementAndGet();
  }

  /**
   * Add a job to the the job pool.
   *
   * @param job the job
   * @return the dispatched job
   */
  public Job  add(Job job) {
    if ((job == null)) {
      throw new AssertionError();
    }
    if (!isRunning) {
      return null;
    }

    // Tag the Job as belonging to this pool and add it to the set of current Jobs.
    job.setJobPool(this);
    job.setSequence(getSequenceNumber());
    synchronized (currentJobs) {
      currentJobs.add(job);
    }
    synchronized (listeners) {
      for (JobPoolListener listener : listeners) {
        listener.onAdd(job, size());
      }
    }

    return dispatch(job);
  }

  private Job dispatch(Job job) {
    if (StringUtils.isEmpty(job.getExclKey())) {
      pendingQueue.add(job);
      return job;
    }

    // Insert request into stage if there's already a request with the same queue name in flight.
    synchronized (waitingJobs) {
      String exclusiveKey = job.getExclKey();
      if (waitingJobs.containsKey(exclusiveKey)) {
        // There is already a request in flight. Queue up.
        Queue<Job> stagedRequests = waitingJobs.get(exclusiveKey);
        if (stagedRequests == null) {
          stagedRequests = new PriorityQueue<>();
        }
        stagedRequests.add(job);
        waitingJobs.put(exclusiveKey, stagedRequests);
      } else {
        // Insert 'null' queue for this queue name, indicating there is now a request in flight.
        waitingJobs.put(exclusiveKey, null);
        pendingQueue.add(job);
      }
      return job;
    }
  }

  /**
   * Finish.
   *
   * @param job the job to finish
   */
  void finish(Job job) {
    // Remove from the set of requests currently being processed.
    synchronized (currentJobs) {
      currentJobs.remove(job);
    }
    synchronized (listeners) {
      for (JobPoolListener listener : listeners) {
        listener.onRemove(job, size());
      }
    }

    if (StringUtils.isEmpty(job.getExclKey())) {
      return;
    }

    synchronized (waitingJobs) {
      String exclusiveKey = job.getExclKey();
      Queue<Job> waitingRequests = waitingJobs.get(exclusiveKey);
      if (waitingRequests != null && !waitingRequests.isEmpty()) {
        Job found = waitingRequests.poll();
        pendingQueue.add(found);
      } else {
        waitingJobs.remove(exclusiveKey);
      }
    }
  }

  /**
   * Gets tasks.
   *
   * @return the tasks
   */
  public List<Job> getJobs() {
    return getJobs(job -> true);
  }

  /**
   * Gets tasks.
   *
   * @param filter the filter
   * @return the tasks
   */
  public List<Job> getJobs(RequestFilter filter) {
    List<Job> jobList = new LinkedList<>();
    synchronized (currentJobs) {
      for (Job job : currentJobs) {
        if (filter.apply(job)) {
          jobList.add(job);
        }
      }
    }
    return jobList;
  }

  /**
   * Gets tasks by tag.
   *
   * @param tag the tag
   * @return the tasks by tag
   */
  public List<Job> getJobsByTag(final Object tag) {
    return getJobs(job -> job.getTag() == tag);
  }

  /**
   * Gets jobs by exclusive key.
   *
   * @param key the key
   * @return the jobs by exclusive key
   */
  public List<Job> getJobsByExclusiveKey(final String key) {
    return getJobs(job -> StringUtils.equals(key, job.getExclKey()));
  }

  /**
   * Cancel all.
   */
  public void cancelAll() {
    cancel(job -> true);
  }

  /**
   * Cancel.
   *
   * @param filter the filter
   */
  public void cancel(RequestFilter filter) {
    synchronized (currentJobs) {
      currentJobs.stream().filter(filter::apply).forEach(Job::cancel);
    }
  }

  /**
   * Cancel by tag.
   *
   * @param tag the tag
   */
  public void cancelByTag(final Object tag) {
    cancel(job -> job.getTag() == tag);
  }

  /**
   * Cancel by exclusive key.
   *
   * @param key the key
   */
  public void cancelByExclusiveKey(final String key) {
    cancel(job -> StringUtils.equals(job.getExclKey(), key));
  }

  /**
   * Add job listener.
   *
   * @param listener the listener
   */
  public void addJobListener(JobPoolListener listener) {
    synchronized (listeners) {
      listeners.add(listener);
    }
  }

  /**
   * Remove job listener.
   *
   * @param listener the listener
   */
  public void removeJobListener(JobPoolListener listener) {
    synchronized (listeners) {
      listeners.remove(listener);
    }
  }

  /**
   * The interface Request filter.
   */
  public interface RequestFilter {

    /**
     * Apply boolean.
     *
     * @param job the job
     * @return the boolean
     */
    boolean apply(Job job);
  }

  /**
   * The interface Job pool listener.
   *
   */
  public interface JobPoolListener {

    /**
     * On add.
     *
     * @param job the job
     * @param size the size
     */
    void onAdd(Job job, int size);

    /**
     * On remove.
     *
     * @param job the task
     * @param size the size
     */
    void onRemove(Job job, int size);
  }
}
