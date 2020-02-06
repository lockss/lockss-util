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
}
