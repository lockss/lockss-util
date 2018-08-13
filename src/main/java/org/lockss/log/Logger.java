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
import java.beans.*;
import java.sql.SQLException;
import java.text.Format;
import java.util.regex.*;

import org.lockss.util.*;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.config.*;

import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 * log4j2 wrapper that provides compatibility with old LOCKSS logger.
 *
 * Assumes that a standard log4j config file gets loaded to set the default
 * (ROOT) log level and establish filter, appenders, etc.


 */
public class Logger {

  public static final String PREFIX = "org.lockss." + "log.";
  static final String PARAM_DEFAULT_LEVEL = PREFIX + "default.level";
  static final String DEFAULT_DEFAULT_LEVEL = "info";

  /** Sets the log level of the named logger */
  static final String PARAM_LOG_LEVEL = PREFIX + "<logname>.level";

  /** Not supported, kept so can log error if used */
  static final String PARAM_LOG_TARGETS = PREFIX + "targets";

  /** System property for name of default log level */
  public static final String SYSPROP_DEFAULT_LOG_LEVEL =
    "org.lockss.defaultLogLevel";

  /** Not supported, kept so can log error if used */
  public static final String SYSPROP_DEFAULT_LOG_TARGET =
    "org.lockss.defaultLogTarget";

  private static final int MIN_LEVEL = 1;

  /** Critical errors require immediate attention from a human. */
  public static final int LEVEL_CRITICAL = 1;
  /** Errors indicate that the system may not operate correctly, but won't
   * damage anything. */
  public static final int LEVEL_ERROR = 2;
  /** Errors caused by misbehavior of some server out of our control. */
  public static final int LEVEL_SITE_ERROR = 3;
  /** Warnings are conditions that should not normally arise but don't
      prevent the system from continuing to run correctly. */
  public static final int LEVEL_WARNING = 4;
  /** Warnings about misbehavior of some server out of our control. */
  public static final int LEVEL_SITE_WARNING = 5;
  /** Informative messages that should normally be logged. */
  public static final int LEVEL_INFO = 6;
  /** Debugging messages. */
  public static final int LEVEL_DEBUG = 7;
  /** Debugging messages. */
  public static final int LEVEL_DEBUG1 = 7;
  /** Detailed debugging that would not produce a ridiculous amount of
   * output if it were enabled system-wide. */
  public static final int LEVEL_DEBUG2 = 8;
  /** Debugging messages that produce more output than would be reasonable
   * if this level were enabled system-wide.  (<i>Eg</i>, messages in inner
   * loops, or per-file, per-hash step, etc.) */
  public static final int LEVEL_DEBUG3 = 9;

  private static final int MAX_LEVEL = 9;

  static final String SITE_ERROR = "SITE_ERROR";
  static final String SITE_WARNING = "SITE_WARNING";
  static final String DEBUG2 = "DEBUG2";
  static final String DEBUG3 = "DEBUG3";

  // TK - These levels are defined in log4j2.xml, but the static init here
  // may (usually does) happen before log4j loads that file, and it assumes
  // the levels have been defined.  It would be better to defer the init so
  // that the values don't need to be repeated here.
  static {
    Level.forName(SITE_ERROR, 210);
    Level.forName(SITE_WARNING, 310);
    Level.forName(DEBUG2, 550);
    Level.forName(DEBUG3, 600);
  }

