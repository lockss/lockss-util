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

import java.util.Objects;

/**
 * The version information of the Java platform
 */
public class JavaVersionWsResult {
  private String version;
  private String specificationVersion;
  private String runtimeVersion;
  private String runtimeName;

  /**
   * Provides the Java version string.
   * 
   * @return a String with the value of the <tt>java.version</tt> System
   * property
   */
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Provides the Java specification version string.
   * 
   * @return a String with the value of the
   * <tt>java.specification.version</tt> System property
   */
  public String getSpecificationVersion() {
    return specificationVersion;
  }
  public void setSpecificationVersion(String specificationVersion) {
    this.specificationVersion = specificationVersion;
  }

  /**
   * Provides the Java runtime version string.
   * 
   * @return a String with the value of the
   * <tt>java.runtime.version</tt> System property
   */
  public String getRuntimeVersion() {
    return runtimeVersion;
  }
  public void setRuntimeVersion(String runtimeVersion) {
    this.runtimeVersion = runtimeVersion;
  }

  /**
   * Provides the Java runtime name string.
   * 
   * @return a String with the value of the
   * <tt>java.runtime.name</tt> System property
   */
  public String getRuntimeName() {
    return runtimeName;
  }
  public void setRuntimeName(String runtimeName) {
    this.runtimeName = runtimeName;
  }

  @Override
  public String toString() {
    return "[JavaVersionWsResult version=" + version
      + ", specificationVersion=" + specificationVersion
      + ", runtimeVersion=" + runtimeVersion
      + ", runtimeName=" + runtimeName + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JavaVersionWsResult that = (JavaVersionWsResult) o;
    return Objects.equals(version, that.version) &&
        Objects.equals(specificationVersion, that.specificationVersion) &&
        Objects.equals(runtimeVersion, that.runtimeVersion) &&
        Objects.equals(runtimeName, that.runtimeName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, specificationVersion, runtimeVersion, runtimeName);
  }
}
