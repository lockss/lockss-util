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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An Abstract Job Class.
 *
 * @param <I> the input needed to run the job
 * @param <R> the result returned on completion
 */
public abstract class Job<I, R> implements Comparable<Job<I, R>> {
  /**
   * The Job State.
   */
  public enum State {
    PENDING, EXECUTING, FINISHED
  }

  /**
   * The Job Priority.
   */
  public enum Priority {
    LOW, NORMAL, HIGH, IMMEDIATE
  }

  private static final ExecutorService sExecutor =
      new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1L, TimeUnit.SECONDS, new SynchronousQueue<>());
  private JobPool jobPool;
  private Result.Listener listener;
  private Integer sequenceNum;
  private boolean isDelivered = false;
  private boolean isCanceled = false;
  private boolean isTimeout = false;
  private RetryPolicy retryPolicy;
  private Object tag;
  private long timeOut = 0;
  private Future<R> future;
  private Priority priority = Priority.NORMAL;
  private State state = State.PENDING;
  private I input;
  private String key;

  /**
   * Instantiates a new Job with an listener for errors
   *
   * @param listener the listener
   */
  public Job(I input, Result.Listener listener) {
    this.listener = listener;
    this.input = input;
    setRetryPolicy(new DefaultRetryPolicy());
  }

  /**
   * Is job pending boolean.
   *
   * @return the boolean
   */
  public boolean isPending() {
    return State.PENDING.equals(state);
  }

  /**
   * Is job executing boolean.
   *
   * @return the boolean
   */
  public boolean isExecuting() {
    return State.EXECUTING.equals(state);
  }

  /**
   * Is job done boolean.
   *
   * @return the boolean
   */
  public boolean isDone() {
    return State.FINISHED.equals(state);
  }

  /**
   * Is job canceled boolean.
   *
   * @return the boolean
   */
  public boolean isCanceled() {
    return isCanceled;
  }

  /**
   * Has job timeout boolean.
   *
   * @return the boolean
   */
  public boolean isTimeout() {
    return isTimeout;
  }

  /**
   * Mark delivered.
   */
  public void markDelivered() {
    isDelivered = true;
  }

  /**
   * Has had result delivered boolean.
   *
   * @return the boolean
   */
  public boolean hasHadResultDelivered() {
    return isDelivered;
  }

  /**
   * Gets state.
   *
   * @return the state
   */
  public State getState() {
    return state;
  }

  /**
   * Gets tag.
   *
   * @return the tag
   */
  public Object getTag() {
    return tag;
  }

  /**
   * Sets a optional tag to be used to identify a group of jobs. This tag is not exclusive and
   * multiple jobs with the same tag can be run simultaneously.  It is used for filtering purposes
   * only.
   *
   * @param tag the tag
   */
  public Job<I,R>  setTag(Object tag) {
    this.tag = tag;
    return this;
  }

  /**
   * Gets input used to process this job.
   *
   * @return the input used to process the job
   */
  public I getInput() {
    return input;
  }

  /**
   * Sets data needed to process this job.
   *
   * @param input the data
   */
  public Job<I,R> setInput(I input) {
    this.input = input;
    return this;
  }

  /**
   * Sets timeout.
   *
   * @param timeout the timeout
   * @param unit the units used for timeout (see java.util.concurrent.TimeUnit)
   * @throws IllegalArgumentException if timeout or unit is invalid.
   */
  public void setTimeout(long timeout, TimeUnit unit) {
    if (timeout < 0) {
      throw new IllegalArgumentException("timeout < 0");
    }
    if (unit == null) {
      throw new IllegalArgumentException("unit == null");
    }
    long millis = unit.toMillis(timeout);
    if (millis > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Timeout too large.");
    }
    if (millis == 0 && timeout > 0) {
      throw new IllegalArgumentException("Timeout too small.");
    }
    timeOut = millis;
  }

  /**
   * Gets timeout.
   *
   * @return the timeout
   */
  public long getTimeout() {
    return timeOut;
  }

  /**
   * Reset timeout.
   */
  public void resetTimeout() {
    timeOut = 0;
  }

  /**
   * Gets priority.
   *
   * @return the priority
   */
  public Priority getPriority() {
    return priority;
  }

  /**
   * Sets priority.
   *
   * @param priority the priority
   */
  public Job<I,R>  setPriority(Priority priority) {
    this.priority = priority;
    return this;
  }

  /**
   * Gets sequence as set when added to JobPool.
   *
   * @return the sequence
   * @throws IllegalStateException if sequence is unset.
   */
  public final int getSequence() throws IllegalStateException {
    if (sequenceNum == null) {
      throw new IllegalStateException("getSequence called before added to Pool");
    }
    return sequenceNum;
  }

  /**
   * Called by JobPool to set sequence number when adding a job.
   *
   * @param sequence the sequence
   */
  final void setSequence(int sequence) {
    sequenceNum = sequence;
  }

  /**
   * Gets retry policy.
   *
   * @return the retry policy
   */
  public RetryPolicy getRetryPolicy() {
    return retryPolicy;
  }

  /**
   * Sets retry policy.
   *
   * @param retryPolicy the retry policy
   */
  public Job<I,R>  setRetryPolicy(RetryPolicy retryPolicy) {
    this.retryPolicy = retryPolicy;
    return this;
  }

  /**
   * Cancel this Job
   */
  public void cancel() {
    isCanceled = true;
    cancelFuture();
  }

  /**
   * Deliver error for this job.
   *
   * @param error the error
   */
  public void deliverError(Throwable error) {
    if (listener != null) {
      listener.onError(error);
    }
  }

  /**
   * Deliver result.
   *
   * @param result the result
   */
  public void deliverResult(R result) {
    if (listener != null) {
      listener.onResult(result);
    }
  }

  @Override
  public int compareTo(Job another) {
    Priority left = this.getPriority();
    Priority right = another.getPriority();

    // High-priority requests are "lesser" so they are sorted to the front.
    // Equal priorities are sorted by sequence number to provide FIFO ordering.
    return left == right ? this.sequenceNum - another.sequenceNum
        : right.ordinal() - left.ordinal();
  }

  /**
   * Sets key used for exclusive execution.
   *
   * @param key the exclusive key
   */
  public Job<I,R>  setExclKey(String key) {
    this.key = key;
    return this;
  }


  /**
   * Gets the exclusive identifying key.  Jo
   *
   * @return the exclusive key
   */
  public  String getExclKey() {
    return this.key;
  }

