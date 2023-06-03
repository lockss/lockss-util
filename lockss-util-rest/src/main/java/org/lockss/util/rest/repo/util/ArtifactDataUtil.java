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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.io.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.util.CharArrayBuffer;
import org.lockss.util.rest.multipart.MultipartMessage;
import org.lockss.util.rest.multipart.MultipartResponse;
import org.lockss.util.rest.repo.LockssRepository;
import org.lockss.util.rest.repo.model.Artifact;
import org.lockss.util.rest.repo.model.ArtifactData;
import org.lockss.util.rest.repo.model.ArtifactIdentifier;
import org.lockss.util.rest.repo.RestLockssRepository;
import org.lockss.log.L4JLogger;
import org.lockss.util.rest.repo.model.ArtifactProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Common utilities and adapters for LOCKSS repository ArtifactData objects.
 */
public class ArtifactDataUtil {
    private final static L4JLogger log = L4JLogger.getLogger();

  /** Return a SessionOutputBuffer with a UTF-8 encoder, bound to the
   * OutputStream */
  public static SessionOutputBufferImpl getSessionOutputBuffer(OutputStream os) {
    SessionOutputBufferImpl buffer =
      new SessionOutputBufferImpl(new HttpTransportMetricsImpl(),
                                 4096, 4096,
                                 StandardCharsets.UTF_8.newEncoder());
    buffer.bind(os);
    return buffer;
  }

  /** Return a SessionInputBuffer with a UTF-8 decoder, bound to the
   * InputStream */
  public static SessionInputBufferImpl getSessionInputBuffer(InputStream is) {
    SessionInputBufferImpl buffer =
      new SessionInputBufferImpl(new HttpTransportMetricsImpl(),
                                 4096, 4096, null,
                                 StandardCharsets.UTF_8.newDecoder());
    buffer.bind(is);
    return buffer;
  }

    /**
     * Adapter that takes an {@code ArtifactData} and returns an InputStream containing an HTTP response stream
     * representation of the artifact.
     *
     * @param artifactData
     *          An {@code ArtifactData} to to transform.
     * @return An {@code InputStream} containing an HTTP response stream representation of the artifact.
     * @throws IOException
     * @throws HttpException
     */
    public static InputStream getHttpResponseStreamFromArtifactData(ArtifactData artifactData) throws IOException {
        InputStream httpResponse =
            getHttpResponseStreamFromHttpResponse(getHttpResponseFromArtifactData(artifactData));

	// getBytesRead() hasn't been computed yet
//         artifactData.setContentLength(artifactData.getBytesRead());

        return httpResponse;
    }


    /**
     * Adapter that takes an {@code ArtifactData} and returns an Apache {@code HttpResponse} object representation of
     * the artifact.
     *
     * This is effectively the inverse operation of {@code ArtifactDataFactory#fromHttpResponse(HttpResponse)}.
     *
     * @param artifactData
     *          An {@link ArtifactData} to transform to an HttpResponse object.
     * @return An {@link HttpResponse} object containing a representation of the artifact.
     * @throws HttpException
     * @throws IOException
     */
    public static HttpResponse getHttpResponseFromArtifactData(ArtifactData artifactData) {
        // Craft a new HTTP response object representation from the artifact
        BasicHttpResponse response = new BasicHttpResponse(artifactData.getHttpStatus());

        // Create an InputStreamEntity from artifact InputStream
        response.setEntity(new InputStreamEntity(artifactData.getInputStream()));

        // Add artifact headers into HTTP response
        if (artifactData.getHttpHeaders() != null) {

            // Compile a list of headers
            artifactData.getHttpHeaders().forEach((headerName, headerValues) ->
                headerValues.forEach((headerValue) ->
                    response.addHeader(headerName, headerValue)
            ));
        }

        return response;
    }

    /**
     * Adapter that takes an ArtifactData's ArtifactIdentifier and returns an array of Apache Header objects representing
     * the ArtifactIdentifier.
     *
     * @param artifact
     *          An {@code ArtifactData} whose {@code ArtifactIdentifier} will be adapted.
     * @return An {@code Header[]} representing the {@code ArtifactData}'s {@code ArtifactIdentifier}.
     */
    private static Header[] getArtifactIdentifierHeaders(ArtifactData artifact) {
        return getArtifactIdentifierHeaders(artifact.getIdentifier());
    }