  // Mapping between numeric level and string
  static LevelDescr[] allLevelDescrs = {
    new LevelDescr(LEVEL_CRITICAL, "Critical", Level.FATAL),
    new LevelDescr(LEVEL_ERROR, "Error", Level.ERROR),
    new LevelDescr(LEVEL_SITE_ERROR, "SiteError", Level.getLevel(SITE_ERROR)),
    new LevelDescr(LEVEL_WARNING, "Warning", Level.WARN),
    new LevelDescr(LEVEL_SITE_WARNING, "SiteWarning",
		   Level.getLevel(SITE_WARNING)),
    new LevelDescr(LEVEL_INFO, "Info", Level.INFO),
    // There must be entries for both "Debug" and "Debug1" in table.
    // Whichever string is last will be used in messages
    new LevelDescr(LEVEL_DEBUG1, "Debug1", Level.DEBUG),
    new LevelDescr(LEVEL_DEBUG, "Debug", Level.DEBUG),
    new LevelDescr(LEVEL_DEBUG2, "Debug2", Level.getLevel(DEBUG2)),
    new LevelDescr(LEVEL_DEBUG3, "Trace", Level.TRACE),
    new LevelDescr(LEVEL_DEBUG3, "Debug3", Level.getLevel(DEBUG3)),
  };

  // Array of LevelDescr indexed by LOCKSS Logger level
  static LevelDescr levelDescrs[] = new LevelDescr[MAX_LEVEL + 1];
  static {
    for (LevelDescr ld : allLevelDescrs) {
      levelDescrs[ld.level] = ld;
    }
  }

  // Map LevelDescr indexed by LOCKSS Logger level
  static Map<Level,LevelDescr> log4jLevelDescrs = new HashMap<>();
  static {
    for (LevelDescr ld : allLevelDescrs) {
      log4jLevelDescrs.put(ld.log4jLevel, ld);
    }
  }

  // Default default log level if config parameter not set.
  public static final int DEFAULT_LEVEL = LEVEL_INFO;

  private static Map<String, Logger> logs = new HashMap<String, Logger>();

  /** Experimental for use in unit tests */
  public static void resetLogs() {
//     logs = new HashMap<String, Logger>();
  }

  private static boolean deferredInitDone = false;
  protected static Logger myLog;

  private org.apache.logging.log4j.Logger log;

  private String name;			// this log's name
  private boolean idThread = true;

  /** Create a LOCKSS logger wrapping the log4j logger */
  protected Logger(org.apache.logging.log4j.Logger log) {
    this.log = log;
  }

  /**
   * Logger factory.  Return the unique instance
   * of <code>Logger</code> with the given name, creating it if necessary.
   * @param name identifies the log instance, appears in output
   */
  public static Logger getLogger(String name) {
    return getWrappedLogger(name);
  }

  /**
   * <p>Convenience method to name a logger after a class.
   * Simply calls {@link #getLogger(String)} with the result of
   * {@link Class#getSimpleName()}.</p>
   * @param clazz The class after which to name the returned logger.
   * @return A logger named after the given class.
   * @since 1.56
   */
  public static Logger getLogger(Class<?> clazz) {
    return getLogger(clazz.getSimpleName());
  }

  private static void deferredInit() {
    if (!deferredInitDone) {

      deferredInitDone = true;
      myLog = Logger.getWrappedLogger("Logger");

      // Must set this true before calling getWrappedLogger or will
      // recurse.  Rest of this is careful not to need the deferred init to
      // be done.
      getLoggerContext().addPropertyChangeListener(new PropertyChangeListener() {
	  @Override
	  public void propertyChange(final PropertyChangeEvent evt) {
// 	    myLog.debug("event: " + evt);
	    switch (evt.getPropertyName()) {
	    case LoggerContext.PROPERTY_CONFIG:
	      installLockssLevels(false);
	    }
	  }
	});

      // Must process defaultLogLevel sysprop at startup, as otherwise
      // wouldn't take effect until first config load
      String spLevel = System.getProperty(SYSPROP_DEFAULT_LOG_LEVEL);
      if (!StringUtils.isBlank(spLevel)) {
	try {
	  dynamicRootLevel = getLog4JLevel(spLevel);
	  installLockssLevels(false);
	} catch (IllegalLevelException e) {
	  // fall through
	}
      }
      if (!StringUtils.isBlank(System.getProperty(SYSPROP_DEFAULT_LOG_TARGET))) {
	myLog.error(SYSPROP_DEFAULT_LOG_TARGET +
		    " sysprop not supported; use log4j2 config instead: " +
		    System.getProperty(SYSPROP_DEFAULT_LOG_TARGET),
		    new Throwable());
      }
    }
  }

