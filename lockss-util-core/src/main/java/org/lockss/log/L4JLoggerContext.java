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

import java.net.URI;
import java.util.*;

import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.util.CronExpression;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.status.StatusLogger;

import org.lockss.util.time.*;

/**
 * This LoggerContext:<ul>
 *
 * <li>arranges for Loggers returned by LogManager.getLogger() to be
 * instances of L4JLogger.</li>
 *
 * <li>maintains a map (nameMap) of logger short name to fully-qualified
 * name, added to as loggers are created.  This facilitates setting log
 * levels using only the short name.</li>
 *
 * <li>stackLevelMap contains {@value PARAM_STACKTRACE_LEVEL} and {@value
 * PARAM_STACKTRACE_SEVERITY}, because this is an easy place for {@link
 * L4JContextDataInjector} to find them.</li>
 *
 * <li>Logs a Timestamp message when first started.  (This turns out to be
 * a good place to log a startup message no matter what sort of logger is
 * created first.)</li>
 *
 * <li>Arranges for a Timestamp message to be logged at midnight</li>
 * </ul>
 */
public class L4JLoggerContext extends LoggerContext {

  private static org.apache.logging.log4j.Logger log =
    StatusLogger.getLogger();

  private static Marker TS_MARKER = MarkerManager.getMarker("Timestamp");

  // Each short name may map to more than one fq name.  <i>Eg</i>, creating
  // loggers <tt>org.lockss.log.Logger</tt> and
  // <tt>org.lockss.util.Logger</tt> results in <tt>Logger</tt> being
  // mapped to both fq names
  private SetValuedMap<String,String> nameMap = new HashSetValuedHashMap<>();

  private Map<String,Level> stackLevelMap = null;

  public L4JLoggerContext(final String name,
			  final Object externalContext,
			  final URI configLocn) {
    super(name, externalContext, configLocn);
  }

  public L4JLoggerContext(final String name) {
    super(name);
  }

  private boolean once = true;

  private synchronized boolean once() {
    boolean ret = once;
    once = false;
    return ret;
  }

  @Override
  protected org.apache.logging.log4j.core.Logger
    newInstance(final LoggerContext ctx,
		final String name,
		final MessageFactory messageFactory) {
    if (once()) {
      // Log a Timestamp: message at startup
      L4JLogger tslog = new L4JLogger(ctx, "Timestamp", messageFactory);
      FastDateFormat df =
	FastDateFormat.getInstance("EEE dd MMM yyyy HH:mm:ss zzz");
      tslog.info(TS_MARKER, df.format(TimeBase.nowDate()) + "\n");

      // Schedule a Timestamp message every midnight.
      try {
	ConfigurationScheduler scheduler = getConfiguration().getScheduler();
        if (!scheduler.isExecutorServiceSet()) {
	  // make sure we have a thread pool
	  scheduler.incrementScheduledItems();
        }
        if (!scheduler.isStarted()) {
	  scheduler.start();
        }
	scheduler.scheduleWithCron(new CronExpression("0 0 0 * * ?"),
				   new Runnable() {
	    public void run() {
	      tslog.info(TS_MARKER, df.format(TimeBase.nowDate()) + "\n");
	    }});
      } catch (java.text.ParseException e) {
	log.warn("Can't schedule midnight timestamp", e);
      }

    }
    updateNameMap(name);
    org.apache.logging.log4j.core.Logger res =
      new L4JLogger(ctx, name, messageFactory);
    return res;
  }

  /** Return Collection of fully-qualified names of loggers whose last
   * component matches <tt>shortName</tt>.
   */
  public synchronized Collection<String> getFQNames(String shortName) {
    Set<String> res = nameMap.get(shortName);
    return res != null
      ? Collections.unmodifiableSet(res)
      : Collections.emptyList();
  }

  /** Set the configured logger levels. */
  public void setStackLevelMap(Map<String,Level> stackLevelMap) {
    this.stackLevelMap = stackLevelMap;
    log.debug("L4JLoggerContext: Set stackLevelMap: {}", stackLevelMap);
  }

  /** Return the configured logger levels (and {@value
   * PARAM_STACKTRACE_LEVEL} and {@value PARAM_STACKTRACE_SEVERITY}). */
  public Map<String,Level> getStackLevelMap() {
    return stackLevelMap;
  }

  /** For any LoggerConfig with an unqualified name, set the level
   * of the LoggerConfig corresponding to each fq name ending with
   * the unqualified name */
  public void setFqLevels() {
    Map<String,LoggerConfig> map = getConfiguration().getLoggers();
    for (String logname : map.keySet()) {
      if (logname.indexOf(".") < 0) {
	// no dot. Get its LoggerConfig.
	LoggerConfig lcfg = map.get(logname);
	Level shortLevel = lcfg.getLevel();
	// Want to do this only if the short name's LoggerConfig has a
	// level explicitly set, but don't know how to do that, as
	// getLevel() inherits from parent.
	if (shortLevel != null) {
	  for (String fqName : getFQNames(logname)) {
	    // If the fq logname has its own LoggerConfig, let it
	    // take precedence.
	    if (!fqName.equals(getConfiguration().getLoggerConfig(fqName).getName())) {
	      log.debug("L4JLoggerContext: Set level {} to {}",
			fqName, shortLevel);
	      Configurator.setLevel(fqName, shortLevel);
	    }	    
	  }
	}
      }
    }
  }


  /** Add a shortName -> fqName mapping for a newly created logger.  If
   * this is the first time we've see this fqName (why wouldn't it be?),
   * and the shortname has a configured level, set the level for the new
   * fq name. */
  private synchronized void updateNameMap(String fqName) {
    if (fqName.indexOf(".") > 0) {
      String shortName = fqName.substring(fqName.lastIndexOf('.')+1);
      if (nameMap.put(shortName, fqName)) {
	log.trace("L4JLoggerContext: Added {} -> {}", shortName, fqName);

	LoggerConfig lcfg = getConfiguration().getLoggerConfig(shortName);
	if (shortName.equals(lcfg.getName())
	    && !fqName.equals(getConfiguration().getLoggerConfig(fqName).getName())) {
	  Level shortLevel = lcfg.getLevel();
	  log.debug("L4JLoggerContext: Set level {} to {}",
		    fqName, shortLevel);
	  Configurator.setLevel(fqName, shortLevel);
	}
      }
    }
  }
}
