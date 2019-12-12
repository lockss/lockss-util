/*
 * $Id$
 */

/*

 Copyright (c) 2015 Board of Trustees of Leland Stanford Jr. University,
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
 * Container for the information related to a URL that is the result of a query.
 */
public class UrlWsResult {
  private String url;
  private Integer versionCount;
  private Long currentVersionSize;
  private Float pollWeight;

  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public Integer getVersionCount() {
    return versionCount;
  }
  public void setVersionCount(Integer versionCount) {
    this.versionCount = versionCount;
  }
  public Long getCurrentVersionSize() {
    return currentVersionSize;
  }
  public void setCurrentVersionSize(Long currentVersionSize) {
    this.currentVersionSize = currentVersionSize;
  }
  public Float getPollWeight() {
    return pollWeight;
  }
  public void setPollWeight(Float pollWeight) {
    this.pollWeight = pollWeight;
  }

  @Override
  public String toString() {
    return "[UrlWsResult url=" + url + ", versionCount=" + versionCount
	+ ", currentVersionSize=" + currentVersionSize
	+ ", pollWeight=" + pollWeight + "]";
  }
}
