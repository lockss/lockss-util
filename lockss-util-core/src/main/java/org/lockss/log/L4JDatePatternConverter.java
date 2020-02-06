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
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.*;
import org.apache.logging.log4j.core.pattern.*;

import org.lockss.util.time.TimeBase;

/**
 * Extension of standard DatePatternConverter, which appends " (sim XXX)"
 * to the timestamp if the LOCKSS TimeBase is set in simulated mode.
 *
 * Invoked with <tt>%ld{...}</tt> in a layout pattern.
 *
 * Can't extend DatePatternConverter because its constructor is private,
 * so we encapsulate and delegate. *
 */
@Plugin(name = "L4JDatePatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({ "ld", "ldate" })
@PerformanceSensitive("allocation")
public class L4JDatePatternConverter extends LogEventPatternConverter
  implements ArrayPatternConverter {

  protected DatePatternConverter pat;

  protected L4JDatePatternConverter(final String[] options) {
    super("Date", "date");
    pat = DatePatternConverter.newInstance(options);
  }

  public static L4JDatePatternConverter newInstance(final String[] options) {
    return new L4JDatePatternConverter(options);
  }

  @Override
  public void format(final LogEvent event, final StringBuilder buffer) {
    pat.format(event, buffer);
    if (TimeBase.isSimulated()) {
      buffer.append(" (sim ");
      buffer.append(TimeBase.nowMs());
      buffer.append(")");
    }
  }

  @Override
  public void format(final StringBuilder toAppendTo, final Object... objects) {
    pat.format(toAppendTo, objects);
  }
}
