/*

Copyright (c) 2020 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util.rest.crawler;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lockss.log.L4JLogger;
import org.lockss.util.rest.RestBaseClient;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.ws.entities.CrawlWsResult;
import org.lockss.ws.entities.PollWsResult;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * A client of the Crawler REST service.
 * 
 * @author Fernando García-Loygorri
 */
public class RestCrawlerClient extends RestBaseClient<RestCrawlerClient> {
  private static L4JLogger log = L4JLogger.getLogger();

  /**
   * Constructor
   * 
   * @param serviceUrl A String with the information necessary to access the
   *                   REST Crawler web service.
   */
  public RestCrawlerClient(String serviceUrl) {
    super(RestCrawlerClient.class);
    setServiceUrl(serviceUrl);
  }

  /**
   * Sends to the Crawler REST service a request to perform a crawl.
   * 
   * @param crawlDesc A CrawlDesc with the description of the crawl to be
   *                  performed.
   * @return a CrawlJob with information regarding the job performing the crawl.
   * @throws LockssRestException if there were problems performing the crawl.
   */
  public CrawlJob callCrawl(CrawlDesc crawlDesc) throws LockssRestException {
    log.debug2("crawlDesc = {}", crawlDesc);

    try {
      // Make the REST call.
      log.trace("Calling RestUtil.callRestService");
      ResponseEntity<CrawlJob> response = callRestService("/jobs", null, null,
	    HttpMethod.POST, null, crawlDesc, CrawlJob.class, "Unable to schedule crawl");
      log.trace("Back from RestUtil.callRestService");

      CrawlJob result = response.getBody();
      log.debug2("result = {}", result);
      return result;
    } catch (RuntimeException e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Provides the selected properties of selected polls in the system.
   *
   * @param crawlQuery A String with the
   *                  <a href="package-summary.html#SQL-Like_Query">SQL-like
   *                  query</a> used to specify what properties to retrieve from
   *                  which polls.
   * @return a {@code List<CrawlWsResult>} with the results.
   * @throws LockssRestException if there were problems making the query.
   */
  public List<CrawlWsResult> queryCrawls(String crawlQuery)
      throws LockssRestException {
    log.debug2("crawlQuery = {}", crawlQuery);
    try {
      // Prepare the query parameters.
      Map<String, String> queryParams = new HashMap<>(1);
      queryParams.put("crawlQuery", crawlQuery);
      log.trace("queryParams = {}", queryParams);

      // Make the REST call.
      log.trace("Calling RestUtil.callRestService");
      ResponseEntity<String> response = callRestService("/crawls", null,
          queryParams, HttpMethod.GET, null, null, String.class,
          "Can't query crawls");
      log.trace("Back from RestUtil.callRestService");

      // Get the response body.
      List<CrawlWsResult> result = getJsonMapper().readValue(response.getBody(),
          new TypeReference<List<CrawlWsResult>>(){});

      log.debug2("result = {}", result);
      return result;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }
}
