/*

 Copyright (c) 2019-2020 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util.rest;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import org.apache.http.conn.ConnectTimeoutException;
import org.junit.*;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.test.*;
import org.lockss.log.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.mockserver.junit.*;
import org.mockserver.client.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

/**
 * Test class for org.lockss.util.rest.RestUtil.
 */
public class TestRestUtil extends LockssTestCase5 {
  private static L4JLogger log = L4JLogger.getLogger();

  /**
   * Tests error reporting for network errors
   */
  @Test
  public void testNetworkErrors() {
    String message = "Cannot perform call to fake-fake";

    try {
      doCallRestService("http://fake-fake:12345/v2/api-docs", message);
      fail("Should have thrown LockssRestException");
    } catch (LockssRestException lre) {
      assertMatchesRE(message + ".*UnknownHostException.*fake-fake",
		      lre.getMessage());
      assertClass(UnknownHostException.class, lre.getCause());
      assertMatchesRE("^fake-fake", lre.getCause().getMessage());
    }

    message = "Cannot perform call to 192.0.2.0";

    try {
      doCallRestService("http://192.0.2.0:23456/v2/api-docs", message);
      fail("Should have thrown LockssRestException");
    } catch (LockssRestException lre) {
      assertMatchesRE(message, lre.getMessage());
      // 192.0.2.0 isn't reliably unroutable. On some systems this throws
      // SocketTimeoutException
//       assertClass(NoRouteToHostException.class, lre.getCause());
//       assertMatchesRE("^No route to host", lre.getCause().getMessage());
    }

    message = "Cannot perform call to 127.0.0.1";

    try {
      doCallRestService("http://127.0.0.1:45678/v2/api-docs", message);
      fail("Should have thrown LockssRestException");
    } catch (LockssRestException lre) {
      assertMatchesRE(message + ".*Connection refused", lre.getMessage());
      assertClass(ConnectException.class, lre.getCause());
      assertMatchesRE("Connection refused", lre.getCause().getMessage());
    }

    message = "Cannot perform call to www.lockss.org";

    try {
      doCallRestService("http://www.lockss.org:45678/v2/api-docs", message);
      fail("Should have thrown LockssRestException");
    } catch (LockssRestException lre) {
      assertMatchesRE(message + ".*SocketTimeoutException", lre.getMessage());
      assertClass(ConnectTimeoutException.class, lre.getCause());
      assertMatchesRE("connect timed out", lre.getCause().getMessage());
    }
  }

  @Rule
  public MockServerRule msRule = new MockServerRule(this);

  private MockServerClient msClient;

  @Test

