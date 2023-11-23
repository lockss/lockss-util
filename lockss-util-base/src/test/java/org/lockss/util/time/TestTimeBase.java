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

import java.util.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for <code>org.lockss.scheduler.TimeBase</code>
 */

public class TestTimeBase {

  @Test
  public void testConstants() {
    assertEquals(Long.MAX_VALUE, TimeBase.MAX);
  }

  @Test
  public void testReal() {
    TimeBase.setReal();
    assertFalse(TimeBase.isSimulated());
    long now = TimeBase.nowMs();
    TimerUtil.guaranteedSleep(10);
    assertTrue(TimeBase.nowMs() > now);
  }

  @Test
  public void testSimulated() throws Exception {
    TimeBase.setSimulated();
    assertTrue(TimeBase.isSimulated());
    assertEquals(0, TimeBase.nowMs());
    TimeBase.setSimulated(100);
    assertTrue(TimeBase.isSimulated());
    assertEquals(100, TimeBase.nowMs());
    TimeBase.step();
    assertEquals(101, TimeBase.nowMs());
    TimeBase.step(10);
    assertEquals(111, TimeBase.nowMs());
    assertEquals(new Date(111), TimeBase.nowDate());

    assertEquals(11, TimeBase.msSince(100));
    assertEquals(89, TimeBase.msUntil(200));

    TimeBase.setReal();
    assertFalse(TimeBase.isSimulated());
    TimeBase.setSimulated(100);
    assertTrue(TimeBase.isSimulated());

    TimeBase.setReal();
    assertFalse(TimeBase.isSimulated());
    TimeBase.setSimulated("1970/1/1 0:00:00");
    assertTrue(TimeBase.isSimulated());
    assertEquals(0, TimeBase.nowMs());
    TimeBase.setSimulated("1970/1/1 01:00:00");
    assertTrue(TimeBase.isSimulated());
    assertEquals(3600000, TimeBase.nowMs());
    TimeBase.setSimulated("1970/1/2 01:00:00");
    assertTrue(TimeBase.isSimulated());
    assertEquals(90000000, TimeBase.nowMs());
  }

  @Test
  public void testCalendar() throws Exception {
    TimeBase.setReal();
    Calendar tbcal = TimeBase.nowCalendar();
    Calendar cal = Calendar.getInstance();
    int tbyear = tbcal.get(Calendar.YEAR);
    int year = cal.get(Calendar.YEAR);
    assertTrue(year == tbyear || (year == tbyear + 1 &&
				  tbcal.get(Calendar.MONTH) == 11 &&
				  cal.get(Calendar.MONTH) == 0));

    TimeBase.setSimulated("1970/6/15 01:00:00");
    Calendar simtbcal = TimeBase.nowCalendar();
    assertEquals(1970, simtbcal.get(Calendar.YEAR));
    assertEquals(5, simtbcal.get(Calendar.MONTH));
  }

}
