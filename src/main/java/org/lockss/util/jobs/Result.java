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
 * A wrapper class around a return object of some type T which was used to define the Job object for this is a result.
 *
 * @param <T> the type parameter
 */
public class Result<T> {

  /**
   * The  result if one was obtained.
   */
  public final T result;
  /**
   * The error if one occurred.
   */
  public final Throwable error;

  private Result(T result) {
    this(result, null);
  }

  private Result(Throwable error) {
    this(null, error);
  }

  private Result(T result, Throwable error) {
    this.result = result;
    this.error = error;
  }

  /**
   * static constructor for an object for which there is a result and no error.
   *
   * @param <T> the type parameter
   * @param result the result
   * @return the result
   */
  public static <T> Result<T> success(T result) {
    return new Result<>(result);
  }

  /**
   * create an error result which has a throwable but no actual return object
   *
   * @param <T> the type parameter
   * @param error the error
   * @return the result
   */
  public static <T> Result<T> error(Throwable error) {
    return new Result<>(error);
  }


  /**
   * return true if there was no error.
   *
   * @return the boolean
   */
  public boolean isSuccess() {
    return error == null;
  }

  /**
   *  An interface for an listener for job results..
   *
   * @param <T> the result object parameter
   */
  public interface Listener<T> {

    /**
     * respond to a result.
     *
     * @param result the result
     */
    void onResult(T result);
  }

  /**
   * The for a listener for job errors
   */
  public interface ErrorListener {

    /**
     * respond to an error.
     *
     * @param error the error
     */
    void onError(Throwable error);
  }

}
