/*

Copyright (c) 2000-2022, Board of Trustees of Leland Stanford Jr. University

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

package org.lockss.util.rest.repo.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lockss.log.L4JLogger;
import org.lockss.util.rest.repo.model.ArtifactData;
import org.lockss.util.rest.repo.model.ArtifactIdentifier;
import org.lockss.util.test.LockssTestCase5;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Test class for the ArtifactData factory {@code org.lockss.laaws.rs.util.ArtifactDataFactory}.
 */
public class TestArtifactDataFactory extends LockssTestCase5 {
    private final static L4JLogger log = L4JLogger.getLogger();

    private static final String ARTIFACT_BYTES = "If kittens could talk, they would whisper soft riddles into my ear," +
            " tickling me with their whiskers, making me laugh.";

    private static final String ARTIFACT_HTTP_ENCODED = "HTTP/1.1 200 OK\n" +
            "Server: nginx/1.12.0\n" +
            "Date: Wed, 30 Aug 2017 22:36:15 GMT\n" +
            "Content-Type: text/html\n" +
            "Content-Length: 118\n" +
            "Last-Modified: Fri, 07 Jul 2017 09:43:40 GMT\n" +
            "Connection: keep-alive\n" +
            "ETag: \"595f57cc-76\"\n" +
            "Accept-Ranges: bytes\n" +
            "\n" +
            ARTIFACT_BYTES;

    private static final String ARTIFACT_WARC_ENCODED = "WARC/1.0\n" +
            "WARC-Record-ID: <urn:uuid:74e3b795-c1e6-49ce-8b27-de7e747322b7>\n" +
            "Content-Length: " + ARTIFACT_HTTP_ENCODED.length() + "\n" +
            "WARC-Date: 1\n" +
            "WARC-Type: response\n" +
            "WARC-Target-URI: http://biorisk.pensoft.net/article_preview.php?id=1904\n" +
            "Content-Type: application/http; msgtype=response\n" +
            "X-LockssRepo-Artifact-Id: 74e3b795-c1e6-49ce-8b27-de7e747322b7\n" +
            "X-LockssRepo-Artifact-Namespace: demo\n" +
            "X-LockssRepo-Artifact-AuId: testauid\n" +
            "X-LockssRepo-Artifact-Uri: http://biorisk.pensoft.net/article_preview.php?id=1904\n" +
            "X-LockssRepo-Artifact-Version: 1" +
            "\n" +
            ARTIFACT_HTTP_ENCODED;


    @BeforeEach
    public void setUp() throws Exception {

    }

    @Test
    public void fromHttpResponseStream() {
        String expectedErrMsg = "InputStream is null";

        try {
            // Attempt with null InputStream
            ArtifactData artifact = ArtifactDataUtil.fromHttpResponseStream(null);
            fail(String.format("Expected IllegalArgumentException (%s) to be thrown", expectedErrMsg));
        } catch (IllegalArgumentException e) {
            assertEquals(expectedErrMsg, e.getMessage());
        } catch (IOException e) {
            fail("Unexpected IOException was caught");
        }

        try {
            // Attempt with malformed HTTP response stream
            String malformedResponse = "You contain the soul memory of a million stars. - PATWE";

            expectedErrMsg = String.format(
                    "org.apache.http.ProtocolException: Not a valid protocol version: %s",
                    malformedResponse
            );

            ArtifactDataUtil.fromHttpResponseStream(new ByteArrayInputStream(malformedResponse.getBytes()));
            fail(String.format("Expected IllegalArgumentException (%s) to be thrown", expectedErrMsg));
        } catch (IOException e) {
            assertEquals(expectedErrMsg, e.getMessage());
        }

        try {
            // Attempt successful
            ArtifactData artifact =
                ArtifactDataUtil.fromHttpResponseStream(new ByteArrayInputStream(ARTIFACT_HTTP_ENCODED.getBytes()));
            assertNotNull(artifact);

            HttpHeaders headers = artifact.getHttpHeaders();
            assertNotNull(headers);
            assertEquals(MediaType.TEXT_HTML, headers.getContentType());

            InputStream inputStream = artifact.getInputStream();
            assertNotNull(inputStream);
            assertEquals(ARTIFACT_BYTES, IOUtils.toString(inputStream));

            StatusLine statusLine = artifact.getHttpStatus();
            assertNotNull(statusLine);
            assertEquals(new ProtocolVersion("HTTP", 1, 1), artifact.getHttpStatus().getProtocolVersion());
            assertEquals(200, artifact.getHttpStatus().getStatusCode());
            assertEquals("OK", artifact.getHttpStatus().getReasonPhrase());

            URI storageUrl = artifact.getStorageUrl();
            assertNull(storageUrl);
        } catch (IOException e) {
            fail(String.format("Unexpected IOException was caught: %s", e.getMessage()));
        }

        // TODO: Test of fromHttpResponseStream whose method signature takes additional headers
    }

    @Test
    public void fromHttpResponse() {
        String expectedErrMsg = "HttpResponse is null";

        try {
            // Attempt parsing an ArtifactData out of a null HttpResponse
            ArtifactDataUtil.fromHttpResponse(null);
            fail(String.format("Expected IllegalArgumentException (%s) to be thrown", expectedErrMsg));
        } catch (IllegalArgumentException e) {
            assertEquals(expectedErrMsg, e.getMessage());
        } catch (IOException e) {
            fail(String.format("Expected IllegalArgumentException (%s) to be thrown", expectedErrMsg));
        }

        try {
            HttpResponse response = ArtifactDataUtil.getHttpResponseFromStream(
                    new ByteArrayInputStream(ARTIFACT_HTTP_ENCODED.getBytes())
            );

            ArtifactData artifact = ArtifactDataUtil.fromHttpResponse(response);
            assertNotNull(artifact);

            HttpHeaders headers = artifact.getHttpHeaders();
            assertNotNull(headers);
            assertEquals(MediaType.TEXT_HTML, headers.getContentType());

            InputStream inputStream = artifact.getInputStream();
            assertNotNull(inputStream);
            assertEquals(ARTIFACT_BYTES, IOUtils.toString(inputStream));

            StatusLine statusLine = artifact.getHttpStatus();
            assertNotNull(statusLine);
            assertEquals(new ProtocolVersion("HTTP", 1, 1), artifact.getHttpStatus().getProtocolVersion());
            assertEquals(200, artifact.getHttpStatus().getStatusCode());
            assertEquals("OK", artifact.getHttpStatus().getReasonPhrase());

            URI storageUrl = artifact.getStorageUrl();
            assertNull(storageUrl);
        } catch (IOException e) {
            fail(String.format("Unexpected IOException was caught: %s", e.getMessage()));
        } catch (HttpException e) {
            fail(String.format("Unexpected HttpException was caught: %s", e.getMessage()));
        }
    }

    @Test
    public void fromResource() {
    }

    @Test
    public void fromResourceStream() {
        // TODO: Test two method signatures of fromResourceStream()
    }



    @Test
    public void testHttpResponseHeaderTransforms() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(123);

        headers.add("k", "v1");
        headers.add("k", "v2");

        Header[] transHeaders = ArtifactDataUtil.transformHttpHeadersToHeaderArray(headers);
        for (Header header : transHeaders) {
            log.info("header = {}", header);
        }
    }
}
