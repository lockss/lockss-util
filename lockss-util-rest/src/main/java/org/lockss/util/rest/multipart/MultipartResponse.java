/*

 Copyright (c) 2017-2019 Board of Trustees of Leland Stanford Jr. University,
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.*;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;
import org.lockss.util.rest.HttpResponseStatusAndHeaders;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.lockss.log.L4JLogger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * A representation of an HTTP Multipart response.
 */
public class MultipartResponse {
  private static L4JLogger log = L4JLogger.getLogger();

  private HttpStatus statusCode;
  private String statusMessage;
  private HttpHeaders responseHeaders;
  private LinkedHashMap<String, Part> parts = new LinkedHashMap<String, Part>();
  private LockssRestException lre;

  /**
   * Default constructor.
   */
  public MultipartResponse() {
  }

  /**
   * Constructor using a MIME Multipart response entity.
   * 
   * @param response
   *          A {@code ResponseEntity<MimeMultipart>} with the MIME multipart
   *          response.
   * @throws IOException
   *           if there are problems getting a part payload.
   * @throws MessagingException
   *           if there are other problems.
   */
  public MultipartResponse(ResponseEntity<MimeMultipart> response)
      throws IOException, MessagingException {
    final String DEBUG_HEADER = "MultipartResponse(response): ";

    // Populate the status code.
    statusCode = response.getStatusCode();
    log.trace(DEBUG_HEADER + "statusCode = " + statusCode);

    // Populate the response headers.
    responseHeaders = response.getHeaders();
    if (log.isTraceEnabled())
      log.trace(DEBUG_HEADER + "responseHeaders = " + responseHeaders);

    // The MIME Multipart response body.
    MimeMultipart responseBody = response.getBody();

    // Check whether no body has been received.
    if (responseBody == null) {
      // Yes.
      if (log.isTraceEnabled()) log.trace(DEBUG_HEADER + "responseBody = null");
      return;
    } else {
      // Yes.
      if (log.isTraceEnabled()) log.trace(DEBUG_HEADER + "responseBody != null");
    }

    // Get the count of parts in the response body.
    int partCount = 0;

    try {
      partCount = responseBody.getCount();
      if (log.isTraceEnabled()) log.trace(DEBUG_HEADER + "partCount = " + partCount);
    } catch (MessagingException me) {
      log.warn("Cannot get multipart response part count", me);
    }

    if (log.isTraceEnabled()) {
      log.trace(DEBUG_HEADER + "partCount = " + partCount);
      log.trace(DEBUG_HEADER + "responseBody.getContentType() = "
	+ responseBody.getContentType());
    }

    // Loop through all the parts.
    for (int i = 0; i < partCount; i++) {
      if (log.isTraceEnabled()) log.trace(DEBUG_HEADER + "part index = " + i);

      // Get the part body.
      MimeBodyPart partBody = (MimeBodyPart)responseBody.getBodyPart(i);

      // Process the part headers.
      Map<String, String> partHeaders = new HashMap<String, String>();
      
      for (Enumeration<?> enumeration = partBody.getAllHeaders();
  	enumeration.hasMoreElements();) {
  	Header header=(Header)enumeration.nextElement();
  	if (log.isTraceEnabled()) log.trace(DEBUG_HEADER + "header = " + header);

  	String headerName = header.getName();
  	String headerValue = header.getValue();
  	if (log.isTraceEnabled()) log.trace(DEBUG_HEADER + "headerName = "
  	    + headerName + ", headerValue = " + headerValue);

  	partHeaders.put(headerName, headerValue);
      }

      // Create and save the part.
      Part part = new Part();
      part.setHeaders(partHeaders);
      part.setInputStream(partBody.getDataHandler().getInputStream());
      addPart(part);
      if (log.isTraceEnabled())
	log.trace(DEBUG_HEADER + "inputStream = " + part.getInputStream());
    }
  }

