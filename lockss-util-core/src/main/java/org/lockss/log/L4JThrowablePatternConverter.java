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

import org.apache.logging.log4j.core.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.ThrowableFormatOptions;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.*;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.core.pattern.*;

/**
 * Extension of standard Throwable PatternConverter, which outputs the
 * stack trace only if the log message was issued at a succifiently high
 * level (@value{LockssLogger#PARAM_STACKTRACE_SEVERITY}), or the issuing Logger's
 * level is set sufficiently low (@value{LockssLogger#PARAM_STACKTRACE_LEVEL}).
 *
 * Invoked with <tt>%lex</tt> in a layout pattern.
 */
@Plugin(name = "L4JThrowablePatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({ "lex", "lthrowable", "lexception" })
public class L4JThrowablePatternConverter extends ThrowablePatternConverter {

  private static org.apache.logging.log4j.Logger myLog =
    StatusLogger.getLogger();

    /**
     * Constructor.
     * @param name Name of converter.
     * @param style CSS style for output.
     * @param options options, may be null.
     * @param config
     */
    protected L4JThrowablePatternConverter(final String name,
					   final String style,
					   final String[] options,
					   final Configuration config) {
      super(name, style, options, config);
    }

    /**
     * Gets an instance of the class.
     *
     * @param config
     * @param options pattern options, may be null.  If first element is "short",
     *                only the first line of the throwable will be formatted.
     * @return instance of class.
     */
    public static
      L4JThrowablePatternConverter newInstance(final Configuration config,
					       final String[] options) {
      return new L4JThrowablePatternConverter("LockssThrowable",
					      "throwable", options, config);
    }

    @Override
    public void format(final LogEvent event, final StringBuilder buffer) {
      final Throwable throwable = event.getThrown();
      if (throwable == null || !options.anyLines()) {
	super.format(event, buffer);
	return;
      }
      ReadOnlyStringMap cdata = event.getContextData();
      myLog.debug("cdata: {}", cdata);
      myLog.debug("cstack peek: {}", event.getContextStack().peek());
      boolean includeStackTrace = false;
      if (cdata == null ||
	  (!cdata.containsKey(LockssLogger.PARAM_STACKTRACE_SEVERITY) &&
	   !cdata.containsKey(LockssLogger.PARAM_STACKTRACE_LEVEL))) {
	includeStackTrace = true;
      } else {
	if (cdata.containsKey(LockssLogger.PARAM_STACKTRACE_SEVERITY)) {
	  includeStackTrace =
	    ((Level)cdata.getValue(LockssLogger.PARAM_STACKTRACE_SEVERITY)).isLessSpecificThan(event.getLevel());
	}
	if (!includeStackTrace &&
	    cdata.containsKey(LockssLogger.PARAM_STACKTRACE_LEVEL)) {
	  String peek = event.getContextStack().peek();
	  Level lev = peek != null ? Level.getLevel(peek) : null;
	  myLog.debug("lev: {}", lev);
	  if (lev != null) {
	    includeStackTrace =
	      lev.isLessSpecificThan((Level)cdata.getValue(LockssLogger.PARAM_STACKTRACE_LEVEL));
	  }
	}
      }
      myLog.debug("format: {}", event.getContextData());
      myLog.debug("include: {}", includeStackTrace);
      myLog.debug("name: {}", event.getLoggerName());

      if (includeStackTrace) {
	formatFull(throwable, getSuffix(event), buffer);
      } else {
	formatSuppress(throwable, getSuffix(event), buffer);
      }
    }

  private void formatSuppress(final Throwable throwable, final String suffix,
			      final StringBuilder buffer) {
    final int len = buffer.length();
    if (len > 0 && !Character.isWhitespace(buffer.charAt(len - 1))) {
      buffer.append(": ");
    }
    buffer.append(throwable.toString());
  }

  private void formatFull(final Throwable throwable, final String suffix,
			  final StringBuilder buffer) {
    final StringWriter w = new StringWriter();

    throwable.printStackTrace(new PrintWriter(w));
    final int len = buffer.length();
    if (len > 0 && !Character.isWhitespace(buffer.charAt(len - 1))) {
      buffer.append(": ");
    }
    if (!options.allLines() ||
	!Strings.LINE_SEPARATOR.equals(options.getSeparator()) ||
	Strings.isNotBlank(suffix)) {
      final StringBuilder sb = new StringBuilder();
      final String[] array = w.toString().split(Strings.LINE_SEPARATOR);
      final int limit = options.minLines(array.length) - 1;
      final boolean suffixNotBlank = Strings.isNotBlank(suffix);
      for (int i = 0; i <= limit; ++i) {
	sb.append(array[i]);
	if (suffixNotBlank) {
	  sb.append(' ');
	  sb.append(suffix);
	}
	if (i < limit) {
	  sb.append(options.getSeparator());
	}
      }
      buffer.append(sb.toString());

    } else {
      buffer.append(w.toString());
    }
  }

}
