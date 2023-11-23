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

import java.util.*;

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
      Map<String,Level> params = ctx.getStackLevelMap();
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
