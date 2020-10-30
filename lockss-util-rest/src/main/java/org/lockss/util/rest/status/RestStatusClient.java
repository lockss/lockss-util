/*

 Copyright (c) 2018-2020 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util.rest.status;

import org.springframework.http.*;
import org.lockss.log.*;
import org.lockss.util.rest.RestBaseClient;
import org.lockss.util.rest.exception.*;
import org.lockss.util.rest.status.ApiStatus;

/**
 * A client of the status endpoint of a REST service.
 */
public class RestStatusClient extends RestBaseClient {
  private static L4JLogger log = L4JLogger.getLogger();

  /**
   * Constructor without authentication and with default timeouts.
   * 
   * @param serviceUrl A String with the information necessary to access the
   *                   REST Poller web service.
   */
  public RestStatusClient(String serviceUrl) {
    super(serviceUrl);
  }

  /**
   * Constructor without authentication and with specified timeouts.
   * 
   * @param serviceUrl     A String with the information necessary to access the
   *                       REST Poller web service.
   * @param connectTimeout A long with the connection timeout in milliseconds.
   * @param readTimeout    A long with the read timeout in milliseconds.
   */
  public RestStatusClient(String serviceUrl,
			  long connectTimeout, long readTimeout) {
    super(serviceUrl, connectTimeout, readTimeout);
  }

  /**
   * Provides the status of the REST service.
   * 
   * @return an ApiStatus with the status of the REST service.
   * @throws LockssRestException if there were problems getting the status.
   */
  public ApiStatus getStatus() throws LockssRestException  {
    try {
      ResponseEntity<ApiStatus> response = callRestService("/status", null,
	  null, HttpMethod.GET, null, null, ApiStatus.class,
	  "Can't get status");
      int status = response.getStatusCodeValue();
      log.debug2("status = " + status);
      ApiStatus result = response.getBody();
      log.debug2("result = " + result);
      return result;
    } catch (RuntimeException e) {
      throw new LockssRestNetworkException(e);
    }
  }
}
