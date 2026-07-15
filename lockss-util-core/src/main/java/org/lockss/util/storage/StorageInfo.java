/*

Copyright (c) 2000-2025 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.util.storage;

import java.io.Serializable;
import java.util.List;
import org.lockss.util.os.PlatformUtil;

/**
 * Information about a storage area, such as used and free space
 */
public class StorageInfo implements Serializable {

  private String type;                  // Type of the storage area
  private String name;                  // Name of the storage area
  private String path;                  // Path, if applicable
  private List<StorageInfo> components; // Storage areas that comprise this one
  private long sizeKB = -1;             // Size of the storage area (in KB)
  private long usedKB = -1;             // Size of the used storage area (in KB)
  private long availKB = -1;            // Size of the available storage area (in KB)
  private double percentUsed = -1.0;    // Percentage of the storage area used
  private String percentUsedString;     // Percentage of the storage area used, formatted as a string

  /**
   * Default constructor.
   */
  public StorageInfo() {
  }

  /** Create a StorageInfo representing the disk usage information in the
   * DF structure
   * @param df disk usage info from PlatformUtil
   * @return StorageInfo
   */
  public static StorageInfo fromDF(PlatformUtil.DF df) {
    return fromDF("disk", df);
  }

  /** Return bytes as KB, rounded */
  public static long toKBRounded(long bytes) {
    return (bytes + 512) / 1024;
  }

  /** Create a StorageInfo representing the JVM memory.  Not particularly
   * meaningful, provided so in-memory implementations have something easy
   * to return
   * @return StorageInfo
   */
  public static StorageInfo fromRuntime() {
    Runtime rt = Runtime.getRuntime();
    StorageInfo si = new StorageInfo("memory")
      .setAvailKB(toKBRounded(rt.freeMemory()))
      .setSizeKB(toKBRounded(rt.maxMemory()))
      .setUsedKB(toKBRounded(rt.totalMemory()));
    si.setPercentUsed((double)si.getUsedKB() / (double)si.getSizeKB());
    si.setPercentUsedString(Math.round(100 * si.getPercentUsed()) + "%");
    return si;
  }

  /** Create a StorageInfo representing the disk usage information in the
   * DF structure
   * @param type type string
   * @param df disk usage info from PlatformUtil
   * @return StorageInfo
   */
  public static StorageInfo fromDF(String type, PlatformUtil.DF df) {
    StorageInfo res = new StorageInfo(type);
    if (df != null) {
      res.name = df.getMnt();
      res.sizeKB = df.getSize();
      res.usedKB = df.getUsed();
      res.availKB = df.getAvail();
      res.percentUsedString = df.getPercentString();
      res.percentUsed = df.getPercent();
    }
    return res;
  }

  /** Create a StorageInfo containing only a type string
   * @param type type string
   * @return StorageInfo
   */
  public StorageInfo(String type) {
    this.type = type;
  }

  /** Return storage type: {@code disk}, {@code memory}, etc. */
  public String getType() {
    return type;
  }

  public StorageInfo setType(String type) {
    this.type = type;
    return this;
  }

  /** Return storage name, e.g., mount point */
  public String getName() {
    return name;
  }

  public StorageInfo setName(String name) {
    this.name = name;
    return this;
  }

  /** Return storage path or not if not applicable */
  public String getPath() {
    return path;
  }

  public StorageInfo setPath(String path) {
    this.path = path;
    return this;
  }

  /** Return total size in KB */
  public long getSizeKB() {
    return sizeKB;
  }

  public StorageInfo setSizeKB(long sizeKB) {
    this.sizeKB = sizeKB;
    return this;
  }

  /** Return used size in KB */
  public long getUsedKB() {
    return usedKB;
  }

  public StorageInfo setUsedKB(long usedKB) {
    this.usedKB = usedKB;
    return this;
  }

  /** Return available size in KB */
  public long getAvailKB() {
    return availKB;
  }

  public StorageInfo setAvailKB(long availKB) {
    this.availKB = availKB;
    return this;
  }

  /** Return percent used as a string: <code><i>nn<i>%</code> */
  public String getPercentUsedString() {
    return percentUsedString;
  }

  public StorageInfo setPercentUsedString(String percentUsedString) {
    this.percentUsedString = percentUsedString;
    return this;
  }

  /** Return percent used as a double between 0.0 and 1.0 */
  public double getPercentUsed() {
    return percentUsed;
  }

  public StorageInfo setPercentUsed(double percentUsed) {
    this.percentUsed = percentUsed;
    return this;
  }

  /** Return StorageInfo of component storage areas, or null. */
  public List<StorageInfo> getComponents() {
    return components;
  }

  public StorageInfo setComponents(List<StorageInfo> components) {
    this.components = components;
    return this;
  }

  /** Return true if on the same device as <i>other<i>.  I.e., if the name
   * and type are the same */
  public boolean isSameDevice(StorageInfo other) {
    if (other == null || name == null || type == null) {
      return false;
    }
    return type.equals(other.getType()) && name.equals(other.getName());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[StorageInfo");
    addIf(sb, "type", type);
    addIf(sb, "name", name);
    addIf(sb, "path", path);
    addIf(sb, "size", sizeKB, "KB");
    addIf(sb, "used", usedKB, "KB");
    addIf(sb, "avail", availKB, "KB");
    addIf(sb, "used %", percentUsedString);
    sb.append("]");
    return sb.toString();
  }

  private static void addIf(StringBuilder sb, String label, long val) {
    addIf(sb, label, val, null);
  }

  private static void addIf(StringBuilder sb, String label, long val,
                            String units) {
    if (val >= 0) {
      sb.append(" ");
      sb.append(label);
      sb.append(": ");
      sb.append(val);
      if (units != null) {
        sb.append(units);
      }
    }
  }

  private static void addIf(StringBuilder sb, String label, String val) {
    addIf(sb, label, val, null);
  }

  private static void addIf(StringBuilder sb, String label, String val,
                            String units) {
    if (val != null) {
      sb.append(" ");
      sb.append(label);
      sb.append(": ");
      sb.append(val);
      if (units != null) {
        sb.append(units);
      }
    }
  }
}
