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

import java.util.TimeZone;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.lockss.util.test.LockssTestCase5;

public class TestTimeConstants extends LockssTestCase5 {

  private static final long _SECOND = 1000L;
  private static final long _MINUTE = 1000L * 60L;
  private static final long _HOUR   = 1000L * 60L * 60L;
  private static final long _DAY    = 1000L * 60L * 60L * 24L;
  private static final long _WEEK   = 1000L * 60L * 60L * 24L * 7L;
  private static final long _YEAR   = 1000L * 60L * 60L * 24L * 365L;
  
  private static final String _GMT = "GMT";
  private static final String _UTC = "UTC";
  
  @ParameterizedTest
  @MethodSource("argsMilliseconds")
  public void testMilliseconds(long expected, long actual) {
    assertEquals(expected, actual);
  }
  
  public static Stream<Arguments> argsMilliseconds() {
    return Stream.of(Arguments.of(_SECOND, TimeConstants.SECOND),
                     Arguments.of(_MINUTE, TimeConstants.MINUTE),
                     Arguments.of(_HOUR, TimeConstants.HOUR),
                     Arguments.of(_DAY, TimeConstants.DAY),
                     Arguments.of(_WEEK, TimeConstants.WEEK),
                     Arguments.of(_YEAR, TimeConstants.YEAR));
  }

  @ParameterizedTest
  @MethodSource("argsTimeZones")
  public void testTimeZones(String expectedId, String actualId, TimeZone actualTz) {
    assertEquals(expectedId, actualId);
    assertEquals(expectedId, actualTz.getID());
  }
  
  public static Stream<Arguments> argsTimeZones() {
    return Stream.of(Arguments.of(_GMT, TimeConstants.TIMEZONE_ID_GMT, TimeConstants.TIMEZONE_GMT),
                     Arguments.of(_UTC, TimeConstants.TIMEZONE_ID_UTC, TimeConstants.TIMEZONE_UTC));
  }
  
}
