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

package org.lockss.ws.entities;

import java.util.Arrays;
import javax.activation.DataHandler;

/**
 * A wrapper for the parameters used to perform an import operation.
 */
public class ImportWsParams {
  private String sourceUrl;
  private DataHandler dataHandler;
  private String targetId;
  private String targetUrl;
  private String[] properties;

  /**
   * Provides the URL of the source to be imported.
   * 
   * @return a String with the URL.
   */
  public String getSourceUrl() {
    return sourceUrl;
  }

  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  /**
   * Provides the pushed file to be imported.
   *
   * @return a DataHandler through which to obtain the content.
   */
  public DataHandler getDataHandler() {
    return dataHandler;
  }

  public void setDataHandler(DataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  /**
   * Provides the identifier of the target where to store the imported data.
   * 
   * @return a String with the identifier.
   */
  public String getTargetId() {
    return targetId;
  }

  public void setTargetId(String targetId) {
    this.targetId = targetId;
  }

  /**
   * Provides the URL of the target location where to store the imported data.
   * 
   * @return a String with the URL.
   */
  public String getTargetUrl() {
    return targetUrl;
  }

  public void setTargetUrl(String targetUrl) {
    this.targetUrl = targetUrl;
  }

  /**
   * Provides the additional properties of the import operation.
   * 
   * @return a String[] with the properties.
   */
  public String[] getProperties() {
    return properties;
  }

  public void setProperties(String[] properties) {
    this.properties = properties;
  }

  @Override
  public String toString() {
    return "[ImportWsParams: sourceUrl=" + sourceUrl + ", targetId=" + targetId
	+ ", targetUrl=" + targetUrl + ", properties="
	+ Arrays.toString(properties) + "]";
  }
}
