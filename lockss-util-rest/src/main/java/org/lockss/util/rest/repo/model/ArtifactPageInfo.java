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

package org.lockss.util.rest.repo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A page of artifact results.
 */
public class ArtifactPageInfo {
  private List<Artifact> artifacts = new ArrayList<>();
  private PageInfo pageInfo = null;

  /**
   * Provides the artifacts included in the page.
   * 
   * @return a List<Artifact> with the artifacts included in the page.
   */
  public List<Artifact> getArtifacts() {
    return artifacts;
  }

  /**
   * Saves the artifacts included in the page.
   * @param artifacts A List<Artifact> with the artifacts included in the page.
  **/
  public void setArtifacts(List<Artifact> artifacts) {
    this.artifacts = artifacts;
  }

  /**
   * Provides the pagination information.
   * 
   * @return a PageInfo with the pagination information.
   */
  public PageInfo getPageInfo() {
    return pageInfo;
  }

  /**
   * Saves the pagination information.
   * 
   * @param pageInfo A PageInfo with the pagination information.
   */
  public void setPageInfo(PageInfo pageInfo) {
    this.pageInfo = pageInfo;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArtifactPageInfo artifactPageInfo = (ArtifactPageInfo) o;
    return Objects.equals(this.artifacts, artifactPageInfo.artifacts) &&
        Objects.equals(this.pageInfo, artifactPageInfo.pageInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(artifacts, pageInfo);
  }

  @Override
  public String toString() {
    return "[ArtifactPageInfo artifacts=" + artifacts
	+ ", pageInfo=" + pageInfo + "]";
  }
}
