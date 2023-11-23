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

/**
 * Container for the information related to a plugin that is the result of a
 * query.
 */
public class PluginWsResult {
  private String pluginId;
  private String name;
  private String version;
  private String type;
  private Map<String, String> definition;
  private String registry;
  private String url;
  private Integer auCount;
  private String publishingPlatform;

  /**
   * Provides the plugin identifier.
   * 
   * @return A String with the identifier.
   */
  public String getPluginId() {
    return pluginId;
  }
  public void setPluginId(String pluginId) {
    this.pluginId = pluginId;
  }

  /**
   * Provides the plugin name.
   * 
   * @return A String with the name.
   */
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Provides the plugin version.
   * 
   * @return A String with the version.
   */
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Provides the plugin type.
   * 
   * @return A String with the type.
   */
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Provides the plugin definition properties.
   * 
   * @return a {@code Map<String, String>} with the properties.
   */
  public Map<String, String> getDefinition() {
    return definition;
  }
  public void setDefinition(Map<String, String> definition) {
    this.definition = definition;
  }

  /**
   * Provides the plugin registry name.
   * 
   * @return A String with the registry name.
   */
  public String getRegistry() {
    return registry;
  }
  public void setRegistry(String registry) {
    this.registry = registry;
  }

  /**
   * Provides the plugin URL.
   * 
   * @return A String with the URL.
   */
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Provides the count of Archival Units configured with this plugin.
   * 
   * @return An Integer with the count.
   */
  public Integer getAuCount() {
    return auCount;
  }
  public void setAuCount(Integer auCount) {
    this.auCount = auCount;
  }

  /**
   * Provides the plugin publishing platform name.
   * 
   * @return a String with the publishing platform name.
   */
  public String getPublishingPlatform() {
    return publishingPlatform;
  }
  public void setPublishingPlatform(String publishingPlatform) {
    this.publishingPlatform = publishingPlatform;
  }

  @Override
  public String toString() {
    return "PluginWsResult [pluginId=" + pluginId + ", name=" + name
	+ ", version=" + version + ", type=" + type + ", definition="
	+ definition + ", registry=" + registry + ", url=" + url + ", auCount="
	+ auCount + ", publishingPlatform=" + publishingPlatform + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PluginWsResult that = (PluginWsResult) o;
    return Objects.equals(pluginId, that.pluginId) &&
        Objects.equals(name, that.name) &&
        Objects.equals(version, that.version) &&
        Objects.equals(type, that.type) &&
        Objects.equals(definition, that.definition) &&
        Objects.equals(registry, that.registry) &&
        Objects.equals(url, that.url) &&
        Objects.equals(auCount, that.auCount) &&
        Objects.equals(publishingPlatform, that.publishingPlatform);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pluginId, name, version, type, definition, registry, url, auCount, publishingPlatform);
  }
}
