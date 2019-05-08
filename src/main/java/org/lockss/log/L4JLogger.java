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
import java.util.regex.*;

import org.lockss.util.*;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.message.*;
import org.apache.logging.log4j.util.*;


/**
 * log4j2 wrapper and extensions that provide compatibility with legacy
 * LOCKSS logger:<ul>
 */
public class L4JLogger extends org.apache.logging.log4j.core.Logger {

  private static final String FQCN = L4JLogger.class.getName();


  public static L4JLogger cast(org.apache.logging.log4j.Logger log) {
    try {
      return (L4JLogger)log;
    } catch (ClassCastException e) {
      throw new ClassCastException("LOCKSS L4JLogger-aware code used, but LOCKSS' Log4J2 configuration is not in effect: "
				   + e.getMessage());
    }
  }

  public static L4JLogger getLogger(String name) {
    return cast(LogManager.getLogger(name));
  }

  public static L4JLogger getLogger(Class clazz) {
    return cast(LogManager.getLogger(clazz));
  }

  public static L4JLogger getLogger() {
    return cast(LogManager.getLogger(StackLocatorUtil.getCallerClass(2)));
  }

  protected L4JLogger(final LoggerContext context,
		      final String name,
		      final MessageFactory messageFactory) {
    super(context, name, messageFactory);
  }

  @Override
  public void logIfEnabled(final String fqcn, final Level level,
			   final Marker marker, final String message,
			   final Throwable t) {
    if (isEnabled(level, marker, message, t)) {
      ThreadContext.push(getLevel().name());
      try {
	logMessage(fqcn, level, marker, message, t);
      } finally {
	ThreadContext.pop();
      }
    }
  }



