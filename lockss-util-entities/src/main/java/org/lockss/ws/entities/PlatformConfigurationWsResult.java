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

/**
 * The daemon platform configuration information.
 */
public class PlatformConfigurationWsResult {

  private String hostName;
  private String ipAddress;
  private List<String> groups;
  private String project;
  private String v3Identity;
  private String mailRelay;
  private String adminEmail;
  private List<String> disks;
  private long currentTime;
  private long uptime;
  private DaemonVersionWsResult daemonVersion;
  private JavaVersionWsResult javaVersion;
  private PlatformWsResult platform;
  private String currentWorkingDirectory;
  private List<String> properties;
  private String buildHost;
  private long buildTimestamp;

  /**
   * Provides the host name.
   * 
   * @return a String with the host name.
   */
  public String getHostName() {
    return hostName;
  }
  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  /**
   * Provides the IP address.
   * 
   * @return a String with the IP address.
   */
  public String getIpAddress() {
    return ipAddress;
  }
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  /**
   * Provides the groups.
   * 
   * @return a {@code List<String>} with the groups.
   */
  public List<String> getGroups() {
    return groups;
  }
  public void setGroups(List<String> groups) {
    this.groups = groups;
  }

  /**
   * Provides the project name.
   * 
   * @return a String with the project name.
   */
  public String getProject() {
    return project;
  }
  public void setProject(String project) {
    this.project = project;
  }

  /**
   * Provides the V3 identity.
   * 
   * @return a String with the V3 identity.
   */
  public String getV3Identity() {
    return v3Identity;
  }
  public void setV3Identity(String v3Identity) {
    this.v3Identity = v3Identity;
  }

  /**
   * Provides the name of the mail relay.
   * 
   * @return a String with the mail relay name.
   */
  public String getMailRelay() {
    return mailRelay;
  }
  public void setMailRelay(String mailRelay) {
    this.mailRelay = mailRelay;
  }

  /**
   * Provides the administrative email account name.
   * 
   * @return a String with the administrative email account name.
   */
  public String getAdminEmail() {
    return adminEmail;
  }
  public void setAdminEmail(String adminEmail) {
    this.adminEmail = adminEmail;
  }

  /**
   * Provides the disk labels.
   * 
   * @return a {@code List<String>} with the disks labels.
   */
  public List<String> getDisks() {
    return disks;
  }
  public void setDisks(List<String> disks) {
    this.disks = disks;
  }

  /**
   * Provides the current timestamp.
   * 
   * @return a long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public long getCurrentTime() {
    return currentTime;
  }
  public void setCurrentTime(long currentTime) {
    this.currentTime = currentTime;
  }

  /**
   * Provides the server uptime.
   * 
   * @return a long with the uptime in millisecons.
   */
  public long getUptime() {
    return uptime;
  }
  public void setUptime(long uptime) {
    this.uptime = uptime;
  }

  /**
   * Provides the daemon version information.
   * 
   * @return a DaemonVersionWsResult with the daemon version.
   */
  public DaemonVersionWsResult getDaemonVersion() {
    return daemonVersion;
  }
  public void setDaemonVersion(DaemonVersionWsResult daemonVersion) {
    this.daemonVersion = daemonVersion;
  }

  /**
   * Provides the java version information.
   * 
   * @return a JavaVersionWsResult with the java version.
   */
  public JavaVersionWsResult getJavaVersion() {
    return javaVersion;
  }
  public void setJavaVersion(JavaVersionWsResult javaVersion) {
    this.javaVersion = javaVersion;
  }

  /**
   * Provides the platform information.
   * 
   * @return a PlatformWsResult with the platform information.
   */
  public PlatformWsResult getPlatform() {
    return platform;
  }
  public void setPlatform(PlatformWsResult platform) {
    this.platform = platform;
  }

  /**
   * Provides the current working directory.
   * 
   * @return a String with the current working directory.
   */
  public String getCurrentWorkingDirectory() {
    return currentWorkingDirectory;
  }
  public void setCurrentWorkingDirectory(String currentWorkingDirectory) {
    this.currentWorkingDirectory = currentWorkingDirectory;
  }

  /**
   * Provides the daemon properties.
   * 
   * @return a {@code List<String>} with the properties.
   */
  public List<String> getProperties() {
    return properties;
  }
  public void setProperties(List<String> properties) {
    this.properties = properties;
  }

  /**
   * Provides the build host name.
   * 
   * @return a String with the build host name.
   */
  public String getBuildHost() {
    return buildHost;
  }
  public void setBuildHost(String buildHost) {
    this.buildHost = buildHost;
  }

  /**
   * Provides the build timestamp.
   * 
   * @return a long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public long getBuildTimestamp() {
    return buildTimestamp;
  }
  public void setBuildTimestamp(long buildTimestamp) {
    this.buildTimestamp = buildTimestamp;
  }

  @Override
  public String toString() {
    return "[PlatformConfigurationWsResult hostName=" + hostName
	+ ", ipAddress=" + ipAddress + ", groups=" + groups + ", project="
	+ project + ", v3Identity=" + v3Identity + ", mailRelay=" + mailRelay
	+ ", adminEmail=" + adminEmail + ", disks=" + disks + ", currentTime="
	+ currentTime + ", uptime=" + uptime
        + ", daemonVersion=" + daemonVersion
        + ", javaVersion=" + javaVersion
        + ", platform=" + platform
	+ ", currentWorkingDirectory=" + currentWorkingDirectory
	+ ", properties=" + properties + ", buildHost=" + buildHost
	+ ", buildTimestamp=" + buildTimestamp + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PlatformConfigurationWsResult that = (PlatformConfigurationWsResult) o;
    return currentTime == that.currentTime &&
        uptime == that.uptime &&
        buildTimestamp == that.buildTimestamp &&
        Objects.equals(hostName, that.hostName) &&
        Objects.equals(ipAddress, that.ipAddress) &&
        Objects.equals(groups, that.groups) &&
        Objects.equals(project, that.project) &&
        Objects.equals(v3Identity, that.v3Identity) &&
        Objects.equals(mailRelay, that.mailRelay) &&
        Objects.equals(adminEmail, that.adminEmail) &&
        Objects.equals(disks, that.disks) &&
        Objects.equals(daemonVersion, that.daemonVersion) &&
        Objects.equals(javaVersion, that.javaVersion) &&
        Objects.equals(platform, that.platform) &&
        Objects.equals(currentWorkingDirectory, that.currentWorkingDirectory) &&
        Objects.equals(properties, that.properties) &&
        Objects.equals(buildHost, that.buildHost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hostName, ipAddress, groups, project, v3Identity, mailRelay, adminEmail, disks, currentTime,
        uptime, daemonVersion, javaVersion, platform, currentWorkingDirectory, properties, buildHost, buildTimestamp);
  }
}
