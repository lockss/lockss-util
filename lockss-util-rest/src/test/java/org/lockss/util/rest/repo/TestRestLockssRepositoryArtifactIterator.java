/*
 * Copyright (c) 2019-2024, Board of Trustees of Leland Stanford Jr. University,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lockss.util.rest.repo;

import java.util.*;

import org.junit.jupiter.api.*;
import org.lockss.log.L4JLogger;
import org.lockss.util.*;
import org.lockss.util.rest.RestUtil;
import org.lockss.util.rest.repo.model.Artifact;
import org.lockss.util.rest.repo.RestLockssRepositoryArtifactIterator.Params;
import org.lockss.util.test.LockssTestCase5;
import org.springframework.http.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.*;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;


/**
 * Test class for org.lockss.util.rest.repo.RestLockssRepositoryArtifactIterator.
 *
 * @author Fernando García-Loygorri
 */
public class TestRestLockssRepositoryArtifactIterator extends LockssTestCase5 {
  private final static L4JLogger log = L4JLogger.getLogger();

  private final static String BASEURL = "http://localhost:24610";
  private final static String NS1 = "ns1";
  private final static String AUID1 = "auId";
  private RestTemplate restTemplate;
  private MockRestServiceServer mockServer;
  private String endpoint;
  private UriComponentsBuilder builder;

  @BeforeEach
  public void setupMock() {
    restTemplate = RestUtil.getRestTemplate();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    endpoint = String.format("%s/aus/%s/artifacts", BASEURL, AUID1);
    builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("namespace", NS1);
  }

  /** Retuen json string for Artifact */
  String jsonArt(String uuid, String uri, int version) {
    return String.format("{\"uuid\":\"%s\",\"uri\":\"%s\",\"version\":%s}",
                         uuid, uri, version);
  }

  /** Set up a series of mock server expectations and responses for a
   * sequence of requests for artifact iterator pages.  The client is
   * expected to request pages of the sizes specified in pageSizes, the
   * length of which determines the number of expected requests.
   * Negative sizes mean not to expect &limit= in the request.  Each
   * request but the first is expected to have a continuationToken, each
   * response but the last includes a continuationToken.  Artifact UUIDs,
   * URIs, versions, and continuationTokens are numbered sequentially.
   *
   * The setup (now obscured by this templated code) looks like<code>
   *     mockServer.expect(MockRestRequestMatchers.requestTo(endpoint
   * 	?namespace=ns1&limit=<LLL>[&continuationToken=ns1:auId:<UUU>:<PS>:<CNT>"))
   *     .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
   *     .andRespond(MockRestResponseCreators.withSuccess({"artifacts":[
   * 	  {"uuid":"<UUU>","uri":"<URI>","version":<VVV},
   *      ...
   * 	  ], "pageInfo":{[CCC]}})
   * 	MediaType.APPLICATION_JSON));
   */
  void mockNPages(String prefix, int ... pageSizes) {
    int cnt = 0;             // sequantially number UUIDs, URIs, versions
    // length of pageSizes determines number of responses
    String lastContTok = null;
    for (int ix = 0; ix < pageSizes.length; ix++) {
      int size = pageSizes[ix]; // number of artifacts expected in this req
      String reqUrl;
      if (size > 0) {
        reqUrl = String.format("%s?namespace=%s&limit=%s", endpoint, NS1, size);
      } else {
        reqUrl = String.format("%s?namespace=%s", endpoint, NS1);
        size = -size;
      }
      if (lastContTok != null) {
        reqUrl +=  "&continuationToken=" + lastContTok;
      }
      ResponseActions m =
        mockServer.expect(MockRestRequestMatchers.requestTo(reqUrl));
      m.andExpect(MockRestRequestMatchers.method(HttpMethod.GET));
      String pageInfo;
      if (ix < pageSizes.length - 1) {
        lastContTok = String.format("ns1:auId:uriB:%s:%s", size, cnt);
        pageInfo =
          String.format("{\"continuationToken\":\"ns1:auId:uriB:%s:%s\"}",
                        size, cnt);
      } else {
        pageInfo = "{}";
      }
      List<String> jArts = new ArrayList<>();
      for (int aix = 1; aix <= size; aix++) {
        cnt++;
        jArts.add(jsonArt("UUID" + prefix + "_" + cnt, "URI" + prefix + "_" + cnt, cnt));
      }

      log.trace("Adding response: {}",
                "{\"artifacts\":[" + String.join(",", jArts) + "], "
                + "\"pageInfo\":" + pageInfo + "}");

      m.andRespond(MockRestResponseCreators
                   .withSuccess("{\"artifacts\":["
                                + String.join(",", jArts) + "], "
                                + "\"pageInfo\":" + pageInfo + "}",
                                MediaType.APPLICATION_JSON));
    }
  }

