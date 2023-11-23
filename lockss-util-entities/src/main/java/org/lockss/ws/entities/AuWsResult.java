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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.*;

/**
 * Container for the information related to an archival unit that is the result
 * of a query.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuWsResult {
  private String auId;
  private String name;
  private String volume;
  private String pluginName;
  private String tdbYear;
  private String accessType;
  private Long contentSize;
  private Long diskUsage;
  private String repositoryPath;
  private Double recentPollAgreement;
  private Double highestPollAgreement;
  private String publishingPlatform;
  private String tdbPublisher;
  private Boolean availableFromPublisher;
  private String substanceState;
  private Long creationTime;
  private String crawlProxy;
  private String crawlWindow;
  private String crawlPool;
  private Long lastCompletedCrawl;
  private Long lastCrawl;
  private String lastCrawlResult;
  private Long lastCompletedDeepCrawl;
  private Long lastDeepCrawl;
  private String lastDeepCrawlResult;
  private Integer lastCompletedDeepCrawlDepth;
  private Long lastMetadataIndex;
  private Long lastCompletedPoll;
  private Long lastPoll;
  private String lastPollResult;
  private Boolean currentlyCrawling;
  private Boolean currentlyPolling;
  private String subscriptionStatus;
  private AuConfigurationWsResult auConfiguration;
  private List<String> newContentCrawlUrls;
  private List<String> urlStems;
  private Boolean isBulkContent;
  private List<PeerAgreementsWsResult> peerAgreements;
  private List<UrlWsResult> urls;
  private Collection<String> accessUrls;
  private List<String> substanceUrls;
  private List<String> articleUrls;
  private String journalTitle;
  private String tdbProvider;

  /**
   * Provides the Archival Unit identifier.
   * 
   * @return a String with the identifier.
   */
  public String getAuId() {
    return auId;
  }
  public void setAuId(String auId) {
    this.auId = auId;
  }

  /**
   * Provides the Archival Unit name.
   * 
   * @return a String with the name.
   */
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Provides the Archival Unit volume name.
   * 
   * @return a String with the volume name.
   */
  public String getVolume() {
    return volume;
  }
  public void setVolume(String volume) {
    this.volume = volume;
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
   * Provides the Archival Unit publication year from the TDB.
   * 
   * @return a String with the publication year from the TDB.
   */
  public String getTdbYear() {
    return tdbYear;
  }
  public void setTdbYear(String tdbYear) {
    this.tdbYear = tdbYear;
  }

  /**
   * Provides the Archival Unit access type.
   * 
   * @return a String with the access type.
   */
  public String getAccessType() {
    return accessType;
  }
  public void setAccessType(String accessType) {
    this.accessType = accessType;
  }

  /**
   * Provides the size of the Archival Unit.
   * 
   * @return a Long with the size in bytes.
   */
  public Long getContentSize() {
    return contentSize;
  }
  public void setContentSize(Long contentSize) {
    this.contentSize = contentSize;
  }

  /**
   * Provides the space occupied on disk by the Archival Unit.
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
   * Provides the Archival Unit repository path.
   * 
   * @return a String with the repository path.
   */
  public String getRepositoryPath() {
    return repositoryPath;
  }
  public void setRepositoryPath(String repositoryPath) {
    this.repositoryPath = repositoryPath;
  }

  /**
   * Provides the Archival Unit most recent poll agreement percentage.
   * 
   * @return a Double with the most recent poll agreement percentage.
   */
  public Double getRecentPollAgreement() {
    return recentPollAgreement;
  }
  public void setRecentPollAgreement(Double recentPollAgreement) {
    this.recentPollAgreement = recentPollAgreement;
  }

  /**
   * Provides the Archival Unit highest poll agreement percentage.
   * 
   * @return a Double with the highest poll agreement percentage.
   */
  public Double getHighestPollAgreement() {
    return highestPollAgreement;
  }
  public void setHighestPollAgreement(Double highestPollAgreement) {
    this.highestPollAgreement = highestPollAgreement;
  }

  /**
   * Provides the Archival Unit publishing platform name.
   * 
   * @return a String with the publishing platform name.
   */
  public String getPublishingPlatform() {
    return publishingPlatform;
  }
  public void setPublishingPlatform(String publishingPlatform) {
    this.publishingPlatform = publishingPlatform;
  }

  /**
   * Provides the Archival Unit publisher name from the TDB.
   * 
   * @return a String with the publisher name from the TDB.
   */
  public String getTdbPublisher() {
    return tdbPublisher;
  }
  public void setTdbPublisher(String tdbPublisher) {
    this.tdbPublisher = tdbPublisher;
  }

  /**
   * Provides an indication of whether the Archival Unit is available from the
   * publisher website.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean getAvailableFromPublisher() {
    return availableFromPublisher;
  }
  public void setAvailableFromPublisher(Boolean availableFromPublisher) {
    this.availableFromPublisher = availableFromPublisher;
  }

  /**
   * Provides the Archival Unit substance state.
   * 
   * @return a String with the substance state.
   */
  public String getSubstanceState() {
    return substanceState;
  }
  public void setSubstanceState(String substanceState) {
    this.substanceState = substanceState;
  }

  /**
   * Provides the Archival Unit creation timestamp.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getCreationTime() {
    return creationTime;
  }
  public void setCreationTime(Long creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * Provides the Archival Unit crawl proxy name.
   * 
   * @return a String with the crawl proxy name.
   */
  public String getCrawlProxy() {
    return crawlProxy;
  }
  public void setCrawlProxy(String crawlProxy) {
    this.crawlProxy = crawlProxy;
  }

  /**
   * Provides the Archival Unit crawl window.
   * 
   * @return a String with the crawl window.
   */
  public String getCrawlWindow() {
    return crawlWindow;
  }
  public void setCrawlWindow(String crawlWindow) {
    this.crawlWindow = crawlWindow;
  }

  /**
   * Provides the Archival Unit crawl pool name.
   * 
   * @return a String with the crawl pool name.
   */
  public String getCrawlPool() {
    return crawlPool;
  }
  public void setCrawlPool(String crawlPool) {
    this.crawlPool = crawlPool;
  }
  /**
   * Provides the timestamp of the last completed crawl of the Archival Unit.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getLastCompletedCrawl() {
    return lastCompletedCrawl;
  }
  public void setLastCompletedCrawl(Long lastCompletedCrawl) {
    this.lastCompletedCrawl = lastCompletedCrawl;
  }

  /**
   * Provides the timestamp of the last crawl of the Archival Unit.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getLastCrawl() {
    return lastCrawl;
  }
  public void setLastCrawl(Long lastCrawl) {
    this.lastCrawl = lastCrawl;
  }

  /**
   * Provides the result of the last crawl of the Archival Unit.
   * 
   * @return a String with the last crawl result.
   */
  public String getLastCrawlResult() {
    return lastCrawlResult;
  }
  public void setLastCrawlResult(String lastCrawlResult) {
    this.lastCrawlResult = lastCrawlResult;
  }

  /**
   * Provides the timestamp of the last completed deep crawl of the
   * Archival Unit.
   *
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getLastCompletedDeepCrawl() {
    return lastCompletedDeepCrawl;
  }
  public void setLastCompletedDeepCrawl(Long lastCompletedDeepCrawl) {
    this.lastCompletedDeepCrawl = lastCompletedDeepCrawl;
  }

  /**
   * Provides the timestamp of the last deep crawl of the Archival Unit.
   *
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getLastDeepCrawl() {
    return lastDeepCrawl;
  }
  public void setLastDeepCrawl(Long lastDeepCrawl) {
    this.lastDeepCrawl = lastDeepCrawl;
  }

  /**
   * Provides the result of the last deep crawl of the Archival Unit.
   *
   * @return a String with the last crawl result.
   */
  public String getLastDeepCrawlResult() {
    return lastDeepCrawlResult;
  }
  public void setLastDeepCrawlResult(String lastDeepCrawlResult) {
    this.lastDeepCrawlResult = lastDeepCrawlResult;
  }

  /**
   * Provides the depth of the last deep crawl of the Archival Unit.
   *
   * @return an Integer with the depth
   */
  public Integer getLastCompletedDeepCrawlDepth() {
    return lastCompletedDeepCrawlDepth;
  }
  public void setLastCompletedDeepCrawlDepth(Integer
					     lastCompletedDeepCrawlDepth) {
    this.lastCompletedDeepCrawlDepth = lastCompletedDeepCrawlDepth;
  }

  /**
   * Provides the timestamp of the last metadata indexing of the Archival Unit.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getLastMetadataIndex() {
    return lastMetadataIndex;
  }
  public void setLastMetadataIndex(Long lastMetadataIndex) {
    this.lastMetadataIndex = lastMetadataIndex;
  }

  /**
   * Provides the timestamp of the last completed poll of the Archival Unit.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getLastCompletedPoll() {
    return lastCompletedPoll;
  }
  public void setLastCompletedPoll(Long lastCompletedPoll) {
    this.lastCompletedPoll = lastCompletedPoll;
  }

  /**
   * Provides the timestamp of the last poll of the Archival Unit.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getLastPoll() {
    return lastPoll;
  }
  public void setLastPoll(Long lastPoll) {
    this.lastPoll = lastPoll;
  }

  /**
   * Provides the result of the last poll of the Archival Unit.
   * 
   * @return a String with the last poll result.
   */
  public String getLastPollResult() {
    return lastPollResult;
  }
  public void setLastPollResult(String lastPollResult) {
    this.lastPollResult = lastPollResult;
  }

  /**
   * Provides an indication of whether the Archival Unit is currently in the
   * process of crawling.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean getCurrentlyCrawling() {
    return currentlyCrawling;
  }
  public void setCurrentlyCrawling(Boolean currentlyCrawling) {
    this.currentlyCrawling = currentlyCrawling;
  }

  /**
   * Provides an indication of whether the Archival Unit is currently in the
   * process of polling.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean getCurrentlyPolling() {
    return currentlyPolling;
  }
  public void setCurrentlyPolling(Boolean currentlyPolling) {
    this.currentlyPolling = currentlyPolling;
  }

  /**
   * Provides the Archival Unit subscription status.
   * 
   * @return a String with the subscription status.
   */
  public String getSubscriptionStatus() {
    return subscriptionStatus;
  }
  public void setSubscriptionStatus(String subscriptionStatus) {
    this.subscriptionStatus = subscriptionStatus;
  }

  /**
   * Provides the Archival Unit configuration.
   * 
   * @return a AuConfigurationWsResult with the configuration.
   */
  public AuConfigurationWsResult getAuConfiguration() {
    return auConfiguration;
  }
  public void setAuConfiguration(AuConfigurationWsResult auConfiguration) {
    this.auConfiguration = auConfiguration;
  }

  /**
   * Provides the Archival Unit URLs to crawl new content.
   * 
   * @return a {@code List<String>} with the URLs to crawl new content.
   */
  public List<String> getNewContentCrawlUrls() {
    return newContentCrawlUrls;
  }
  public void setNewContentCrawlUrls(List<String> newContentCrawlUrls) {
    this.newContentCrawlUrls = newContentCrawlUrls;
  }

  /**
   * Provides the Archival Unit URL stems.
   * 
   * @return a {@code List<String>} with the URL stems.
   */
  public List<String> getUrlStems() {
    return urlStems;
  }
  public void setUrlStems(List<String> urlStems) {
    this.urlStems = urlStems;
  }

  /**
   * Provides an indication of whether the Archival Unit contains bulk content,
   * as opposed to harvested content.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean getIsBulkContent() {
    return isBulkContent;
  }
  public void setIsBulkContent(Boolean isBulkContent) {
    this.isBulkContent = isBulkContent;
  }

  /**
   * Provides the Archival Unit peer agreements.
   * 
   * @return a {@code List<PeerAgreementsWsResult>} with the peer agreements.
   */
  public List<PeerAgreementsWsResult> getPeerAgreements() {
    return peerAgreements;
  }
  public void setPeerAgreements(List<PeerAgreementsWsResult> peerAgreements) {
    this.peerAgreements = peerAgreements;
  }

  /**
   * Provides the Archival Unit URLs.
   * 
   * @return a {@code List<UrlWsResult>} with the URLs.
   */
  public List<UrlWsResult> getUrls() {
    return urls;
  }
  public void setUrls(List<UrlWsResult> urls) {
    this.urls = urls;
  }

  /**
   * Provides the Archival Unit access URLs.
   *
   * @return a {@code Collection<String>} with the access URLs.
   */
  public Collection<String> getAccessUrls() {
    return accessUrls;
  }
  public void setAccessUrls(Collection<String> accessUrls) {
    this.accessUrls = accessUrls;
  }

  /**
   * Provides the Archival Unit substance URLs.
   * 
   * @return a {@code List<String>} with the substance URLs.
   */
  public List<String> getSubstanceUrls() {
    return substanceUrls;
  }
  public void setSubstanceUrls(List<String> substanceUrls) {
    this.substanceUrls = substanceUrls;
  }

  /**
   * Provides the Archival Unit article URLs.
   * 
   * @return a {@code List<String>} with the article URLs.
   */
  public List<String> getArticleUrls() {
    return articleUrls;
  }
  public void setArticleUrls(List<String> articleUrls) {
    this.articleUrls = articleUrls;
  }

  /**
   * Provides the Archival Unit journal title.
   * 
   * @return a String with the journal title.
   */
  public String getJournalTitle() {
    return journalTitle;
  }
  public void setJournalTitle(String journalTitle) {
    this.journalTitle = journalTitle;
  }

  /**
   * Provides the Archival Unit provider name from the TDB.
   * 
   * @return a String with the provider name from the TDB.
   */
  public String getTdbProvider() {
    return tdbProvider;
  }
  public void setTdbProvider(String tdbProvider) {
    this.tdbProvider = tdbProvider;
  }

  @Override
  public String toString() {
    return "[AuWsResult auId=" + auId + ", name=" + name + ", volume=" + volume
	+ ", pluginName=" + pluginName + ", tdbYear=" + tdbYear
	+ ", accessType=" + accessType + ", contentSize=" + contentSize
	+ ", diskUsage=" + diskUsage + ", repositoryPath=" + repositoryPath
	+ ", recentPollAgreement=" + recentPollAgreement
	+ ", highestPollAgreement=" + highestPollAgreement
	+ ", publishingPlatform=" + publishingPlatform + ", tdbPublisher="
	+ tdbPublisher + ", availableFromPublisher=" + availableFromPublisher
	+ ", substanceState=" + substanceState + ", creationTime="
	+ creationTime + ", crawlProxy=" + crawlProxy + ", crawlWindow="
	+ crawlWindow + ", crawlPool=" + crawlPool + ", lastCompletedCrawl="
	+ lastCompletedCrawl + ", lastCrawl=" + lastCrawl
	+ ", lastCrawlResult=" + lastCrawlResult
	+ ", lastCompletedDeepCrawl=" + lastCompletedDeepCrawl
	+ ", lastDeepCrawl=" + lastDeepCrawl
	+ ", lastDeepCrawlResult=" + lastDeepCrawlResult
	+ ", lastCompletedDeepCrawlDepth=" + lastCompletedDeepCrawlDepth
	+ ", lastMetadataIndex=" + lastMetadataIndex
        + ", lastCompletedPoll=" + lastCompletedPoll
	+ ", lastPoll=" + lastPoll + ", lastPollResult=" + lastPollResult
	+ ", currentlyCrawling=" + currentlyCrawling + ", currentlyPolling="
	+ currentlyPolling + ", subscriptionStatus=" + subscriptionStatus
	+ ", auConfiguration=" + auConfiguration + ", newContentCrawlUrls="
	+ newContentCrawlUrls + ", urlStems=" + urlStems + ", isBulkContent="
	+ isBulkContent + ", peerAgreements=" + peerAgreements + ", urls="
	+ urls + ", substanceUrls=" + substanceUrls + ", articleUrls="
	+ articleUrls + ", journalTitle=" + journalTitle + ", tdbProvider="
	+ tdbProvider + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuWsResult that = (AuWsResult) o;
    return Objects.equals(auId, that.auId) &&
        Objects.equals(name, that.name) &&
        Objects.equals(volume, that.volume) &&
        Objects.equals(pluginName, that.pluginName) &&
        Objects.equals(tdbYear, that.tdbYear) &&
        Objects.equals(accessType, that.accessType) &&
        Objects.equals(contentSize, that.contentSize) &&
        Objects.equals(diskUsage, that.diskUsage) &&
        Objects.equals(repositoryPath, that.repositoryPath) &&
        Objects.equals(recentPollAgreement, that.recentPollAgreement) &&
        Objects.equals(highestPollAgreement, that.highestPollAgreement) &&
        Objects.equals(publishingPlatform, that.publishingPlatform) &&
        Objects.equals(tdbPublisher, that.tdbPublisher) &&
        Objects.equals(availableFromPublisher, that.availableFromPublisher) &&
        Objects.equals(substanceState, that.substanceState) &&
        Objects.equals(creationTime, that.creationTime) &&
        Objects.equals(crawlProxy, that.crawlProxy) &&
        Objects.equals(crawlWindow, that.crawlWindow) &&
        Objects.equals(crawlPool, that.crawlPool) &&
        Objects.equals(lastCompletedCrawl, that.lastCompletedCrawl) &&
        Objects.equals(lastCrawl, that.lastCrawl) &&
        Objects.equals(lastCrawlResult, that.lastCrawlResult) &&
        Objects.equals(lastCompletedDeepCrawl, that.lastCompletedDeepCrawl) &&
        Objects.equals(lastDeepCrawl, that.lastDeepCrawl) &&
        Objects.equals(lastDeepCrawlResult, that.lastDeepCrawlResult) &&
        Objects.equals(lastCompletedDeepCrawlDepth, that.lastCompletedDeepCrawlDepth) &&
        Objects.equals(lastMetadataIndex, that.lastMetadataIndex) &&
        Objects.equals(lastCompletedPoll, that.lastCompletedPoll) &&
        Objects.equals(lastPoll, that.lastPoll) &&
        Objects.equals(lastPollResult, that.lastPollResult) &&
        Objects.equals(currentlyCrawling, that.currentlyCrawling) &&
        Objects.equals(currentlyPolling, that.currentlyPolling) &&
        Objects.equals(subscriptionStatus, that.subscriptionStatus) &&
        Objects.equals(auConfiguration, that.auConfiguration) &&
        Objects.equals(newContentCrawlUrls, that.newContentCrawlUrls) &&
        Objects.equals(urlStems, that.urlStems) &&
        Objects.equals(isBulkContent, that.isBulkContent) &&
        Objects.equals(peerAgreements, that.peerAgreements) &&
        Objects.equals(urls, that.urls) &&
        Objects.equals(substanceUrls, that.substanceUrls) &&
        Objects.equals(articleUrls, that.articleUrls) &&
        Objects.equals(journalTitle, that.journalTitle) &&
        Objects.equals(tdbProvider, that.tdbProvider);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auId, name, volume, pluginName, tdbYear, accessType, contentSize, diskUsage, repositoryPath,
        recentPollAgreement, highestPollAgreement, publishingPlatform, tdbPublisher, availableFromPublisher,
        substanceState, creationTime, crawlProxy, crawlWindow, crawlPool, lastCompletedCrawl, lastCrawl,
        lastCrawlResult, lastCompletedDeepCrawl, lastDeepCrawl, lastDeepCrawlResult, lastCompletedDeepCrawlDepth,
        lastMetadataIndex, lastCompletedPoll, lastPoll, lastPollResult, currentlyCrawling, currentlyPolling,
        subscriptionStatus, auConfiguration, newContentCrawlUrls, urlStems, isBulkContent, peerAgreements, urls,
        substanceUrls, articleUrls, journalTitle, tdbProvider);
  }
}
