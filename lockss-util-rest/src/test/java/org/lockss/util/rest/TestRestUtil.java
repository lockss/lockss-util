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

import java.io.*;
import java.util.*;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.junit.*;
import org.lockss.util.*;
import org.lockss.util.rest.exception.*;
import org.lockss.util.rest.multipart.*;
import org.lockss.util.test.*;
import org.lockss.log.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.mockserver.junit.*;
import org.mockserver.client.*;
import org.mockserver.model.Header;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

/**
 * Test class for org.lockss.util.rest.RestUtil.
 */
public class TestRestUtil extends LockssTestCase5 {
  private static L4JLogger log = L4JLogger.getLogger();

  static final String CT_JSON = "application/json; charset=utf-8";

  int port;

  @Rule
  public MockServerRule msRule = new MockServerRule(this);

  @Before
  public void getPort() throws LockssRestException {
    port = msRule.getPort();
  }

  private MockServerClient msClient;

  /**
   * Tests error reporting for network errors
   */
  @Test
  public void testNetworkErrors() {
    String message = "Cannot perform call to fake-fake";

    try {
      doCallRestService("http://fake-fake:12345/v3/api-docs", message,
			String.class);
      fail("Should have thrown LockssRestException");
    } catch (LockssRestException lre) {
      assertMatchesRE(message + ".*UnknownHostException.*fake-fake",
		      lre.getMessage());
      assertClass(UnknownHostException.class, lre.getCause());
      assertMatchesRE("^fake-fake", lre.getCause().getMessage());
    }

    message = "Cannot perform call to 192.0.2.0";

    try {
      doCallRestService("http://192.0.2.0:23456/v3/api-docs", message,
			String.class);
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
      doCallRestService("http://127.0.0.1:45678/v3/api-docs", message,
			String.class);
      fail("Should have thrown LockssRestException");
    } catch (LockssRestException lre) {
      assertMatchesRE(message + ".*Connection refused", lre.getMessage());
      assertClass(ConnectException.class, lre.getCause());
      assertMatchesRE("Connection refused", lre.getCause().getMessage());
    }

    message = "Cannot perform call to www.lockss.org";

    try {
      doCallRestService("http://www.lockss.org:45678/v3/api-docs", message,
			String.class);
      fail("Should have thrown LockssRestException");
    } catch (LockssRestException lre) {
      assertMatchesRE(message + ".*ConnectTimeoutException", lre.getMessage());
      assertClass(ConnectTimeoutException.class, lre.getCause());
      assertMatchesRE("Connect timed out", lre.getCause().getMessage());
    }
  }

  @Test
  public void testIsRetryableException() throws LockssRestException {
    assertTrue(RestUtil.isRetryableException(new UnknownHostException("uncle")));
    assertTrue(RestUtil.isRetryableException(new ConnectException("carbuncle")));
    assertFalse(RestUtil.isRetryableException(new SocketTimeoutException("tim")));;
  }

  @Test
  public void test200String() throws LockssRestException {
    String exp = "string body";

    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/foo"))
      .respond(response()
	       .withStatusCode(200)
	       .withHeaders(new Header("Content-Type", "text/plain"))
	       .withBody(exp));
    ResponseEntity<String> resp =
      doCallRestService("http://localhost:" + port + "/foo", "bar",
			String.class);

    HttpStatusCode statusCode = resp.getStatusCode();
    HttpStatus status = HttpStatus.valueOf(statusCode.value());
    log.debug("status = {}", status);
    log.debug("response = {}", resp);
    assertEquals(exp, resp.getBody());
  }

  @Test
  public void test200Multi() throws IOException {
    String boundaryVal = "boundless-enthusiasm";
    String cr = "\r\n";
    String cr2 = cr + cr;
    String boundary = "--" + boundaryVal + "\r\n";
    String lastBoundary = "--" + boundaryVal + "--\r\n";

    String multipartBody =
      boundary +
      "Content-Disposition: form-data; name=\"ppp-111\"" + cr +
      "P1h1: P1v1" + cr +
      "P1h2: P1v2" + cr2 +
      "part 1 data" + cr +
      boundary +
      "Content-Disposition: form-data; name=\"ppp-222\"" + cr +
      "P2h1: P2v1" + cr +
      "P2h2: P2v2" + cr2 +
      "part 2 data" + cr +
      lastBoundary;

    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/foo"))
      .respond(response()
	       .withStatusCode(200)
	       .withHeaders(new Header("Content-Type",
				       "multipart/form-data; boundary=" +
				       boundaryVal))
	       .withBody(multipartBody));


    RestTemplate template = RestUtil.getRestTemplate();
    template.getMessageConverters().add(new MultipartMessageHttpMessageConverter());

    ResponseEntity<MultipartMessage> resp =
      doCallRestService(template,
			"http://localhost:" + port + "/foo", "bar",
			null, MultipartMessage.class);

    log.debug("resp: {}", resp);
    MultipartResponse mresp = new MultipartResponse(resp);
    Map<String, MultipartResponse.Part> parts = mresp.getParts();

    assertEquals(2, parts.size());
    log.debug("mresp: {}", mresp);
    MultipartResponse.Part p1 = parts.get("ppp-111");
    assertInputStreamMatchesString("part 1 data", p1.getInputStream());
    HttpHeaders p1h = p1.getHeaders();
    assertEquals(ListUtil.list("P1v1"), p1h.get("P1h1"));
    assertEquals(ListUtil.list("P1v2"), p1h.get("P1h2"));
    MultipartResponse.Part p2 = parts.get("ppp-222");
    assertInputStreamMatchesString("part 2 data", p2.getInputStream());
    HttpHeaders p2h = p2.getHeaders();
    assertEquals(ListUtil.list("P2v1"), p2h.get("P2h1"));
    assertEquals(ListUtil.list("P2v2"), p2h.get("P2h2"));
  }

