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
import org.lockss.util.rest.RestUtil;
import org.lockss.util.rest.repo.model.Artifact;
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
  private final static String BASEURL = "http://localhost:24610";
  private final static String NS1 = "ns1";
  private final static String AUID1 = "auId";
  private RestTemplate restTemplate;
  private MockRestServiceServer mockServer;
  private String endpoint;
  private UriComponentsBuilder builder;

  /**
   * Set up code to be run before each test.
   */
  @BeforeEach
  public void setupMock() {
    restTemplate = RestUtil.getRestTemplate();
    mockServer = MockRestServiceServer.createServer(restTemplate);
    endpoint = String.format("%s/aus/%s/artifacts", BASEURL, AUID1);
    builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("namespace", NS1);
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
  public void testPopulatedRepository() throws Exception {
    mockServer.expect(MockRestRequestMatchers.requestTo(endpoint + "?namespace="+ NS1)).andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    .andRespond(MockRestResponseCreators.withSuccess("{\"artifacts\":["
	+ "{\"uuid\":\"1\",\"version\":3},{\"uuid\":\"2\",\"version\":2},"
	+ "{\"uuid\":\"3\",\"version\":1}], \"pageInfo\":{}}",
	MediaType.APPLICATION_JSON));

    RestLockssRepositoryArtifactIterator repoIterator =
	new RestLockssRepositoryArtifactIterator(restTemplate, builder);

    assertTrue(repoIterator.hasNext());
    Artifact artifact = repoIterator.next();
    assertEquals("1", artifact.getUuid());
    assertEquals(3, artifact.getVersion().intValue());
    assertTrue(repoIterator.hasNext());
    artifact = repoIterator.next();
    assertEquals("2", artifact.getUuid());
    assertEquals(2, artifact.getVersion().intValue());
    assertTrue(repoIterator.hasNext());
    artifact = repoIterator.next();
    assertEquals("3", artifact.getUuid());
    assertEquals(1, artifact.getVersion().intValue());
    assertFalse(repoIterator.hasNext());
    mockServer.verify();
    assertThrows(NoSuchElementException.class, () -> {repoIterator.next();});
  }

  @Test
  public void testPagination() throws Exception {
    // First server call.
    mockServer.expect(MockRestRequestMatchers.requestTo(endpoint + "?namespace="+ NS1 +"&limit=2"))
    .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    .andRespond(MockRestResponseCreators.withSuccess("{\"artifacts\":["
	+ "{\"uuid\":\"1\",\"uri\":\"uriA\",\"version\":3},"
	+ "{\"uuid\":\"2\",\"uri\":\"uriB\",\"version\":2}], "
	+ "\"pageInfo\":{\"continuationToken\":\"ns1:auId:uriB:2:123456\"}}",
	MediaType.APPLICATION_JSON));

    // Second server call.
    mockServer.expect(MockRestRequestMatchers.requestTo(endpoint
	+ "?namespace="+ NS1 +"&limit=2&continuationToken=ns1:auId:uriB:2:123456"))
    .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    .andRespond(MockRestResponseCreators.withSuccess("{\"artifacts\":["
	+ "{\"uuid\":\"3\",\"uri\":\"uriC\",\"version\":9},"
	+ "{\"uuid\":\"4\",\"uri\":\"uriC\",\"version\":1}], "
	+ "\"pageInfo\":{}}",
	MediaType.APPLICATION_JSON));

    RestLockssRepositoryArtifactIterator repoIterator =
	new RestLockssRepositoryArtifactIterator(restTemplate, builder, 2);

    assertTrue(repoIterator.hasNext());
    Artifact artifact = repoIterator.next();
    assertEquals("1", artifact.getUuid());
    assertEquals("uriA", artifact.getUri());
    assertEquals(3, artifact.getVersion().intValue());
    assertTrue(repoIterator.hasNext());
    artifact = repoIterator.next();
    assertEquals("2", artifact.getUuid());
    assertEquals("uriB", artifact.getUri());
    assertEquals(2, artifact.getVersion().intValue());
    assertTrue(repoIterator.hasNext());

    artifact = repoIterator.next();
    assertEquals("3", artifact.getUuid());
    assertEquals("uriC", artifact.getUri());
    assertEquals(9, artifact.getVersion().intValue());
    assertTrue(repoIterator.hasNext());
    artifact = repoIterator.next();
    assertEquals("4", artifact.getUuid());
    assertEquals("uriC", artifact.getUri());
    assertEquals(1, artifact.getVersion().intValue());
    assertFalse(repoIterator.hasNext());
    mockServer.verify();
    assertThrows(NoSuchElementException.class, () -> {repoIterator.next();});
  }

  @Test
  public void testCleaner() throws Exception {

    // Mos enough server responses to cause the queue to fill and the
    // producer thread to be waiting on queue.offer()

    // 1st server call.
    mockServer.expect(MockRestRequestMatchers.requestTo(endpoint + "?namespace="+ NS1 +"&limit=2"))
    .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    .andRespond(MockRestResponseCreators.withSuccess("{\"artifacts\":["
	+ "{\"uuid\":\"1\",\"uri\":\"uriA\",\"version\":3},"
	+ "{\"uuid\":\"2\",\"uri\":\"uriB\",\"version\":2}], "
	+ "\"pageInfo\":{\"continuationToken\":\"ns1:auId:uriB:2:123456\"}}",
	MediaType.APPLICATION_JSON));

    // 2nd server call.
    mockServer.expect(MockRestRequestMatchers.requestTo(endpoint
	+ "?namespace="+ NS1 +"&limit=2&continuationToken=ns1:auId:uriB:2:123456"))
    .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    .andRespond(MockRestResponseCreators.withSuccess("{\"artifacts\":["
	+ "{\"uuid\":\"3\",\"uri\":\"uriC\",\"version\":9},"
	+ "{\"uuid\":\"4\",\"uri\":\"uriC\",\"version\":1}], "
	+ "\"pageInfo\":{\"continuationToken\":\"ns1:auId:uriB:2:123457\"}}",
	MediaType.APPLICATION_JSON));

    // 3rd server call.
    mockServer.expect(MockRestRequestMatchers.requestTo(endpoint
	+ "?namespace="+ NS1 +"&limit=2&continuationToken=ns1:auId:uriB:2:123457"))
    .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    .andRespond(MockRestResponseCreators.withSuccess("{\"artifacts\":["
	+ "{\"uuid\":\"3\",\"uri\":\"uriC\",\"version\":9},"
	+ "{\"uuid\":\"4\",\"uri\":\"uriC\",\"version\":1}], "
	+ "\"pageInfo\":{\"continuationToken\":\"ns1:auId:uriB:2:123458\"}}",
	MediaType.APPLICATION_JSON));

    // 4th server call.
    mockServer.expect(MockRestRequestMatchers.requestTo(endpoint
	+ "?namespace="+ NS1 +"&limit=2&continuationToken=ns1:auId:uriB:2:123458"))
    .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    .andRespond(MockRestResponseCreators.withSuccess("{\"artifacts\":["
	+ "{\"uuid\":\"3\",\"uri\":\"uriC\",\"version\":9},"
	+ "{\"uuid\":\"4\",\"uri\":\"uriC\",\"version\":1}], "
	+ "\"pageInfo\":{\"continuationToken\":\"ns1:auId:uriB:2:123459\"}}",
	MediaType.APPLICATION_JSON));

    // Last server call.
    mockServer.expect(MockRestRequestMatchers.requestTo(endpoint
	+ "?namespace="+ NS1 +"&limit=2&continuationToken=ns1:auId:uriB:2:123459"))
    .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    .andRespond(MockRestResponseCreators.withSuccess("{\"artifacts\":["
	+ "{\"uuid\":\"3\",\"uri\":\"uriC\",\"version\":9},"
	+ "{\"uuid\":\"4\",\"uri\":\"uriC\",\"version\":1}], "
	+ "\"pageInfo\":{}}",
	MediaType.APPLICATION_JSON));

    MyRestLockssRepositoryArtifactIterator iter =
	new MyRestLockssRepositoryArtifactIterator(restTemplate, builder, 2);

    assertTrue(iter.hasNext());
    Artifact artifact = iter.next();
    assertEquals("1", artifact.getUuid());

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
                                           UriComponentsBuilder builder,
                                           Integer limit) {
      super(restTemplate, builder, limit);
    }

    ThreadData getTD() {
      return tdata;
    }
  }
}
