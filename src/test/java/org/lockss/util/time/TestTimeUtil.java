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

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.lockss.util.test.LockssTestCase5;

public class TestTimeUtil extends LockssTestCase5 {

  @ParameterizedTest
  @MethodSource("argsTimeIntervalToString")
  public void testTimeIntervalToString(String expected, long input) throws Exception {
    assertEquals(expected, TimeUtil.timeIntervalToString(input));
  }
  
  public static Stream<Arguments> argsTimeIntervalToString() {
    return Stream.of(Arguments.of("0ms", 0L),
                     Arguments.of("1000ms", TimeConstants.SECOND),
                     Arguments.of("-1000ms", - TimeConstants.SECOND),
                     Arguments.of("9000ms", 9L * TimeConstants.SECOND),
                     Arguments.of("-9000ms", - 9L * TimeConstants.SECOND),
                     Arguments.of("10s", 10L * TimeConstants.SECOND),
                     Arguments.of("1m0s", TimeConstants.MINUTE),
                     Arguments.of("1h0m0s", TimeConstants.HOUR),
                     Arguments.of("2d3h0m", 2L * TimeConstants.DAY + 3L * TimeConstants.HOUR),
                     Arguments.of("20d23h0m", 3L * TimeConstants.WEEK - 1L * TimeConstants.HOUR),
                     Arguments.of("-20d23h0m", - (3L * TimeConstants.WEEK - 1L * TimeConstants.HOUR)),
                     Arguments.of("3w0d0h", 3L * TimeConstants.WEEK));
  }

  @ParameterizedTest
  @MethodSource("argsTimeIntervalToLongString")
  public void testTimeIntervalToLongString(String expected, long input) throws Exception {
    assertEquals(expected, TimeUtil.timeIntervalToLongString(input));
  }
  
  @Test
  public static Stream<Arguments> argsTimeIntervalToLongString() throws Exception {
    return Stream.of(Arguments.of("0 seconds", 0L),
                     Arguments.of("1 second", TimeConstants.SECOND),
                     Arguments.of("-1 second", - TimeConstants.SECOND),
                     Arguments.of("9 seconds", 9L * TimeConstants.SECOND),
                     Arguments.of("-9 seconds", - 9L * TimeConstants.SECOND),
                     Arguments.of("10 seconds", 10L * TimeConstants.SECOND),
                     Arguments.of("1 minute", TimeConstants.MINUTE),
                     Arguments.of("1 hour", TimeConstants.HOUR),
                     Arguments.of("2 days, 3 hours", 2L * TimeConstants.DAY + 3L * TimeConstants.HOUR),
                     Arguments.of("20 days, 23 hours, 45 minutes", 3L * TimeConstants.WEEK - TimeConstants.HOUR + 45L * TimeConstants.MINUTE),
                     Arguments.of("12 days, 13 minutes, 1 second", 12L * TimeConstants.DAY + 13L * TimeConstants.MINUTE + TimeConstants.SECOND),
                     Arguments.of("21 days", 3L * TimeConstants.WEEK));
  }
  
}
