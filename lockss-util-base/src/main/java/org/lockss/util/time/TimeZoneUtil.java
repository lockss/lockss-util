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

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneUtil {

  /**
   * <p>
   * The ID for the GMT time zone.
   * </p>
   */
  public static final String TIMEZONE_ID_GMT = "GMT";
  
  /**
   * <p>
   * The GMT time zone.
   * </p>
   */
  public static final TimeZone TIMEZONE_GMT = getExactTimeZone(TIMEZONE_ID_GMT);
  
  /**
   * <p>
   * The ID for the UTC time zone.
   * </p>
   */
  public static final String TIMEZONE_ID_UTC = "UTC";
  
  /**
   * <p>
   * The UTCtime zone.
   * </p>
   */
  public static final TimeZone TIMEZONE_UTC = getExactTimeZone(TIMEZONE_ID_UTC);

  /** The default timezone, GMT */
  public static final TimeZone DEFAULT_TIMEZONE = TIMEZONE_GMT;
  
  /**
   * <p>
   * Returns a {@link TimeZone} instance with exactly the given identifier, or
   * throws {@link IllegalArgumentException}.
   * </p>
   * <p>
   * This is useful because {@link TimeZone#getTimeZone(String)} always returns
   * something but not necessarily what the caller intended; if the time zone
   * identifier has a typo, or if the underlying system's Java time zone data is
   * broken or missing (e.g. well-known annoyance with third-party Java 7 on
   * Ubuntu 16.04), GMT is returned, probably unbeknownst to the caller. The
   * downside of this method is that {@link TimeZone#getTimeZone(String)} might
   * return a {@link TimeZone} instance that is equivalent to the one requested
   * but with another identifier, which this method refuses to do.
   * </p>
   * 
   * @param tzid
   *          A time zone identifier.
   * @return A {@link TimeZone} instance, for which {@link TimeZone#getID()}
   *         returns the same as the given time zone identifier.
   * @throws IllegalArgumentException
   * @since 1.74
   * @see TimeZone#getAvailableIDs()
   * @see TimeZone#getTimeZone(String)
   */
  public static TimeZone getExactTimeZone(String tzid) throws IllegalArgumentException {
    if (tzid == null) {
      throw new IllegalArgumentException("Time zone identifier cannot be null");
    }
    TimeZone tz = TimeZone.getTimeZone(tzid);
    String actualTzid = tz.getID();
    if (!actualTzid.equals(tzid)) {
      throw new IllegalArgumentException("Unknown time zone identifier: " + tzid);
    }
    return tz;
  }
  
  /**
   * <p>
   * Performs a test to determine if basic time zone data is available. On some
   * systems (e.g. third-party Java 7 Ubuntu 16.04), Java time zone data may be
   * broken or missing, and the effect is that
   * {@link TimeZone#getTimeZone(String)} silently returns GMT.
   * </p>
   * 
   * @return True if and only the test time zones in {@link #BASIC_TIME_ZONES}
   *         seem to exist properly.
   * @since 1.74
   * @see #BASIC_TIME_ZONES
   */
  public static boolean isBasicTimeZoneDataAvailable() {
    if (TimeZone.getAvailableIDs() == null) {
      return false;
    }
    for (String id : BASIC_TIME_ZONES) {
      if (!BASIC_TIME_ZONES.contains(id)) {
        return false;
      }
      if (!id.equals(TimeZone.getTimeZone(id).getID())) {
        return false;
      }
    }
    return true;
  }
  
  public static final List<String> BASIC_TIME_ZONES = Arrays.asList(
      "GMT",
      "UTC",
      "America/Los_Angeles",
      "America/New_York",
      "US/Pacific",
      "US/Eastern"
  );

}
