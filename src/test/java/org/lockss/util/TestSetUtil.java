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

import org.apache.commons.collections4.iterators.ArrayIterator;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * Tests the {@link SetUtil} class.
 * </p>
 * 
 * @since 1.1.0
 * @see SetUtil
 */
public class TestSetUtil {
  
  private Set<String> s1;

  @BeforeEach
  public void setUp() {
    s1 = new HashSet<String>(Arrays.asList("1", "2", "4"));
  }

  @Test
  public void testArgs() {
    assertEquals(s1, SetUtil.set("1", "2", "4"));
  }

  @Test
  public void testEmpty() {
    assertEquals(Collections.emptySet(), SetUtil.set());
    assertEquals(Collections.emptySet(), SetUtil.theSet(Collections.emptyList()));
  }

  @Test
  public void testFromArray() {
    String[] arr = {"1", "2", "4"};
    assertEquals(s1, SetUtil.fromArray(arr));
    assertEquals(SetUtil.set(arr), SetUtil.fromArray(arr));
  }

  @Test
  public void testFromCSV() {
    String csv = "1,2,4";
    assertEquals(s1, SetUtil.fromCSV(csv));
  }

  @Test
  public void testFromIterator() {
    String arr[] = {"1", "2", "4"};
    assertEquals(s1, SetUtil.fromIterator(new ArrayIterator<String>(arr)));
  }

  @Test
  public void testTheSet() {
    Set<String> s2 = SetUtil.theSet(ListUtil.list("1", "2", "3"));
    assertTrue(s2 instanceof Set);
    assertEquals(3, s2.size());
    assertTrue(s2.contains(new String("1")));
    assertTrue(s2.contains("2"));
    assertTrue(s2.contains("3"));
  }

  @Test
  public void testImmutableSetOfType() {
    String[] arr = {"1", "2", "4"};
    Set<String> s2 = SetUtil.set(arr);
    Set<String> s3 = SetUtil.immutableSetOfType(s2, String.class);
    assertEquals(s2, s3);
    s2.add("21");
    assertEquals(s2.size(), s3.size() + 1);
    assertEquals(SetUtil.set(arr), s3);
    assertThrows(UnsupportedOperationException.class, () -> s3.add("d"));
  }

  @Test
  public void testImmutableSetOfSuperType() {
    Set<List> s2 = SetUtil.set(new ArrayList(), new LinkedList());
    Set<List> s3 = SetUtil.immutableSetOfType(s2, List.class);
    assertEquals(s2, s3);
    Set<Throwable> s4 = SetUtil.set(new Error(), new LinkageError());
    Set<Throwable> s5 = SetUtil.immutableSetOfType(s4, Throwable.class);
    assertEquals(s4, s5);
  }

  @Test
  public void testImmutableSetOfWrongType() {
    Set<?> s0 = SetUtil.set("foo", "bar", new Integer(7));
    assertThrows(ClassCastException.class, () -> SetUtil.immutableSetOfType(s0, String.class));
    Set<Integer> s2 = SetUtil.set(new Integer(4), null);
    assertEquals(s2, SetUtil.immutableSetOfTypeOrNull(s2, Integer.class));
    assertThrows(NullPointerException.class, () -> SetUtil.immutableSetOfType(s2, Integer.class));
  }

}
