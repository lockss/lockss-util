/*
 * Copyright (c) 2000-2017 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.util.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;

/**
 * The type Job Queue.
 */
public class JobPool {

  private final AtomicInteger mSequenceGenerator = new AtomicInteger();
  private final Set<Job<?, ?>> mCurrentJobs = new HashSet<>();
  private final PriorityBlockingQueue<Job<?, ?>> mPendingQueue
      = new PriorityBlockingQueue<>();
  private final Map<String, Queue<Job<?, ?>>> mWaitingJobs
      = new HashMap<>();
  private final ResultDelivery mDelivery;
  private final List<JobPoolListener> mJobPoolListeners = new ArrayList<>();
  private Dispatcher mDispatcher;
  private ExecutorService mJobExecutor;
  private boolean isRunning = false;

  /**
   * Instantiates a new Job pool of a.
   *
   * @param delivery the delivery
   */
  public JobPool(ResultDelivery delivery) {
    assert (delivery != null);
    mDelivery = delivery;
  }

  /**
   * Start this job pool. This will create an executor and dispatcher.
   */
  public synchronized void start() {
    stop();

    try {
      mJobExecutor = createExecutor();
      mDispatcher = new Dispatcher(mPendingQueue, mJobExecutor, mDelivery);
      mDispatcher.start();
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
   * Stop all the jobs in this Job Pool and clear the queues.
   */
  public synchronized void stop() {
    isRunning = false;
    if (mDispatcher != null) {
      mDispatcher.quit();
    }
    if (mJobExecutor != null) {
      mJobExecutor.shutdownNow();
    }

    synchronized (mCurrentJobs) {
      mCurrentJobs.clear();
      mPendingQueue.clear();
      mWaitingJobs.clear();
      mSequenceGenerator.set(0);
    }
  }

  /**
   * return the size of the current job queue
   *
   * @return the int
   */
  public int size() {
    return mCurrentJobs.size();
  }

  /**
   * return a sequence number for a specific job.
   * @return
   */
  private int getSequenceNumber() {
    return mSequenceGenerator.incrementAndGet();
  }

  /**
   * Add a job to the the job pool.
   *
   * @param <I> the type parameter
   * @param <O> the type parameter
   * @param job the job
   * @return the job
   */
  public <I, O> Job<I, O> add(Job<I, O> job) {
    assert (job != null);
    if (!isRunning) {
      return null;
    }

    // Tag the Job as belonging to this pool and add it to the set of current Jobs.
    job.setJobPool(this);
    job.setSequence(getSequenceNumber());
    synchronized (mCurrentJobs) {
      mCurrentJobs.add(job);
    }
    synchronized (mJobPoolListeners) {
      for (JobPoolListener<I, O> listener : mJobPoolListeners) {
        listener.onAdd(job, size());
      }
    }

    return dispatch(job);
  }

  private <I, O> Job<I, O> dispatch(Job<I, O> job) {
    if (StringUtils.isEmpty(job.getExclusiveKey())) {
      mPendingQueue.add(job);
      return job;
    }

    // Insert request into stage if there's already a request with the same queue name in flight.
    synchronized (mWaitingJobs) {
      String exclusiveKey = job.getExclusiveKey();
      if (mWaitingJobs.containsKey(exclusiveKey)) {
        // There is already a request in flight. Queue up.
        Queue<Job<?, ?>> stagedRequests = mWaitingJobs.get(exclusiveKey);
        if (stagedRequests == null) {
          stagedRequests = new PriorityQueue<Job<?, ?>>();
        }
        stagedRequests.add(job);
        mWaitingJobs.put(exclusiveKey, stagedRequests);
      } else {
        // Insert 'null' queue for this queue name, indicating there is now a request in flight.
        mWaitingJobs.put(exclusiveKey, null);
        mPendingQueue.add(job);
      }
      return job;
    }
  }

  /**
   * Finish.
   *
   * @param <I> the type parameter
   * @param <O> the type parameter
   * @param job the job
   */
  <I, O> void finish(Job<I, O> job) {
    // Remove from the set of requests currently being processed.
    synchronized (mCurrentJobs) {
      mCurrentJobs.remove(job);
    }
    synchronized (mJobPoolListeners) {
      for (JobPoolListener listener : mJobPoolListeners) {
        listener.onRemove(job, size());
      }
    }

    if (StringUtils.isEmpty(job.getExclusiveKey())) {
      return;
    }

    synchronized (mWaitingJobs) {
      String exclusiveKey = job.getExclusiveKey();
      Queue<Job<?, ?>> waitingRequests = mWaitingJobs.get(exclusiveKey);
      if (waitingRequests != null && !waitingRequests.isEmpty()) {
        Job<?, ?> queueJob = waitingRequests.poll();
        mPendingQueue.add(queueJob);
      } else {
        mWaitingJobs.remove(exclusiveKey);
      }
    }
  }

  /**
   * Gets jobs.
   *
   * @return the jobs
   */
  public List<Job<?, ?>> getJobs() {
    return getJobs(job -> true);
  }

  /**
   * Gets jobs.
   *
   * @param filter the filter
   * @return the jobs
   */
  public List<Job<?, ?>> getJobs(RequestFilter filter) {
    List<Job<?, ?>> jobs = new LinkedList<>();
    synchronized (mCurrentJobs) {
      for (Job<?, ?> job : mCurrentJobs) {
        if (filter.apply(job)) {
          jobs.add(job);
        }
      }
    }
    return jobs;
  }

  /**
   * Gets jobs by tag.
   *
   * @param tag the tag
   * @return the jobs by tag
   */
  public List<Job<?, ?>> getJobsByTag(final Object tag) {
    return getJobs(job -> job.getTag() == tag);
  }

  /**
   * Gets jobs by exclusive key.
   *
   * @param key the key
   * @return the jobs by exclusive key
   */
  public List<Job<?, ?>> getJobsByExclusiveKey(final String key) {
    return getJobs(job -> StringUtils.equals(key, job.getExclusiveKey()));
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
    synchronized (mCurrentJobs) {
      mCurrentJobs.stream().filter(filter::apply).forEach(Job::cancel);
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
    cancel(job -> StringUtils.equals(job.getExclusiveKey(), key));
  }

  /**
   * Add job listener.
   *
   * @param <I> the type parameter
   * @param <O> the type parameter
   * @param listener the listener
   */
  public <I, O> void addJobListener(JobPoolListener<I, O> listener) {
    synchronized (mJobPoolListeners) {
      mJobPoolListeners.add(listener);
    }
  }

  /**
   * Remove job listener.
   *
   * @param <I> the type parameter
   * @param <O> the type parameter
   * @param listener the listener
   */
  public <I, O> void removeJobListener(JobPoolListener<I, O> listener) {
    synchronized (mJobPoolListeners) {
      mJobPoolListeners.remove(listener);
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
    boolean apply(Job<?, ?> job);
  }

  /**
   * The interface Job pool listener.
   *
   * @param <I> the type parameter
   * @param <O> the type parameter
   */
  public interface JobPoolListener<I, O> {

    /**
     * On add.
     *
     * @param job the job
     * @param size the size
     */
    void onAdd(Job<I, O> job, int size);

    /**
     * On remove.
     *
     * @param job the job
     * @param size the size
     */
    void onRemove(Job<I, O> job, int size);
  }
}
