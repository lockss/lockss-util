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

import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.*;

/**
 * Container for the information related to a title database title that is the
 * result of a query.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TdbTitleWsResult {
  private String name;
  private TdbPublisherWsResult tdbPublisher;
  private String id;
  private String proprietaryId;
  private List<String> proprietaryIds;
  private String publicationType;
  private String issn;
  private String issnL;
  private String eIssn;
  private String printIssn;
  private List<String> issns;

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public TdbPublisherWsResult getTdbPublisher() {
    return tdbPublisher;
  }
  public void setTdbPublisher(TdbPublisherWsResult tdbPublisher) {
    this.tdbPublisher = tdbPublisher;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  /**
   * @deprecated Replaced by {@link #getProprietaryIds()}
   */
  @Deprecated public String getProprietaryId() {
    return proprietaryId;
  }
  /**
   * @deprecated Replaced by {@link #setProprietaryIds(List)}
   */
  @Deprecated public void setProprietaryId(String proprietaryId) {
    this.proprietaryId = proprietaryId;
  }
  public List<String> getProprietaryIds() {
    return proprietaryIds;
  }
  public void setProprietaryIds(List<String> proprietaryIds) {
    this.proprietaryIds = proprietaryIds;
  }
  public String getPublicationType() {
    return publicationType;
  }
  public void setPublicationType(String publicationType) {
    this.publicationType = publicationType;
  }
  public String getIssn() {
    return issn;
  }
  public void setIssn(String issn) {
    this.issn = issn;
  }
  public String getIssnL() {
    return issnL;
  }
  public void setIssnL(String issnL) {
    this.issnL = issnL;
  }
  public String getEIssn() {
    return eIssn;
  }
  public void setEIssn(String eIssn) {
    this.eIssn = eIssn;
  }
  public String getPrintIssn() {
    return printIssn;
  }
  public void setPrintIssn(String printIssn) {
    this.printIssn = printIssn;
  }
  public List<String> getIssns() {
    return issns;
  }
  public void setIssns(List<String> issns) {
    this.issns = issns;
  }

  @Override
  public String toString() {
    return "TdbTitleWsResult [name=" + name + ", tdbPublisher=" + tdbPublisher
	+ ", id=" + id + ", proprietaryId=" + proprietaryId
	+ ", proprietaryIds=" + proprietaryIds + ", publicationType="
	+ publicationType + ", issn=" + issn + ", issnL=" + issnL + ", eIssn="
	+ eIssn + ", printIssn=" + printIssn + ", issns=" + issns + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TdbTitleWsResult that = (TdbTitleWsResult) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(tdbPublisher, that.tdbPublisher) &&
        Objects.equals(id, that.id) &&
        Objects.equals(proprietaryId, that.proprietaryId) &&
        Objects.equals(proprietaryIds, that.proprietaryIds) &&
        Objects.equals(publicationType, that.publicationType) &&
        Objects.equals(issn, that.issn) &&
        Objects.equals(issnL, that.issnL) &&
        Objects.equals(eIssn, that.eIssn) &&
        Objects.equals(printIssn, that.printIssn) &&
        Objects.equals(issns, that.issns);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, tdbPublisher, id, proprietaryId, proprietaryIds, publicationType,
        issn, issnL, eIssn, printIssn, issns);
  }
}
