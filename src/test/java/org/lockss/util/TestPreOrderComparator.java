/*

Copyright (c) 2000-2018, Board of Trustees of Leland Stanford Jr. University
All rights reserved.

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

package org.lockss.util;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.lockss.util.test.LockssTestCase5;

/**
 * @since 1.5.0
 * @see PreOrderComparator
 */
public class TestPreOrderComparator extends LockssTestCase5 {

  public static int compareToNullHigh(String str1, String str2) {
    if (str1 == null) {
      return (str2 == null) ? 0 : 1;
    }
    if (str2 == null) {
      return -1;
    }
    return str1.compareTo(str2);
  }
  
  public void assertPreOrderCompareTo(String s1, String s2) {
    assertTrue(PreOrderComparator.preOrderCompareTo(s1, s2) < 0);
    assertTrue(PreOrderComparator.preOrderCompareTo(s2, s1) > 0);
  }

  public void assertPreOrderCompareToNullHigh(String s1, String s2) {
    assertTrue(PreOrderComparator.preOrderCompareToNullHigh(s1, s2) < 0);
    assertTrue(PreOrderComparator.preOrderCompareToNullHigh(s2, s1) > 0);
  }

  @Test
  public void testPreOrderCompareTo() {
    assertEquals(0, PreOrderComparator.preOrderCompareTo("", ""));
    assertEquals(0, PreOrderComparator.preOrderCompareTo("a", "a"));
    assertEquals(0, PreOrderComparator.preOrderCompareTo("uni-\u00eb-code",
                                                             "uni-\u00eb-code"));
    assertEquals(0, PreOrderComparator.preOrderCompareTo("1/", "1/"));
    assertEquals(0, PreOrderComparator.preOrderCompareTo("1/2.3", "1/2.3"));
    assertPreOrderCompareTo("a", "b");
    assertPreOrderCompareTo("", "1");
    assertPreOrderCompareTo("abc", "abc/");
    assertPreOrderCompareTo("abc", "abc.");

    // This is where compteToSlashFirst differs from natural String order
    assertFalse("a/".compareTo("a.") < 0);
    assertPreOrderCompareTo("a/", "a.");
    assertFalse("a/b".compareTo("a.b") < 0);
    assertPreOrderCompareTo("a/b", "a.b");
  }

  @Test
  public void testCompareToSlashFirstNullHigh() {
    assertEquals(0, PreOrderComparator.preOrderCompareToNullHigh("", ""));
    assertEquals(0, PreOrderComparator.preOrderCompareToNullHigh("a", "a"));
    assertEquals(0, PreOrderComparator.preOrderCompareToNullHigh(null, null));
    assertEquals(0, PreOrderComparator.preOrderCompareToNullHigh("1/2.3", "1/2.3"));
    assertPreOrderCompareToNullHigh("a/b", null);
    assertPreOrderCompareToNullHigh("a.b", null);

    assertTrue(compareToNullHigh("a/", "a.") > 0);
    assertPreOrderCompareToNullHigh("a/", "a.");
    assertTrue(compareToNullHigh("a/b", "a.b") > 0);
    assertPreOrderCompareToNullHigh("a/b", "a.b");
  }

  @Test
  public void testPreOrderComparator() {
    Collection<String> coll = new TreeSet<String>(PreOrderComparator.INSTANCE);
    coll.addAll(ListUtil.list("http://foo:80/",
                              "http://foo/a/b.c",
                              "http://foo/a/b/c",
                              "",
                              "http://foo/a.b/c",
                              "http://foo/a/b",
                              "http://foo/a/",
                              "http://foo/"));
    assertThat(coll, contains("",
                              "http://foo/",
                              "http://foo/a/",
                              "http://foo/a/b",
                              "http://foo/a/b/c",
                              "http://foo/a/b.c",
                              "http://foo/a.b/c",
                              "http://foo:80/"));
  }
  
}
