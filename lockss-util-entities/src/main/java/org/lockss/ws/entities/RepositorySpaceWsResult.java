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
 * Container for the information related to a repository space that is the
 * result of a query.
 */
public class RepositorySpaceWsResult {
  private String repositorySpaceId;
  private Long size;
  private Long used;
  private Long free;
  private Double percentageFull;
  private Integer activeCount;
  private Integer inactiveCount;
  private Integer deletedCount;
  private Integer orphanedCount;

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
   * Provides the size of the repository space.
   * 
   * @return a Long with the size in bytes.
   */
  public Long getSize() {
    return size;
  }
  public void setSize(Long size) {
    this.size = size;
  }

  /**
   * Provides the size of the repository space that is used.
   * 
   * @return a Long with the size in bytes.
   */
  public Long getUsed() {
    return used;
  }
  public void setUsed(Long used) {
    this.used = used;
  }

  /**
   * Provides the size of the repository space that is free.
   * 
   * @return a Long with the size in bytes.
   */
  public Long getFree() {
    return free;
  }
  public void setFree(Long free) {
    this.free = free;
  }

  /**
   * Provides the percentage of occupation of the repository space.
   * 
   * @return a Double with the percentage.
   */
  public Double getPercentageFull() {
    return percentageFull;
  }
  public void setPercentageFull(Double percentageFull) {
    this.percentageFull = percentageFull;
  }

  /**
   * Provides the count of active Archival Units in the repository space.
   * 
   * @return a Integer with the count.
   */
  public Integer getActiveCount() {
    return activeCount;
  }
  public void setActiveCount(Integer activeCount) {
    this.activeCount = activeCount;
  }

  /**
   * Provides the count of inactive Archival Units in the repository space.
   * 
   * @return a Integer with the count.
   */
  public Integer getInactiveCount() {
    return inactiveCount;
  }
  public void setInactiveCount(Integer inactiveCount) {
    this.inactiveCount = inactiveCount;
  }

  /**
   * Provides the count of deleted Archival Units in the repository space.
   * 
   * @return a Integer with the count.
   */
  public Integer getDeletedCount() {
    return deletedCount;
  }
  public void setDeletedCount(Integer deletedCount) {
    this.deletedCount = deletedCount;
  }

  /**
   * Provides the count of orphaned Archival Units in the repository space.
   * 
   * @return a Integer with the count.
   */
  public Integer getOrphanedCount() {
    return orphanedCount;
  }
  public void setOrphanedCount(Integer orphanedCount) {
    this.orphanedCount = orphanedCount;
  }

  @Override
  public String toString() {
    return "RepositorySpaceWsResult [repositorySpaceId=" + repositorySpaceId
	+ ", size=" + size + ", used=" + used + ", free=" + free
	+ ", percentageFull=" + percentageFull + ", activeCount=" + activeCount
	+ ", inactiveCount=" + inactiveCount + ", deletedCount=" + deletedCount
	+ ", orphanedCount=" + orphanedCount + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RepositorySpaceWsResult that = (RepositorySpaceWsResult) o;
    return Objects.equals(repositorySpaceId, that.repositorySpaceId) &&
        Objects.equals(size, that.size) &&
        Objects.equals(used, that.used) &&
        Objects.equals(free, that.free) &&
        Objects.equals(percentageFull, that.percentageFull) &&
        Objects.equals(activeCount, that.activeCount) &&
        Objects.equals(inactiveCount, that.inactiveCount) &&
        Objects.equals(deletedCount, that.deletedCount) &&
        Objects.equals(orphanedCount, that.orphanedCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(repositorySpaceId, size, used, free, percentageFull, activeCount,
        inactiveCount, deletedCount, orphanedCount);
  }
}
