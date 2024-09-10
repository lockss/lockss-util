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

package org.lockss.util.time;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.lockss.util.test.LockssTestCase5;

public class TestTimeUtil extends LockssTestCase5 {

  private static final long _SECOND = 1000L;
  private static final long _MINUTE = 1000L * 60L;
  private static final long _HOUR   = 1000L * 60L * 60L;
  private static final long _DAY    = 1000L * 60L * 60L * 24L;
  private static final long _WEEK   = 1000L * 60L * 60L * 24L * 7L;
  private static final long _YEAR   = 1000L * 60L * 60L * 24L * 365L;
  
  @ParameterizedTest
  @MethodSource("argsMilliseconds")
  public void testMilliseconds(long expected, long actual) {
    assertEquals(expected, actual);
  }
  
  public static Stream<Arguments> argsMilliseconds() {
    return Stream.of(Arguments.of(_SECOND, TimeUtil.SECOND),
                     Arguments.of(_MINUTE, TimeUtil.MINUTE),
                     Arguments.of(_HOUR, TimeUtil.HOUR),
                     Arguments.of(_DAY, TimeUtil.DAY),
                     Arguments.of(_WEEK, TimeUtil.WEEK),
                     Arguments.of(_YEAR, TimeUtil.YEAR));
  }

  @ParameterizedTest
  @MethodSource("argsTimeIntervalToString")
  public void testTimeIntervalToString(String expected, long input) throws Exception {
    assertEquals(expected, TimeUtil.timeIntervalToString(input));
  }
  
  public static Stream<Arguments> argsTimeIntervalToString() {
    return Stream.of(Arguments.of("0ms", 0L),
                     Arguments.of("1000ms", TimeUtil.SECOND),
                     Arguments.of("-1000ms", - TimeUtil.SECOND),
                     Arguments.of("9000ms", 9L * TimeUtil.SECOND),
                     Arguments.of("-9000ms", - 9L * TimeUtil.SECOND),
                     Arguments.of("10s", 10L * TimeUtil.SECOND),
                     Arguments.of("1m0s", TimeUtil.MINUTE),
                     Arguments.of("1h0m0s", TimeUtil.HOUR),
                     Arguments.of("2d3h0m", 2L * TimeUtil.DAY + 3L * TimeUtil.HOUR),
                     Arguments.of("20d23h0m", 3L * TimeUtil.WEEK - 1L * TimeUtil.HOUR),
                     Arguments.of("-20d23h0m", - (3L * TimeUtil.WEEK - 1L * TimeUtil.HOUR)),
                     Arguments.of("3w0d0h", 3L * TimeUtil.WEEK));
  }

  @ParameterizedTest
  @MethodSource("argsTimeIntervalToLongString")
  public void testTimeIntervalToLongString(String expected, long input) throws Exception {
    assertEquals(expected, TimeUtil.timeIntervalToLongString(input));
  }
  
  public static Stream<Arguments> argsTimeIntervalToLongString() throws Exception {
    return Stream.of(Arguments.of("0 seconds", 0L),
                     Arguments.of("1 second", TimeUtil.SECOND),
                     Arguments.of("-1 second", - TimeUtil.SECOND),
                     Arguments.of("9 seconds", 9L * TimeUtil.SECOND),
                     Arguments.of("-9 seconds", - 9L * TimeUtil.SECOND),
                     Arguments.of("10 seconds", 10L * TimeUtil.SECOND),
                     Arguments.of("1 minute", TimeUtil.MINUTE),
                     Arguments.of("1 hour", TimeUtil.HOUR),
                     Arguments.of("2 days, 3 hours", 2L * TimeUtil.DAY + 3L * TimeUtil.HOUR),
                     Arguments.of("20 days, 23 hours, 45 minutes", 3L * TimeUtil.WEEK - TimeUtil.HOUR + 45L * TimeUtil.MINUTE),
                     Arguments.of("12 days, 13 minutes, 1 second", 12L * TimeUtil.DAY + 13L * TimeUtil.MINUTE + TimeUtil.SECOND),
                     Arguments.of("21 days", 3L * TimeUtil.WEEK));
  }
  
}
