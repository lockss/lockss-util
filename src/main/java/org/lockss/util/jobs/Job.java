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
 * @param <I> the data parameter
 * @param <O> the result object parameter
 */
public abstract class Job<I, O> implements Comparable<Job<I, O>> {

  private static final ExecutorService sExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
      1L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
  private transient JobPool mJobPool;
  private Result.ErrorListener mErrorListener;
  private Integer mSequence;
  private boolean mResultDelivered = false;
  private boolean mIsCanceled = false;
  private boolean mIsTimeout = false;
  private RetryPolicy mRetryPolicy;
  private Object mTag;
  private long mTimeout = 0;
  private Future<O> mFuture;
  private Priority mPriority = Priority.NORMAL;
  private State mState = State.PENDING;

  /**
   * Instantiates a new Job.
   *
   * @param listener the listener
   */
  public Job(Result.ErrorListener listener) {
    mErrorListener = listener;
    setRetryPolicy(new DefaultRetryPolicy());
  }

  /**
   * Is pending boolean.
   *
   * @return the boolean
   */
  public boolean isPending() {
    return State.PENDING.equals(mState);
  }

  /**
   * Is executing boolean.
   *
   * @return the boolean
   */
  public boolean isExecuting() {
    return State.EXECUTING.equals(mState);
  }

  /**
   * Is done boolean.
   *
   * @return the boolean
   */
  public boolean isDone() {
    return State.FINISHED.equals(mState);
  }

  /**
   * Is canceled boolean.
   *
   * @return the boolean
   */
  public boolean isCanceled() {
    return mIsCanceled;
  }

  /**
   * Is timeout boolean.
   *
   * @return the boolean
   */
  public boolean isTimeout() {
    return mIsTimeout;
  }

  /**
   * Mark delivered.
   */
  public void markDelivered() {
    mResultDelivered = true;
  }

  /**
   * Has had result delivered boolean.
   *
   * @return the boolean
   */
  public boolean hasHadResultDelivered() {
    return mResultDelivered;
  }

  /**
   * Gets state.
   *
   * @return the state
   */
  public State getState() {
    return mState;
  }

  /**
   * Gets tag.
   *
   * @return the tag
   */
  public Object getTag() {
    return mTag;
  }

  /**
   * Sets tag.
   *
   * @param tag the tag
   */
  public void setTag(Object tag) {
    mTag = tag;
  }

  /**
   * Gets timeout.
   *
   * @return the timeout
   */
  public long getTimeout() {
    return mTimeout;
  }

  /**
   * Sets timeout.
   *
   * @param timeout the timeout
   * @param unit the unit
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
    mTimeout = millis;
  }

  /**
   * Reset timeout.
   */
  public void resetTimeout() {
    mTimeout = 0;
  }

  /**
   * Gets priority.
   *
   * @return the priority
   */
  public Priority getPriority() {
    return mPriority;
  }

  /**
   * Sets priority.
   *
   * @param priority the priority
   */
  public void setPriority(Priority priority) {
    mPriority = priority;
  }

  /**
   * Gets sequence.
   *
   * @return the sequence
   */
  public final int getSequence() {
    if (mSequence == null) {
      throw new IllegalStateException("getSequence called before setSequence");
    }
    return mSequence;
  }

  /**
   * Sets sequence.
   *
   * @param sequence the sequence
   */
  final void setSequence(int sequence) {
    mSequence = sequence;
  }

  /**
   * Gets retry policy.
   *
   * @return the retry policy
   */
  public RetryPolicy getRetryPolicy() {
    return mRetryPolicy;
  }

  /**
   * Sets retry policy.
   *
   * @param retryPolicy the retry policy
   */
  public void setRetryPolicy(RetryPolicy retryPolicy) {
    mRetryPolicy = retryPolicy;
  }

  /**
   * Sets job pool.
   *
   * @param pool the pool
   */
  public void setJobPool(JobPool pool) {
    this.mJobPool = pool;
  }

  /**
   * Cancel.
   */
  public void cancel() {
    mIsCanceled = true;
    cancelFuture();
  }

  /**
   * Deliver error.
   *
   * @param error the error
   */
  public void deliverError(Throwable error) {
    if (mErrorListener != null) {
      mErrorListener.onError(error);
    }
  }

  @Override
  public int compareTo(Job another) {
    Priority left = this.getPriority();
    Priority right = another.getPriority();

    // High-priority requests are "lesser" so they are sorted to the front.
    // Equal priorities are sorted by sequence number to provide FIFO ordering.
    return left == right ? this.mSequence - another.mSequence
        : right.ordinal() - left.ordinal();
  }

  /**
   * Gets exclusive key.
   *
   * @return the exclusive key
   */
// public abstract methods
  public abstract String getExclusiveKey();

  /**
   * Gets data.
   *
   * @return the data
   */
  public abstract I getData();

  /**
   * Sets data.
   *
   * @param data the data
   */
  public abstract void setData(I data);

  /**
   * Deliver result.
   *
   * @param result the result
   */
  protected abstract void deliverResult(O result);

  /**
   * On execute o.
   *
   * @return the o
   * @throws Exception the exception
   */
  protected abstract O onExecute() throws Exception;

  /**
   * On finish.
   */
  protected void onFinish() {
    mErrorListener = null;
  }

  /**
   * Finish.
   */
  void finish() {
    mState = State.FINISHED;
    if (mJobPool != null) {
      mJobPool.finish(this);
      onFinish();
    }
  }

  // protected methods

  /**
   * Execute result.
   *
   * @return the result
   * @throws Throwable the throwable
   */
  final Result<O> execute() throws Throwable {
    mState = State.EXECUTING;
    for (; ; ) {
      mIsTimeout = false;
      try {
        mFuture = sExecutor.submit(() -> onExecute());

        O res = mTimeout > 0 ? mFuture.get(getTimeout(), TimeUnit.MILLISECONDS)
            : mFuture.get();

        return Result.success(res);
      } catch (TimeoutException e) {
        mIsTimeout = true;
        retryOrNot(e);
      } catch (ExecutionException e) {
        retryOrNot(e.getCause());
      } finally {
        cancelFuture();
      }
    }
  }

  private void retryOrNot(Throwable e) throws Throwable {
    if (mRetryPolicy != null) {
      mRetryPolicy.retryOrNot(e);
    } else {
      throw e;
    }
  }

  private void cancelFuture() {
    if (mFuture != null && !mFuture.isCancelled()) {
      mFuture.cancel(true);
    }
  }

  /**
   * The enum State.
   */
  public enum State {
    /**
     * Pending state.
     */
    PENDING, /**
     * Executing state.
     */
    EXECUTING, /**
     * Finished state.
     */
    FINISHED,
  }

  /**
   * The enum Priority.
   */
  public enum Priority {
    /**
     * Low priority.
     */
    LOW, /**
     * Normal priority.
     */
    NORMAL, /**
     * High priority.
     */
    HIGH, /**
     * Immediate priority.
     */
    IMMEDIATE
  }

}

