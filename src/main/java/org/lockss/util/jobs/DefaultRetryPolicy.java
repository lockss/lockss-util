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

/**
 * The type Default retry policy.
 */
public class DefaultRetryPolicy implements RetryPolicy {

  /**
   * The constant DEFAULT_MAX_RETRIES.
   */
  public static final int DEFAULT_MAX_RETRIES = 0;

  private final int mMaxNumRetries;

  private int mCurrentRetryCount;

  /**
   * Instantiates a new Default retry policy.
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
    mMaxNumRetries = maxNumRetries;
  }

  /**
   * Gets current retry count.
   *
   * @return the current retry count
   */
  public int getCurrentRetryCount() {
    return mCurrentRetryCount;
  }

  @Override
  public void retryOrNot(Throwable error) throws Throwable {
    mCurrentRetryCount++;
    if (!hasAttemptRemaining()) {
      throw error;
    }
  }

  /**
   * Has attempt remaining boolean.
   *
   * @return the boolean
   */
  protected boolean hasAttemptRemaining() {
    return mCurrentRetryCount <= mMaxNumRetries;
  }
}
