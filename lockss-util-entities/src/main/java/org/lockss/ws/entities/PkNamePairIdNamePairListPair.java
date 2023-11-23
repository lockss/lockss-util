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

/**
 * A PK-name-pair/id-name-pair-list pair.
 */
public class PkNamePairIdNamePairListPair {
  private PkNamePair key;
  private List<IdNamePair> values;

  /**
   * Default constructor.
   */
  public PkNamePairIdNamePairListPair() {
  }

  /**
   * Constructor.
   * 
   * @param key
   *          A PkNamePair with the key.
   * @param values
   *          A {@code List<IdNamePair>} with the values.
   */
  public PkNamePairIdNamePairListPair(PkNamePair key, List<IdNamePair> values) {
    this.key = key;
    this.values = values;
  }

  /**
   * Provides the key of the pair.
   * 
   * @return a PkNamePair with the key.
   */
  public PkNamePair getKey() {
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

  public void setKey(PkNamePair key) {
    this.key = key;
  }

  public void setValues(List<IdNamePair> values) {
    this.values = values;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[PkNamePairIdNamePairListPair key=");
    builder.append(key);
    builder.append(", values=");
    builder.append(values);
    builder.append("]");
    return builder.toString();
  }
}
