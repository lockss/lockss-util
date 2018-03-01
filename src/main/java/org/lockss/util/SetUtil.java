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
import java.util.function.*;

/**
 * <p>
 * {@link Set} utilities.
 * </p>
 * 
 * @since 1.1.0
 */
public class SetUtil {
  
  /**
   * <p>
   * Prevents instantiation.
   * </p>
   * 
   * @since 1.1.0
   */
  private SetUtil() {
    // Prevent instantiation
  }

  /**
   * <p>
   * Creates a modifiable set from any number of arguments.
   * </p>
   * 
   * @param elements
   *          A succession of elements (possibly zero).
   * @return A modifiable {@link Set} of those elements (an empty set if no
   *         elements).
   * @param <T>
   *          The type of element contained in the set.
   * @since 1.1.0
   * @see CollectionUtil2#collection(Supplier, Object...)
   */
  public static <T> Set<T> set(T... elements) {
    return CollectionUtil2.collection(HashSet::new, elements);
  }
  
  /**
   * <p>
   * Checks that all elements of the set are of the specified type, and returns
   * an unmodifiable copy of the set, with null elements disallowed.
   * </p>
   * 
   * @param set
   *          The input set.
   * @param type
   *          The class with which all items of the set must be
   *          assignment-compatible.
   * @param <T>
   *          The type of element contained in the set.
   * @throws NullPointerException
   *           if the set is null or if any element is null.
   * @throws ClassCastException
   *           if an item is not of the proper type.
   * @since 1.1.0
   * @see #immutableSetOfType(Set, Class, boolean)
   */
  public static <T> Set<T> immutableSetOfType(Set<?> set, Class<T> type) {
    return immutableSetOfType(set, type, false);
  }

  /**
   * <p>
   * Checks that all elements of the set are of the specified type, and returns
   * an unmodifiable copy of the set, with null elements allowed.
   * </p>
   * 
   * @param set
   *          The input set.
   * @param type
   *          The class with which all items of the set must be
   *          assignment-compatible.
   * @param <T>
   *          The type of element contained in the set.
   * @throws NullPointerException
   *           if the set is null
   * @throws ClassCastException
   *           if an item is not of the proper type.
   * @since 1.1.0
   * @see #immutableSetOfType(Set, Class, boolean)
   */
  public static <T> Set<T> immutableSetOfTypeOrNull(Set<?> set, Class<T> type) {
    return immutableSetOfType(set, type, true);
  }

  /**
   * <p>
   * Checks that all elements of the set are of the specified type, and returns
   * an unmodifiable copy of the set, with null elements either allowed or
   * disallowed.
   * </p>
   * 
   * @param set
   *          The input set.
   * @param type
   *          The class with which all items of the set must be
   *          assignment-compatible.
   * @param nullOk
   *          Whether null elements are allowed.
   * @param <T>
   *          The type of element contained in the set.
   * @throws NullPointerException
   *           if the set is null or if any element is null.
   * @throws ClassCastException
   *           if an item is not of the proper type.
   * @since 1.1.0
   * @see CollectionUtil2#immutableCollectionOfType(IntFunction, UnaryOperator, Collection, Class, boolean)
   */
  private static <T> Set<T> immutableSetOfType(Set<?> set,
                                               Class<T> type,
                                               boolean nullOk) {
    return CollectionUtil2.immutableCollectionOfType((IntFunction<Set<T>>)HashSet::new,
                                                    Collections::unmodifiableSet,
                                                    set,
                                                    type,
                                                    nullOk);
  }
  
  /**
   * <p>
   * Now that {@link #set(Object...)} is generic, this method does
   * exactly the same thing and simply calls it.
   * </p>
   * 
   * @param array
   *          An array of objects.
   * @param <T>
   *          The type of element contained in the set.
   * @return A modifiable {@link Set} made of the objects in the array.
   * @since 1.1.0
   * @deprecated Use {@link #set(Object...)} instead.
   * @see #set(Object...)
   */
  @Deprecated
  public static <T> Set<T> fromArray(T[] array) {
    return set(array);
  }

  /**
   * <p>
   * Create a set containing the elements of the given collection.
   * </p>
   * 
   * @param coll
   *    A collection.
   * @param <T>
   *          The type of element contained in the set.
   * @return A set made from the elements of the given collection.
   * @since 1.1.0
   * @see HashSet#HashSet(Collection)
   */
  public static <T> Set<T> theSet(Collection<T> coll) {
    return new HashSet<T>(coll);
  }

  /**
   * <p>
   * Create a set containing the elements of the given list.
   * </p>
   * 
   * @param list
   *    A list.
   * @param <T>
   *          The type of element contained in the set.
   * @return A set made from the elements of the given list.
   * @since 1.1.0
   * @see #theSet(Collection)
   */
  public static <T> Set<T> fromList(List<T> list) {
    return theSet(list);
  }

  /**
   * <p>
   * Creates a set containing the elements of the given iterator.
   * </p>
   * 
   * @param iterator
   *          An iterator.
   * @param <T>
   *          The type of element contained in the set.
   * @return A set built from consuming the iterator.
   * @since 1.1.0
   * @see CollectionUtil2#fromIterator(Supplier, Iterator)
   */
  public static <T> Set<T> fromIterator(Iterator<T> iterator) {
    return CollectionUtil2.fromIterator(HashSet::new, iterator);
  }

  /**
   * <p>
   * Creates a set containing the elements of the given iterable.
   * </p>
   * 
   * @param iterable
   *          An iterable.
   * @param <T>
   *          The type of element contained in the set.
   * @return A set built from the elements in the iterable.
   * @since 1.1.0
   * @see CollectionUtil2#fromIterable(Supplier, Iterable)
   */
  public static <T> Set<T> fromIterable(Iterable<T> iterable) {
    return fromIterator(iterable.iterator());
  }
  
  /**
   * <p>
   * Creates a set containing the elements of a comma separated string, with
   * the caveat that the processing is done by {@link StringTokenizer} with the
   * delimiter <code>","</code>.
   * <p>
   * <p>
   * {@link StringTokenizer} simplistically looks for separators without a
   * quoting mechanism to return tokens containing the delimiter, nor does it
   * trim whitespace between tokens and delimiters. It also does not return
   * empty tokens, so calling this method with <code>",,,"</code> will return an
   * empty set, not a set of four empty strings.
   * </p>
   *
   * @param csv
   *          A simplistic CSV string.
   * @return A set of tokens as separated by commas in the given input string.
   * @since 1.1.0
   * @see CollectionUtil2#fromCsvStringTokenizer(Supplier, String)
   **/
  public static Set<String> fromCSV(String csv) {
    return CollectionUtil2.fromCsvStringTokenizer(HashSet::new, csv);
  }
}
