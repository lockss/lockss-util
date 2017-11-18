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
import java.util.function.*;

/**
 * <p>
 * {@link Collection} utilities.
 * </p>
 * 
 * @since 1.1.0
 */
public class CollectionUtil2 {

  /**
   * <p>
   * Creates a new collection from any number of arguments.
   * </p>
   * 
   * @param funcEmptyCollection
   *          A {@link Supplier} functor returning a brand new collection.
   * @param elements
   *          A succession of elements (possibly zero).
   * @param <T>
   *          The type of element contained in the collection.
   * @param <C>
   *          The type of collection.
   * @return A collection of those elements (an empty collection if no
   *         elements).
   */
  protected static <T, C extends Collection<T>> C collection(Supplier<C> funcEmptyCollection,
                                                             T... elements) {
    C ret = funcEmptyCollection.get();
    if (elements != null) {
      Collections.addAll(ret, elements);
    }
    return ret;
  }
  
  /**
   * <p>
   * Checks that all elements of the collection are of the specified type, and
   * returns an unmodifiable copy of the collection, with null elements either
   * allowed or disallowed.
   * </p>
   * 
   * @param funcEmptyCollectionOfSize
   *          An {@link IntFunction} functor that accepts a size and produces an
   *          empty collection on that size (e.g.
   *          {@link ArrayList#ArrayList(int)}, {@link HashSet#HashSet(int)}).
   * @param funcImmutableCollection
   *          A {@link UnaryOperator} functor that accepts a collection and
   *          returns an unmodifiable view of it (e.g.
   *          {@link Collections#unmodifiableList(List)},
   *          {@link Collections#unmodifiableSet(Set)}).
   * @param coll
   *          The input collection.
   * @param type
   *          The class with which all items of the collection must be
   *          assignment-compatible.
   * @param nullOk
   *          Whether null elements are allowed.
   * @param <T>
   *          The type of element contained in the collection.
   * @param <C>
   *          The type of collection.
   * @return An immutable copy of the collection.
   * @throws NullPointerException
   *           if the collection is null or if any element is null.
   * @throws ClassCastException
   *           if an item is not of the proper type.
   * @since 1.1.0
   */
  protected static <T, C extends Collection<T>> C immutableCollectionOfType(IntFunction<C> funcEmptyCollectionOfSize,
                                                                            UnaryOperator<C> funcImmutableCollection,
                                                                            Collection<?> coll,
                                                                            Class<T> type,
                                                                            boolean nullOk) {
    C ret = funcEmptyCollectionOfSize.apply(coll.size());
    for (Object element : coll) {
      if (element == null) {
        if (!nullOk) {
          throw new NullPointerException("Null elements not allowed");
        }
      }
      else if (!type.isInstance(element)) {
        throw new ClassCastException(String.format("Element not of type %s: %s", type.getName(), element.getClass().getName()));
      }
      ret.add(type.cast(element));
    }
    return funcImmutableCollection.apply(ret);
  }

  /**
   * <p>
   * Creates a collection containing the elements of the given iterator.
   * </p>
   * 
   * @param funcEmptyCollection
   *          A {@link Supplier} functor returning a brand new collection.
   * @param iterator
   *          An iterator.
   * @param <T>
   *          The type of element contained in the collection.
   * @param <C>
   *          The type of collection.
   * @return A collection built from consuming the iterator.
   * @since 1.1.0
   */
  protected static <T, C extends Collection<T>> C fromIterator(Supplier<C> funcEmptyCollection,
                                                               Iterator<T> iterator) {
    C ret = funcEmptyCollection.get();
    while (iterator.hasNext()) {
      ret.add(iterator.next());
    }
    return ret;
  }
  
  /**
   * <p>
   * Creates a collection containing the elements of the given iterable.
   * </p>
   * 
   * @param funcEmptyCollection
   *          A {@link Supplier} functor returning a brand new collection.
   * @param iterable
   *          An iterable.
   * @param <T>
   *          The type of element contained in the collection.
   * @param <C>
   *          The type of collection.
   * @return A collection built from consuming the iterable.
   * @since 1.1.0
   */
  protected static <T, C extends Collection<T>> C fromIterable(Supplier<C> funcEmptyCollection,
                                                               Iterable<T> iterable) {
    C ret = funcEmptyCollection.get();
    if (iterable != null) {
      for (T element : iterable) {
        ret.add(element);
      }
    }
    return ret;
  }
  
  /**
   * <p>
   * Creates a collection containing the elements of a comma separated string, with
   * the caveat that the processing is done by {@link StringTokenizer} with the
   * delimiter <code>","</code>.
   * </p>
   * <p>
   * {@link StringTokenizer} simplistically looks for separators without a
   * quoting mechanism to return tokens containing the delimiter, nor does it
   * trim whitespace between tokens and delimiters. It also does not return
   * empty tokens, so calling this method with <code>",,,"</code> will return an
   * empty collection, not a collection of four empty strings.
   * </p>
   *
   * @param funcEmptyCollection
   *          A {@link Supplier} functor that generates an empty collection.
   * @param csv
   *          A simplistic CSV string.
   * @param <C>
   *          The type of collection.
   * @return A collection of tokens as separated by commas in the given input string.
   * @since 1.1.0
   * @see CollectionUtil2#fromCsvStringTokenizer(Supplier, String)
   **/
  protected static <C extends Collection<String>> C fromCsvStringTokenizer(Supplier<C> funcEmptyCollection,
                                                                           String csv) {
    C ret = funcEmptyCollection.get();
    StringTokenizer st = new StringTokenizer(csv, ",");
    while (st.hasMoreTokens()) {
       ret.add(st.nextToken());
    }
    return ret;
  }

}
