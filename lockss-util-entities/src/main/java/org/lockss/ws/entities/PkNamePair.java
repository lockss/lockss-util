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

/**
 * A primary key/name pair.
 * 
 * @author Fernando Garcia-Loygorri
 */
public class PkNamePair implements Comparable<PkNamePair> {
  private Long pk;
  private String name;

  /**
   * Default constructor.
   */
  public PkNamePair() {
  }

  /**
   * Constructor.
   * 
   * @param pk
   *          A Long with the primary key.
   * @param name
   *          A String with the name.
   */
  public PkNamePair(Long pk, String name) {
    this.pk = pk;
    this.name = name;
  }

  public Long getPk() {
    return pk;
  }
  public void setPk(Long pk) {
    this.pk = pk;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Provides an indication of whether two of these objects are the same.
   * 
   * @param one
   *          An object with the first object.
   * @param other
   *          An object with the other object.
   * @return a boolean with <code>true</code> if both objects are the same,
   *         <code>false</code> otherwise.
   */
  private static boolean equals(Object one, Object other) {
    return (one == null && other == null) || (one != null && one.equals(other));
  }

  /**
   * Provides an indication of whether some other object is equal to this one.
   * 
   * @param other
   *          An object with which to compare.
   * @return a boolean with <code>true</code> if both objects are the same,
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Object other) {
    return other instanceof PkNamePair && equals(pk, ((PkNamePair) other).pk) &&
      equals(name, ((PkNamePair) other).name);
  }

  /**
   * Provides a hash code value for the object.
   * 
   * @return an int with the hash code value for this object.
   */
  @Override
  public int hashCode() {
    if (pk == null) {
      if (name == null) return 643;
      return name.hashCode() + 1;
    }
    if (name == null) return pk.hashCode() + 2;
    return pk.hashCode() * name.hashCode();
  }

  /**
   * Compares this object with the specified object for order. Returns a
   * negative integer, zero, or a positive integer as this object is less than,
   * equal to, or greater than the specified object.
   * 
   * @param other
   *          A PkNamePair with the other object used in the comparison.
   * @return an int that is negative, zero, or positive as this object is to be
   *         sorted before, the same or after the other object.
   */
  @Override
  public int compareTo(PkNamePair other) {
    // Emptier objects appear earlier in the sort.
    if (other == null) {
      return 1;
    }

    int result = name.compareTo(other.getName());

    if (result == 0) {
      result = pk.compareTo(other.getPk());
    }

    return result;
  }

  @Override
  public String toString() {
    return "[PkNamePair pk=" + pk + ", name=" + name + "]";
  }
}