  public void debug2(final Marker marker, final CharSequence message) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, null);
  }

  public void debug2(final Marker marker, final CharSequence message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, t);
  }

  public void debug2(final Marker marker, final Message msg) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, msg, msg != null ? msg.getThrowable() : null);
  }

  public void debug2(final Marker marker, final Message msg, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, msg, t);
  }

  public void debug2(final Marker marker, final Object message) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, null);
  }

  public void debug2(final Marker marker, final Object message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, t);
  }

  public void debug2(final Marker marker, final String message) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, (Throwable) null);
  }

  public void debug2(final Marker marker, final String message, final Object... params) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, params);
  }

  public void debug2(final Marker marker, final String message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, t);
  }

  public void debug2(final Message msg) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, msg, msg != null ? msg.getThrowable() : null);
  }

  public void debug2(final Message msg, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, msg, t);
  }

  public void debug2(final CharSequence message) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, null);
  }

  public void debug2(final CharSequence message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, t);
  }

  public void debug2(final Object message) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, null);
  }

  public void debug2(final Object message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, t);
  }

  public void debug2(final String message) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, (Throwable) null);
  }

  public void debug2(final String message, final Object... params) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, params);
  }

  public void debug2(final String message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, t);
  }

  public void debug2(final Supplier<?> msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, msgSupplier, (Throwable) null);
  }

  public void debug2(final Supplier<?> msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, msgSupplier, t);
  }

  public void debug2(final Marker marker, final Supplier<?> msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, msgSupplier, (Throwable) null);
  }

  public void debug2(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, paramSuppliers);
  }

  public void debug2(final Marker marker, final Supplier<?> msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, msgSupplier, t);
  }

  public void debug2(final String message, final Supplier<?>... paramSuppliers) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, paramSuppliers);
  }

  public void debug2(final Marker marker, final MessageSupplier msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, msgSupplier, (Throwable) null);
  }

  public void debug2(final Marker marker, final MessageSupplier msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, msgSupplier, t);
  }

  public void debug2(final MessageSupplier msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, msgSupplier, (Throwable) null);
  }

  public void debug2(final MessageSupplier msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, msgSupplier, t);
  }

  public void debug2(final Marker marker, final String message, final Object p0) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, p0);
  }

  public void debug2(final Marker marker, final String message, final Object p0, final Object p1) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, p0, p1);
  }

  public void debug2(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, p0, p1, p2);
  }

  public void debug2(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
		     final Object p3) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, p0, p1, p2, p3);
  }

  public void debug2(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
		     final Object p3, final Object p4) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, p0, p1, p2, p3, p4);
  }

  public void debug2(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
		     final Object p3, final Object p4, final Object p5) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, p0, p1, p2, p3, p4, p5);
  }

  public void debug2(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
		     final Object p3, final Object p4, final Object p5,
		     final Object p6) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, p0, p1, p2, p3, p4, p5, p6);
  }

  public void debug2(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
		     final Object p3, final Object p4, final Object p5,
		     final Object p6, final Object p7) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
  }

  public void debug2(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
		     final Object p3, final Object p4, final Object p5,
		     final Object p6, final Object p7, final Object p8) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
  }

  public void debug2(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
		     final Object p3, final Object p4, final Object p5,
		     final Object p6, final Object p7, final Object p8, final Object p9) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
  }

  public void debug2(final String message, final Object p0) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, p0);
  }

  public void debug2(final String message, final Object p0, final Object p1) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, p0, p1);
  }

  public void debug2(final String message, final Object p0, final Object p1, final Object p2) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, p0, p1, p2);
  }

  public void debug2(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, p0, p1, p2, p3);
  }

  public void debug2(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
		     final Object p4) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, p0, p1, p2, p3, p4);
  }

  public void debug2(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
		     final Object p4, final Object p5) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, p0, p1, p2, p3, p4, p5);
  }

  public void debug2(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
		     final Object p4, final Object p5, final Object p6) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, p0, p1, p2, p3, p4, p5, p6);
  }

  public void debug2(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
		     final Object p4, final Object p5, final Object p6,
		     final Object p7) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
  }

  public void debug2(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
		     final Object p4, final Object p5, final Object p6,
		     final Object p7, final Object p8) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
  }

  public void debug2(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
		     final Object p4, final Object p5, final Object p6,
		     final Object p7, final Object p8, final Object p9) {
    logIfEnabled(FQCN, L4JLevel.DEBUG2, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
  }

  public boolean isDebug2Enabled() {
    return isEnabled(L4JLevel.DEBUG2, null, null);
  }

  public boolean isDebug2Enabled(final Marker marker) {
    return isEnabled(L4JLevel.DEBUG2, marker, (Object) null, null);
  }

  public void siteWarning(final Marker marker, final CharSequence message) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, null);
  }

  public void siteWarning(final Marker marker, final CharSequence message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, t);
  }

  public void siteWarning(final Marker marker, final Message msg) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, msg, msg != null ? msg.getThrowable() : null);
  }

  public void siteWarning(final Marker marker, final Message msg, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, msg, t);
  }

  public void siteWarning(final Marker marker, final Object message) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, null);
  }

  public void siteWarning(final Marker marker, final Object message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, t);
  }

  public void siteWarning(final Marker marker, final String message) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, (Throwable) null);
  }

  public void siteWarning(final Marker marker, final String message, final Object... params) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, params);
  }

  public void siteWarning(final Marker marker, final String message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, t);
  }

  public void siteWarning(final Message msg) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, msg, msg != null ? msg.getThrowable() : null);
  }

  public void siteWarning(final Message msg, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, msg, t);
  }

  public void siteWarning(final CharSequence message) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, null);
  }

  public void siteWarning(final CharSequence message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, t);
  }

  public void siteWarning(final Object message) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, null);
  }

  public void siteWarning(final Object message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, t);
  }

  public void siteWarning(final String message) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, (Throwable) null);
  }

  public void siteWarning(final String message, final Object... params) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, params);
  }

  public void siteWarning(final String message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, t);
  }

  public void siteWarning(final Supplier<?> msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, msgSupplier, (Throwable) null);
  }

  public void siteWarning(final Supplier<?> msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, msgSupplier, t);
  }

  public void siteWarning(final Marker marker, final Supplier<?> msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, msgSupplier, (Throwable) null);
  }

  public void siteWarning(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, paramSuppliers);
  }

  public void siteWarning(final Marker marker, final Supplier<?> msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, msgSupplier, t);
  }

  public void siteWarning(final String message, final Supplier<?>... paramSuppliers) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, paramSuppliers);
  }

  public void siteWarning(final Marker marker, final MessageSupplier msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, msgSupplier, (Throwable) null);
  }

  public void siteWarning(final Marker marker, final MessageSupplier msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, msgSupplier, t);
  }

  public void siteWarning(final MessageSupplier msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, msgSupplier, (Throwable) null);
  }

  public void siteWarning(final MessageSupplier msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, msgSupplier, t);
  }

  public void siteWarning(final Marker marker, final String message, final Object p0) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, p0);
  }

  public void siteWarning(final Marker marker, final String message, final Object p0, final Object p1) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, p0, p1);
  }

  public void siteWarning(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, p0, p1, p2);
  }

  public void siteWarning(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			  final Object p3) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, p0, p1, p2, p3);
  }

  public void siteWarning(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			  final Object p3, final Object p4) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, p0, p1, p2, p3, p4);
  }

  public void siteWarning(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			  final Object p3, final Object p4, final Object p5) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, p0, p1, p2, p3, p4, p5);
  }

  public void siteWarning(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			  final Object p3, final Object p4, final Object p5,
			  final Object p6) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, p0, p1, p2, p3, p4, p5, p6);
  }

  public void siteWarning(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			  final Object p3, final Object p4, final Object p5,
			  final Object p6, final Object p7) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
  }

  public void siteWarning(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			  final Object p3, final Object p4, final Object p5,
			  final Object p6, final Object p7, final Object p8) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
  }

  public void siteWarning(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			  final Object p3, final Object p4, final Object p5,
			  final Object p6, final Object p7, final Object p8, final Object p9) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
  }

  public void siteWarning(final String message, final Object p0) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, p0);
  }

  public void siteWarning(final String message, final Object p0, final Object p1) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, p0, p1);
  }

  public void siteWarning(final String message, final Object p0, final Object p1, final Object p2) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, p0, p1, p2);
  }

  public void siteWarning(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, p0, p1, p2, p3);
  }

  public void siteWarning(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			  final Object p4) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, p0, p1, p2, p3, p4);
  }

  public void siteWarning(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			  final Object p4, final Object p5) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, p0, p1, p2, p3, p4, p5);
  }

  public void siteWarning(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			  final Object p4, final Object p5, final Object p6) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, p0, p1, p2, p3, p4, p5, p6);
  }

  public void siteWarning(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			  final Object p4, final Object p5, final Object p6,
			  final Object p7) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
  }

  public void siteWarning(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			  final Object p4, final Object p5, final Object p6,
			  final Object p7, final Object p8) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
  }

  public void siteWarning(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			  final Object p4, final Object p5, final Object p6,
			  final Object p7, final Object p8, final Object p9) {
    logIfEnabled(FQCN, L4JLevel.SITE_WARNING, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
  }

  public boolean isSiteWarningEnabled() {
    return isEnabled(L4JLevel.SITE_WARNING, null, null);
  }

  public boolean isSiteWarningEnabled(final Marker marker) {
    return isEnabled(L4JLevel.SITE_WARNING, marker, (Object) null, null);
  }

  public void siteError(final Marker marker, final CharSequence message) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, null);
  }

  public void siteError(final Marker marker, final CharSequence message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, t);
  }

  public void siteError(final Marker marker, final Message msg) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, msg, msg != null ? msg.getThrowable() : null);
  }

  public void siteError(final Marker marker, final Message msg, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, msg, t);
  }

  public void siteError(final Marker marker, final Object message) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, null);
  }

  public void siteError(final Marker marker, final Object message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, t);
  }

  public void siteError(final Marker marker, final String message) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, (Throwable) null);
  }

  public void siteError(final Marker marker, final String message, final Object... params) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, params);
  }

  public void siteError(final Marker marker, final String message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, t);
  }

  public void siteError(final Message msg) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, msg, msg != null ? msg.getThrowable() : null);
  }

  public void siteError(final Message msg, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, msg, t);
  }

  public void siteError(final CharSequence message) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, null);
  }

  public void siteError(final CharSequence message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, t);
  }

  public void siteError(final Object message) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, null);
  }

  public void siteError(final Object message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, t);
  }

  public void siteError(final String message) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, (Throwable) null);
  }

  public void siteError(final String message, final Object... params) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, params);
  }

  public void siteError(final String message, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, t);
  }

  public void siteError(final Supplier<?> msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, msgSupplier, (Throwable) null);
  }

  public void siteError(final Supplier<?> msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, msgSupplier, t);
  }

  public void siteError(final Marker marker, final Supplier<?> msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, msgSupplier, (Throwable) null);
  }

  public void siteError(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, paramSuppliers);
  }

  public void siteError(final Marker marker, final Supplier<?> msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, msgSupplier, t);
  }

  public void siteError(final String message, final Supplier<?>... paramSuppliers) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, paramSuppliers);
  }

  public void siteError(final Marker marker, final MessageSupplier msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, msgSupplier, (Throwable) null);
  }

  public void siteError(final Marker marker, final MessageSupplier msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, msgSupplier, t);
  }

  public void siteError(final MessageSupplier msgSupplier) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, msgSupplier, (Throwable) null);
  }

  public void siteError(final MessageSupplier msgSupplier, final Throwable t) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, msgSupplier, t);
  }

  public void siteError(final Marker marker, final String message, final Object p0) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, p0);
  }

  public void siteError(final Marker marker, final String message, final Object p0, final Object p1) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, p0, p1);
  }

  public void siteError(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, p0, p1, p2);
  }

  public void siteError(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			final Object p3) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, p0, p1, p2, p3);
  }

  public void siteError(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			final Object p3, final Object p4) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, p0, p1, p2, p3, p4);
  }

  public void siteError(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			final Object p3, final Object p4, final Object p5) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, p0, p1, p2, p3, p4, p5);
  }

  public void siteError(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			final Object p3, final Object p4, final Object p5,
			final Object p6) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6);
  }

  public void siteError(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			final Object p3, final Object p4, final Object p5,
			final Object p6, final Object p7) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
  }

  public void siteError(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			final Object p3, final Object p4, final Object p5,
			final Object p6, final Object p7, final Object p8) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
  }

  public void siteError(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
			final Object p3, final Object p4, final Object p5,
			final Object p6, final Object p7, final Object p8, final Object p9) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
  }

  public void siteError(final String message, final Object p0) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, p0);
  }

  public void siteError(final String message, final Object p0, final Object p1) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, p0, p1);
  }

  public void siteError(final String message, final Object p0, final Object p1, final Object p2) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, p0, p1, p2);
  }

  public void siteError(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, p0, p1, p2, p3);
  }

  public void siteError(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			final Object p4) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, p0, p1, p2, p3, p4);
  }

  public void siteError(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			final Object p4, final Object p5) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, p0, p1, p2, p3, p4, p5);
  }

  public void siteError(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			final Object p4, final Object p5, final Object p6) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, p0, p1, p2, p3, p4, p5, p6);
  }

  public void siteError(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			final Object p4, final Object p5, final Object p6,
			final Object p7) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
  }

  public void siteError(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			final Object p4, final Object p5, final Object p6,
			final Object p7, final Object p8) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
  }

  public void siteError(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
			final Object p4, final Object p5, final Object p6,
			final Object p7, final Object p8, final Object p9) {
    logIfEnabled(FQCN, L4JLevel.SITE_ERROR, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
  }

  public boolean isSiteErrorEnabled() {
    return isEnabled(L4JLevel.SITE_ERROR, null, null);
  }

  public boolean isSiteErrorEnabled(final Marker marker) {
    return isEnabled(L4JLevel.SITE_ERROR, marker, (Object) null, null);
  }

}