    /**
     * Adapter that takes an {@code ArtifactIdentifier} and returns an array of Apache Header objects representing the
     * ArtifactIdentifier.
     *
     * @param id
     *          An {@code ArtifactIdentifier} to adapt.
     * @return A {@code Header[]} representing the {@code ArtifactIdentifier}.
     */
    private static Header[] getArtifactIdentifierHeaders(ArtifactIdentifier id) {
        Collection<Header> headers = new HashSet<>();
        headers.add(new BasicHeader(ArtifactConstants.ARTIFACT_NAMESPACE_KEY, id.getNamespace()));
        headers.add(new BasicHeader(ArtifactConstants.ARTIFACT_AUID_KEY, id.getAuid()));
        headers.add(new BasicHeader(ArtifactConstants.ARTIFACT_URI_KEY, id.getUri()));
        headers.add(new BasicHeader(ArtifactConstants.ARTIFACT_VERSION_KEY, String.valueOf(id.getVersion())));

        return headers.toArray(new Header[headers.size()]);
    }

    /**
     * Adapts an {@code HttpResponse} object to an InputStream containing a HTTP response stream representation of the
     * {@code HttpResponse} object.
     *
     * @param response
     *          A {@code HttpResponse} to adapt.
     * @return An {@code InputStream} containing a HTTP response stream representation of this {@code HttpResponse}.
     * @throws IOException
     */
    public static InputStream getHttpResponseStreamFromHttpResponse(HttpResponse response) throws IOException {
        // Return the concatenation of the header and content streams
        return new SequenceInputStream(
            new ByteArrayInputStream(getHttpResponseHeader(response)),
            response.getEntity().getContent()
        );
    }

    private static HttpResponse getHttpResponseHeadersFromArtifactData(ArtifactData artifactData) {
        // Craft a new HTTP response object representation from the artifact
        BasicHttpResponse response = new BasicHttpResponse(artifactData.getHttpStatus());

        // Add artifact headers into HTTP response
        if (artifactData.getHttpHeaders() != null) {

            // Compile a list of headers
            artifactData.getHttpHeaders().forEach((headerName, headerValues) ->
                headerValues.forEach((headerValue) ->
                    response.addHeader(headerName, headerValue)
                ));
        }

        return response;
    }

    public static byte[] getHttpResponseHeader(ArtifactData ad) throws IOException {
        return ArtifactDataUtil.getHttpResponseHeader(
            ArtifactDataUtil.getHttpResponseHeadersFromArtifactData(ad));
    }

    public static byte[] getHttpResponseHeader(HttpResponse response) throws IOException {
        try (UnsynchronizedByteArrayOutputStream headerStream = new UnsynchronizedByteArrayOutputStream()) {

            // Create a new SessionOutputBuffer from the OutputStream
          SessionOutputBufferImpl outputBuffer =
              getSessionOutputBuffer(headerStream);

            // Write the HTTP response header
            writeHttpResponseHeader(response, outputBuffer);

            // Flush anything remaining in the buffer
            outputBuffer.flush();

            return headerStream.toByteArray();
        }
    }

    /**
     * Writes an {@code ArtifactData} to an {@code OutputStream} as a HTTP response stream.
     *
     * @param artifactData
     *          The {@code ArtifactData} to encode as an HTTP response stream and write to an {@code OutputStream}.
     * @param output
     *          The {@code OutputStream} to write to.
     * @throws IOException
     * @throws HttpException
     */
    public static void writeHttpResponseStream(ArtifactData artifactData, OutputStream output) throws IOException {
        writeHttpResponse(
                getHttpResponseFromArtifactData(artifactData),
                output
        );
    }

    /**
     * Writes a HTTP response stream representation of a {@code HttpResponse} to an {@code OutputStream}.
     * @param response
     *          A {@code HttpResponse} to convert to an HTTP response stream and write to the {@code OutputStream}.
     * @param output
     *          The {@code OutputStream} to write to.
     * @throws IOException
     */
    public static void writeHttpResponse(HttpResponse response, OutputStream output) throws IOException {
        // Create a new SessionOutputBuffer from the OutputStream
          SessionOutputBufferImpl outputBuffer =
              getSessionOutputBuffer(output);

        // Re-construct the response
        writeHttpResponseHeader(response, outputBuffer);
        outputBuffer.flush();
        response.getEntity().writeTo(output);
        output.flush();
    }

