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

package org.lockss.util.lang;

import org.junit.jupiter.api.*;
import org.lockss.util.test.LockssTestCase5;

/**
 * Test class for <code>org.lockss.util.LockssRandom</code>
 */
public class TestLockssRandom extends LockssTestCase5 {

  LockssRandom rand;

  @BeforeEach
  public void setUp() throws Exception {
    rand = new LockssRandom();
  }

  public void testNextBits(int bits) {
    boolean upper = false;
    boolean lower = false;
    long max;
    long mid;
    String lrange, urange;
    if (bits < 64) {
      max = (((long)1) << bits) - 1;
      mid = (max / 2);
      lrange = "0 - " + mid;
    } else {
      max = Long.MAX_VALUE;
      mid = 0;
      lrange = Long.MIN_VALUE + " - 0";
    }
    urange = mid + " - " + max;
    for (int ix = 0; ix < 1000; ix++) {
      long val = rand.nextBits(bits);
      assertTrue(val <= max,
                 "nextBits(" + bits + ") > max: " + val + " > " + max);
      if (val <= mid) {
	lower = true;
      } else {
	upper = true;
      }
    }
    assertTrue(lower, "No values in range " + lrange);
    assertTrue(upper, "No values in range " + urange);
  }

  @Test
  public void testNextBits() {
    testNextBits(1);
    testNextBits(2);
    testNextBits(4);
    testNextBits(8);
    testNextBits(16);
    testNextBits(31);
    testNextBits(32);
    testNextBits(33);
    testNextBits(63);
    testNextBits(64);
  }

  @Test
  public void testNextLong() {
    boolean upper = false;
    boolean lower = false;
    long max = Long.MAX_VALUE;
    long mid = 0;
    String lrange = Long.MIN_VALUE + " - 0";
    String urange = mid + " - " + max;
    for (int ix = 0; ix < 1000; ix++) {
      long val = rand.nextLong();
      if (val <= mid) {
	lower = true;
      } else {
	upper = true;
      }
    }
    assertTrue(lower, "No values in range " + lrange);
    assertTrue(upper, "No values in range " + urange);
  }

}
