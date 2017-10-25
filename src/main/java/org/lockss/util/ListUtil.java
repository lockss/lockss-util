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

/**
 * <p>
 * {@link List} utilities.
 * </p>
 * 
 * @since 1.0.0
 */
public class ListUtil {

  /**
   * <p>
   * Prevents instantiation.
   * </p>
   * 
   * @since 1.0.0
   */
  private ListUtil() {
    // Prevent instantiation
  }
  
  /**
   * <p>
   * Creates a modifiable list from any number of arguments.
   * </p>
   * <p>
   * {@link Arrays#asList(Object...)} differs from this in that it returns a
   * fixed-size list backed by the varargs array.
   * </p>
   * 
   * @param elements
   *          A succession of elements (possibly zero).
   * @return A modifiable {@link List} of those elements (an empty list if no
   *         items).
   * @since 1.0.0
   */
  public static <T> List<T> list(T... elements) {
    ArrayList<T> ret = new ArrayList<T>();
    if (elements != null) {
      CollectionUtils.addAll(ret, elements);
    }
    return ret;
  }
  
  /**
   * <p>
   * Makes a new, single list made of the elements of all the given lists
   * concatenated together. If a given list is null, it is ignored. If no input
   * lists are given (taking null lists into account), the result is an empty
   * list.
   * </p>
   * 
   * @param lists
   *          A succession of lists (possibly zero).
   * @return A new, single {@link List} made of the elements of all the non-null
   *         lists concatenated together.
   * @since 1.0.0
   */
  public static <T> List<T> append(List<T>... lists) {
    List<T> ret = new ArrayList<T>();
    if (lists != null) {
      for (List<T> list : lists) {
        if (list != null) {
          ret.addAll(list);
        }
      }
    }
    return ret;
  }
  
  /**
   * <p>
   * Now that {@link #list(Object...)} is generic, this method does
   * exactly the same thing and simply calls it.
   * </p>
   * 
   * @param array
   *          An array of objects.
   * @return A modifiable {@link List} made of the objects in the array.
   * @since 1.0.0
   * @deprecated Use {@link #list(Object...)} instead.
   * @see #list(Object...)
   */
  @Deprecated
  public static <T> List<T> fromArray(T[] array) {
    return list(array);
  }
  
  /** Add all elements of ofList to toList  */
  public static LinkedList prependAll(List ofList, LinkedList toList) {
    if (ofList == null) {
      if (toList == null) {
	return new LinkedList();
      } else {
	return toList;
      }
    }
    if (toList == null) {
      return new LinkedList(ofList);
    }
    List revOfList = new ArrayList(ofList);
    Collections.reverse(revOfList);
    for (Iterator iter = revOfList.iterator(); iter.hasNext(); ) {
      toList.addFirst(iter.next());
    }
    return toList;
  }

  /**
   * <p>
   * Returns a trimmed {@link ArrayList} equal to the given {@link List}
   * instance.
   * </p>
   * 
   * @param lst
   *          A list.
   * @return An {@link ArrayList} instance of the same elements, trimmed to the
   *         size of the given list. If the input list is itself an
   *         {@link ArrayList} instance, that instance is trimmed an returned
   *         without allocating a new instance.
   * @since 1.0.0
   * @see ArrayList#ArrayList(Collection)
   * @see ArrayList#trimToSize()
   */
  public static <T> ArrayList<T> minimalArrayList(List<T> lst) {
    if (lst instanceof ArrayList) {
      ArrayList<T> alst = (ArrayList<T>)lst;
      alst.trimToSize();
      return alst;
    }
    else {
      return new ArrayList<T>(lst);
    }
  }

  /**
   * <p>
   * Creates a list containing the elements of the given iterator.
   * </p>
   * <p>
   * The preferred way of doing this is with
   * {@link IteratorUtils#toList(Iterator)}, which behaves exactly the same way
   * as this method.
   * </p>
   * 
   * @param iterator
   *          An iterator.
   * @return A list built from consuming the iterator.
   * @since 1.0.0 Collections instead.
   * @see IteratorUtils#toList(Iterator)
   */
  public static <T> List<T> fromIterator(Iterator<T> iterator) {
    return IteratorUtils.toList(iterator);
  }

