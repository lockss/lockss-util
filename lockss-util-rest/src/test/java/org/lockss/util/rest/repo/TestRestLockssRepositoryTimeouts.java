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
package org.lockss.util.rest.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lockss.log.L4JLogger;
import org.lockss.util.Constants;
import org.lockss.util.rest.RestUtil;
import org.lockss.util.rest.RestUtil.LockssRestTemplateSettings;
import org.lockss.util.rest.exception.LockssRestNetworkException;
import org.lockss.util.rest.repo.model.Artifact;
import org.lockss.util.rest.repo.util.ArtifactSpec;
import org.lockss.util.test.LockssTestCase5;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.model.Header;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Tests that the calls of {@link RestLockssRepository} that don't respond until
 * the service has finished doing their work are given the long read timeout,
 * and that the rest are held to the general one.
 */
@ExtendWith(MockServerExtension.class)
public class TestRestLockssRepositoryTimeouts extends LockssTestCase5 {
  private static L4JLogger log = L4JLogger.getLogger();

  /** Read timeout for normal calls */
  private static final long READ_TIMEOUT = 1 * Constants.SECOND;

  /** Read timeout for calls that produce no response until they're done */
  private static final long LONG_READ_TIMEOUT = 30 * Constants.SECOND;

  /** How long the service takes to respond: longer than the general read
   * timeout, shorter than the long one */
  private static final int RESPONSE_DELAY = 3;

  private static final String NS = "ns1";
  private static final String AUID = "auid1";

  private MockServerClient msClient;
  private RestLockssRepository repository;

  @BeforeEach
  public void makeRepository(MockServerClient client) throws Exception {
    msClient = client;
    msClient.reset();

    RestTemplate restTemplate = RestUtil.getRestTemplateBuilder(
        LockssRestTemplateSettings.withTimeouts(READ_TIMEOUT, READ_TIMEOUT)
            .withLongReadTimeout(LONG_READ_TIMEOUT)).build();

    repository = new RestLockssRepository(
        new URL("http://localhost:" + msClient.getPort()), restTemplate,
        null, null);
  }

  /**
   * A call with no excuse for being slow gives up after the general read
   * timeout rather than blocking the calling thread.
   */
  @Test
  public void testOrdinaryCallTimesOut() throws Exception {
    msClient
        .when(request()
            .withMethod("GET")
            .withPath("/namespaces"))
        .respond(response()
            .withDelay(TimeUnit.SECONDS, RESPONSE_DELAY)
            .withStatusCode(200)
            .withHeaders(new Header("Content-Type", "application/json"))
            .withBody("[\"" + NS + "\"]"));

    LockssRestNetworkException e =
        assertThrows(LockssRestNetworkException.class,
                     () -> repository.getNamespaces());

    assertClass(SocketTimeoutException.class, e.getCause());
  }

  /**
   * createArtifact doesn't respond until the service has compressed the
   * artifact and stored it in a temporary WARC, which can take arbitrarily
   * long, so it must not be held to the general read timeout.
   */
  @Test
  public void testAddArtifactIsNotCutOffByReadTimeout() throws Exception {
    ArtifactSpec spec = new ArtifactSpec()
        .setNamespace(NS)
        .setAuid(AUID)
        .setUrl("http://www.lockss.org/foo.txt")
        .generateContent();

    msClient
        .when(request()
            .withMethod("POST")
            .withPath("/artifacts"))
        .respond(response()
            .withDelay(TimeUnit.SECONDS, RESPONSE_DELAY)
            .withStatusCode(200)
            .withHeaders(new Header("Content-Type", "application/json"))
            .withBody(artifactJson("uuid1")));

    Artifact artifact = repository.addArtifact(spec.getArtifactData());

    assertEquals("uuid1", artifact.getUuid());
  }

  /**
   * finishBulkStore blocks until the AU's artifacts have been copied to
   * permanent storage and indexed, so it gets the long read timeout; starting
   * a bulk store returns immediately, so it doesn't.
   */
  @Test
  public void testFinishBulkStoreIsNotCutOffByReadTimeout() throws Exception {
    msClient
        .when(request()
            .withMethod("POST")
            .withPath("/aus/" + AUID + "/bulk"))
        .respond(response()
            .withDelay(TimeUnit.SECONDS, RESPONSE_DELAY)
            .withStatusCode(200));

    LockssRestNetworkException e =
        assertThrows(LockssRestNetworkException.class,
                     () -> repository.startBulkStore(NS, AUID));

    assertClass(SocketTimeoutException.class, e.getCause());

    repository.finishBulkStore(NS, AUID);
  }

  /**
   * Provides the JSON of an Artifact, as the service would return it from
   * createArtifact.
   */
  private String artifactJson(String uuid) {
    return "{" +
        "\"uuid\":\"" + uuid + "\"," +
        "\"namespace\":\"" + NS + "\"," +
        "\"auid\":\"" + AUID + "\"," +
        "\"uri\":\"http://www.lockss.org/foo.txt\"," +
        "\"version\":1," +
        "\"committed\":false," +
        "\"storageUrl\":\"file:///tmp/warc\"," +
        "\"contentLength\":3," +
        "\"contentDigest\":\"SHA-256:0\"," +
        "\"collectionDate\":0}";
  }
}
