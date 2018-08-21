/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
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
 * level (@value{Logger#PARAM_STACKTRACE_SEVERITY}), or the issuing Logger's
 * level is set sufficiently low (@value{Logger#PARAM_STACKTRACE_LEVEL}).
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
	  (!cdata.containsKey(Logger.PARAM_STACKTRACE_SEVERITY) &&
	   !cdata.containsKey(Logger.PARAM_STACKTRACE_SEVERITY))) {
	includeStackTrace = true;
      } else {
	if (cdata.containsKey(Logger.PARAM_STACKTRACE_SEVERITY)) {
	  includeStackTrace =
	    ((Level)cdata.getValue(Logger.PARAM_STACKTRACE_SEVERITY)).isLessSpecificThan(event.getLevel());
	}
	if (!includeStackTrace &&
	    cdata.containsKey(Logger.PARAM_STACKTRACE_LEVEL)) {
	  Level lev = Level.getLevel(event.getContextStack().peek());
	  myLog.debug("lev: {}", lev);
	  includeStackTrace =
	    lev.isLessSpecificThan((Level)cdata.getValue(Logger.PARAM_STACKTRACE_LEVEL));
	}
      }
      myLog.debug("format: {}", event.getContextData());
      myLog.debug("include: {}", includeStackTrace);
      myLog.debug("name: {}", event.getLoggerName());

      if (includeStackTrace) {
	super.format(event, buffer);
      } else {
	formatSuppress(throwable, getSuffix(event), buffer);
      }
    }

  private void formatSuppress(final Throwable throwable, final String suffix,
			      final StringBuilder buffer) {
    final StringWriter w = new StringWriter();

    throwable.printStackTrace(new PrintWriter(w));
    final int len = buffer.length();
    if (len > 0 && !Character.isWhitespace(buffer.charAt(len - 1))) {
      buffer.append(' ');
    }
    final StringBuilder sb = new StringBuilder();
    final String[] array = w.toString().split(Strings.LINE_SEPARATOR);
    final int limit = 0/*Math.min(1, array.length)*/;
    //     final int limit = options.minLines(array.length) - 1;
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

  }

}
