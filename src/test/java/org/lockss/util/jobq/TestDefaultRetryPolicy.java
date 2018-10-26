/*
 * Copyright (c) 2018 Board of Trustees of Leland Stanford Jr. University,
 * all rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 * STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Stanford University shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization from Stanford University.
 */

package org.lockss.util.jobq;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDefaultRetryPolicy  {

  DefaultRetryPolicy retry0Policy;
  DefaultRetryPolicy retry3Policy;

  @BeforeEach
  void setUp() {
    retry0Policy = new DefaultRetryPolicy(0);
    retry3Policy = new DefaultRetryPolicy(3);
  }


  @Test
  void testRetryOrNot() {
    // retry 0 should fail immediately.
    assertFalse(retry0Policy.retryOrNot());

    // retry 3 should succeed 3x then fail.
    assertTrue(retry3Policy.retryOrNot());
    assertTrue(retry3Policy.retryOrNot());
    assertTrue(retry3Policy.retryOrNot());
    assertFalse(retry3Policy.retryOrNot());
  }


  @Test
  void testGetCurrentRetryCount() {
    // we start with zero retries.
    assertEquals(0,retry0Policy.getCurrentRetryCount());
    assertEquals(0, retry3Policy.getCurrentRetryCount());
    retry3Policy.retryOrNot();
    assertEquals(1, retry3Policy.getCurrentRetryCount());
    retry3Policy.retryOrNot();
    assertEquals(2, retry3Policy.getCurrentRetryCount());
    retry3Policy.retryOrNot();
    assertEquals(3, retry3Policy.getCurrentRetryCount());
  }


}