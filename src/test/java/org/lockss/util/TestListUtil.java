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

import org.apache.commons.collections4.*;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * <p>
 * Tests the {@link ListUtil} class.
 * </p>
 * 
 * @since 1.0.0
 * @see ListUtil
 */
public class TestListUtil {

  private static void assertImmutable(List<?> lst) {
    try {
      lst.add(null);
      fail("Expected unmodifiable list but got modifiable list");
    }
    catch (UnsupportedOperationException expected) {
      // Expected
    }
  }
  
  private static void assertModifiable(List<?> lst) {
    try {
      lst.add(null);
    }
    catch (UnsupportedOperationException uoe) {
      fail("Expected modifiable list but got unmodifiable list");
    }
    lst.remove(lst.size() - 1);
  }

  private static <T> LinkedList<T> linkedList(T... elements) {
    return new LinkedList<T>(Arrays.asList(elements));
  }
  
  @Test
  public void testList() {
    List<String> ls1 = ListUtil.list();
    assertEquals(0, ls1.size());
    assertModifiable(ls1);
    
    List<String> ls2 = ListUtil.list((String)null);
    assertEquals(1, ls2.size());
    assertNull(ls2.get(0));
    assertModifiable(ls2);
    
    List<String> ls3 = ListUtil.list((String[])null);
    assertEquals(0, ls3.size());
    assertModifiable(ls3);
    
    List<String> ls4 = ListUtil.list(new String[] {});
    assertEquals(0, ls4.size());
    assertModifiable(ls4);
    
    String[] ar5 = {"foo", null, "baz"};
    List<String> ls5 = ListUtil.list(ar5);
    assertEquals(ar5.length, ls5.size());
    assertThat(ls5, contains(ar5));
    assertModifiable(ls5);

    String[] ar6 = {"foo", "bar", "baz"};
    List<String> ls6 = ListUtil.list(ar6);
    assertEquals(ar6.length, ls6.size());
    assertThat(ls6, contains(ar6));
    assertModifiable(ls6);
  }
  
  @Test
  public void testAppend() {
    assertThat(ListUtil.append(), empty());
    
    assertThat(ListUtil.append(null), empty());
    
    assertThat(ListUtil.append(null, null, null), empty());
    
    assertThat(ListUtil.append(ListUtil.list("1a", "1b", "1c"), ListUtil.list("2a"), ListUtil.list("3a", "3b")),
               contains("1a", "1b", "1c", "2a", "3a", "3b"));

    assertThat(ListUtil.append(ListUtil.list("1a", "1b", "1c"), null, ListUtil.list("3a", "3b")),
               contains("1a", "1b", "1c", "3a", "3b"));

    assertThat(ListUtil.append(ListUtil.list("1a", "1b", "1c"), ListUtil.list(), ListUtil.list("3a", "3b")), 
               contains("1a", "1b", "1c", "3a", "3b"));
  }
  
//  @Test
//  public void testFromArray() {
//    String arr[] = {"1", "2", "4"};
//    assertIsomorphic(arr, ListUtil.fromArray(arr));
//  }

  @Test
  public void testPrependAll() {
    assertThat(ListUtil.prependAll(null, null), empty());

    assertThat(ListUtil.prependAll(ListUtil.list(), null), empty());
    
    assertThat(ListUtil.prependAll(ListUtil.list("1", "2", "3"), null),
               contains("1", "2", "3"));

    assertThat(ListUtil.prependAll(null, linkedList()), empty());
    
    assertThat(ListUtil.prependAll(ListUtil.list(), linkedList()), empty());
    
    assertThat(ListUtil.prependAll(ListUtil.list("1", "2", "3"), linkedList()),
               contains("1", "2", "3"));
    
    assertThat(ListUtil.prependAll(null, linkedList("4", "5", "6")),
               contains("4", "5", "6"));
    
    assertThat(ListUtil.prependAll(ListUtil.list(), linkedList("4", "5", "6")),
               contains("4", "5", "6"));

    assertThat(ListUtil.prependAll(ListUtil.list("1", "2", "3"), linkedList("4", "5", "6")),
               contains("1", "2", "3", "4", "5", "6"));
  }
  
