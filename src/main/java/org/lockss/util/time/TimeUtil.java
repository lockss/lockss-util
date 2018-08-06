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

public class TimeUtil {

  /** Generate a string representing the time interval.
   * @param millis the time interval in milliseconds
   * @return a string in the form dDhHmMsS
   */
  public static String timeIntervalToString(long millis) {
    StringBuilder sb = new StringBuilder();

    if (millis < 0) {
      sb.append("-");
      millis = -millis;
    }
    return posTimeIntervalToString(millis, sb);
  }

  private static String posTimeIntervalToString(long millis, StringBuilder sb) {
    if (millis < 10 * TimeConstants.SECOND) {
      sb.append(millis);
      sb.append("ms");
    } else {
      boolean force = false;
      String stop = null;
      for (int ix = 0; ix < units.length; ix++) {
        UD iu = units[ix];
        long n = millis / iu.millis;
        if (force || n >= iu.threshold) {
          millis %= iu.millis;
          sb.append(n);
          sb.append(iu.str);
          force = true;
          if (stop == null) {
            if (iu.stop != null) {
              stop = iu.stop;
            }
          } else {
            if (stop.equals(iu.str)) {
              break;
            }
          }
        }
      }
    }
    return sb.toString();
  }

  /** Generate a more verbose string representing the time interval.
   * @param millis the time interval in milliseconds
   * @return a string in the form "<d> days, <h> hours, <m> minutes, <s>
   * seconds"
   */
  public static String timeIntervalToLongString(long millis) {
    StringBuilder sb = new StringBuilder();
    long temp = 0;
    if (millis < 0) {
      sb.append("-");
      millis = -millis;
    }
    if (millis >= TimeConstants.SECOND) {
      temp = millis / TimeConstants.DAY;
      if (temp > 0) {
        sb.append(numberOfUnits(temp, "day"));
        millis -= temp * TimeConstants.DAY;
        if (millis >= TimeConstants.MINUTE) {
          sb.append(", ");
        }
      }
      temp = millis / TimeConstants.HOUR;
      if (temp > 0) {
        sb.append(numberOfUnits(temp, "hour"));
        millis -= temp * TimeConstants.HOUR;
        if (millis >= TimeConstants.MINUTE) {
          sb.append(", ");
        }
      }
      temp = millis / TimeConstants.MINUTE;
      if (temp > 0) {
        sb.append(numberOfUnits(temp, "minute"));
        millis -= temp * TimeConstants.MINUTE;

        if(millis >= TimeConstants.SECOND) {
          sb.append(", ");
        }
      }
      temp = millis / TimeConstants.SECOND;
      if (temp > 0) {
        sb.append(numberOfUnits(temp, "second"));
      }
      return sb.toString();
    }
    else {
      return "0 seconds";
    }
  }
  
  private static final String numberOfUnits(long number, String singular) {
    return Long.toString(number) + " " + (number == 1L ? singular : singular + "s");
  }
  
  // Unit Descriptor
  private static class UD {
    String str;                         // suffix string
    long millis;                        // milliseconds in unit
    int threshold;                      // min units to output
    String stop;                        // last unit to output if this matched

    UD(String str, long millis) {
      this(str, millis, 1);
    }

    UD(String str, long millis, int threshold) {
      this(str, millis, threshold, null);
    }

    UD(String str, long millis, int threshold, String stop) {
      this.str = str;
      this.millis = millis;
      this.threshold = threshold;
      this.stop = stop;
    }
  }

  static UD units[] = {
    new UD("w", TimeConstants.WEEK, 3, "h"),
    new UD("d", TimeConstants.DAY, 1, "m"),
    new UD("h", TimeConstants.HOUR),
    new UD("m", TimeConstants.MINUTE),
    new UD("s", TimeConstants.SECOND, 0),
  };

}