//----------------------------------------------------------------------
// abstract methods - the only method that a subclass needs to implement
//----------------------------------------------------------------------

  /**
   * Execute the job and return the result of type R.
   *
   * @return the job result.
   * @throws JobQException the exception
   */
  protected abstract R onExecute() throws JobQException;


//----------------------------------------------------------------------
// protected methods.
//----------------------------------------------------------------------

  /**
   * On finish.
   */
  protected void onFinish() {
    listener = null;
  }

  /**
   * Finish.
   */
  void finish() {
    state = State.FINISHED;
    if (jobPool != null) {
      jobPool.finish(this);
      onFinish();
    }
  }

  /**
   * Execute the Job and Return the result.
   *
   * @return the result
   * @throws JobQException The exception returned from this job.
   */
  final Result<R> execute() throws JobQException, InterruptedException {
    state = State.EXECUTING;
    for (; ; ) {
      isTimeout = false;
      try {
        future = sExecutor.submit(new Callable<R>() {
          @Override
          public R call() throws Exception {
            return Job.this.onExecute();
          }
        });
        R res = timeOut > 0 ? future.get(getTimeout(), TimeUnit.MILLISECONDS)
            : future.get();
        return Result.success(res);
      }
      catch (TimeoutException te) {
        isTimeout = true;
        if (!retryOrNot()) {
          throw new JobQException("timeout.", te);
        }

      }
      catch (ExecutionException ee) {
        if (!retryOrNot()) {
          throw new JobQException("execution failed.", ee);
        }
      }
      catch (InterruptedException ie) {
        throw ie;
      }
      finally {
        cancelFuture();
      }
    }
  }

  /**
   * Set by JobPool when adding job.
   *
   * @param pool the pool
   */
  void setJobPool(JobPool pool) {
    this.jobPool = pool;
  }

  private boolean retryOrNot() {
    if (retryPolicy != null) {
      return retryPolicy.retryOrNot();
    }
    return false;
  }

  private void cancelFuture() {
    if (future != null && !future.isCancelled()) {
      future.cancel(true);
    }
  }

}