  public void exampleTest() throws LockssRestException {
    int port = msRule.getPort();
    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/foo"))
      .respond(response()
	       .withStatusCode(200));
    ResponseEntity<String> resp =
      doCallRestService("http://localhost:" + port + "/foo", "bar");

    log.info("resp: {}", resp);
//     assertTrue(result.wasSuccessful());

//     verify(postRequestedFor(urlMatching("/my/resource/[a-z0-9]+"))
//             .withRequestBody(matching(".*<message>1234</message>.*"))
//             .withHeader("Content-Type", notMatching("application/json")));
  }
  /**
   * Tests the success evaluation of an HTTP status code.
   */
  @Test
  public void testIsSuccess() {
    assertFalse(RestUtil.isSuccess(HttpStatus.CONTINUE));
    assertFalse(RestUtil.isSuccess(HttpStatus.SWITCHING_PROTOCOLS));
    assertFalse(RestUtil.isSuccess(HttpStatus.PROCESSING));
    assertFalse(RestUtil.isSuccess(HttpStatus.CHECKPOINT));
    assertTrue(RestUtil.isSuccess(HttpStatus.OK));
    assertTrue(RestUtil.isSuccess(HttpStatus.CREATED));
    assertTrue(RestUtil.isSuccess(HttpStatus.ACCEPTED));
    assertTrue(RestUtil.isSuccess(HttpStatus.NON_AUTHORITATIVE_INFORMATION));
    assertTrue(RestUtil.isSuccess(HttpStatus.NO_CONTENT));
    assertTrue(RestUtil.isSuccess(HttpStatus.RESET_CONTENT));
    assertTrue(RestUtil.isSuccess(HttpStatus.PARTIAL_CONTENT));
    assertTrue(RestUtil.isSuccess(HttpStatus.MULTI_STATUS));
    assertTrue(RestUtil.isSuccess(HttpStatus.ALREADY_REPORTED));
    assertTrue(RestUtil.isSuccess(HttpStatus.IM_USED));
    assertFalse(RestUtil.isSuccess(HttpStatus.MULTIPLE_CHOICES));
    assertFalse(RestUtil.isSuccess(HttpStatus.MOVED_PERMANENTLY));
    assertFalse(RestUtil.isSuccess(HttpStatus.FOUND));
    assertFalse(RestUtil.isSuccess(HttpStatus.MOVED_TEMPORARILY));
    assertFalse(RestUtil.isSuccess(HttpStatus.SEE_OTHER));
    assertFalse(RestUtil.isSuccess(HttpStatus.NOT_MODIFIED));
    assertFalse(RestUtil.isSuccess(HttpStatus.USE_PROXY));
    assertFalse(RestUtil.isSuccess(HttpStatus.TEMPORARY_REDIRECT));
    assertFalse(RestUtil.isSuccess(HttpStatus.PERMANENT_REDIRECT));
    assertFalse(RestUtil.isSuccess(HttpStatus.BAD_REQUEST));
    assertFalse(RestUtil.isSuccess(HttpStatus.UNAUTHORIZED));
    assertFalse(RestUtil.isSuccess(HttpStatus.PAYMENT_REQUIRED));
    assertFalse(RestUtil.isSuccess(HttpStatus.FORBIDDEN));
    assertFalse(RestUtil.isSuccess(HttpStatus.NOT_FOUND));
    assertFalse(RestUtil.isSuccess(HttpStatus.METHOD_NOT_ALLOWED));
    assertFalse(RestUtil.isSuccess(HttpStatus.NOT_ACCEPTABLE));
    assertFalse(RestUtil.isSuccess(HttpStatus.PROXY_AUTHENTICATION_REQUIRED));
    assertFalse(RestUtil.isSuccess(HttpStatus.REQUEST_TIMEOUT));
    assertFalse(RestUtil.isSuccess(HttpStatus.CONFLICT));
    assertFalse(RestUtil.isSuccess(HttpStatus.GONE));
    assertFalse(RestUtil.isSuccess(HttpStatus.LENGTH_REQUIRED));
    assertFalse(RestUtil.isSuccess(HttpStatus.PRECONDITION_FAILED));
    assertFalse(RestUtil.isSuccess(HttpStatus.PAYLOAD_TOO_LARGE));
    assertFalse(RestUtil.isSuccess(HttpStatus.REQUEST_ENTITY_TOO_LARGE));
    assertFalse(RestUtil.isSuccess(HttpStatus.URI_TOO_LONG));
    assertFalse(RestUtil.isSuccess(HttpStatus.REQUEST_URI_TOO_LONG));
    assertFalse(RestUtil.isSuccess(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    assertFalse(RestUtil.isSuccess(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE));
    assertFalse(RestUtil.isSuccess(HttpStatus.EXPECTATION_FAILED));
    assertFalse(RestUtil.isSuccess(HttpStatus.I_AM_A_TEAPOT));
    assertFalse(RestUtil.isSuccess(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE));
    assertFalse(RestUtil.isSuccess(HttpStatus.METHOD_FAILURE));
    assertFalse(RestUtil.isSuccess(HttpStatus.DESTINATION_LOCKED));
    assertFalse(RestUtil.isSuccess(HttpStatus.UNPROCESSABLE_ENTITY));
    assertFalse(RestUtil.isSuccess(HttpStatus.LOCKED));
    assertFalse(RestUtil.isSuccess(HttpStatus.FAILED_DEPENDENCY));
    assertFalse(RestUtil.isSuccess(HttpStatus.UPGRADE_REQUIRED));
    assertFalse(RestUtil.isSuccess(HttpStatus.PRECONDITION_REQUIRED));
    assertFalse(RestUtil.isSuccess(HttpStatus.TOO_MANY_REQUESTS));
    assertFalse(RestUtil.isSuccess(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE));
    assertFalse(RestUtil.isSuccess(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS));
    assertFalse(RestUtil.isSuccess(HttpStatus.INTERNAL_SERVER_ERROR));
    assertFalse(RestUtil.isSuccess(HttpStatus.NOT_IMPLEMENTED));
    assertFalse(RestUtil.isSuccess(HttpStatus.BAD_GATEWAY));
    assertFalse(RestUtil.isSuccess(HttpStatus.SERVICE_UNAVAILABLE));
    assertFalse(RestUtil.isSuccess(HttpStatus.GATEWAY_TIMEOUT));
    assertFalse(RestUtil.isSuccess(HttpStatus.HTTP_VERSION_NOT_SUPPORTED));
    assertFalse(RestUtil.isSuccess(HttpStatus.VARIANT_ALSO_NEGOTIATES));
    assertFalse(RestUtil.isSuccess(HttpStatus.INSUFFICIENT_STORAGE));
    assertFalse(RestUtil.isSuccess(HttpStatus.LOOP_DETECTED));
    assertFalse(RestUtil.isSuccess(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED));
    assertFalse(RestUtil.isSuccess(HttpStatus.NOT_EXTENDED));
    assertFalse(RestUtil.isSuccess(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED));
  }

  /**
   * Performs a call to a REST service.
   * 
   * @param url
   *          A String with the URL where to access the REST service.
   * @param message
   *          A String with the message to be returned in case of errors.
   * @return A @{ResponseEntity<String>} with the response provided by the REST
   *         service.
   * @throws LockssRestException
   *           if there are problems.
   */
  private ResponseEntity<String> doCallRestService(String url, String message)
      throws LockssRestException {
    RestTemplate restTemplate = RestUtil.getRestTemplate(2000, 2000);

    // Create the URI of the request to the REST service.
    URI uri = UriComponentsBuilder.newInstance()
	.uriComponents(UriComponentsBuilder.fromUriString(url).build())
	.build().encode().toUri();
    log.trace("uri = {}", uri);

    // Perform the call.
    return RestUtil.callRestService(restTemplate, uri, HttpMethod.GET,
	new HttpEntity<String>(null, new HttpHeaders()), String.class, message);
  }
}
