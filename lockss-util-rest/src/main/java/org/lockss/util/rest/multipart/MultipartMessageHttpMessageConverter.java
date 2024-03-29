/*

 Copyright (c) 2017-2020 Board of Trustees of Leland Stanford Jr. University,
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

    multipartMessage.writeTo(outputMessage.getBody());
  }
}
