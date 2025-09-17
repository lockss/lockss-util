/*

Copyright (c) 2000-2025 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.lockss.util.storage.StorageInfo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Information about the repository
 */
@Schema(title = "Repository Information",
        description = "Information about the repository")
public class RepositoryInfo implements Serializable {
  @JsonProperty("storeInfo")
  private StorageInfo storeInfo = null;

  @JsonProperty("indexInfo")
  private StorageInfo indexInfo = null;

  @JsonProperty("repositoryStatistics")
  private RepositoryStatistics repoStats = null;

  /**
   * Default constructor.
   */
  public RepositoryInfo() {
  }

  public RepositoryInfo(StorageInfo storeInfo, StorageInfo indexInfo) {
    this.storeInfo = storeInfo;
    this.indexInfo = indexInfo;
  }

  public RepositoryInfo storeInfo(StorageInfo storeInfo) {
    this.storeInfo = storeInfo;
    return this;
  }

  /**
   * Information about the repository's storage areas
   * @return storeInfo
   **/
  @Schema(title = "Storage Area Information",
          description = "Information about the repository's storage areas",
          requiredMode = RequiredMode.REQUIRED,
          nullable = false)
  @NotNull
  @Valid
  public StorageInfo getStoreInfo() {
    return storeInfo;
  }

  public void setStoreInfo(StorageInfo storeInfo) {
    this.storeInfo = storeInfo;
  }

  public RepositoryInfo indexInfo(StorageInfo indexInfo) {
    this.indexInfo = indexInfo;
    return this;
  }

  /**
   * Information about the repository's artifact index
   * @return indexInfo
   **/
  @Schema(title = "Artifact Index Information",
          description = "Information about the repository's artifact index",
          requiredMode = RequiredMode.REQUIRED,
          nullable = false)
  @NotNull
  @Valid
  public StorageInfo getIndexInfo() {
    return indexInfo;
  }

  public void setIndexInfo(StorageInfo indexInfo) {
    this.indexInfo = indexInfo;
  }

  public RepositoryInfo repositoryStatistics(RepositoryStatistics repoStats) {
    this.repoStats = repoStats;
    return this;
  }

  /**
   * Miscellaneous statistics about the repository
   * @return indexInfo
   **/
  @Schema(title = "Repository Statistics",
          description = "Miscellaneous statistics about the repository",
          requiredMode = RequiredMode.NOT_REQUIRED,
          nullable = true)
  @NotNull
  public RepositoryStatistics getRepositoryStatistics() {
    return repoStats;
  }

  public void setRepositoryStatistics(RepositoryStatistics repoStats) {
    this.repoStats = repoStats;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RepositoryInfo repositoryInfo = (RepositoryInfo) o;
    return Objects.equals(this.storeInfo, repositoryInfo.storeInfo) &&
        Objects.equals(this.indexInfo, repositoryInfo.indexInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(storeInfo, indexInfo);
  }

  @Override
  public String toString() {
    return "[RepositoryInfo store: " + storeInfo + " index: " + indexInfo + "]";
  }
}
