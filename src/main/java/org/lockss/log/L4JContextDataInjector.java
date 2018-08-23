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

import java.util.*;
import org.lockss.util.*;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.impl.*;
import org.apache.logging.log4j.util.*;


/** This class is part of the mechanism to make the LOCKSS config params
 * that determine when statcktraces should be included in the log available
 * to layout patterns.  It grabs the params {@value
 * LockssLogger#PARAM_STACKTRACE_LEVEL} and {@value
 * LockssLogger#PARAM_STACKTRACE_SEVERITY} from the LoggerContext
 * (L4JLoggerContext) and injects then into the context data which is
 * accessible from the LogEvent.
 */
public class L4JContextDataInjector implements ContextDataInjector {


  private L4JLoggerContext loggerCtx;

  public L4JContextDataInjector() {
  }

  L4JLoggerContext getLoggerContext() {
    if (loggerCtx == null) {
      LoggerContext ctx0 = (LoggerContext)LogManager.getContext(false);
      if (ctx0 instanceof L4JLoggerContext) {
	loggerCtx = (L4JLoggerContext)ctx0;
      }
    }
    return loggerCtx;
  }

  /**
   */
  @Override
  public StringMap injectContextData(List properties, StringMap reusable) {
    if (properties == null || properties.isEmpty()) {
      // assume context data is stored in a copy-on-write data structure
      // that is safe to pass to another thread
      return (StringMap)rawContextData();
    }
    // first copy configuration properties into the result
    ThreadContextDataInjector.copyProperties(properties, reusable);

    // then copy context data key-value pairs (may overwrite configuration
    // properties)
    reusable.putAll(rawContextData());
    return reusable;
  }

  @Override
  public ReadOnlyStringMap rawContextData() {
    SortedArrayStringMap res =  new SortedArrayStringMap(2);
    L4JLoggerContext ctx = getLoggerContext();
    if (ctx != null) {
      // If the LoggerContext is a L4JLoggerContext and it has a level map,
      // stores those levels in the result.
      Map<String,Level> params = ctx.getLevelMap();
      if (params != null) {
	putIfSet(res, params, LockssLogger.PARAM_STACKTRACE_LEVEL);
	putIfSet(res, params, LockssLogger.PARAM_STACKTRACE_SEVERITY);
      }
    }
    return res;
  }

  void putIfSet(SortedArrayStringMap to, Map<String,Level> from, String key) {
    if (from.containsKey(key)) {
      to.putValue(key, from.get(key));
    }
  }
}
