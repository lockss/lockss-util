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

/**
 * <p>
 * {@link Map} utilities.
 * </p>
 * 
 * @since 1.1.0
 */
public class MapUtil {

  /**
   * <p>
   * Prevents instantiation.
   * </p>
   * 
   * @since 1.0.0
   */
  private MapUtil() {
    // Prevent instantiation
  }

  protected static abstract class KeyValuePairIterator<K, V> {
    protected Class<K> keyType;
    protected Class<V> valueType;
    protected Iterator<?> iterator;
    
    public KeyValuePairIterator(Class<K> keyType,
                                Class<V> valueType,
                                Iterator<?> iterator) {
      this.keyType = keyType;
      this.valueType = valueType;
      this.iterator = iterator;
    }
    
    public boolean hasNextKeyValuePair() {
      return iterator.hasNext(); 
    }
    
    public abstract K nextKey();
    public abstract V nextValue();
  }
  
  protected static class SequentialKeyValuePairIterator<K, V> extends KeyValuePairIterator<K, V> {

    public SequentialKeyValuePairIterator(Class<K> keyType,
                                          Class<V> valueType,
                                          Iterator<?> iterator) {
      super(keyType, valueType, iterator);
    }
    
    @Override
    public K nextKey() {
      return keyType.cast(iterator.next());
    }

    @Override
    public V nextValue() {
      try {
        return valueType.cast(iterator.next());
      }
      catch (NoSuchElementException nsee) {
        throw new IllegalArgumentException("Even number of arguments required");
      }
    }
    
  }
  
  protected static class ListOfListsKeyValuePairIterator<K, V> extends KeyValuePairIterator<K, V> {

    protected List<?> sub;
    
    public ListOfListsKeyValuePairIterator(Class<K> keyType,
                                           Class<V> valueType,
                                           Iterator<?> iterator) {
      super(keyType, valueType, iterator);
    }
    
    @Override
    public K nextKey() {
      sub = (List<?>)iterator.next();
      if (sub.size() != 2) {
        throw new IllegalArgumentException("Sublist is not of size 2");
      }
      return keyType.cast(sub.get(0));
    }

    @Override
    public V nextValue() {
      return valueType.cast(sub.get(1));
    }
    
  }
    
  /**
   * Create a map from any number of pairs of arguments. */
  public static <K, V> Map<K, V> map(Object... elements) {
    return (Map<K, V>)map(Object.class, Object.class, elements);
  }

  public static <K, V> Map<K, V> map(Class<K> keyType,
                                     Class<V> valueType,
                                     Object... elements) {
    if (elements.length % 2 != 0) {
      throw new IllegalArgumentException("Even number of arguments required");
    }
    KeyValuePairIterator<K, V> iter = new SequentialKeyValuePairIterator<K, V>(keyType, valueType, new ArrayIterator<Object>(elements));
    Map<K, V> map = new HashMap<K, V>();
    while (iter.hasNextKeyValuePair()) {
      map.put(iter.nextKey(), iter.nextValue());
    }
    return map;
  }
  
  /** Return a map with keys and values taken from alternating list
   * elements (<code>[key1, val1, key2, val2, ...]</code>), or from
   * sublists (<code>[ [key1, val1], [key2, val2], ...]</code>) */
  public static <K, V> Map<K, V> fromList(List<?> keyValuePairs) {
    return (Map<K, V>)fromList(Object.class, Object.class, keyValuePairs);
  }

  public static <K, V> Map<K, V> fromList(Class<K> keyType,
                                          Class<V> valueType,
                                          List<?> keyValuePairs) {
    Map<K, V> map = new HashMap<K, V>();
    if (keyValuePairs.isEmpty()) {
      return map;
    }
    KeyValuePairIterator<K, V> iter =
        keyValuePairs.get(0) instanceof List
        ? new ListOfListsKeyValuePairIterator<K, V>(keyType, valueType, keyValuePairs.iterator())
        : new SequentialKeyValuePairIterator<K, V>(keyType, valueType, keyValuePairs.iterator());
    while (iter.hasNextKeyValuePair()) {
      map.put(iter.nextKey(), iter.nextValue());
    }
    return map;
  }

  /** Returns a copy of the map, treating keys as semicolon-separated list
   * of alternative actual keys.  (<i>Eg</i>, the map [k1 =&gt; v1, k2;k3 =&gt;
   * v2] is transformed into [k1 =&gt; v1, k2 =&gt; v2, k3 =&gt; v2].  The keys are
   * trimmed.
   */
  public static <V> Map<String, V> expandAlternativeKeyLists(Map<String, V> map) {
    Map<String, V> res = new HashMap<String, V>();
    for (Map.Entry<String, V> ent : map.entrySet()) {
      String multiKey = ent.getKey();
      V val = ent.getValue();
      for (String key : multiKey.split(";")) {
        String trimmed = key.trim();
        if (trimmed.length() > 0) {
          res.put(key.trim(), val);
        }
      }
    }
    return res;
  }
}