  @Test
  public void test200Json() throws LockssRestException {
    Map exp = MapUtil.map("foo", "bar", "gorp", "frazz");

    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/foo"))
      .respond(response()
	       .withStatusCode(200)
	       .withHeaders(new Header("Content-Type", CT_JSON))
	       .withBody(toJson(exp)));
    ResponseEntity<Map> resp =
      doCallRestService("http://localhost:" + port + "/foo", "bar",
			Map.class);

    HttpStatusCode statusCode = resp.getStatusCode();
    HttpStatus status = HttpStatus.valueOf(statusCode.value());
    log.debug("status = {}", status);
    log.debug("response = {}", resp);
    Map resMap = resp.getBody();
    assertEquals(exp, resMap);
  }

  @Test
  public void test401Json() throws LockssRestException {
    RestResponseErrorBody.RestResponseError error =
        new RestResponseErrorBody.RestResponseError()
            .setMessage("in a bottle")
            .setPath("test")
            .setTimestamp(1234);

    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/foo"))
      .respond(response()
	       .withStatusCode(401)
	       .withHeaders(new Header("Content-Type", CT_JSON))
	       .withBody(toJson(error)));

    try {
      ResponseEntity<RestResponseErrorBody.RestResponseError> resp =
          doCallRestService("http://localhost:" + port + "/foo", "bar",
			  RestResponseErrorBody.RestResponseError.class);
      Assert.fail("Should have thrown, but returned: " + resp);
    } catch (LockssRestHttpException e) {
      assertEquals(HttpStatus.UNAUTHORIZED, e.getHttpStatus());
      assertEquals(error, e.getRestResponseError());
      assertEquals("in a bottle", e.getServerErrorMessage());
    }

  }

  @Test
  public void test404String() throws LockssRestException {
    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/foo"))
      .respond(response()
	       .withStatusCode(404)
	       .withHeaders(new Header("Content-Type", "text/plain"))
	       .withBody("Error body"));

    try {
      ResponseEntity<RestResponseErrorBody.RestResponseError> resp =
          doCallRestService("http://localhost:" + port + "/foo", "bar",
			  RestResponseErrorBody.RestResponseError.class);
      Assert.fail("Should have thrown, but returned: " + resp);
    } catch (LockssRestHttpException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
      assertEquals("Error body", e.getServerErrorMessage());
    }
  }

  /* This is a (failed) attempt to reproduce this error:

     17:32:52.300 [ConfigHandler] INFO  BaseConfigFile: Couldn't load remote config URL: http://lockss-u:lockss-p@localhost:24620/config/file/cluster: java.lang.IllegalArgumentException: Auth scheme may not be null

     which occurs deep inside httpclient after a 401 response.
  */
  @Test
  public void testJson401Full() throws LockssRestException {
    Map exp = MapUtil.map("foo", "bar", "gorp", "frazz");

    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/foo"))
      .respond(response()
	       .withStatusCode(401)
	       .withHeaders(new Header("Content-Type", CT_JSON),
			    new Header("WWW-Authenticate", "Basic"),
			    new Header("X-Content-Type-Options", "nosniff"),
			    new Header("X-XSS-Protection", "1; mode=block"),
			    new Header("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate"),
			    new Header("Pragma", "no-cache"),
			    new Header("Expires", "0"),
			    new Header("X-Frame-Options", "DENY"),
			    new Header("Date", "Thu, 17 Sep 2020 23:42:39 GMT"))
	       .withBody(toJson(exp)));
    try {
      HttpHeaders hdrs = new HttpHeaders();
      hdrs.add("Authorization", "Basic bG9ja3NzLXU6bG9ja3NzLXA=");
      ResponseEntity<Map> resp =
	doCallRestService("http://localhost:" + port + "/foo", "bar",
			  hdrs, Map.class);
      Assert.fail("Should have thrown, but returned: " + resp);
    } catch (LockssRestHttpException e) {
      assertEquals(HttpStatus.UNAUTHORIZED, e.getHttpStatus());
    }
  }

  String toJson(Map map) {
    try {
      return new ObjectMapper().writeValueAsString(map);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Json error", e);
    }
  }

  String toJson(RestResponseErrorBody.RestResponseError error) {
    try {
      return new ObjectMapper().writeValueAsString(error);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Json error", e);
    }
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
  private <T> ResponseEntity<T> doCallRestService(String url, String message,
						  Class<T> responseType)
      throws LockssRestException {
    return doCallRestService(url, message, new HttpHeaders(), responseType);
  }

  private <T> ResponseEntity<T> doCallRestService(String url, String message,
						  HttpHeaders reqHeaders,
						  Class<T> responseType)
      throws LockssRestException {
    return doCallRestService(RestUtil.getRestTemplate(2000, 2000),
			     url, message, reqHeaders, responseType);
  }

  private <T> ResponseEntity<T> doCallRestService(RestTemplate template,
						  String url, String message,
						  HttpHeaders reqHeaders,
						  Class<T> responseType)
      throws LockssRestException {

    // Create the URI of the request to the REST service.
    URI uri = UriComponentsBuilder.newInstance()
	.uriComponents(UriComponentsBuilder.fromUriString(url).build())
	.build().encode().toUri();
    log.trace("uri = {}", uri);

    // Perform the call.
    return RestUtil.callRestService(template, uri, HttpMethod.GET,
	new HttpEntity<String>(null, reqHeaders), responseType, message);
  }
}
