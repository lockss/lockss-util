/*

 Copyright (c) 2019-2020 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.util.storage;

import java.io.Serializable;
import java.util.List;
import org.lockss.util.os.PlatformUtil;

/**
 * Information about a storage area, such as used and free space
 */
public class StorageInfo implements Serializable {

  private String type;   // Currently just informative: "disk", "memory", etc.
  private String name;   // Indentifying name such as mount point
  private String path;   // Path, if applicable
  private long sizeKB = -1; // Size in KB of the storage area.
  private long usedKB = -1; // Size in KB of the used storage area.
  private long availKB = -1; // Size in KB of the available storage area
  private String percentUsedString;
  private double percentUsed = -1.0;
  private List<StorageInfo> components;	// Storage areas that comprise this one

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
