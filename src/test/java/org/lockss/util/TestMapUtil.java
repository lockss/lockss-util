/*

Copyright (c) 2000, Board of Trustees of Leland Stanford Jr. University
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

import static org.junit.jupiter.api.Assertions.*;

public class TestMapUtil {
  
  @Test
  public void testGoodArgs() {
    assertEquals(0, MapUtil.map().size());
    String[] arr = {"a", "1", "b", "2", "c", "3", "d", "4", "e", "5",
                    "f", "6", "g", "7", "h", "8", "j", "9", "k", "10"};
    Map<Object, Object> exp = new HashMap<Object, Object>();
    for (int ix = 0 ; ix < arr.length ; ) {
      exp.put(arr[ix++], arr[ix++]);
    }
    assertEquals(exp, MapUtil.map(arr));
    assertEquals(exp, MapUtil.map(String.class, String.class, arr));
  }
  
  @Test
  public void testBadArgs() {
    assertThrows(IllegalArgumentException.class, () -> MapUtil.map(1, 2, 3));
    assertThrows(NullPointerException.class, () -> MapUtil.map(null, String.class, "x", "y"));
    assertThrows(NullPointerException.class, () -> MapUtil.map(String.class, null, "x", "y"));
    assertThrows(ClassCastException.class, () -> MapUtil.map(String.class, String.class, 1, "y"));
    assertThrows(ClassCastException.class, () -> MapUtil.map(String.class, String.class, "x", 2));
  }

  @Test
  public void testFromList() {
    assertEquals(MapUtil.map(),
                 MapUtil.fromList(ListUtil.list()));
    assertEquals(MapUtil.map("FOO", "bar", "One", "Two"),
		 MapUtil.fromList(ListUtil.list("FOO", "bar", "One", "Two")));
    assertEquals(MapUtil.map("foo", "bar", "one", "two"),
		 MapUtil.fromList(ListUtil.list(ListUtil.list("foo", "bar"),
						ListUtil.list("one", "two"))));
    assertThrows(IllegalArgumentException.class,
                 () -> MapUtil.fromList(ListUtil.list("FOO", "bar", "One")));
    assertThrows(IllegalArgumentException.class,
                 () -> MapUtil.fromList(ListUtil.list(ListUtil.list("foo", "bar"),
                                                      ListUtil.list("one"))));
  }

  @Test
  public void testExpandMultiKeys() {
    assertEquals(MapUtil.map(),
		 MapUtil.expandAlternativeKeyLists(MapUtil.map()));
    assertEquals(MapUtil.map("1", "A"),
		 MapUtil.expandAlternativeKeyLists(MapUtil.map("1", "A")));
    assertEquals(MapUtil.map("1", "A", "2", "A"),
		 MapUtil.expandAlternativeKeyLists(MapUtil.map("1;2", "A")));
    assertEquals(MapUtil.map("1", "A", "2", "A"),
		 MapUtil.expandAlternativeKeyLists(MapUtil.map("1 ; 2", "A")));
    assertEquals(MapUtil.map("1", "A", "2", "B", "*", "B"),
		 MapUtil.expandAlternativeKeyLists(MapUtil.map("1", "A", "2;*", "B")));
    assertEquals(MapUtil.map("1", "A", "2", "B", "*", "B"),
		 MapUtil.expandAlternativeKeyLists(MapUtil.map(" 1 ", "A", "2;; *", "B")));
  }

}
