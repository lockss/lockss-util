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

import java.util.Arrays;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.lockss.util.test.LockssTestCase5;

public class TestTimeZoneUtil extends LockssTestCase5 {

  @Test
  public void testIsBasicTimeZoneDataAvailable() {
    assertTrue(TimeZoneUtil.isBasicTimeZoneDataAvailable());
  }
  
  @Test
  public void testGoodTimeZones() throws Exception {
    for (String id : TimeZoneUtil.BASIC_TIME_ZONES) {
      TimeZone tz = TimeZoneUtil.getExactTimeZone(id);
      assertEquals(id, tz.getID());
      assertEquals("GMT".equals(id), "GMT".equals(tz.getID()));
    }
  }

  @Test
  public void testBadTimeZones() throws Exception {
    for (String id : Arrays.asList(null,
                                   "Foo",
                                   "America/Copenhagen",
                                   "Europe/Tokyo")) {
      try {
        TimeZone tz = TimeZoneUtil.getExactTimeZone(id);
        fail("Should have thrown IllegalArgumentException: " + id);
      }
      catch (IllegalArgumentException iae) {
        if (id == null) {
          assertEquals("Time zone identifier cannot be null", iae.getMessage());
        }
        else {
          assertEquals("Unknown time zone identifier: " + id, iae.getMessage());
        }
      }
    }
  }
  
}