    /**
     * Writes a {@code HttpResponse} object's HTTP status and headers to an {@code OutputStream}.
     * @param response
     *          A {@code HttpResponse} whose HTTP status and headers will be written to the {@code OutputStream}.
     * @param outputBuffer
     *          The {@code OutputStream} to write to.
     * @throws IOException
     */
    private static void writeHttpResponseHeader(HttpResponse response, SessionOutputBufferImpl outputBuffer) throws IOException {
        try {
            // Write the HTTP response header
            DefaultHttpResponseWriter responseWriter = new DefaultHttpResponseWriter(outputBuffer);
            responseWriter.write(response);
        } catch (HttpException e) {
            log.error("Caught HttpException while attempting to write the headers of an HttpResponse using DefaultHttpResponseWriter");
            throw new IOException(e);
        }
    }

    public static byte[] getHttpStatusByteArray(StatusLine httpStatus) throws IOException {
        UnsynchronizedByteArrayOutputStream output = new UnsynchronizedByteArrayOutputStream();
        CharArrayBuffer lineBuf = new CharArrayBuffer(128);

        // Create a new SessionOutputBuffer and bind the UnsynchronizedByteArrayOutputStream
          SessionOutputBufferImpl outputBuffer =
              getSessionOutputBuffer(output);

        // Write HTTP status line
        BasicLineFormatter.INSTANCE.formatStatusLine(lineBuf, httpStatus);
        outputBuffer.writeLine(lineBuf);
        outputBuffer.flush();

        // Flush and close UnsynchronizedByteArrayOutputStream
        output.flush();
        output.close();

        // Return HTTP status byte array
        return output.toByteArray();
    }

  public static MultiValueMap<String, Object> generateMultipartMapFromArtifactData(
      ArtifactData artifactData, LockssRepository.IncludeContent includeContent, long smallContentThreshold)
      throws IOException {

    String artifactUuid = artifactData.getIdentifier().getUuid();

    // Holds multipart response parts
    MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

    //// Add artifact repository properties multipart
    {
      // Part's headers
      HttpHeaders partHeaders = new HttpHeaders();
      partHeaders.setContentType(MediaType.APPLICATION_JSON);

      // Add repository properties multipart to multiparts list
      parts.add(RestLockssRepository.MULTIPART_ARTIFACT_PROPS,
          new HttpEntity<>(getArtifactProperties(artifactData), partHeaders));
    }

    //// Add HTTP response header multiparts if present
    if (artifactData.isHttpResponse()) {
      //// HTTP status part
      {
        // Part's headers
        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpResponse httpResponse = new BasicHttpResponse(artifactData.getHttpStatus());

        httpResponse.setHeaders(
            transformHttpHeadersToHeaderArray(artifactData.getHttpHeaders()));

        byte[] header = ArtifactDataUtil.getHttpResponseHeader(httpResponse);

        // Create resource containing HTTP status byte array
        Resource resource = new NamedByteArrayResource(artifactUuid, header);

        // Add artifact headers multipart
        parts.add(RestLockssRepository.MULTIPART_ARTIFACT_HTTP_RESPONSE_HEADER,
            new HttpEntity<>(resource, partHeaders));
      }
    }

    //// Add artifact content part if requested or if small enough
    if ((includeContent == LockssRepository.IncludeContent.ALWAYS) ||
        (includeContent == LockssRepository.IncludeContent.IF_SMALL
            && artifactData.getContentLength() <= smallContentThreshold)) {

      // Create content part headers
      HttpHeaders partHeaders = new HttpHeaders();

      if (artifactData.hasContentLength()) {
        partHeaders.setContentLength(artifactData.getContentLength());
      }

      HttpHeaders artifactHeaders = artifactData.getHttpHeaders();

      // Attempt to parse and set the Content-Type of the part using MediaType. If the Content-Type is not
      // specified (null) then omit the header. If an error occurs due to an malformed Content-Type, set
      // the X-Lockss-Content-Type to the malformed value and omit the Content-Type header.

      // If artifact Content-Type specifed...
      //     set Content-Type and X-Lockss-Content-Type to same value
      // .. else ..
      //     set Content-Type to application/octet (or leave null)

      try {
        MediaType type = artifactHeaders.getContentType();
        partHeaders.setContentType(type);
        if (type != null) {
          partHeaders.set(ArtifactConstants.X_LOCKSS_CONTENT_TYPE,
              artifactHeaders.getFirst(HttpHeaders.CONTENT_TYPE));
        }
      } catch (InvalidMediaTypeException e) {
        partHeaders.set(ArtifactConstants.X_LOCKSS_CONTENT_TYPE,
            artifactHeaders.getFirst(HttpHeaders.CONTENT_TYPE));
      }

      // FIXME: Filename must be set or else Spring will treat the part as a parameter instead of a file
      partHeaders.setContentDispositionFormData(
          RestLockssRepository.MULTIPART_ARTIFACT_PAYLOAD, RestLockssRepository.MULTIPART_ARTIFACT_PAYLOAD);

      // Artifact content
//      InputStreamResource resource = new NamedInputStreamResource(artifactUuid, artifactData.getInputStream());
      InputStreamResource resource = new InputStreamResource(artifactData.getInputStream());

      // Assemble content part and add to multiparts map
      parts.add(RestLockssRepository.MULTIPART_ARTIFACT_PAYLOAD,
          new HttpEntity<>(resource, partHeaders));
    }

    return parts;
  }

