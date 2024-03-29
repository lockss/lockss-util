/*
 * $Id$
 */

/*

 Copyright (c) 2014 Board of Trustees of Leland Stanford Jr. University,
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
