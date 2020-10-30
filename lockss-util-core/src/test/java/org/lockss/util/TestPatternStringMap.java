/*

Copyright (c) 2000-2020 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Except as contained in this notice, the name of Stanford University shall not
be used in advertising or otherwise to promote the sale, use or other dealings
in this Software without prior written authorization from Stanford University.

*/

package org.lockss.util;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.lockss.util.*;
import org.lockss.util.test.*;

public class TestPatternStringMap extends LockssTestCase5 {

  @Test
  public void testToString() {
    PatternStringMap ppm1 = new PatternStringMap("a.*b,2;ccc,foo");
    assertEquals("[pm: [a.*b: 2], [ccc: foo]]", ppm1.toString());
  }

  @Test
  public void testGetMatch() {
    PatternStringMap ppm1 = new PatternStringMap("a.*b,foo;ccc,bar");
    assertEquals(null, ppm1.getMatch("a123c"));
    assertEquals("df", ppm1.getMatch("a123c", "df"));
    assertEquals("foo", ppm1.getMatch("a123b"));
    assertEquals("foo", ppm1.getMatch("accccb"));
    assertEquals("bar", ppm1.getMatch("bccccb"));
    assertEquals("bar", ppm1.getMatch("bccccb", "not"));
  }

  @Test
  public void testEmpty() {
    PatternStringMap ppm = PatternStringMap.EMPTY;
    assertEquals(null, ppm.getMatch("a"));
    assertEquals("42", ppm.getMatch("a", "42"));
    assertEquals("[pm: EMPTY]", ppm.toString());
  }

  @Test
  public void testIsEmpty() {
    assertTrue(PatternStringMap.EMPTY.isEmpty());
    assertTrue(new PatternStringMap("").isEmpty());
    assertFalse(new PatternStringMap("a.*b,2;ccc,xxx").isEmpty());
  }

  @Test
  public void testIll() {
    try {
      PatternStringMap ppm1 = new PatternStringMap("a[)2");
      fail("Should throw: Malformed");
    } catch (IllegalArgumentException e) {
      assertMatchesRE("no comma", e.getMessage());
    }
    try {
      PatternStringMap ppm1 = new PatternStringMap("a,1;bbb");
      fail("Should throw: Malformed");
    } catch (IllegalArgumentException e) {
      assertMatchesRE("no comma", e.getMessage());
    }
    try {
      PatternStringMap ppm1 = new PatternStringMap("a[),2");
      fail("Should throw: illegal regexp");
    } catch (IllegalArgumentException e) {
      assertMatchesRE("Illegal regexp", e.getMessage());
    }
  }

}
