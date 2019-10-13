/*

 Copyright (c) 2019 Board of Trustees of Leland Stanford Jr. University,
 all rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Except as contained in this notice, the name of Stanford University shall not
 be used in advertising or otherwise to promote the sale, use or other dealings
 in this Software without prior written authorization from Stanford University.

 */
package org.lockss.util.rest.exception;

import java.io.IOException;

public abstract class LockssRestException extends IOException {
  private static final long serialVersionUID = -7396951390932243207L;

  /**
   * Default constructor.
   */
  public LockssRestException() {
    super();
  }

  /**
   * Constructor with a specified message.
   * 
   * @param message
   *          A String with the exception message.
   */
  public LockssRestException(String message) {
    super(message);
  }

  /**
   * Constructor with a specified cause.
   * 
   * @param cause
   *          A Throwable with the exception cause.
   */
  public LockssRestException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor with specified message and cause.
   * 
   * @param message
   *          A String with the exception message.
   * @param cause
   *          A Throwable with the exception cause.
   */
  public LockssRestException(String message, Throwable cause) {
    super(message, cause);
  }

  public String getShortMessage() {
    return getMessage();
  }

}
