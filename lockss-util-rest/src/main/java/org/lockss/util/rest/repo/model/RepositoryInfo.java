/*

 Copyright (c) 2019-2020 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.lockss.util.storage.StorageInfo;
import org.springframework.validation.annotation.Validated;

/**
 * Information about a repository and its storage areas
 */
public class RepositoryInfo implements Serializable {
  @JsonProperty("storeInfo")
  private StorageInfo storeInfo = null;

  @JsonProperty("indexInfo")
  private StorageInfo indexInfo = null;

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
   * Get storeInfo
   * @return storeInfo
   **/
  @Schema(required = true, description = "")
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
   * Get indexInfo
   * @return indexInfo
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public StorageInfo getIndexInfo() {
    return indexInfo;
  }

  public void setIndexInfo(StorageInfo indexInfo) {
    this.indexInfo = indexInfo;
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
