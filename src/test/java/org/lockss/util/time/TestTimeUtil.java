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

package org.lockss.util.time;

import org.junit.jupiter.api.Test;
import org.lockss.util.test.LockssTestCase5;

public class TestTimeUtil extends LockssTestCase5 {

  @Test
  public void testTimeIntervalToString() throws Exception {
    assertEquals("0ms", TimeUtil.timeIntervalToString(0));
    assertEquals("1000ms", TimeUtil.timeIntervalToString(TimeConstants.SECOND));
    assertEquals("-1000ms", TimeUtil.timeIntervalToString(- TimeConstants.SECOND));
    assertEquals("9000ms", TimeUtil.timeIntervalToString(TimeConstants.SECOND * 9));
    assertEquals("-9000ms", TimeUtil.timeIntervalToString(- TimeConstants.SECOND * 9));
    assertEquals("10s", TimeUtil.timeIntervalToString(TimeConstants.SECOND * 10));
    assertEquals("1m0s", TimeUtil.timeIntervalToString(TimeConstants.MINUTE));
    assertEquals("1h0m0s", TimeUtil.timeIntervalToString(TimeConstants.HOUR));
    assertEquals("2d3h0m",
                 TimeUtil.timeIntervalToString(TimeConstants.DAY * 2 + TimeConstants.HOUR * 3));
    assertEquals("20d23h0m",
                 TimeUtil.timeIntervalToString(TimeConstants.WEEK * 3 - (TimeConstants.HOUR * 1)));
    assertEquals("-20d23h0m",
                 TimeUtil.timeIntervalToString(- (TimeConstants.WEEK * 3 - (TimeConstants.HOUR * 1))));
    assertEquals("3w0d0h", TimeUtil.timeIntervalToString(TimeConstants.WEEK * 3));
  }

  @Test
  public void testTimeIntervalToLong() throws Exception {
    assertEquals("0 seconds", TimeUtil.timeIntervalToLongString(0));
    assertEquals("1 second", TimeUtil.timeIntervalToLongString(TimeConstants.SECOND));
    assertEquals("-1 second", TimeUtil.timeIntervalToLongString(- TimeConstants.SECOND));
    assertEquals("9 seconds", TimeUtil.timeIntervalToLongString(TimeConstants.SECOND * 9));
    assertEquals("-9 seconds",
                 TimeUtil.timeIntervalToLongString(- TimeConstants.SECOND * 9));
    assertEquals("10 seconds",
                 TimeUtil.timeIntervalToLongString(TimeConstants.SECOND * 10));
    assertEquals("1 minute", TimeUtil.timeIntervalToLongString(TimeConstants.MINUTE));
    assertEquals("1 hour", TimeUtil.timeIntervalToLongString(TimeConstants.HOUR));
    assertEquals("2 days, 3 hours",
                 TimeUtil.timeIntervalToLongString(TimeConstants.DAY * 2 + TimeConstants.HOUR * 3));
    assertEquals("20 days, 23 hours, 45 minutes",
                 TimeUtil.timeIntervalToLongString(TimeConstants.WEEK * 3 - (TimeConstants.HOUR * 1)
                                                     + TimeConstants.MINUTE * 45));
    assertEquals("12 days, 13 minutes, 1 second",
                 TimeUtil.timeIntervalToLongString(TimeConstants.DAY * 12 + TimeConstants.MINUTE * 13
                                                     + TimeConstants.SECOND));
    assertEquals("21 days", TimeUtil.timeIntervalToLongString(TimeConstants.WEEK * 3));
  }
  
}
