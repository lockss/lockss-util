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

import org.springframework.core.io.ByteArrayResource;

/**
 * Named resource useful to create HTTP Multipart requests.
 */
public class NamedByteArrayResource extends ByteArrayResource {
  private String name;
  private String description;

  /**
   * Constructor.
   * @param name A String with the name of the resource.
   * @param byteArray A byte[] with the resource content.
   */
  public NamedByteArrayResource(String name, byte[] byteArray) {
    super(byteArray);
    this.name = name;
  }

  /**
   * Constructor.
   * @param name A String with the name of the resource.
   * @param byteArray A byte[] with the resource content.
   * @param description A String with the description of the resource.
   */
  public NamedByteArrayResource(String name, byte[] byteArray,
      String description) {
    super(byteArray, description);
    this.name = name;
    this.description = (description != null ? description : "");
  }

  @Override
  public String getFilename() {
    return name;
  }

  @Override
  public String getDescription() {
    return "Named byte array resource [name: " + getFilename() + ", byteCount: "
	+ getByteArray().length + ", description: " + description + "]";
  }
}
