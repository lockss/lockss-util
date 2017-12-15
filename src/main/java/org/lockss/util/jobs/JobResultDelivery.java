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

import java.util.concurrent.Executor;

/**
 * The type Job result delivery.
 */
public class JobResultDelivery implements ResultDelivery {

  private final Executor mResponsePoster;

  /**
   * Instantiates a new Job result delivery.
   *
   * @param executor the executor
   */
  public JobResultDelivery(Executor executor) {
    mResponsePoster = executor;
  }

  @Override
  public void postResult(Job<?, ?> task, Result<?> result) {
    postResult(task, result, null);
  }

  @Override
  public void postResult(Job<?, ?> task, Result<?> result, Runnable runnable) {
    mResponsePoster.execute(new ResponseDeliveryRunnable(task, result, runnable));
  }

  @Override
  public void postError(Job<?, ?> task, Throwable error) {
    Result<?> result = Result.error(error);
    mResponsePoster.execute(new ResponseDeliveryRunnable(task, result, null));
  }

  @SuppressWarnings("rawtypes")
  private class ResponseDeliveryRunnable implements Runnable {

    private final Job mJob;
    private final Result mResult;
    private final Runnable mRunnable;

    /**
     * Instantiates a new Response delivery runnable.
     *
     * @param task the task
     * @param result the result
     * @param runnable the runnable
     */
    ResponseDeliveryRunnable(Job task, Result result, Runnable runnable) {
      mJob = task;
      mResult = result;
      mRunnable = runnable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
      // If this request has canceled, finish it and don't deliver.
      if (mJob.isCanceled()) {
        mJob.finish();
        return;
      }

      if (mResult.isSuccess()) {
        mJob.markDelivered();
        mJob.deliverResult(mResult.result);
      } else {
        mJob.deliverError(mResult.error);
      }

      mJob.finish();

      if (mRunnable != null) {
        mRunnable.run();
      }
    }
  }
}
