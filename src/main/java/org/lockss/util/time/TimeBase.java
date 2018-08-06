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
import java.util.function.Consumer;

import org.slf4j.*;

import java.text.*;

/**
 * TimeBase allows use of a simulated time base for testing.

 * Instead of calling <code>System.currentTimeMillis()</code> or <code>new
 * Date()</code>, other parts of the system should call {@link #nowMs()} or
 * {@link #nowDate()}.  When in real mode (the default), these methods
 * return the same value as the normal methods.  In simulated mode, they
 * return the contents of an internal counter, which can be incremented
 * programmatically.  This allows time-dependent functions to be tested
 * quickly and predictably.
 */
public class TimeBase {
  
  private static final Logger log = LoggerFactory.getLogger(TimeBase.class);
  
  /** A long time from now. */
  public static final long MAX = Long.MAX_VALUE;

  private static volatile boolean isSimulated = false;
  private static volatile long simulatedTime;

  private static List<Consumer<Long>> observers = new ArrayList<Consumer<Long>>();
  
  /** No instances */
  private TimeBase() {
  }

  /** Set TimeBase into real mode.
   */
  public static void setReal() {
    isSimulated = false;
  }

  /** Set TimeBase into simulated mode.
   * @param time  Simulated time to set as current
   */
  public static void setSimulated(long time) {
    isSimulated = true;
    simulatedTime = time;
  }

  /** Set TimeBase into simulated mode.
   * @param time Date/time string to set as current time, in format
   * <code>yyyy/MM/dd HH:mm:ss</code>
   */
  public static void setSimulated(String dateTime) throws ParseException {
    DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    fmt.setTimeZone(TimeConstants.DEFAULT_TIMEZONE);
    fmt.setLenient(true);
    simulatedTime = fmt.parse(dateTime).getTime();
    isSimulated = true;
  }

  /** Set TimeBase into simulated mode, at time 0 */
  public static void setSimulated() {
    setSimulated(0);
  }

  /** Return true iff simulated time base is in effect */
  public static boolean isSimulated() {
    return isSimulated;
  }

  /** Step simulated time base by n ticks */
  public static void step(long n) {
    if (!isSimulated) {
      throw new IllegalStateException("Can't step TimeBase when in real mode");
    }
    simulatedTime += n;
    notifyObservers();
  }

  /** Step simulated time base by 1 tick */
  public static void step() {
    step(1);
  }

  /** Return the current time, in milliseconds.  In real mode, this returns
   * System.currentTimeMillis(); in simulated mode it returns the simulated
   * time.
   */
  public static long nowMs() {
    if (isSimulated) {
      return simulatedTime;
    } else {
      return System.currentTimeMillis();
    }
  }

  /** Return the current time, as a Date.  In real mode, this returns
   * new Date(); in simulated mode it returns the simulated time as a Date.
   */
  public static Date nowDate() {
    if (isSimulated) {
      return new Date(simulatedTime);
    } else {
      return new Date();
    }
  }

  /** Return the number of milliseconds since the argument
   * @param when a time
   */
  public static long msSince(long when) {
    return nowMs() - when;
  }

  /** Return the number of milliseconds until the argument
   * @param when a time
   */
  public static long msUntil(long when) {
    return when - nowMs();
  }

  /** Return a Calendar set to the current real or simulated time */
  public static Calendar nowCalendar() {
    Calendar res = Calendar.getInstance();
    res.setTimeInMillis(nowMs());
    return res;
  }

  public static void registerObserver(Consumer<Long> callback) {
    observers.add(callback);
  }
  
  private static void notifyObservers() {
    for (Consumer<Long> observer : observers) {
      try {
        observer.accept(simulatedTime);
      }
      catch (Throwable thr) {
        log.debug(String.format("Observer %s threw %s", observer, thr));
      }
    }
  }
  
}
