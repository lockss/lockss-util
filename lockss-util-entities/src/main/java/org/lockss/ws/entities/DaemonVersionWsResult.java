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
 * The version information of the daemon.
 */
public class DaemonVersionWsResult {
  private String fullVersion;
  private int majorVersion;
  private int minorVersion;
  private int buildVersion;

  /**
   * Provides the full version of the daemon.
   * 
   * @return a String with the full version.
   */
  public String getFullVersion() {
    return fullVersion;
  }
  public void setFullVersion(String fullVersion) {
    this.fullVersion = fullVersion;
  }

  /**
   * Provides the major version of the daemon.
   * 
   * @return an int with the major version.
   */
  public int getMajorVersion() {
    return majorVersion;
  }
  public void setMajorVersion(int majorVersion) {
    this.majorVersion = majorVersion;
  }

  /**
   * Provides the minor version of the daemon.
   * 
   * @return an int with the minor version.
   */
  public int getMinorVersion() {
    return minorVersion;
  }
  public void setMinorVersion(int minorVersion) {
    this.minorVersion = minorVersion;
  }

  /**
   * Provides the build version of the daemon.
   * 
   * @return an int with the build version.
   */
  public int getBuildVersion() {
    return buildVersion;
  }
  public void setBuildVersion(int buildVersion) {
    this.buildVersion = buildVersion;
  }

  @Override
  public String toString() {
    return "DaemonVersionWsResult [fullVersion=" + fullVersion
	+ ", majorVersion=" + majorVersion + ", minorVersion=" + minorVersion
	+ ", buildVersion=" + buildVersion + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DaemonVersionWsResult that = (DaemonVersionWsResult) o;
    return majorVersion == that.majorVersion && minorVersion == that.minorVersion &&
        buildVersion == that.buildVersion && Objects.equals(fullVersion, that.fullVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fullVersion, majorVersion, minorVersion, buildVersion);
  }
}