  void mockNPages(int ... pageSizes) {
    mockNPages("", pageSizes);
  }

  @Test
  public void testEmptyIter() throws Exception {
    mockServer
      .expect(MockRestRequestMatchers.requestTo(endpoint + "?namespace=" + NS1)).andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
      .andRespond(MockRestResponseCreators.withSuccess("{\"artifacts\":[], \"pageInfo\":{}}",
	MediaType.APPLICATION_JSON));

    RestLockssRepositoryArtifactIterator repoIterator =
	new RestLockssRepositoryArtifactIterator(restTemplate, builder);
    assertFalse(repoIterator.hasNext());
    mockServer.verify();
    assertThrows(NoSuchElementException.class, () -> {repoIterator.next();});
  }

  @Test
  public void testErrorResponse() throws Exception {
    mockServer
      .expect(MockRestRequestMatchers.requestTo(endpoint + "?namespace=" + NS1)).andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
      .andRespond(withStatus(HttpStatus.BAD_REQUEST));

    RestLockssRepositoryArtifactIterator repoIterator =
	new RestLockssRepositoryArtifactIterator(restTemplate, builder);
    assertThrowsMatch(RuntimeException.class, "400", 
                      () -> {repoIterator.hasNext();});
    mockServer.verify();
    assertThrows(IllegalStateException.class, () -> {repoIterator.next();});
  }

  @Test
  public void test404Response() throws Exception {
    mockServer
      .expect(MockRestRequestMatchers.requestTo(endpoint + "?namespace=" + NS1)).andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
      .andRespond(withStatus(HttpStatus.NOT_FOUND));

    RestLockssRepositoryArtifactIterator repoIterator =
	new RestLockssRepositoryArtifactIterator(restTemplate, builder);
    assertFalse(repoIterator.hasNext());
    mockServer.verify();
    assertThrows(NoSuchElementException.class, () -> {repoIterator.next();});
  }

  @Test
  public void testSinglePage() throws Exception {
    mockNPages(-3);

    RestLockssRepositoryArtifactIterator repoIterator =
	new RestLockssRepositoryArtifactIterator(restTemplate, builder);

    assertTrue(repoIterator.hasNext());
    Artifact artifact = repoIterator.next();
    assertEquals("UUID_1", artifact.getUuid());
    assertEquals(1, artifact.getVersion());
    assertTrue(repoIterator.hasNext());
    artifact = repoIterator.next();
    assertEquals("UUID_2", artifact.getUuid());
    assertEquals(2, artifact.getVersion());
    assertTrue(repoIterator.hasNext());
    artifact = repoIterator.next();
    assertEquals("UUID_3", artifact.getUuid());
    assertEquals(3, artifact.getVersion());
    assertFalse(repoIterator.hasNext());
    mockServer.verify();
    assertThrows(NoSuchElementException.class, () -> {repoIterator.next();});
  }

  @Test
  public void testPagination() throws Exception {
    mockNPages(2, 2);

    RestLockssRepositoryArtifactIterator repoIterator =
      new RestLockssRepositoryArtifactIterator(restTemplate, builder, null,
                                               new Params().setPageSize(2));

    assertTrue(repoIterator.hasNext());
    Artifact artifact = repoIterator.next();
    assertEquals("UUID_1", artifact.getUuid());
    assertEquals("URI_1", artifact.getUri());
    assertEquals(1, artifact.getVersion());
    assertTrue(repoIterator.hasNext());
    artifact = repoIterator.next();
    assertEquals("UUID_2", artifact.getUuid());
    assertEquals("URI_2", artifact.getUri());
    assertEquals(2, artifact.getVersion());
    assertTrue(repoIterator.hasNext());

    artifact = repoIterator.next();
    assertEquals("UUID_3", artifact.getUuid());
    assertEquals("URI_3", artifact.getUri());
    assertEquals(3, artifact.getVersion());
    assertTrue(repoIterator.hasNext());
    artifact = repoIterator.next();
    assertEquals("UUID_4", artifact.getUuid());
    assertEquals("URI_4", artifact.getUri());
    assertEquals(4, artifact.getVersion());
    assertFalse(repoIterator.hasNext());
    mockServer.verify();
    assertThrows(NoSuchElementException.class, () -> {repoIterator.next();});
  }

  @Test
  public void testPageSizes() throws Exception {
    mockNPages(2, 7, 20, 20, 20);
    int total = 2 + 7 + 20 + 20 + 20;

    RestLockssRepositoryArtifactIterator repoIterator =
      new RestLockssRepositoryArtifactIterator(restTemplate, builder, null,
                                               new Params()
                                               .setPageSizes(ListUtil.list(2, 7, 20)));

    for (int cnt = 1; cnt <= total; cnt++) {
      assertTrue(repoIterator.hasNext());
      Artifact artifact = repoIterator.next();
      assertEquals("UUID_"+cnt, artifact.getUuid());
      assertEquals("URI_"+cnt, artifact.getUri());
      assertEquals(cnt, artifact.getVersion());
    }
    assertFalse(repoIterator.hasNext());
    mockServer.verify();
    assertThrows(NoSuchElementException.class, () -> {repoIterator.next();});
  }