  private static Map<String, String> getArtifactProperties(ArtifactData ad) {
    Map<String, String> props = new HashMap<>();
    ArtifactIdentifier id = ad.getIdentifier();

    putIfNotNull(props, Artifact.ARTIFACT_NAMESPACE_KEY, id.getNamespace());
    putIfNotNull(props, Artifact.ARTIFACT_UUID_KEY, id.getUuid());
    props.put(Artifact.ARTIFACT_AUID_KEY, id.getAuid());
    props.put(Artifact.ARTIFACT_URI_KEY, id.getUri());

    Integer version = id.getVersion();
    if (version != null && version > 0) {
      props.put(Artifact.ARTIFACT_VERSION_KEY, String.valueOf(id.getVersion()));
    }

    if (ad.hasContentLength()) {
      props.put(Artifact.ARTIFACT_LENGTH_KEY, String.valueOf(ad.getContentLength()));
    }

    putIfNotNull(props, Artifact.ARTIFACT_DIGEST_KEY, ad.getContentDigest());
    putIfNonZero(props, Artifact.ARTIFACT_COLLECTION_DATE_KEY, ad.getCollectionDate());

    return props;
  }

  private static void putIfNonZero(Map props, String k, long v) {
    if (v == 0) return;
    props.put(k, String.valueOf(v));
  }

  private static void putIfNotNull(Map props, String k, String v) {
    if (v == null) return;
    props.put(k, v);
  }

  /**
   * Instantiates an {@code ArtifactData} from an {@code InputStream} containing the byte stream of an HTTP response.
   *
   * @param responseStream An {@code InputStream} containing an HTTP response byte stream which in turn encodes an artifact.
   * @return An {@code ArtifactData} representing the artifact encoded in an HTTP response input stream.
   * @throws IOException
   */
  public static ArtifactData fromHttpResponseStream(InputStream responseStream) throws IOException {
    if (responseStream == null) {
      throw new IllegalArgumentException("InputStream is null");
    }

    return fromHttpResponseStream(null, responseStream);
  }

