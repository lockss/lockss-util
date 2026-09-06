/*

Copyright (c) 2000-2026, Board of Trustees of Leland Stanford Jr. University.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
package org.lockss.util.rest.repo;

import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lockss.util.rest.RestUtil;
import org.lockss.util.test.LockssTestCase5;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

/**
 * Tests {@link RestLockssRepository#getAuIdCount(String)}.
 * <p>
 * The point of that method is not only that it returns the right number but
 * that it costs one request. Counting used to mean paging every AUID in the
 * namespace across the network and discarding them, which is what made the
 * "Repositories and Namespaces" status table slow on large machines. These
 * tests therefore assert on the shape and the number of requests, not just on
 * the returned value.
 */
public class TestRestLockssRepositoryAuIdCount extends LockssTestCase5 {
  private static final String BASEURL = "http://localhost:24610";
  private static final String NS1 = "ns1";

  private RestTemplate restTemplate;
  private MockRestServiceServer mockServer;
  private RestLockssRepository repo;

  @BeforeEach
  public void makeRepository() throws Exception {
    restTemplate = RestUtil.getRestTemplate();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    repo = new RestLockssRepository(new URL(BASEURL), restTemplate, null, null);
  }

  /**
   * One request, asking for a single item, and the answer read from the page
   * info rather than from the AUIDs in the body.
   */
  @Test
  public void testCountIsOneRequestAndComesFromTotalCount() throws Exception {
    mockServer
        .expect(ExpectedCount.once(),
            MockRestRequestMatchers.requestTo(
                BASEURL + "/aus?namespace=" + NS1 + "&limit=1"))
        .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
        .andRespond(MockRestResponseCreators.withSuccess(
            "{\"auids\":[\"auid1\"],"
                + "\"pageInfo\":{\"totalCount\":42,\"itemsInPage\":1}}",
            MediaType.APPLICATION_JSON));

    assertEquals(42, repo.getAuIdCount(NS1));

    // Fails if the count issued any request beyond the one expected above,
    // which is what paging the AUIDs would have done.
    mockServer.verify();
  }

  /**
   * The count must come from the reported total, not from how many AUIDs the
   * page happened to contain: with a limit of one those two numbers differ for
   * any namespace with more than one AU, so a implementation that counted the
   * body would return 1 here.
   */
  @Test
  public void testCountIgnoresTheNumberOfAuidsInTheBody() throws Exception {
    mockServer
        .expect(ExpectedCount.once(),
            MockRestRequestMatchers.requestTo(
                BASEURL + "/aus?namespace=" + NS1 + "&limit=1"))
        .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
        .andRespond(MockRestResponseCreators.withSuccess(
            "{\"auids\":[\"auid1\"],"
                + "\"pageInfo\":{\"totalCount\":1000,\"itemsInPage\":1,"
                + "\"continuationToken\":\"tok\"}}",
            MediaType.APPLICATION_JSON));

    assertEquals(1000, repo.getAuIdCount(NS1));
    mockServer.verify();
  }

  /** An empty namespace reports zero rather than failing. */
  @Test
  public void testEmptyNamespaceCountsZero() throws Exception {
    mockServer
        .expect(ExpectedCount.once(),
            MockRestRequestMatchers.requestTo(
                BASEURL + "/aus?namespace=" + NS1 + "&limit=1"))
        .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
        .andRespond(MockRestResponseCreators.withSuccess(
            "{\"auids\":[],\"pageInfo\":{\"totalCount\":0,\"itemsInPage\":0}}",
            MediaType.APPLICATION_JSON));

    assertEquals(0, repo.getAuIdCount(NS1));
    mockServer.verify();
  }

  /**
   * Against a repository service too old to report a total, the count has to
   * still be right. It falls back to the interface default, which enumerates —
   * slow, but the behaviour callers had before the total existed. Here that
   * shows up as the first request returning no total, followed by the paging
   * requests the enumeration makes.
   */
  @Test
  public void testFallsBackToEnumerationWhenNoTotalIsReported()
      throws Exception {
    // The count attempt: a page with no totalCount in it.
    mockServer
        .expect(ExpectedCount.once(),
            MockRestRequestMatchers.requestTo(
                BASEURL + "/aus?namespace=" + NS1 + "&limit=1"))
        .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
        .andRespond(MockRestResponseCreators.withSuccess(
            "{\"auids\":[\"auid1\"],\"pageInfo\":{\"itemsInPage\":1}}",
            MediaType.APPLICATION_JSON));

    // The fallback enumeration, which asks without a limit.
    mockServer
        .expect(ExpectedCount.once(),
            MockRestRequestMatchers.requestTo(
                BASEURL + "/aus?namespace=" + NS1))
        .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
        .andRespond(MockRestResponseCreators.withSuccess(
            "{\"auids\":[\"auid1\",\"auid2\",\"auid3\"],"
                + "\"pageInfo\":{\"itemsInPage\":3}}",
            MediaType.APPLICATION_JSON));

    assertEquals(3, repo.getAuIdCount(NS1));
    mockServer.verify();
  }
}
