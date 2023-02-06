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

package org.lockss.util;

import java.util.*;
import java.util.function.*;

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
   * @param <T>
   *          The type of element contained in the list.
   * @return A modifiable {@link List} of those elements (an empty list if no
   *         elements).
   * @since 1.0.0
   * @see CollectionUtil2#collection(Supplier, Object...)
   */
  public static <T> List<T> list(T... elements) {
    return CollectionUtil2.collection(ArrayList::new, elements);
  }
  
  /* NOTE: The following non-varargs methods cannot be removed because
   * external plugin code relies on them.  It must stay until either
   * the plugins are modified to call something else, or binary
   * compatibility is provided through some other mechanism.
   */

  /**
   * Create list from arg list. */
  private static <T> List<T> list1(T object1) {
      List<T> l = new ArrayList<>();
      l.add(object1);
      return l;
  }

  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1) {
      List<T> l = new ArrayList<>(1);
      l.add(object1);
      return l;
  }

  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1,
                                 T object2) {
      List<T> l = list1(object1);
      l.add(object2);
      return l;
  }

  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1,
                                 T object2,
                                 T object3) {
      List<T> l = list(object1, object2);
      l.add(object3);
      return l;
  }

  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1,
                                 T object2,
                                 T object3,
                                 T object4) {
      List<T> l = list(object1, object2, object3);
      l.add(object4);
      return l;
  }

  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1,
                                 T object2,
                                 T object3,
                                 T object4,
                                 T object5) {
      List<T> l = list(object1, object2, object3, object4);
      l.add(object5);
      return l;
  }

  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1,
                                 T object2,
                                 T object3,
                                 T object4,
                                 T object5,
                                 T object6) {
      List<T> l = list(object1, object2, object3, object4, object5);
      l.add(object6);
      return l;
  }
  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1,
                                 T object2,
                                 T object3,
                                 T object4,
                                 T object5,
                                 T object6,
                                 T object7) {
      List<T> l = list(object1, object2, object3, object4, object5,
                       object6);
      l.add(object7);
      return l;
  }
  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1,
                                 T object2,
                                 T object3,
                                 T object4,
                                 T object5,
                                 T object6,
                                 T object7,
                                 T object8) {
      List<T> l = list(object1, object2, object3, object4, object5,
                       object6, object7);
      l.add(object8);
      return l;
  }
  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1,
                                 T object2,
                                 T object3,
                                 T object4,
                                 T object5,
                                 T object6,
                                 T object7,
                                 T object8,
                                 T object9) {
      List<T> l = list(object1, object2, object3, object4, object5,
                       object6, object7, object8);
      l.add(object9);
      return l;
  }
  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1,
                                 T object2,
                                 T object3,
                                 T object4,
                                 T object5,
                                 T object6,
                                 T object7,
                                 T object8,
                                 T object9,
                                 T object10) {
      List<T> l = list(object1, object2, object3, object4, object5,
                       object6, object7, object8, object9);
      l.add(object10);
      return l;
  }

  /**
   * Create list from arg list. */
  public static <T> List<T> list(T object1,
                                 T object2,
                                 T object3,
                                 T object4,
                                 T object5,
                                 T object6,
                                 T object7,
                                 T object8,
                                 T object9,
                                 T object10,
                                 T object11) {
      List<T> l = list(object1, object2, object3, object4, object5,
                       object6, object7, object8, object9, object10);
      l.add(object11);
      return l;
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
   * @param <T>
   *          The type of element contained in the list.
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
   * Now that {@link #list(Object...)} is generic, this method does exactly the
   * same thing and simply calls it.
   * </p>
   * 
   * @param array
   *          An array of objects.
   * @param <T>
   *          The type of element contained in the list.
   * @return A modifiable {@link List} made of the objects in the array.
   * @since 1.0.0
   * @deprecated Use {@link #list(Object...)} instead.
   * @see #list(Object...)
   */
  @Deprecated
  public static <T> List<T> fromArray(T[] array) {
    return list(array);
  }
  
  /**
   * <p>
   * Prepends the elements of <code>ofList</code> to the linked list
   * <code>toList</code>, creating a new linked list from <code>ofList</code> if
   * <code>toList</code> is null.
   * </p>
   * <p>
   * If <code>toList</code> is null, the result is a new linked list made from
   * <code>ofList</code>. If <code>ofList</code> is null,
   * </p>
   * 
   * @param ofList
   *          The list whose elements are prepended.
   * @param toList
   *          The list to which elements are prepended.
   * @param <T>
   *          The type of element contained in the list.
   * @return A linked list with the elements of the first prepended to the
   *         elements of the second.
   * @since 1.0.0
   * @see LinkedList#addFirst(Object)
   **/
  public static <T> LinkedList<T> prependAll(List<T> ofList, LinkedList<T> toList) {
    if (toList == null) {
      return ofList == null ? new LinkedList<T>() : new LinkedList<T>(ofList);
    }
    if (ofList == null) {
      return toList;
    }
    List<T> revOfList = reverseCopy(ofList);
    for (T item : revOfList) {
      toList.addFirst(item);
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
   * @param <T>
   *          The type of element contained in the list.
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
   * @param <T>
   *          The type of element contained in the list.
   * @return A list built from consuming the iterator.
   * @since 1.0.0
   * @see CollectionUtil2#fromIterator(Supplier, Iterator)
   */
  public static <T> List<T> fromIterator(Iterator<T> iterator) {
    return CollectionUtil2.fromIterator(ArrayList::new, iterator);
  }

  /**
   * <p>
   * Creates a list containing the elements of the given iterable.
   * </p>
   * 
   * @param iterable
   *          An iterable.
   * @param <T>
   *          The type of element contained in the list.
   * @return A list built from the elements in the iterable.
   * @since 1.0.0
   * @see CollectionUtil2#fromIterable(Supplier, Iterable)
   */
  public static <T> List<T> fromIterable(Iterable<T> iterable) {
    return CollectionUtil2.fromIterable(ArrayList::new, iterable);
  }

  /**
   * <p>
   * Creates a list containing the elements of a comma separated string, with
   * the caveat that the processing is done by {@link StringTokenizer} with the
   * delimiter <code>","</code>.
   * </p>
   * <p>
   * {@link StringTokenizer} simplistically looks for separators without a
   * quoting mechanism to return tokens containing the delimiter, nor does it
   * trim whitespace between tokens and delimiters. It also does not return
   * empty tokens, so calling this method with <code>",,,"</code> will return an
   * empty list, not a list of four empty strings.
   * </p>
   *
   * @param csv
   *          A simplistic CSV string.
   * @return A list of tokens as separated by commas in the given input string.
   * @since 1.0.0
   * @see CollectionUtil2#fromCsvStringTokenizer(Supplier, String)
   **/
  public static List<String> fromCSV(String csv) {
    return CollectionUtil2.fromCsvStringTokenizer(ArrayList::new, csv);
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
   * @param <T>
   *          The type of element contained in the list.
   * @return An immutable copy of the list.
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
   * @param <T>
   *          The type of element contained in the list.
   * @return An immutable copy of the list.
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
   * @param <T>
   *          The type of element contained in the list.
   * @return An immutable copy of the list.
   * @throws NullPointerException
   *           if the list is null or if any element is null.
   * @throws ClassCastException
   *           if an item is not of the proper type.
   * @since 1.0.0
   */
  protected static <T> List<T> immutableListOfType(List<?> list,
                                                   Class<T> type,
                                                   boolean nullOk) {
    return CollectionUtil2.immutableCollectionOfType((IntFunction<List<T>>)ArrayList::new,
                                                    Collections::unmodifiableList,
                                                    list,
                                                    type,
                                                    nullOk);
  }

  /**
   * <p>
   * Returns a copy of the list, with elements in reverse order.
   * </p>
   * 
   * @param list
   *          The list to reverse.
   * @param <T>
   *          The type of element contained in the list.
   * @return A new list with elements in reverse order from the original list.
   * @since 1.0.0
   * @see Collections#reverse(List)
   */
  public static <T> List<T> reverseCopy(List<T> list) {
    List<T> ret = new ArrayList<T>(list);
    Collections.reverse(ret);
    return ret;
  }
}