  /**
   * Instantiates an {@code ArtifactData} from an {@code InputStream} containing the byte stream of an HTTP response.
   * <p>
   * Allows additional HTTP headers to be injected by passing a {@code HttpHeaders}.
   *
   * @param additionalMetadata A {@code HttpHeader} with additional headers.
   * @param responseStream     An {@code InputStream} containing an HTTP response byte stream which in turn encodes an artifact.
   * @return An {@code ArtifactData} representing the artifact encoded in an HTTP response input stream.
   * @throws IOException
   */
  public static ArtifactData fromHttpResponseStream(HttpHeaders additionalMetadata, InputStream responseStream)
      throws IOException {
    // Attach remaining data in stream as the response entity. We cannot use InputStreamEntity directly because
    // it is now wrapped within a SessionInputBufferImpl so we instantiate a BasicHttpEntity and populate it an
    // IdentityInputStream. We could also have used StrictContentLengthStrategy and ContentLengthInputStream.
    try {
      HttpResponse response = getHttpResponseFromStream(responseStream);

      // Merge additional artifact metadata into HTTP response header
      if (additionalMetadata != null) {
        additionalMetadata.forEach((headerName, headerValues) ->
            headerValues.forEach((headerValue) -> response.setHeader(headerName, headerValue)
            ));
      }

      return fromHttpResponse(response);
    } catch (HttpException e) {
      log.error("An error occurred while attempting to parse a stream as a HTTP response", e);

      throw new IOException(e);
    }
  }

  /**
   * Adapts an {@code InputStream} with an HTTP response into an Apache {@code HttpResponse} object.
   *
   * @param inputStream An {@code InputStream} containing an HTTP response to parse.
   * @return A {@code HttpResponse} representing the HTTP response in the {@code InputStream}.
   * @throws HttpException
   * @throws IOException
   */
  public static HttpResponse getHttpResponseFromStream(InputStream inputStream) throws HttpException, IOException {
    // Create a SessionInputBuffer from the InputStream containing a HTTP response
    SessionInputBufferImpl buffer =
      getSessionInputBuffer(inputStream);

    // Parse the InputStream to a HttpResponse object
    HttpResponse response = (new DefaultHttpResponseParser(buffer)).parse();
//        long len = (new LaxContentLengthStrategy()).determineLength(response);

    // Create and attach an HTTP entity to the HttpResponse
    BasicHttpEntity responseEntity = new BasicHttpEntity();
//        responseEntity.setContentLength(len);
    responseEntity.setContent(new IdentityInputStream(buffer));
    response.setEntity(responseEntity);

    return response;
  }

  /**
   * Instantiates an {@code ArtifactData} from a Apache {@code HttpResponse} object.
   *
   * @param response A {@code HttpResponse} object containing an artifact.
   * @return An {@code ArtifactData} representing the artifact encoded in the {@code HttpResponse} object.
   * @throws IOException
   */
  public static ArtifactData fromHttpResponse(HttpResponse response) throws IOException {
    if (response == null) {
      throw new IllegalArgumentException("HttpResponse is null");
    }

    HttpHeaders headers = transformHeaderArrayToHttpHeaders(response.getAllHeaders());

    ArtifactData artifactData = new ArtifactData(
        null,
        headers,
        response.getEntity().getContent(),
        response.getStatusLine());

//        artifactData.setContentLength(response.getEntity().getContentLength());

    return artifactData;
  }

  /**
   * Instantiates an {@code ArtifactIdentifier} from HTTP headers in a {@code HttpHeaders} object.
   *
   * @param props An {@code Map} object representing HTTP headers containing an artifact identity.
   * @return An {@code ArtifactIdentifier}.
   */
  public static ArtifactIdentifier buildArtifactIdentifier(Map<String, String> props) {
    int version = 0;

    String versionVal = props.get(Artifact.ARTIFACT_VERSION_KEY);
    if (versionVal != null && !versionVal.isEmpty()) {
      version = Integer.parseInt(versionVal);
    }

    return new ArtifactIdentifier(
        props.get(Artifact.ARTIFACT_UUID_KEY),
        props.get(Artifact.ARTIFACT_NAMESPACE_KEY),
        props.get(Artifact.ARTIFACT_AUID_KEY),
        props.get(Artifact.ARTIFACT_URI_KEY),
        version);
  }

  public static ArtifactIdentifier buildArtifactIdentifier(ArtifactProperties props) {
    return new ArtifactIdentifier(
        props.getUuid(),
        props.getNamespace(),
        props.getAuid(),
        props.getUri(),
        props.getVersion());
  }

