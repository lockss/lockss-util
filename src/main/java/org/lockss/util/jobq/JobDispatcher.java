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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * The Job Pool Dispatcher Thread.
 */
public final class JobDispatcher extends Thread {

  /**
   * The pending job queue
   */
  private final BlockingQueue<Job> pendingQueue;
  /**
   * The executor service for this job queue.
   */
  private final ExecutorService executor;
  /**
   * The Result deliverer thread.
   */
  private final ResultDispatcher resultDispatcher;

  private volatile boolean hasQuit = false;

  /**
   * Instantiates a new Dispatcher for a Job Pool.
   *
   * @param pendingQueue the pending queue
   * @param executor the executor for running jobs
   * @param dispatcher the class meant to handle the results of run jobs
   */
  JobDispatcher(BlockingQueue<Job> pendingQueue, ExecutorService executor,
      ResultDispatcher dispatcher) {
    this.pendingQueue = pendingQueue;
    this.executor = executor;
    resultDispatcher = dispatcher;
  }

  /**
   * kill the job dispatcher.
   */
  public void quit() {
    hasQuit = true;
    interrupt();
  }

  /**
   * The business logic of the job pool. Take one job off the queue, check that it hasn't been
   * terminated, then execute the job returning the result to the deliverer.
   */
  @Override
  public void run() {
    setPriority(10);
    while (true) {
      try {
        // Take a job from the queue.
        final Job job = pendingQueue.take();
        // if the job was canceled finish it.
        if (job.isCanceled()) {
          job.finish();
          continue;
        }
        // if the executor is still running execute the job
        if (!executor.isShutdown()) {
          executor.execute(() -> {
            try {
              // finish any jobs that need to be cleared.
              if (job.isCanceled() || job.isTimeout() || job.hasHadResultDelivered()) {
                job.finish();
                return;
              }
              // otherwise execute the job and set the delivery thread to handle it.
              Result result = job.execute();
              resultDispatcher.postResult(job, result);
            }
            catch (Throwable error) {
              resultDispatcher.postError(job, error);
            }
          });
        }
      } catch (InterruptedException e) {
        // We may have been interrupted because it was time to quit.
        if (hasQuit) {
          return;
        }
      }
    }
  }
}
