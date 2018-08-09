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

public class TimeConstants {

  /** The number of milliseconds in a second */
  public static final long SECOND = 1000L;
  
  /** The number of milliseconds in a minute */
  public static final long MINUTE = 60L * SECOND;

  /** The number of milliseconds in an hour */  
  public static final long HOUR = 60L * MINUTE;
  
  /** The number of milliseconds in a day */
  public static final long DAY = 24L * HOUR;

  /** The number of milliseconds in a week */
  public static final long WEEK = 7L * DAY;
  
  /** The number of milliseconds in a (non-leap) year */
  public static final long YEAR = 365L * DAY;

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
  public static final TimeZone TIMEZONE_GMT = TimeZoneUtil.getExactTimeZone(TIMEZONE_ID_GMT);
  
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
  public static final TimeZone TIMEZONE_UTC = TimeZoneUtil.getExactTimeZone(TIMEZONE_ID_UTC);
  
  /** The default timezone, GMT */
  public static final TimeZone DEFAULT_TIMEZONE = TIMEZONE_GMT;

  // Cannot instantiate
  private TimeConstants() {
    
  }
  
}