  /**
   * Returns the value from an {@code HttpHeaders} object for a given key.
   * <p>
   * The value must for this key must be unique.
   *
   * @param headers A {@code HttpHeaders} to return the key's value from.
   * @param key     A {@code String} containing the key of the value to return.
   * @return A {@code String} value, or {@code null} if this key is not found or has multiple values.
   */
  private static String getHeaderValue(HttpHeaders headers, String key) {
    List<String> values = headers.get(key);

    if ((values != null) && !values.isEmpty()) {
      // Allow duplicates of the header as long as the value is the same
      if (values.stream().allMatch(values.get(0)::equals)) {
        return values.get(0);
      }
    }

    // TODO: Should this throw instead?
    return null;
  }

  /**
   * Reorganizes an array of Apache Header objects into a single Spring HttpHeaders object.
   *
   * @param headerArray An array of {@code Header} objects to reorganize.
   * @return A Spring {@code HttpHeaders} object representing the array of Apache {@code Header} objects.
   */
  // TODO: Move this to lockss-util?
  public static HttpHeaders transformHeaderArrayToHttpHeaders(Header[] headerArray) {
    HttpHeaders headers = new HttpHeaders();
    Arrays.stream(headerArray).forEach(header -> headers.add(header.getName(), header.getValue()));

    return headers;
  }

  public static Header[] transformHttpHeadersToHeaderArray(HttpHeaders headers) {
    Header[] result = headers.entrySet()
        .stream()
        .flatMap(entry -> entry.getValue()
            .stream()
            .map(v -> new BasicHeader(entry.getKey(), v)))
        .toArray(Header[]::new);

    return result;
  }

  public static ArtifactData fromTransportResponseEntity(ResponseEntity<MultipartMessage> response) throws IOException {
    try {
      // For JSON object parsing
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      // Assemble ArtifactData object from multipart response parts
      MultipartResponse multipartMessage = new MultipartResponse(response);
      LinkedHashMap<String, MultipartResponse.Part> parts = multipartMessage.getParts();
      ArtifactData result = new ArtifactData();

      //// Set artifact repository properties
      {
        MultipartResponse.Part part = parts.get(RestLockssRepository.MULTIPART_ARTIFACT_PROPS);

        ArtifactProperties props = mapper.readValue(part.getInputStream(), ArtifactProperties.class);

        // Set ArtifactIdentifier
        ArtifactIdentifier id = buildArtifactIdentifier(props);

        result.setIdentifier(id);

        // Set misc. artifact properties
        result.setContentLength(props.getContentLength());
        result.setContentDigest(props.getContentDigest());
      }

      //// Set artifact HTTP response status and headers
      {
        MultipartResponse.Part part = parts.get(RestLockssRepository.MULTIPART_ARTIFACT_HTTP_RESPONSE_HEADER);

        // Parse header part body into HttpHeaders object
        if (part != null) {
          try {
            HttpResponse httpResponse =
                getHttpResponseFromStream(part.getInputStream());

            // Set HTTP status
            result.setHttpStatus(httpResponse.getStatusLine());

            // Set HTTP headers
            result.setHttpHeaders(transformHeaderArrayToHttpHeaders(httpResponse.getAllHeaders()));
          } catch (HttpException e) {
            throw new IOException("Error parsing HTTP response header part", e);
          }
        }
      }

      //// Set artifact content if present
      {
        MultipartResponse.Part part = parts.get(RestLockssRepository.MULTIPART_ARTIFACT_PAYLOAD);

        if (part != null) {
          result.setInputStream(part.getInputStream());

          // Set artifact's Content-Type to value of X-Lockss-Content-Type if present,
          // otherwise use value of Content-Type
          HttpHeaders partHeaders = part.getHeaders();

          String contentType = partHeaders.getFirst(ArtifactConstants.X_LOCKSS_CONTENT_TYPE);

          // Fallback
//          if (StringUtils.isEmpty(contentType)) {
//            contentType = partHeaders.getFirst(HttpHeaders.CONTENT_TYPE);
//          }

          if (!StringUtils.isEmpty(contentType)) {
            result.getHttpHeaders().set(HttpHeaders.CONTENT_TYPE, contentType);
          }
        }
      }

      return result;

    } catch (IOException e) {
      log.error("Could not process MultipartMessage into ArtifactData object", e);
      throw new IOException("Error processing multipart response");
    }
  }
}