  /**
   * Special purpose Logger factory.  Return the unique instance
   * of <code>Logger</code> with the given name, creating it if necessary.
   * This is here primarily so <code>Configuration</code> can create a
   * log without being invoked recursively, which causes its class
   * initialization to not complete correctly.
   * @param name identifies the log instance, appears in output
   * @param initialLevel the initial log level (<code>Logger.LEVEL_XXX</code>).
   */
  protected static Logger getWrappedLogger(String name) {
    return getWrappedLogger(name, (s) -> new Logger(LogManager.getLogger(s)));
  }

  protected static Logger getWrappedLogger(String name,
					   java.util.function.Function<String,Logger> factory) {
    deferredInit();
    // This method MUST NOT make any reference to Configuration !!
    if (name == null) {
      name = genName();
    }
    Logger res = logs.get(name);
    if (res == null) {
      res = factory.apply(name);
      if (myLog != null) myLog.debug2("Creating logger: " + name);
      logs.put(name, res);
    }
    return res;
  }

  static int uncnt = 0;
  static String genName() {
    return "Unnamed" + ++uncnt;
  }

  /** Return numeric log level (<code>Logger.LEVEL_XXX</code>) for given name.
   */
  public static int levelOf(String name) throws IllegalLevelException {
    for (LevelDescr ld : allLevelDescrs) {
      if (ld != null && ld.name.equalsIgnoreCase(name)) {
	return ld.level;
      }
    }
    throw new IllegalLevelException("Log level not found: " + name);
  }

  /** Return name of given log level (<code>Logger.LEVEL_XXX</code>).
   */
  public static String nameOf(int level) {
    LevelDescr ld = levelDescrs[level];
    if (ld == null) {
      return "Unknown";
    }
    return ld.name;
  }

  /**
   * Set minimum severity level logged by this log
   * @param level <code>Logger.LEVEL_XXX</code>
   */
  public void setLevel(int level) {
    if (getLevel() != level) {
      info("Changing log level to " + nameOf(level));
      Configurator.setLevel(log.getName(), getLog4JLevel(level));
    }
  }

  public int getLevel() {
    return getLockssLevel(log.getLevel());
  }

  /**
   * Set minimum severity level logged by this log
   * @param levelName level string
   */
  public void setLevel(String levelName) throws IllegalLevelException {
    setLevel(levelOf(levelName));
  }

  /**
   * Return true if this log is logging at or above specified level
   * Use this in cases where generating the log message is expensive,
   * to avoid the overhead when the message will not be output.
   * @param level (<code>Logger.LEVEL_XXX</code>)
   */
  public boolean isLevel(int level) {
    return getLevel() >= level;
  }

  /** Common case of </code>isLevel()</code> */
  public boolean isDebug() {
    return isLevel(LEVEL_DEBUG);
  }

  /** Common case of </code>isLevel()</code> */
  public boolean isDebug1() {
    return isLevel(LEVEL_DEBUG1);
  }

  /** Common case of </code>isLevel()</code> */
  public boolean isDebug2() {
    return isLevel(LEVEL_DEBUG2);
  }

  /** Common case of </code>isLevel()</code> */
  public boolean isDebug3() {
    return isLevel(LEVEL_DEBUG3);
  }

  /**
   * Log a message with the specified log level
   * @param level log level (<code>Logger.LEVEL_XXX</code>)
   * @param msg log message
   * @param e <code>Throwable</code>
   */
  public void log(int level, String msg, Throwable e) {
    log.log(getLog4JLevel(level), msg, e);
  }

  /** Return the log4j Level corresponding to the LOCKSS integer log level.
   * If out of range, return the closest Level (min or max) */
  static Level getLog4JLevel(int level) {
    LevelDescr ld = level < MIN_LEVEL ? levelDescrs[MIN_LEVEL] :
      (level > MAX_LEVEL ? levelDescrs[MAX_LEVEL] : levelDescrs[level]);
    return ld.log4jLevel;
  }

