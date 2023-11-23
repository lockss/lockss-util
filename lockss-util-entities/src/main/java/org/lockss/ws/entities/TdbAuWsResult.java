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

import java.util.Map;
import java.util.Objects;
import com.fasterxml.jackson.annotation.*;

/**
 * Container for the information related to a title database archival unit that
 * is the result of a query.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TdbAuWsResult {
  private String auId;
  private String name;
  private String pluginName;
  private TdbTitleWsResult tdbTitle;
  private TdbPublisherWsResult tdbPublisher;
  private Boolean down;
  private Boolean active;
  private Map<String, String> params;
  private Map<String, String> attrs;
  private Map<String, String> props;

  public String getAuId() {
    return auId;
  }
  public void setAuId(String auId) {
    this.auId = auId;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getPluginName() {
    return pluginName;
  }
  public void setPluginName(String pluginName) {
    this.pluginName = pluginName;
  }
  public TdbTitleWsResult getTdbTitle() {
    return tdbTitle;
  }
  public void setTdbTitle(TdbTitleWsResult tdbTitle) {
    this.tdbTitle = tdbTitle;
  }
  public TdbPublisherWsResult getTdbPublisher() {
    return tdbPublisher;
  }
  public void setTdbPublisher(TdbPublisherWsResult tdbPublisher) {
    this.tdbPublisher = tdbPublisher;
  }
  public Boolean getDown() {
    return down;
  }
  public void setDown(Boolean down) {
    this.down = down;
  }
  public Boolean getActive() {
    return active;
  }
  public void setActive(Boolean active) {
    this.active = active;
  }
  public Map<String, String> getParams() {
    return params;
  }
  public void setParams(Map<String, String> params) {
    this.params = params;
  }
  public Map<String, String> getAttrs() {
    return attrs;
  }
  public void setAttrs(Map<String, String> attrs) {
    this.attrs = attrs;
  }
  public Map<String, String> getProps() {
    return props;
  }
  public void setProps(Map<String, String> props) {
    this.props = props;
  }

  @Override
  public String toString() {
    return "TdbAuWsResult [auId=" + auId + ", name=" + name + ", pluginName="
	+ pluginName + ", tdbTitle=" + tdbTitle + ", tdbPublisher="
	+ tdbPublisher + ", down=" + down + ", active=" + active + ", params="
	+ params + ", attrs=" + attrs + ", props=" + props + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TdbAuWsResult that = (TdbAuWsResult) o;
    return Objects.equals(auId, that.auId) &&
        Objects.equals(name, that.name) &&
        Objects.equals(pluginName, that.pluginName) &&
        Objects.equals(tdbTitle, that.tdbTitle) &&
        Objects.equals(tdbPublisher, that.tdbPublisher) &&
        Objects.equals(down, that.down) &&
        Objects.equals(active, that.active) &&
        Objects.equals(params, that.params) &&
        Objects.equals(attrs, that.attrs) &&
        Objects.equals(props, that.props);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auId, name, pluginName, tdbTitle, tdbPublisher, down, active, params, attrs, props);
  }
}