  /**
   * <p>
   * Creates a list containing the elements of the given iterable.
   * </p>
   * <p>
   * The preferred way of doing this is with
   * {@link IterableUtils#toList(Iterable)}, which behaves exactly the same way
   * as this method.
   * </p>
   * 
   * @param iterable
   *          An iterable.
   * @return A list built from the elements in the iterable.
   * @since 1.0.0
   * @see IterableUtils#toList(Iterable)
   */
  public static <T> List<T> fromIterable(Iterable<T> iterable) {
    return IterableUtils.toList(iterable);
  }

  /** Create a list containing the elements of a comma separated string */
  public static List<String> fromCSV(String csv) {
    List<String> ret = list();
    StringTokenizer st = new StringTokenizer(csv, ",");
    while (st.hasMoreTokens()) {
      ret.add(st.nextToken());
    }
    return ret;
  }

  /**
   * <p>
   * Checks that all elements of the list are of the specified type, and returns
   * an unmodifiable copy of the list, with null elements disallowed.
   * </p>
   * 
   * @param list
   *          The input list.
   * @param type
   *          The class with which all items of the list must be
   *          assignment-compatible.
   * @throws NullPointerException
   *           if the list is null or if any element is null.
   * @throws ClassCastException
   *           if an item is not of the proper type.
   * @since 1.0.0
   * @see #immutableListOfType(List, Class, boolean)
   */
  public static <T> List<T> immutableListOfType(List<?> list,
                                                Class<T> type) {
    return immutableListOfType(list, type, false);
  }

  /**
   * <p>
   * Checks that all elements of the list are of the specified type, and returns
   * an unmodifiable copy of the list, with null elements allowed.
   * </p>
   * 
   * @param list
   *          The input list.
   * @param type
   *          The class with which all items of the list must be
   *          assignment-compatible.
   * @throws NullPointerException
   *           if the list is null
   * @throws ClassCastException
   *           if an item is not of the proper type.
   * @since 1.0.0
   * @see #immutableListOfType(List, Class, boolean)
   */
  public static <T> List<T> immutableListOfTypeOrNull(List<?> list,
                                                      Class<T> type) {
    return immutableListOfType(list, type, true);
  }

  /**
   * <p>
   * Checks that all elements of the list are of the specified type, and returns
   * an unmodifiable copy of the list, with null elements either allowed or
   * disallowed.
   * </p>
   * 
   * @param list
   *          The input list.
   * @param type
   *          The class with which all items of the list must be
   *          assignment-compatible.
   * @param nullOk
   *          Whether null elements are allowed.
   * @throws NullPointerException
   *           if the list is null or if any element is null.
   * @throws ClassCastException
   *           if an item is not of the proper type.
   * @since 1.0.0
   * @see #immutableListOfType(List, Class, boolean)
   */
  private static <T> List<T> immutableListOfType(List<?> list,
                                                 Class<T> type,
					         boolean nullOk) {
    List<T> ret = new ArrayList<T>(list.size());
    int index = 0;
    for (Object item : list) {
      if (item == null) {
	if (!nullOk) {
	  throw new NullPointerException(String.format("Item at index %d is null", index));
	}
      }
      else if (!type.isInstance(item)) {
        throw new ClassCastException(String.format("Item at index %d is not of type %s: %s",
                                                   index, type.getName(), item.getClass().getName()));
      }
      ret.add(type.cast(item));
      ++index;
    }
    return Collections.unmodifiableList(ret);
  }

  /**
   * <p>
   * Returns a copy of the list, with elements in reverse order.
   * </p>
   * 
   * @param list The list to reverse.
   * @return A new list with elements in reverse order from the original list.
   * @since 1.0.0
   */
  public static <T> List<T> reverseCopy(List<T> list) {
    List<T> ret = new ArrayList<T>(list);
    Collections.reverse(ret);
    return ret;
  }
}
