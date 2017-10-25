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

import org.apache.commons.collections4.*;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * This is the test class for org.lockss.util.ListUtil
 */
public class TestListUtil {

  private void assertModifiable(List<?> lst) {
    try {
      lst.add(null);
    }
    catch (UnsupportedOperationException uoe) {
      fail("Unmodifable list");
    }
    lst.remove(lst.size() - 1);
  }
  
  @Test
  public void testList() {
    List<String> ls1 = ListUtil.list();
    assertEquals(0, ls1.size());
    assertModifiable(ls1);
    
    final String NULL_STRING = null;
    List<String> ls2 = ListUtil.list(NULL_STRING);
    assertEquals(1, ls2.size());
    assertNull(ls2.get(0));
    assertModifiable(ls2);
    
    final String[] NULL_ARRAY = null;
    List<String> ls3 = ListUtil.list(NULL_ARRAY);
    assertEquals(0, ls3.size());
    assertModifiable(ls3);
    
    final String[] EMPTY_ARRAY = {};
    List<String> ls4 = ListUtil.list(EMPTY_ARRAY);
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
    List<String> ls1 = ListUtil.append();
    assertEquals(0, ls1.size());
    
    List<String> ls2 = ListUtil.append(null);
    assertEquals(0, ls2.size());
    
    List<String> ls3 = ListUtil.append(null, null, null);
    assertEquals(0, ls3.size());
    
    List<String> ls4 = ListUtil.append(ListUtil.list("1a", "1b", "1c"), ListUtil.list("2a"), ListUtil.list("3a", "3b"));
    assertEquals(6, ls4.size());
    assertThat(ls4, contains("1a", "1b", "1c", "2a", "3a", "3b"));
    
    List<String> ls5 = ListUtil.append(ListUtil.list("1a", "1b", "1c"), null, ListUtil.list("3a", "3b"));
    assertEquals(5, ls5.size());
    assertThat(ls5, contains("1a", "1b", "1c", "3a", "3b"));

    List<String> ls6 = ListUtil.append(ListUtil.list("1a", "1b", "1c"), ListUtil.list(), ListUtil.list("3a", "3b"));
    assertEquals(5, ls6.size());
    assertThat(ls6, contains("1a", "1b", "1c", "3a", "3b"));
  }
  
//  @Test
//  public void testFromArray() {
//    String arr[] = {"1", "2", "4"};
//    assertIsomorphic(arr, ListUtil.fromArray(arr));
//  }

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

//  @Test
//  public void testFromCSV() {
//    String csv = "1,2,4";
//    String arr[] = {"1", "2", "4"};
//    assertIsomorphic(arr, ListUtil.fromCSV(csv));
//  }
//
//  LinkedList lList(List lst) {
//    return new LinkedList(lst);
//  }
//
//  @Test
//  public void testPrependAll() {
//    assertEquals(Collections.EMPTY_LIST,
//		 ListUtil.prependAll(null, (LinkedList)null));
//    assertEquals(ListUtil.list("1"),
//		 ListUtil.prependAll(ListUtil.list("1"), (LinkedList)null));
//    assertEquals(ListUtil.list("1"),
//		 ListUtil.prependAll(null, lList(ListUtil.list("1"))));
//    assertEquals(Collections.EMPTY_LIST,
//		 ListUtil.prependAll(Collections.EMPTY_LIST,
//				     lList(Collections.EMPTY_LIST)));
//    assertEquals(ListUtil.list("1"),
//		 ListUtil.prependAll(ListUtil.list("1"),
//				     lList(Collections.EMPTY_LIST)));
//    assertEquals(ListUtil.list("1"),
//		 ListUtil.prependAll(Collections.EMPTY_LIST,
//				     lList(ListUtil.list("1"))));
//    assertEquals(ListUtil.list("1", "2", "3"),
//		 ListUtil.prependAll(Collections.EMPTY_LIST,
//				     lList(ListUtil.list("1", "2", "3"))));
//    assertEquals(ListUtil.list("1", "2", "3"),
//		 ListUtil.prependAll(ListUtil.list("1", "2", "3"),
//				     lList(Collections.EMPTY_LIST)));
//    assertEquals(ListUtil.list("1", "2", "3"),
//		 ListUtil.prependAll(ListUtil.list("1"),
//				     lList(ListUtil.list("2", "3"))));
//    assertEquals(ListUtil.list("1", "2", "3"),
//		 ListUtil.prependAll(ListUtil.list("1", "2"),
//				     lList(ListUtil.list("3"))));
//  }
//
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
    assertThrows(UnsupportedOperationException.class, () -> l1.add("d"), "Should not be able to add to immutable list");
    
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
  
}
