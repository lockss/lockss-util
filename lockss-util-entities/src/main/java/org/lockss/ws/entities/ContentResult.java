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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper for the result of a Content web service operation.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="ContentResult")
public class ContentResult {
  private DataHandler dataHandler;
  private Properties properties;

  /**
   * Provides the requested content.
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
   * Provides the properties of the requested content.
   *
   * @return a Properties with the properties of the content.
   */
  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  /**
   * Writes the result content to a file.
   * 
   * @param outputFileSpec
   *          A String with the specification of the file where to write the
   *          result content.
   * @return a File containing the result content.
   * @throws IOException
   */
  public File writeContentToFile(String outputFileSpec) throws IOException {
    File outputFile = new File(outputFileSpec);

    // Write the received file.
    InputStream dhis = null;
    FileOutputStream fos = null;
    byte[] buffer = new byte[1024 * 1024];
    int bytesRead = 0;

    try {
      dhis = dataHandler.getInputStream();
      fos = new FileOutputStream(outputFile);

      while ((bytesRead = dhis.read(buffer)) != -1) {
	fos.write(buffer, 0, bytesRead);
      }
    } finally {
      if (dhis != null) {
	try {
	  dhis.close();
	} catch (IOException ioe) {
	  System.out
	      .println("Exception caught closing DataHandler input stream.");
	}
      }

      if (fos != null) {
	fos.flush();
	fos.close();
      }
    }

    return outputFile;
  }

  @Override
  public String toString() {
    return ("[ContentResult properties=" + properties + "]");
  }
}
