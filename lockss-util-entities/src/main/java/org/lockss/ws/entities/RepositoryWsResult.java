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

import java.util.Map;
import java.util.Objects;

/**
 * Container for the information related to a repository that is the result of a
 * query.
 */
public class RepositoryWsResult {
  private String repositorySpaceId;
  private String directoryName;
  private String auName;
  private Boolean internal;
  private String status;
  private Long diskUsage;
  private String pluginName;
  private Map<String, String> params;

  /**
   * Provides the repository space identifier.
   * 
   * @return a String with the identifier.
   */
  public String getRepositorySpaceId() {
    return repositorySpaceId;
  }
  public void setRepositorySpaceId(String repositorySpaceId) {
    this.repositorySpaceId = repositorySpaceId;
  }

  /**
   * Provides the repository directory name.
   * 
   * @return a String with the directory name.
   */
  public String getDirectoryName() {
    return directoryName;
  }
  public void setDirectoryName(String directoryName) {
    this.directoryName = directoryName;
  }

  /**
   * Provides the Archival Unit name.
   * 
   * @return a String with the name.
   */
  public String getAuName() {
    return auName;
  }
  public void setAuName(String auName) {
    this.auName = auName;
  }

  /**
   * Provides an indication of whether the repository is internal.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean getInternal() {
    return internal;
  }
  public void setInternal(Boolean internal) {
    this.internal = internal;
  }

  /**
   * Provides the repository status.
   * 
   * @return a String with the status.
   */
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Provides the space occupied on disk by the repository.
   * 
   * @return a Long with the occupied space in bytes.
   */
  public Long getDiskUsage() {
    return diskUsage;
  }
  public void setDiskUsage(Long diskUsage) {
    this.diskUsage = diskUsage;
  }

  /**
   * Provides the Archival Unit plugin name.
   * 
   * @return a String with the plugin name.
   */
  public String getPluginName() {
    return pluginName;
  }
  public void setPluginName(String pluginName) {
    this.pluginName = pluginName;
  }

  /**
   * Provides the Archival Unit configuration parameters.
   * 
   * @return a {@code Map<String, String>} with the parameters.
   */
  public Map<String, String> getParams() {
    return params;
  }
  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  @Override
  public String toString() {
    return "RepositoryWsResult [repositorySpaceId=" + repositorySpaceId
	+ ", directoryName=" + directoryName + ", auName=" + auName
	+ ", internal="	+ internal + ", status=" + status + ", diskUsage="
	+ diskUsage + ", pluginName=" + pluginName + ", params=" + params + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RepositoryWsResult that = (RepositoryWsResult) o;
    return Objects.equals(repositorySpaceId, that.repositorySpaceId) &&
        Objects.equals(directoryName, that.directoryName) &&
        Objects.equals(auName, that.auName) &&
        Objects.equals(internal, that.internal) &&
        Objects.equals(status, that.status) &&
        Objects.equals(diskUsage, that.diskUsage) &&
        Objects.equals(pluginName, that.pluginName) &&
        Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(repositorySpaceId, directoryName, auName, internal, status, diskUsage, pluginName, params);
  }
}
