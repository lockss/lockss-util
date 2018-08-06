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

import java.util.*;

import org.slf4j.*;

/** Timer utilities
 */
public class TimerUtil {

  private static final Logger log = LoggerFactory.getLogger(TimerUtil.class);

  // no instances
  private TimerUtil() {
  }

  /**
   * Sleep for <code>ms</code> milliseconds,
   * throwing <code>InterruptedException</code> if interrupted.
   * @param ms length to sleep, in ms
   * @throws InterruptedException
   */
  public static void sleep(long ms) throws InterruptedException {
    long startTime = System.currentTimeMillis();
    Thread.sleep(ms);
    long delta = System.currentTimeMillis() - startTime;
    if (delta < ms) log.error("short sleep(" + ms + ") = " + delta + "ms");
  }

  // Is there a use for one that just returns if interrupted?
//    public static void interruptableSleep(long ms) {
//      try {
//        Thread.currentThread().sleep(ms);
//      }
//      catch (InterruptedException e) {
//      }
//    }

  /**
   * Sleep for <code>ms</code> milliseconds, returning early if interrupted
   * @param ms length to sleep, in ms
   */
  public static void guaranteedSleep(long ms) {
    long expMS = System.currentTimeMillis() + ms;

    for (long nowMS = System.currentTimeMillis();
	 nowMS < expMS;
	 nowMS = System.currentTimeMillis()) {
      try {
	sleep(expMS - nowMS);
      } catch (InterruptedException e) {
	return;
      }
    }
  }

  /**
   * Return the millisecond difference between two <code>Date</code>s.
   * @param d1 the first Date
   * @param d2 the second Date
   * @return the diff, in ms
   */
  public static long diff(Date d1, Date d2) {
    return d1.getTime() - d2.getTime();
  }

  /**
   * Return the milliseconds since <code>Date</code>.
   * @param start the start Date
   * @return the time since in ms
   */
  public static long timeSince(Date start) {
    return diff(new Date(), start);
  }
}
