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

package org.lockss.log;

import org.apache.logging.log4j.*;

/**
 * Constants for custom Log4j Levels corresponding to the LOCKSS Logger
 * levels not present in Log4j.  These Levels are defined in log4j2.xml,
 * but depending on the context this file is likely to be loaded before
 * Log4j has loaded the config.  Level.forName() is used because it will
 * create the Level if it doesn't exist, but requires the numeric levels to
 * be repeated here.  They must agree with those in log4j2.xml.
 */
public class L4JLevel {

  /** Errors caused by misbehavior of some server or component out of our
   * control. */
  public static final Level SITE_ERROR = Level.forName("SITE_ERROR", 210);
  /** Warnings about misbehavior of some server or component out of our
   * control. */
  public static final Level SITE_WARNING = Level.forName("SITE_WARNING", 310);
  /** Between DEBUG and TRACE */
  public static final Level DEBUG2 = Level.forName("DEBUG2", 550);
  /** Equivalent to Log4j TRACE */
  public static final Level DEBUG3 = Level.forName("DEBUG3", 600);
}
