/*

Copyright (c) 2000-2023, Board of Trustees of Leland Stanford Jr. University

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.util.rest;

import java.util.concurrent.TimeUnit;
import org.junit.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockserver.junit.*;
import org.mockserver.client.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;
import org.mockserver.model.Header;

import org.lockss.util.rest.status.ApiStatus;
import org.lockss.log.*;
import org.lockss.util.test.*;
import org.lockss.util.rest.exception.*;
import org.lockss.util.rest.status.RestStatusClient;
import static org.lockss.util.Constants.SECOND;
import static org.lockss.util.Constants.MINUTE;

/**
 * Test class for org.lockss.util.rest.RestStatusClient.
 */
// Use MockServer, which starts a real server, rather than Spring's
// MockRestServiceServer because we want to test timeouts, etc., which the
// latter ignores.
public class TestRestStatusClient extends LockssTestCase5 {
  private static L4JLogger log = L4JLogger.getLogger();

  // injected by the MockServerRule
  private MockServerClient msClient;

  int port;

  String baseUrl() {
    return "http://localhost:" + port;
  }

  @Rule
  public MockServerRule msRule = new MockServerRule(this);

  @Before
  public void getPort() {
    port = msRule.getPort();
  }

  String toJson(ApiStatus as) {
    try {
      return new ObjectMapper().writeValueAsString(as);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Json error", e);
    }
  }

  ApiStatus AS_READY = new ApiStatus()
    .setReady(true).setReason("steady").setComponentName("Comp 1");

  ApiStatus AS_NOTREADY = new ApiStatus()
    .setReady(false).setReason("Starting still").setComponentName("Comp 1");

  @Test
  public void testReady() throws LockssRestException {
    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/status"))
      .respond(response()
	       .withStatusCode(200)
	       .withHeaders(new Header("Content-Type",
				       "application/json; charset=utf-8"))
	       .withBody(toJson(AS_READY)));

    RestStatusClient rsc = new RestStatusClient(baseUrl());

    ApiStatus as = rsc.getStatus();
    log.debug2("resp: {}", as);
    assertTrue(as.isReady());
    assertEquals("Comp 1", as.getComponentName());
  }

  @Test
  public void testNotReady() throws LockssRestException {
    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/status"))
      .respond(response()
	       .withStatusCode(200)
	       .withHeaders(new Header("Content-Type",
				       "application/json; charset=utf-8"))
	       .withBody(toJson(AS_NOTREADY)));

    RestStatusClient rsc = new RestStatusClient(baseUrl());

    ApiStatus as = rsc.getStatus();
    log.debug2("resp: {}", as);
    assertFalse(as.isReady());
    assertEquals("Comp 1", as.getComponentName());
    assertEquals("Starting still", as.getReason());
  }

  @Test
  public void testConnectTimeout() throws LockssRestException {
    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/status")
	    )
      .respond(response()
	       .withStatusCode(200)
	       .withHeaders(new Header("Content-Type",
				       "application/json; charset=utf-8"))
	       .withBody(toJson(AS_READY))
	       );

    RestStatusClient rsc = new RestStatusClient("http://www.lockss.org:45678")
      .setTimeouts(SECOND, MINUTE);

    try {
      ApiStatus as = rsc.getStatus();
      fail("Expected read timeout to throw but returned: " + as);
    } catch (LockssRestNetworkException e) {
      assertMatchesRE("Can't get status.*connect timed out", e.getMessage());
    } catch (LockssRestException e) {
      fail("Should have thrown LockssRestNetworkException but threw LockssRestException: " + e);
    }
  }

  @Test
  public void testResponseTimeout() throws LockssRestException {
    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/status")
	    )
      .respond(response()
	       .withStatusCode(200)
	       .withHeaders(new Header("Content-Type",
				       "application/json; charset=utf-8"))
	       .withDelay(TimeUnit.SECONDS, 10)
	       .withBody(toJson(AS_READY))
	       );

    RestStatusClient rsc = new RestStatusClient(baseUrl())
      .setTimeouts(SECOND, SECOND);

    try {
      ApiStatus as = rsc.getStatus();
      fail("Expected read timeout to throw but returned: " + as);
    } catch (LockssRestNetworkException e) {
      assertMatchesRE("Can't get status.*Read timed out", e.getMessage());
    } catch (LockssRestException e) {
      fail("Should have thrown LockssRestNetworkException but threw LockssRestException: " + e);
    }
  }

  @Test
  public void test500() throws LockssRestException {
    msClient
      .when(request()
            .withMethod("GET")
            .withPath("/status"))
      .respond(response()
	       .withStatusCode(500)
	       .withReasonPhrase("English Pointer Exception")
	       );

    RestStatusClient rsc = new RestStatusClient(baseUrl());

    try {
      ApiStatus as = rsc.getStatus();
      fail("Expected 500 response to throw but returned: " + as);
    } catch (LockssRestHttpException e) {
      assertMatchesRE("Can't get status", e.getMessage());
      assertEquals(500, e.getHttpStatusCode());
      // XXX We always get the default message for the status code
//       assertEquals("English Pointer Exception", e.getHttpStatusMessage());
    } catch (LockssRestException e) {
      fail("Should have thrown LockssRestHttpException but threw LockssRestException: " + e);
    }
  }

}
