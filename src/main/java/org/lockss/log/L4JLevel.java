/*

Copyright (c) 2000-2018 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Except as contained in this notice, the name of Stanford University shall not
be used in advertising or otherwise to promote the sale, use or other dealings
in this Software without prior written authorization from Stanford University.

*/

package org.lockss.log;

import org.apache.logging.log4j.*;

/**
 * Contants for custom Log4j Levels corresponding to the LOCKSS Logger
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
