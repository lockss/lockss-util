/*

 Copyright (c) 2019-2020 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util.rest;

import java.util.*;
import org.lockss.log.L4JLogger;
import org.lockss.util.*;
import org.lockss.util.test.*;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;

/**
 * Test class for org.lockss.util.rest.SpringHeaderUtil.
 */
public class TestSpringHeaderUtil extends LockssTestCase5 {
  private static L4JLogger log = L4JLogger.getLogger();

  void assertEmpty(HttpHeaders h) {
    assertEmpty(h.entrySet());
  }

  @Test
  public void testAddHeaders() {
    HttpHeaders h1 = new HttpHeaders();
    HttpHeaders h2 = new HttpHeaders();
    HttpHeaders exp = new HttpHeaders();

    assertEmpty(SpringHeaderUtil.addHeaders(null, null));
    assertEmpty(SpringHeaderUtil.addHeaders(h1, null));
    assertEmpty(SpringHeaderUtil.addHeaders(null, h2));
    assertEquals(exp, SpringHeaderUtil.addHeaders(null, null));
    h1.add("h1", "h1v1");
    exp.add("h1", "h1v1");
    assertEquals(exp, SpringHeaderUtil.addHeaders(null, h1));
    assertEquals(exp, SpringHeaderUtil.addHeaders(h1, null));
    assertEquals(exp, SpringHeaderUtil.addHeaders(h1, h2));
    assertEquals(exp, h2);
    assertEquals(exp, SpringHeaderUtil.addHeaders(h1, h2));
    h1.clear();
    assertEmpty(h1);
    h1.add("h2", "h2v1");
    exp.add("h2", "h2v1");
    assertEquals(exp, SpringHeaderUtil.addHeaders(h1, h2));
    assertEquals(exp, h2);
    h1.add("h2", "h2v2");
    exp.add("h2", "h2v2");
    assertEquals(exp, SpringHeaderUtil.addHeaders(h1, h2));
    assertEquals(exp, h2);
    assertEquals(ListUtil.list("h2v1", "h2v2"), h2.get("h2"));
    assertEquals(exp, SpringHeaderUtil.addHeaders(h1, h2));
    assertEquals(exp, h2);
    assertEquals(ListUtil.list("h2v1", "h2v2"), h2.get("h2"));
    h1.set("h2", "h2v3");
    exp.set("h2", "h2v3");
    assertEquals(exp, SpringHeaderUtil.addHeaders(h1, h2, true));
    assertEquals(exp, h2);
    assertEquals(ListUtil.list("h2v3"), h2.get("h2"));
  }
}
