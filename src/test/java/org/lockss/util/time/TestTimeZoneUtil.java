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

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.lockss.util.test.LockssTestCase5;

public class TestTimeZoneUtil extends LockssTestCase5 {

  private static final String _GMT = "GMT";
  private static final String _UTC = "UTC";
  
  @ParameterizedTest
  @MethodSource("argsTimeZones")
  public void testTimeZones(String expectedId, String actualId, TimeZone actualTz) {
    assertEquals(expectedId, actualId);
    assertEquals(expectedId, actualTz.getID());
  }
  
  public static Stream<Arguments> argsTimeZones() {
    return Stream.of(Arguments.of(_GMT, TimeZoneUtil.TIMEZONE_ID_GMT, TimeZoneUtil.TIMEZONE_GMT),
                     Arguments.of(_UTC, TimeZoneUtil.TIMEZONE_ID_UTC, TimeZoneUtil.TIMEZONE_UTC));
  }
  
  @BeforeAll
  public static void beforeAllSanityCheck() {
    Assertions.assertTrue(TimeZoneUtil.isBasicTimeZoneDataAvailable());
  }
  
  @ParameterizedTest
  @MethodSource("argsGoodTimeZones")
  public void testGoodTimeZones(String tzid) {
    TimeZone tz = TimeZoneUtil.getExactTimeZone(tzid);
    assertEquals(tzid, tz.getID());
    assertEquals("GMT".equals(tzid), "GMT".equals(tz.getID()));
  }
  
  public static Stream<String> argsGoodTimeZones() {
    return TimeZoneUtil.BASIC_TIME_ZONES.stream();
  }

  @ParameterizedTest
  @MethodSource("argsBadTimeZones")
  public void testBadTimeZones(String tzid) {
    try {
      TimeZone tz = TimeZoneUtil.getExactTimeZone(tzid);
      fail("Should have thrown IllegalArgumentException: " + tzid);
    }
    catch (IllegalArgumentException iae) {
      if (tzid == null) {
        assertEquals("Time zone identifier cannot be null", iae.getMessage());
      }
      else {
        assertEquals("Unknown time zone identifier: " + tzid, iae.getMessage());
      }
    }
  }
  
  public static Stream<String> argsBadTimeZones() {
    return Stream.of(null,
                     "Foo",
                     "America/Copenhagen",
                     "Europe/Tokyo");
  }
  
}
