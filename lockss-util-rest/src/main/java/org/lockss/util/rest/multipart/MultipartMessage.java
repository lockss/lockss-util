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

package org.lockss.util.rest.multipart;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.util.FileItemHeadersImpl;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultipartMessage {

  // Header name constants
  public static final String CONTENT_DISPOSITION = "Content-Disposition";
  public static final String CONTENT_TYPE = "Content-Type";

  // List containing parts of this multipart message
  private List<FileItem> parts = new ArrayList<>();

  public MultipartMessage() {
    // Intentionally left blank
  }

  /**
   * Constructor that reads a {@link MultipartStream} and parses it into {@link FileItem} parts.
   *
   * @param tmpDir Temporary directory in which file-backed multipart parts will be created.
   * @param multipartStream The {@link MultipartStream} to read and parse.
   * @throws IOException Thrown if an {@link IOException} occurred while reading the multipart stream.
   */
  public MultipartMessage(File tmpDir, MultipartStream multipartStream) throws IOException {
    // Construct and configure a DiskFileItemFactory for FileItem parts
    DiskFileItemFactory itemFactory =
        new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, tmpDir);

    // Parse multipart stream
    parseStream(multipartStream, itemFactory);
  }

  /**
   * Parses the multipart stream into parts.
   *
   * @throws IOException
   */
  private void parseStream(MultipartStream multipartStream, FileItemFactory itemFactory) throws IOException {
    boolean nextPart = multipartStream.skipPreamble();

    while (nextPart) {
      // Read part header from stream and parse them
      FileItemHeaders headers = getParsedHeaders(multipartStream.readHeaders());

      // Get Content-Disposition header
      Map<String, String> params = getHeaderParameters(getContentDisposition(headers));

      // Create a new FileItem (i.e., part) using the FileItemFactory
      FileItem part = itemFactory.createItem(
          params.get("name"),
          headers.getHeader(CONTENT_TYPE),
          params.get("filename") == null,
          params.get("filename")
      );

      // Set part header
      part.setHeaders(headers);

      // Read part body into FileItem's OutputStream
      try (OutputStream output = part.getOutputStream()) {
        multipartStream.readBodyData(output);
      }

      // Add part to list of parts
      parts.add(part);

      // nextPart is determined by readBoundary()
      nextPart = multipartStream.readBoundary();
    }
  }

  /**
   * Returns the Content-Disposition field from the part header.
   *
   * @param headers A {@link FileItemHeaders} containing the part header.
   * @return A {@link Header} containing the Content-Disposition header.
   */
  private Header getContentDisposition(FileItemHeaders headers) {
    return new BasicHeader(CONTENT_DISPOSITION, headers.getHeader(CONTENT_DISPOSITION));
  }

  /**
   * Returns the parameters of a header as a {@link Map}.
   *
   * @param header An {@link Header} containing the header.
   * @return A {@link Map<String, String>} containing the parameters of the header.
   */
  private Map<String, String> getHeaderParameters(Header header) {
    LinkedCaseInsensitiveMap<String> parameters = new LinkedCaseInsensitiveMap<>();

    // Q: Is it possible for a header to have more then one element in practice?
    for (HeaderElement element : header.getElements()) {
      for (NameValuePair param : element.getParameters()) {
        parameters.put(param.getName(), param.getValue());
      }
    }

    return parameters;
  }

  /**
   * For {@link MultipartResponse}.
   *
   * @return An {@code int} containing the number of parts in this multipart message.
   */
  public int getCount() {
    return parts.size();
  }

  /**
   * For {@link MultipartResponse}.
   *
   * @param i
   * @return
   */
  public FileItem getPart(int i) {
    return parts.get(i);
  }

  /**
   * Writes this {@link MultipartMessage} to the given {@link OutputStream}.
   *
   * @param body
   */
  public static void writeTo(OutputStream body) {
    // TODO
    throw new NotImplementedException("TODO");
  }

  // ******************************************************************
  // * THE FOLLOWING METHODS WERE TAKEN FROM APACHE COMMONS-FILEUPLOAD!
  // ******************************************************************

  /**
   * From Apache commons-fileupload v1.4:
   * <p>
   * Creates a new instance of {@link FileItemHeaders}.
   *
   * @return The new instance.
   */
  protected FileItemHeadersImpl newFileItemHeaders() {
    return new FileItemHeadersImpl();
  }

  /**
   * From Apache commons-fileupload v1.4:
   * <p>
   * Skips bytes until the end of the current line.
   *
   * @param headerPart The headers, which are being parsed.
   * @param end        Index of the last byte, which has yet been
   *                   processed.
   * @return Index of the \r\n sequence, which indicates
   * end of line.
   */
  private int parseEndOfLine(String headerPart, int end) {
    int index = end;
    for (; ; ) {
      int offset = headerPart.indexOf('\r', index);
      if (offset == -1 || offset + 1 >= headerPart.length()) {
        throw new IllegalStateException(
            "Expected headers to be terminated by an empty line.");
      }
      if (headerPart.charAt(offset + 1) == '\n') {
        return offset;
      }
      index = offset + 1;
    }
  }

  /**
   * From Apache commons-fileupload v1.4:
   * <p>
   * Reads the next header line.
   *
   * @param headers String with all headers.
   * @param header  Map where to store the current header.
   */
  private void parseHeaderLine(FileItemHeadersImpl headers, String header) {
    final int colonOffset = header.indexOf(':');
    if (colonOffset == -1) {
      // This header line is malformed, skip it.
      return;
    }
    String headerName = header.substring(0, colonOffset).trim();
    String headerValue =
        header.substring(header.indexOf(':') + 1).trim();
    headers.addHeader(headerName, headerValue);
  }

  /**
   * From Apache commons-fileupload v1.4:
   *
   * <p> Parses the <code>header-part</code> and returns as key/value
   * pairs.
   *
   * <p> If there are multiple headers of the same names, the name
   * will map to a comma-separated list containing the values.
   *
   * @param headerPart The <code>header-part</code> of the current
   *                   <code>encapsulation</code>.
   * @return A <code>Map</code> containing the parsed HTTP request headers.
   */
  protected FileItemHeaders getParsedHeaders(String headerPart) {
    final int len = headerPart.length();
    FileItemHeadersImpl headers = newFileItemHeaders();
    int start = 0;
    for (; ; ) {
      int end = parseEndOfLine(headerPart, start);
      if (start == end) {
        break;
      }
      StringBuilder header = new StringBuilder(headerPart.substring(start, end));
      start = end + 2;
      while (start < len) {
        int nonWs = start;
        while (nonWs < len) {
          char c = headerPart.charAt(nonWs);
          if (c != ' ' && c != '\t') {
            break;
          }
          ++nonWs;
        }
        if (nonWs == start) {
          break;
        }
        // Continuation line found
        end = parseEndOfLine(headerPart, nonWs);
        header.append(" ").append(headerPart.substring(nonWs, end));
        start = end + 2;
      }
      parseHeaderLine(headers, header.toString());
    }
    return headers;
  }
}
