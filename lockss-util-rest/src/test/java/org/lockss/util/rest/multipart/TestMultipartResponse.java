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

import org.junit.Test;

import org.lockss.util.rest.multipart.MultipartResponse.Part;
import org.lockss.util.test.*;

import org.springframework.http.HttpHeaders;

/**
 * Test class for org.lockss.util.rest.MultipartResponse.
 */
public class TestMultipartResponse extends LockssTestCase5 {

  /**
   * Tests the extraction of the part name from the Content-Disposition header.
   */
  @Test
  public void testGetPartNameFromContentDispositionHeader() {
    assertNull(getPartNameFromContentDispositionHeader("form-data"));
    assertNull(getPartNameFromContentDispositionHeader("form-data;"));
    assertNull(getPartNameFromContentDispositionHeader("form-data name="));

    String name = "";
    assertEquals(name, getPartNameFromContentDispositionHeader(
	"form-data; name=\"" + name + "\""));

    name = "config-data";
    assertEquals(name, getPartNameFromContentDispositionHeader(
	"form-data; name=\"" + name + "\""));
    assertEquals(name, getPartNameFromContentDispositionHeader(
	"name=\"" + name + "\";form-data"));
    assertEquals(name, getPartNameFromContentDispositionHeader(
	"form-data; name=\"" + name + "\";abcd"));
  }

  /**
   * Provides the part name given a Content-Disposition header.
   * 
   * @param contentDisposition
   *          A String with the Content-Disposition header.
   * @return a String with the part name.
   */
  private String getPartNameFromContentDispositionHeader(
      String contentDisposition) {
    return createPart(contentDisposition)
	.getPartNameFromContentDispositionHeader(contentDisposition);
  }

  /**
   * Creates a part given a Content-Disposition header.
   * 
   * @param contentDisposition
   *          A String with the Content-Disposition header.
   * @return a Part with the created part.
   */
  private Part createPart(String contentDisposition) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", contentDisposition);

    Part part = new Part();
    part.setHeaders(headers);

    return part;
  }

  /**
   * Tests the definition of a part name.
   */
  @Test
  public void testGetName() {
    assertNull(getName("form-data"));
    assertNull(getName("form-data;"));
    assertNull(getName("form-data name="));

    String name = "";
    assertEquals(name, getName("form-data; name=\"" + name + "\""));

    name = "config-data";
    assertEquals(name, getName("form-data; name=\"" + name + "\""));
    assertEquals(name, getName("name=\"" + name + "\";form-data"));
    assertEquals(name, getName("form-data; name=\"" + name + "\";abcd"));
  }

  /**
   * Provides the name of a part given a Content-Disposition header.
   * @param contentDisposition
   *          A String with the Content-Disposition header.
   * @return a String with the part name.
   */
  private String getName(String contentDisposition) {
    return createPart(contentDisposition).getName();
  }

  /**
   * Tests the addition of parts.
   */
  @Test
  public void testAddPart() {
    MultipartResponse response = new MultipartResponse();

    Part part = addPart("form-data", response);
    assertEquals(1, response.getParts().size());
    assertEquals("Part-0", part.getName());

    part = addPart("form-data;", response);
    assertEquals(2, response.getParts().size());
    assertEquals("Part-1", part.getName());

    part = addPart("form-data name=", response);
    assertEquals(3, response.getParts().size());
    assertEquals("Part-2", part.getName());

    part = addPart("form-data; name=\"\"", response);
    assertEquals(4, response.getParts().size());
    assertEquals("", part.getName());

    part = addPart("form-data; name=\" \"", response);

    assertEquals(4, response.getParts().size());
    assertEquals("", part.getName());

    String name = "config-data";

    part = addPart("form-data; name=\"" + name + "\"", response);
    assertEquals(5, response.getParts().size());
    assertEquals("config-data", part.getName());

    part = addPart("name=\"" + name + "\";form-data", response);
    assertEquals(5, response.getParts().size());
    assertEquals("config-data", part.getName());

    part = addPart("form-data; name=\"" + name + "\";abcd", response);
    assertEquals(5, response.getParts().size());
    assertEquals("config-data", part.getName());
  }

  /**
   * Adds a part to a MultipartResponse.
   * 
   * @param contentDisposition
   *          A String with the Content-Disposition header.
   * @param response
   *          A MultipartResponse where to add the part.
   * @return a Part with the part just added.
   */
  private Part addPart(String contentDisposition, MultipartResponse response) {
    Part part = createPart(contentDisposition);
    response.addPart(part);

    return part;
  }
}