  @Test
  public void testMinimalArrayList() {
    List<String> lst1 = new ArrayList<>(4);
    lst1.addAll(Arrays.asList("1", "2", "3"));
    List<String> lst1constructor = new ArrayList<String>(lst1);
    List<String> lst1minimal = ListUtil.minimalArrayList(lst1);
    assertEquals(lst1minimal, lst1constructor);
    assertSame(lst1minimal, lst1);

    List<String> lst2 = new LinkedList<>();
    lst2.addAll(Arrays.asList("1", "2", "3"));
    ArrayList<String> lst2minimal = ListUtil.minimalArrayList(lst2);
    assertEquals(lst2, lst2minimal);
    assertThat(lst2minimal, isA(ArrayList.class));
  }
  
  @Test
  public void testFromIterator() {
    assertThrows(NullPointerException.class, () -> ListUtil.fromIterator(null));
    
    assertEquals(0, ListUtil.fromIterator(IteratorUtils.emptyIterator()).size());

    String[] arr = {"1", "2", "4"};
    assertEquals(ListUtil.list(arr),
		 ListUtil.fromIterator(new ArrayIterator<String>(arr)));
  }

  @Test
  public void testFromIterable() {
    assertEquals(0, ListUtil.fromIterable(null).size());
    
    assertEquals(0, ListUtil.fromIterable(IterableUtils.emptyIterable()).size());

    String[] arr = {"1", "2", "4"};
    assertEquals(ListUtil.list(arr),
                 ListUtil.fromIterable(IteratorUtils.asIterable(new ArrayIterator<String>(arr))));
  }

  @Test
  public void testFromCSV() {
    assertThrows(NullPointerException.class, () -> ListUtil.fromCSV(null));

    assertEquals(0, ListUtil.fromCSV("").size());

    assertEquals(0, ListUtil.fromCSV(",,,").size());

    String[] arr = {"1", "2", "4"};
    assertThat(ListUtil.fromCSV(String.join(",", arr)), contains(arr));
  }

//  @Test
//  public void testAppend() {
//    List l1 = ListUtil.list("1", "2", "3");
//    List l2 = ListUtil.list("a", "b", "c");
//    List l3 = ListUtil.list("1", "2", "3", "a", "b", "c");
//    assertEquals(Collections.EMPTY_LIST, ListUtil.append((List)null));
//    assertEquals(Collections.EMPTY_LIST,
//		 ListUtil.append((List)null, (List)null));
//    assertEquals(l3, ListUtil.append(l1, l2));
//  }

  @Test
  public void testImmutableListOfType() {
    String[] arr1 = {"1", "2", "4"};
    List<Object> l0 = ListUtil.list((Object[])arr1);
    List<String> l1 = ListUtil.immutableListOfType(l0, String.class);
    assertEquals(l0, l1);
    l0.add("21");
    assertEquals(l0.size(), l1.size() + 1);
    assertThat(l1, contains(arr1));
    assertImmutable(l1);
    
    List<Object> l2 = ListUtil.list(new Exception(), new LinkageError());
    List<Throwable> l3 = ListUtil.immutableListOfType(l2, Throwable.class);
    assertEquals(l2, l3);
    
    List<Object> l4 = ListUtil.list(new Integer(42), "foo", new Float(7.0f));
    assertThrows(ClassCastException.class, () -> ListUtil.immutableListOfType(l4, Integer.class));

    String[] arr2 = {"a", null, "c"};
    List<?> l5 = ListUtil.list(arr2);
    assertThrows(NullPointerException.class, () -> ListUtil.immutableListOfType(l5, String.class));
    assertThat(ListUtil.immutableListOfTypeOrNull(l5, String.class), contains(arr2));
  }

  @Test
  public void testReverseCopy() {
    assertEquals(0, ListUtil.reverseCopy(new ArrayList<Object>()).size());

    List<Object> ls1 = ListUtil.list("foo", null, new Integer(7));
    List<Object> rv1 = ListUtil.reverseCopy(ls1);
    assertNotSame(ls1, rv1);
    assertThat(rv1, contains(new Integer(7), null, "foo"));
  }

}