  @Test
  public void testParams() throws Exception {
    long getTime = 17 * Constants.MINUTE;

    // Just checking param values, but thread will fetch so Keep mock happy
    mockNPages(2, 7, 20);

    MyRestLockssRepositoryArtifactIterator repoIterator =
      new MyRestLockssRepositoryArtifactIterator(restTemplate, builder, null,
                                               new RestLockssRepositoryArtifactIterator.Params()
                                                 .setQueueLen(5)
                                                 .setQueueGetTimeout(getTime)
                                                 .setPageSizes(ListUtil.list(2, 7, 20)));


    RestLockssRepositoryArtifactIterator.ThreadData td = repoIterator.getTD();
    assertEquals(ListUtil.list(2, 7, 20), td.pageSizes);

    // This is a race condition, as the producer thread may add an item
    // between the remainingCapacity() and size() calls.  There's no
    // better way to find the queue capacity and I don't want to add
    // synchronization to all queue accesses just for tests, but if it
    // fails in practice will need to adopt some fix.  (Some way to
    // precent the producer from adding an error to the queue?)
    assertEquals(5, td.pageQueue.remainingCapacity() + td.pageQueue.size());
    assertEquals(getTime, td.queueGetTimeout);
  }

  @Test
  public void testDefaultParams() throws Exception {
    // Just checking default param values, but thread will fetch so Keep
    // mock happy
    mockNPages(0);

    MyRestLockssRepositoryArtifactIterator repoIterator =
      new MyRestLockssRepositoryArtifactIterator(restTemplate, builder, null);

    RestLockssRepositoryArtifactIterator.ThreadData td = repoIterator.getTD();
    assertNull(td.pageSizes);

    // This is a race condition, as the producer thread may add an item
    // between the remainingCapacity() and size() calls.  There's no
    // better way to find the queue capacity and I don't want to add
    // synchronization to all queue accesses just for tests, but if it
    // fails in practice will need to adopt some fix.  (Some way to
    // precent the producer from adding an error to the queue?)
    assertEquals(RestLockssRepositoryArtifactIterator.DEFAULT_QUEUE_LENGTH,
                 td.pageQueue.remainingCapacity() + td.pageQueue.size());
    assertEquals(RestLockssRepositoryArtifactIterator.DEFAULT_QUEUE_GET_TIMEOUT,
                 td.queueGetTimeout);
  }

  @Test
  public void testCleaner() throws Exception {
    // Mock enough server responses to cause the queue to fill and the
    // producer thread to be waiting on queue.offer()
    mockNPages(2, 2, 2, 2, 2, 2);

    MyRestLockssRepositoryArtifactIterator iter =
      new MyRestLockssRepositoryArtifactIterator(restTemplate, builder, null,
                                                 new Params().setPageSize(2));

    assertTrue(iter.hasNext());
    Artifact artifact = iter.next();
    assertEquals("UUID_1", artifact.getUuid());

    // Discarding the Iterator and forcing a GC should cause the
    // producer thread to exit.  The assert below may not be reliable
    // as there's probably no guarantee about how quickly the Cleaner
    // runs.  If the assertion proves unreliable it could be removed
    // in favor of a manual check for the "Producer thread forcibly
    // terminated" log message.

    RestLockssRepositoryArtifactIterator.ThreadData td = iter.getTD();
    Thread.sleep(500);
    assertFalse(td.terminated);
    iter = null;
    System.gc();
    Thread.sleep(1000);
    assertTrue(td.terminated);
  }

  /** Provide access to the ThreadData */
  static class MyRestLockssRepositoryArtifactIterator
    extends RestLockssRepositoryArtifactIterator {

    MyRestLockssRepositoryArtifactIterator(RestTemplate restTemplate,
                                           UriComponentsBuilder builder) {
      super(restTemplate, builder);
    }

    MyRestLockssRepositoryArtifactIterator(RestTemplate restTemplate,
                                           UriComponentsBuilder builder,
                                           String authHeaderValue) {
      super(restTemplate, builder, authHeaderValue);
    }

    MyRestLockssRepositoryArtifactIterator(RestTemplate restTemplate,
                                           UriComponentsBuilder builder,
                                           String authHeaderValue,
                                           Params params) {
      super(restTemplate, builder, authHeaderValue, params);
    }

    ThreadData getTD() {
      return tdata;
    }
  }
}
