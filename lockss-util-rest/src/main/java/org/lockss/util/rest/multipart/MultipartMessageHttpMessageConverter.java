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

import org.apache.commons.fileupload.MultipartStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultipartMessageHttpMessageConverter implements HttpMessageConverter<MultipartMessage> {

  public static final List<MediaType> SUPPORTED_MEDIA_TYPES =
      Collections.unmodifiableList(Arrays.asList(MediaType.MULTIPART_FORM_DATA));

  private final File tmpDir;

  public MultipartMessageHttpMessageConverter() {
    this(null);
  }

  /**
   * Constructor.
   *
   * @param tmpDir Temporary directory in which file-backed multipart parts will be created.
   */
  public MultipartMessageHttpMessageConverter(File tmpDir) {
    this.tmpDir = tmpDir;
  }

  @Override
  public boolean canRead(Class<?> clazz, MediaType mediaType) {
    if (!clazz.isAssignableFrom(MultipartMessage.class)) {
      return false;
    }

    if (mediaType == null) {
      return true;
    }

    for (MediaType supportedMediaType : getSupportedMediaTypes()) {
      if (supportedMediaType.includes(mediaType)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    if (!MultipartMessage.class.isAssignableFrom(clazz)) {
      return false;
    }
    if (mediaType == null || MediaType.ALL.equals(mediaType)) {
      return true;
    }
    for (MediaType supportedMediaType : getSupportedMediaTypes()) {
      if (supportedMediaType.isCompatibleWith(mediaType)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<MediaType> getSupportedMediaTypes() {
    return SUPPORTED_MEDIA_TYPES;
  }

  @Override
  public MultipartMessage read(Class<? extends MultipartMessage> clazz, HttpInputMessage inputMessage)
      throws IOException, HttpMessageNotReadableException {

    // Headers from HttpInputMessage
    HttpHeaders inputHeaders = inputMessage.getHeaders();

    // Get multipart boundary from HttpInputMessage's Content-Type field
    MediaType inputContentType = inputHeaders.getContentType();
    String boundary = inputContentType.getParameter("boundary");

    if (!StringUtils.hasLength(boundary)) {
      throw new HttpMessageNotReadableException("Multipart boundary is missing");
    }

    // Construct a multipart stream
    MultipartStream multipartStream = new MultipartStream(
        inputMessage.getBody(),
        boundary.getBytes(),
        4096, // FIXME: MultipartStream from commons-fileupload v1.4 provides a default
        null
    );

    return new MultipartMessage(tmpDir, multipartStream);
  }

  @Override
  public void write(MultipartMessage multipartMessage, MediaType contentType, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {

    MultipartMessage.writeTo(outputMessage.getBody());
  }
}