  static int getLockssLevel(Level log4jLevel) {
    LevelDescr ld = log4jLevelDescrs.get(log4jLevel);
    return ld != null ? ld.level : LEVEL_CRITICAL;
  }

  static Level getLog4JLevel(String levelName) throws IllegalLevelException {
    return getLog4JLevel(levelOf(levelName));
  }

  /**
   * Log a message with the specified log level
   * @param level log level (<code>Logger.LEVEL_XXX</code>)
   * @param msg log message
   */
  public void log(int level, String msg) {
    log(level, msg, null);
  }

  static Map<String,Level> dynamicLevels;
  static Level dynamicRootLevel;

  protected static final Pattern LOG_LEVEL_PAT =
    Pattern.compile("org\\.lockss\\.log\\.(.*)\\.level");

  /** Set all log levels specified in the map of LOCKSS-style config values.
   * @param lconfig map of LOCKSS log config params (e.g.,
   *                <code>org.lockss.log.<i>log-name</i>.level</code>) ->
   *                LOCKSS level name.  This is expected to be the subset
   *                of the LOCKSS config relevant to logging.
   */
  public static void setLockssConfig(Map<String,String> lconfig) {
    myLog.debug2("setLockssConfig: " + lconfig);
    Map<String,String> dynLevels = new HashMap<>();
    for (Map.Entry<String,String> ent : lconfig.entrySet()) {
      String key = ent.getKey();
      Matcher mat = LOG_LEVEL_PAT.matcher(key);
      if (mat.matches()) {
	String logname = mat.group(1);
	String val = ent.getValue();
	if (StringUtils.isBlank(logname)) {
	  myLog.error("Illegal log name: " + key);
	  continue;
	}
	dynLevels.put(logname, val);
      }
    }
    if (!StringUtils.isBlank(lconfig.get(PARAM_LOG_TARGETS))) {
      myLog.error(PARAM_LOG_TARGETS +
		  " param not supported; use log4j2 config instead");
    }
    setDynamicLevels(dynLevels, lconfig.get(PARAM_DEFAULT_LEVEL));
  }

  /** Set all log levels specified in the map.
   * @param levels map of logger name -> LOCKSS level name
   */
  public static void setDynamicLevels(Map<String,String> levels,
				      String defLevel) {
    myLog.debug2("setDynamicLevels: " + levels + ", " + defLevel);
    Map<String,Level> map = new HashMap<>();
    for (String key : levels.keySet()) {
      try {
	map.put(key, getLog4JLevel(levels.get(key)));
      } catch (IllegalLevelException e) {
	myLog.error("Ignoring illegal dynamic log level: " + key);
	continue;
      }
    }
    // if any previously set levels are no long set, need to reload the
    // config from scratch to restore levels set in config file.
    boolean needReload =
      dynamicLevels != null &&
      dynamicLevels.keySet().stream().anyMatch(name -> !map.containsKey(name));
    dynamicLevels = map;
    Level newRootLevel = null;
    if (!StringUtils.isBlank(defLevel)) {
      try {
	newRootLevel = getLog4JLevel(defLevel);
      } catch (IllegalLevelException e) {
	// fall through
      }
    }
    String sp = System.getProperty(SYSPROP_DEFAULT_LOG_LEVEL);
    if (!StringUtils.isBlank(sp)) {
      try {
	newRootLevel = getLog4JLevel(sp);
      } catch (IllegalLevelException e) {
	// fall through
      }
    }
    if (newRootLevel == null && dynamicRootLevel != null) {
      needReload = true;
    }
    dynamicRootLevel = newRootLevel;
    installLockssLevels(needReload);
  }

