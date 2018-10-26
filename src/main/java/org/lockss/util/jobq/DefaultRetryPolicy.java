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

/**
 * The Default retry policy which encapsulates a simple try n number of times algorithm.
 * This will attempt a retry maxNumRetries
 */
public class DefaultRetryPolicy implements RetryPolicy {

  /**
   * The  DEFAULT_MAX_RETRIES or 0.
   */
  public static final int DEFAULT_MAX_RETRIES = 0;

  /**
   * The maximum number of retries for each job request.
   */
  private final int maxNumRetries;

  /**
   * The current number of retries.
   */
  private int currentRetryCount =0;

  /**
   * Instantiates a new Default retry policy using #DEFAULT_MAX_RETRIES.
   */
  public DefaultRetryPolicy() {

    this(DEFAULT_MAX_RETRIES);
  }

  /**
   * Instantiates a new Default retry policy.
   *
   * @param maxNumRetries the max num retries
   */
  public DefaultRetryPolicy(int maxNumRetries) {

    this.maxNumRetries = maxNumRetries;
  }

  /**
   * Gets current retry count.
   *
   * @return the current retry count
   */
  public int getCurrentRetryCount() {

    return currentRetryCount;
  }


  /**
   * Has attempt remaining boolean.
   *
   * @return the boolean
   */
  protected boolean hasAttemptRemaining() {
    return currentRetryCount <= maxNumRetries;
  }

  /**
   * Retry or not based on the defined retry policy.
   *
   * @return boolean true iff it is acceptable to retry.
   */
  @Override
  public boolean retryOrNot() {
    currentRetryCount++;
    return hasAttemptRemaining();

  }
}
