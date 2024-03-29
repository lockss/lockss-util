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
import org.lockss.util.auth.AuthUtil;
import org.lockss.util.rest.RestBaseClient;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.ws.entities.CrawlWsResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * A client of the Crawler REST service.
 * 
 * @author Fernando García-Loygorri
 */
public class RestCrawlerClient extends RestBaseClient<RestCrawlerClient> {
  private static L4JLogger log = L4JLogger.getLogger();

  // The value of the Authorization header to be used when calling the REST
  // service.
  private String authHeaderValue = null;


  /**
   * Constructor that takes a base URL to a remote LOCKSS Repository service,
   * and without credentials..
   * 
   * @param serviceUrl A String with the information necessary to access the
   *                   REST Crawler web service.
   */
  public RestCrawlerClient(String serviceUrl) {
    this(serviceUrl, null, null);
  }

  /**
   * Constructor for a Rest Crawler Client without authorization.
   *
   * @param serviceUrl A String with the information necessary to access the
   *                   REST Crawler web service.
   * @param userName      A String with the name of the user used to access the
   *                      remote LOCKSS Crawler service.
   * @param password      A String with the password of the user used to access
   *                      the remote LOCKSS Crawler service.
   */
  public RestCrawlerClient(String serviceUrl,String userName, String password) {
    super(RestCrawlerClient.class);
    setServiceUrl(serviceUrl);
    // Check whether user credentials were passed.
    if (userName != null && password != null) {
      authHeaderValue = AuthUtil.basicAuthHeaderValue(userName, password);
    }

    log.trace("authHeaderValue = {}", authHeaderValue);
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
      // Initialize the request headers.
      HttpHeaders requestHeaders = getInitializedHttpHeaders();

      ResponseEntity<CrawlJob> response = callRestService("/jobs", null, null,
	  HttpMethod.POST, requestHeaders, crawlDesc, CrawlJob.class, "Can't call crawl");
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
  /**
   * Provides a new set of HTTP headers including the Authorization header, if
   * necessary.
   *
   * @return an HttpHeaders with the HTTP headers.
   */
  private HttpHeaders getInitializedHttpHeaders() {
    HttpHeaders result = new HttpHeaders();

    // Check whether the Authorization header needs to be included.
    if (authHeaderValue != null) {
      // Yes.
      result.add("Authorization", authHeaderValue);
    }

    return result;
  }

}