  private static void installLockssLevels(boolean needReload) {
    if (needReload) {
      forceReload();
    }
    if (dynamicRootLevel != null) {
      myLog.debug2("setRootLevel: " + dynamicRootLevel);
      Configurator.setRootLevel(dynamicRootLevel);
    }
    if (dynamicLevels != null) {
      Configurator.setLevel(dynamicLevels);
    }
  }

  private static LoggerContext getLoggerContext() {
    return (LoggerContext)LogManager.getContext(false);
  }

  public static void forceReload() {
    getLoggerContext().reconfigure();
  }

  org.apache.logging.log4j.Logger getLog4Logger() {
    return log;
  }

  public String toString() {
    return "[Logger " + log.getName() + "]";
  }

  // log instance methods
  /** Log a critical message */
  public void critical(String msg) {
    log(LEVEL_CRITICAL, msg, null);
  }

  /** Log a critical message with an exception backtrace */
  public void critical(String msg, Throwable e) {
    log(LEVEL_CRITICAL, msg, e);
  }

  /** Log an error message */
  public void error(String msg) {
    log(LEVEL_ERROR, msg, null);
  }

  /** Log an error message with an exception backtrace */
  public void error(String msg, Throwable e) {
    log(LEVEL_ERROR, msg, e);
  }

  /** Log a site error message */
  public void siteError(String msg) {
    log(LEVEL_SITE_ERROR, msg, null);
  }

  /** Log a site error message with an exception backtrace */
  public void siteError(String msg, Throwable e) {
    log(LEVEL_SITE_ERROR, msg, e);
  }

  /** Log a warning message */
  public void warning(String msg) {
    log(LEVEL_WARNING, msg, null);
  }

  /** Log a warning message with an exception backtrace */
  public void warning(String msg, Throwable e) {
    log(LEVEL_WARNING, msg, e);
  }

  /** Log a site warning message */
  public void siteWarning(String msg) {
    log(LEVEL_SITE_WARNING, msg, null);
  }

  /** Log a site warning message with an exception backtrace */
  public void siteWarning(String msg, Throwable e) {
    log(LEVEL_SITE_WARNING, msg, e);
  }

  /** Log an information message */
  public void info(String msg) {
    log(LEVEL_INFO, msg, null);
  }

  /** Log an information message with an exception backtrace */
  public void info(String msg, Throwable e) {
    log(LEVEL_INFO, msg, e);
  }

  /** Log a level 1 debug message */
  public void debug(String msg) {
    log(LEVEL_DEBUG, msg, null);
  }

  /** Log a level 1 debug message with an exception backtrace */
  public void debug(String msg, Throwable e) {
    log(LEVEL_DEBUG, msg, e);
  }

  /** Log a level 1 debug message */
  public void debug1(String msg) {
    log(LEVEL_DEBUG1, msg, null);
  }

  /** Log a level 1 debug message with an exception backtrace */
  public void debug1(String msg, Throwable e) {
    log(LEVEL_DEBUG1, msg, e);
  }

  /** Log a level 2 debug message */
  public void debug2(String msg) {
    log(LEVEL_DEBUG2, msg, null);
  }

  /** Log a level 2 debug message with an exception backtrace */
  public void debug2(String msg, Throwable e) {
    log(LEVEL_DEBUG2, msg, e);
  }

  /** Log a level 3 debug message */
  public void debug3(String msg) {
    log(LEVEL_DEBUG3, msg, null);
  }

  /** Log a level 3 debug message with an exception backtrace */
  public void debug3(String msg, Throwable e) {
    log(LEVEL_DEBUG3, msg, e);
  }

  public static class IllegalLevelException extends Exception {
    public IllegalLevelException(String msg) {
      super(msg);
    }
  }

  // log level descriptor class
  private static class LevelDescr {
    int level;
    String name;
    Level log4jLevel;

    LevelDescr(int level, String name, Level log4jLevel) {
    this.level = level;
    this.name = name;
    this.log4jLevel = log4jLevel;
    }
    public String toString() {
      return "[ld: " + level + ", " + name + ", " + log4jLevel + "]";
    }
  }

}
