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

import java.util.List;

/**
 * A key/id-name-pair-list pair.
 */
public class KeyIdNamePairListPair {
  private String key;
  private List<IdNamePair> values;

  /**
   * Default constructor.
   */
  public KeyIdNamePairListPair() {
  }

  /**
   * Constructor.
   * 
   * @param key
   *          A String with the key.
   * @param values
   *          A {@code List<IdNamePair>} with the values.
   */
  public KeyIdNamePairListPair(String key, List<IdNamePair> values) {
    this.key = key;
    this.values = values;
  }

  /**
   * Provides the key of the pair.
   * 
   * @return a String with the key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Provides the values of the pair.
   * 
   * @return a {@code {@code List<IdNamePair>}} with the values.
   */
  public List<IdNamePair> getValues() {
    return values;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setValues(List<IdNamePair> values) {
    this.values = values;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("KeyValueListPair [key=");
    builder.append(key);
    builder.append(", values=");
    builder.append(values);
    builder.append("]");
    return builder.toString();
  }
}