  /**
   * Constructor using a LockssRestException.
   * 
   * @param lre
   *          A LockssRestException with the exception.
   */
  public MultipartResponse(LockssRestException lre) {
    final String DEBUG_HEADER = "MultipartResponse(lre): ";

    this.lre = lre;
    HttpResponseStatusAndHeaders status =
	  HttpResponseStatusAndHeaders.fromLockssRestException(lre);

    // Populate the status code and message.
    statusCode = HttpStatus.valueOf(status.getCode());
    statusMessage = status.getMessage();
    if (log.isTraceEnabled()) log.trace(DEBUG_HEADER + "statusCode = " + statusCode);

    if (lre instanceof LockssRestHttpException) {
      // Populate the response headers.
      responseHeaders = ((LockssRestHttpException)lre).getHttpResponseHeaders();
      if (log.isTraceEnabled())
	log.trace(DEBUG_HEADER + "responseHeaders = " + responseHeaders);
    }
  }

  /**
   * Provides the status code of the response.
   *
   * @return an HttpStatus with the response status code.
   */
  public HttpStatus getStatusCode() {
    return statusCode;
  }

  /**
   * Provides the status message of the response.
   *
   * @return a String with the response message.
   */
  public String getStatusMessage() {
    return statusMessage;
  }

  /**
   * Populates the status code of the response.
   * 
   * @param statusCode An HttpStatus with the response status code.
   */
  public void setStatusCode(HttpStatus statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * Provides the headers of the response.
   *
   * @return an HttpHeaders with the response headers.
   */
  public HttpHeaders getResponseHeaders() {
    return responseHeaders;
  }

  /**
   * Populates the headers of the response.
   * 
   * @param responseHeaders An HttpHeaders with the response headers.
   */
  public void setResponseHeaders(HttpHeaders responseHeaders) {
    this.responseHeaders = responseHeaders;
  }

  /**
   * Return the causal LockssRestException, if known, else null
   *
   * @return a LockssRestException
   */
  public LockssRestException getLockssRestException() {
    return lre;
  }

  /**
   * Provides the parts of the response, indexed by the part name.
   * 
   * @return a {@code LinkedHashMap<String, Part>} with the response parts,
   *         indexed by the part name.
   */
  public LinkedHashMap<String, Part> getParts() {
    return parts;
  }

  /**
   * Saves a part.
   * 
   * @param part
   *          A Part with the part to be saved.
   */
  protected void addPart(Part part) {
    final String DEBUG_HEADER = "addPart(): ";
    String partName = part.getName();

    if (partName == null) {
      int index = parts.size();
      partName = "Part-" + index;
      if (log.isTraceEnabled())
	log.trace(DEBUG_HEADER + "Trying partName = '" + partName + "'");

      while (parts.containsKey(partName)) {
	index++;
	partName = "Part-" + index;
	if (log.isTraceEnabled())
	  log.trace(DEBUG_HEADER + "Trying partName = '" + partName + "'");
      }

      part.setName(partName);
    }

    if (log.isTraceEnabled())
      log.trace(DEBUG_HEADER + "partName = '" + partName + "'");

    parts.put(partName, part);
  }

  @Override
  public String toString() {
    return "[MultipartResponse statusCode=" + statusCode + ", responseHeaders="
	+ responseHeaders + ", parts=" + parts + "]";
  }

  /**
   * A representation of an HTTP Multipart response part.
   */
  public static class Part {
    private static L4JLogger log = L4JLogger.getLogger();

    Map<String, String> headers;
    InputStream inputStream;
    String name;

    /**
     * Provides the headers of the part.
     *
     * @return a {@code Map<String, String>} with the part headers.
     */
    public Map<String, String> getHeaders() {
      return headers;
    }

    /**
     * Populates the headers of the part.
     * 
     * @param headers A {@code Map<String, String>} with the part headers.
     */
    public void setHeaders(Map<String, String> headers) {
      this.headers = headers;
    }

    /**
     * Provides the value of the Content-Length header.
     * 
     * @return a long with the value of the Content-Length header, or -1 if
     *         there is no Content-Length header.
     * @throws NumberFormatException
     *           if the Content-Length header value cannot be parsed as a
     *           number.
     */
    public long getContentLength() throws NumberFormatException {
      final String DEBUG_HEADER = "getContentLength(): ";
      String contentLengthValue = headers.get(HttpHeaders.CONTENT_LENGTH);

      if (contentLengthValue == null) {
	return -1;
      }

      if (log.isTraceEnabled()) log.trace(DEBUG_HEADER
	  + "contentLengthValue = " + contentLengthValue);

      long contentLength = Long.parseLong(contentLengthValue);
      if (log.isDebug2Enabled())
	log.debug2(DEBUG_HEADER + "contentLength = " + contentLength);
      return contentLength;
    }

    /**
     * Provides the value of the Last-Modified header.
     * 
     * @return a String with the value of the Last-Modified header.
     */
    public String getLastModified() {
      final String DEBUG_HEADER = "getLastModified(): ";
      String lastModified = headers.get(HttpHeaders.LAST_MODIFIED);
      if (log.isTraceEnabled())
	log.trace(DEBUG_HEADER + "lastModified = " + lastModified);

      return lastModified;
    }

    /**
     * Provides the value of the ETag header.
     * 
     * @return a String with the value of the ETag header.
     */
    public String getEtag() {
      final String DEBUG_HEADER = "getEtag(): ";
      String etag = headers.get(HttpHeaders.ETAG);
      if (log.isTraceEnabled()) log.trace(DEBUG_HEADER + "etag = " + etag);

      return etag;
    }

    /**
     * Provides an input stream to the content of the part.
     *
     * @return an InputStream with the part content input stream.
     */
    public InputStream getInputStream() {
      return inputStream;
    }

    /**
     * Populates the input stream of the part content.
     * 
     * @param payload
     *          An InputStream with the input stream to the content of the part.
     */
    public void setInputStream(InputStream inputStream) {
      this.inputStream = inputStream;
    }

    /**
     * Provides the name of the part.
     *
     * @return a String with the part name.
     */
    public String getName() {
      final String DEBUG_HEADER = "getName(): ";

      if (name == null) {
	// Get the value of the Content-Disposition header.
	String cdHeaderValue = headers.get("Content-Disposition");
	if (log.isTraceEnabled())
	  log.trace(DEBUG_HEADER + "cdHeaderValue = '" + cdHeaderValue + "'");

	if (!StringUtils.isEmpty(cdHeaderValue)) {
	  // Extract the part name from the Content-Disposition header.
	  name = getPartNameFromContentDispositionHeader(cdHeaderValue);
	}
      }

      if (log.isDebug2Enabled()) log.debug2(DEBUG_HEADER + "name = '" + name + "'");
      return name;
    }

    /**
     * Populates the name of the part.
     * 
     * @param name A String with the part name.
     */
    private void setName(String name) {
      this.name = name;
    }

    protected static final Pattern PART_NAME_PAT =
//       Pattern.compile("name=\"(.*)\"[^\"]*");
      Pattern.compile("name=\"(.*)\"");

//       String partNamePrefix = "name=\"";
//         if (cdElement.trim().startsWith(partNamePrefix)) {
//           // Yes: Obtain the part name.
//           partName = StringUtil.upToFinal(
//               cdElement.trim().substring(partNamePrefix.length()), "\"").trim();
//           break;


    /**
     * Provides the part name from the Content-Disposition header.
     *
     * @param contentDisposition
     *          A String with the value of the Content-Disposition header.
     * @return a String with the part name.
     */
    protected String getPartNameFromContentDispositionHeader(
        String contentDisposition) {
      final String DEBUG_HEADER = "getPartNameFromContentDispositionHeader(): ";
      if (log.isDebug2Enabled()) log.debug2(DEBUG_HEADER
	  + "contentDisposition = '" + contentDisposition + "'.");

      String partName = null;

      // Loop through all the elements in the value of the Content-Disposition
      // header.
      for (String cdElement : contentDisposition.split(";")) {
	// Check whether it is the element defining the part name.
	Matcher mat = PART_NAME_PAT.matcher(cdElement.trim());
	if (mat.matches()) {
          partName = mat.group(1).trim();
          break;
        }
      }

      if (log.isDebug2Enabled())
	log.debug2(DEBUG_HEADER + "partName = '" + partName + "'.");
	log.debug2(DEBUG_HEADER + "header = '" + contentDisposition + "'.");
      return partName;
    }

    @Override
    public String toString() {
      return "[Part headers=" + headers + ", name=" + name + ", inputStream="
	  + inputStream + "]";
    }
  }
}
