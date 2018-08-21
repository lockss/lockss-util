/*

Copyright (c) 2000-2018, Board of Trustees of Leland Stanford Jr. University,
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.log;

import java.util.*;

import org.apache.commons.collections4.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.lockss.util.test.LockssTestCase5;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.config.*;

import org.lockss.util.*;

public class TestL4JLogger extends LockssTestCase5 {

  protected static String origSysProp;
  protected static Level origRootLevel;

  /** Return the ListAppender created by log4j2-logger-test.xml */
  protected static ListAppender getListAppender() {
    LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
    Configuration config = ctx.getConfiguration();
    return (ListAppender)config.getAppenders().get("ListAppender");
  }

  /** Get the named Logger */
  protected L4JLogger getLogger(String name) {
    org.apache.logging.log4j.Logger res = LogManager.getLogger(name);
    assertTrue(res instanceof L4JLogger);
    return (L4JLogger)res;
  }

  /** Add ListAppender to record output to loggers below
   * org.lockss.testlogger */
  @BeforeAll
  public static void beforeAll() throws Exception {

    // Record the org.lockss.defaultLogLevel sysprop at startup so we know
    // what to expect the default (root) log level to be.  Actually varying
    // the initial default log level is beyond the scope of this code, as
    // the Logger class may already have been initialized before this code
    // is executed.
    origSysProp = System.getProperty("org.lockss.defaultLogLevel");

    // If default level is illegal ignore it
    if (StringUtils.isBlank(origSysProp)) {
      origRootLevel = null;
    } else {
      try {
	origRootLevel = Logger.getLog4JLevel(Logger.levelOf(origSysProp));
      } catch (Exception e) {
	Assertions.fail("org.lockss.defaultLogLevel set to illegal level string: " +
			origSysProp);
      }
    }

    // Add testing config file to logj4's config.
    System.setProperty("log4j.configurationFile",
		       "log4j2.xml,log4j2-logger-test.xml");

    getLoggerContext().reconfigure();
  }

  private static LoggerContext getLoggerContext() {
    return (LoggerContext)LogManager.getContext(false);
  }

  Level levelDebug2, levelSiteWarn, levelSiteError;

  @BeforeEach
  public void setUpCustomLevels() {
    levelDebug2 = Level.getLevel("DEBUG2");
    levelSiteWarn = Level.getLevel("SITE_WARNING");
    levelSiteError = Level.getLevel("SITE_ERROR");
  }

  void assertIsNullOrLevel(Level level, L4JLogger log) {
    if (level == null) return;
    assertIsLevel(level, log);
  }

  void assertIsLevel(Level level, L4JLogger log) {
    assertEquals(level, log.getLevel());
    assertTrue(log.isEnabled(level));
  }

  void doLogs(Level level, L4JLogger log) {
    if (level == Level.FATAL) {
      log.fatal("crit");
      log.error("err");
    } else if (level == Level.ERROR) {
      log.fatal("crit");
      log.error("err");
      log.warn("warn");
    } else if (level == Level.WARN) {
      log.error("err");
      log.warn("warn");
      log.info("info");
    } else if (level == Level.INFO) {
      log.warn("warn");
      log.info("info");
      log.debug("debug");
    } else if (level == Level.DEBUG) {
      log.info("info");
      log.debug("debug");
      log.debug2("debug2");
    } else if (level == levelDebug2) {
      log.debug("debug");
      log.debug2("debug2");
      log.trace("trace");
    } else if (level == Level.TRACE) {
      log.debug2("debug2");
      log.trace("trace");
    }
  }

  protected void setConfig(Map<String,String> map) {
    Logger.setLockssConfig(map);
  }

  @Test
  public void testFactories() throws Exception {
    L4JLogger l1 = L4JLogger.getLogger("name1");
    assertEquals("name1", l1.getName());
    assertSame(l1, L4JLogger.getLogger("name1"));
    assertSame(l1, getLogger("name1"));

    L4JLogger l2 = L4JLogger.getLogger();
    assertEquals("org.lockss.log.TestL4JLogger", l2.getName());
    assertSame(l2, getLogger(l2.getName()));
    assertSame(l2, L4JLogger.getLogger(TestL4JLogger.class));

  }

  @Test
  public void testFunc() throws Exception {
    L4JLogger logT = getLogger("test");
    L4JLogger logC = getLogger("test.critical.c1");
    L4JLogger logE = getLogger("test.error");
    L4JLogger logW = getLogger("test.warning.w1");
    L4JLogger logI = getLogger("test.info.foo");
    L4JLogger logD = getLogger("test.debug.d.e.f");
    L4JLogger logD2 = getLogger("test.debug2.lll");
    L4JLogger logD3 = getLogger("test.trace.fff");
    L4JLogger logDef = getLogger("default");
    
    assertIsLevel(Level.FATAL, logC);
    assertIsLevel(Level.ERROR, logE);
    assertIsLevel(Level.WARN, logW);
    assertIsLevel(Level.INFO, logI);
    assertIsLevel(Level.DEBUG, logD);
    assertIsLevel(levelDebug2, logD2);
    assertIsLevel(Level.TRACE, logD3);
    assertIsNullOrLevel(origRootLevel, logDef);

    doLogs(Level.FATAL, logC);
    doLogs(Level.ERROR, logE);
    doLogs(Level.WARN, logW);
    doLogs(Level.INFO, logI);
    doLogs(Level.DEBUG, logD);
    doLogs(levelDebug2, logD2);
    doLogs(Level.TRACE, logD3);

    List<String> expDefault =
      ListUtil.list("crit", "crit", "err", "err", "warn", "warn",
		    "info", "info", "debug", "debug",
		    "debug2", "debug2", "trace");

    assertEquals(expDefault, getListAppender().getMessages());

    getListAppender().reset();
    assertEmpty(getListAppender().getMessages());

    Map<String,String> newConfig = new HashMap<String,String>() {{
	put("org.lockss.log.test.debug2.level", "warning");
	put("org.lockss.log.test.warning.level", "debug2");
      }};

    setConfig(newConfig);
    assertEmpty(getListAppender().getMessages());

    assertIsLevel(Level.FATAL, logC);
    assertIsLevel(Level.ERROR, logE);
    assertIsLevel(levelDebug2, logW);
    assertIsLevel(Level.INFO, logI);
    assertIsLevel(Level.DEBUG, logD);
    assertIsLevel(Level.WARN, logD2);
    assertIsLevel(Level.TRACE, logD3);
    assertIsNullOrLevel(origRootLevel, logDef);

    getListAppender().reset();
    doLogs(Level.FATAL, logC);
    doLogs(Level.ERROR, logE);
    doLogs(levelDebug2, logW);
    doLogs(Level.INFO, logI);
    doLogs(Level.DEBUG, logD);
    doLogs(Level.WARN, logD2);
    doLogs(Level.TRACE, logD3);
    doLogs(origRootLevel, logDef);

    List<String> expNew =
      ListUtil.list("crit", "crit", "err", "debug", "debug2",
		    "warn", "info", "info", "debug",
		    "err", "warn", "debug2", "trace");

    assertEquals(expNew, getListAppender().getMessages());

    getListAppender().reset();
    newConfig.put("org.lockss.log.default.level", "warning");
    setConfig(newConfig);

    assertIsLevel(Level.FATAL, logC);
    assertIsLevel(Level.ERROR, logE);
    assertIsLevel(levelDebug2, logW);
    assertIsLevel(Level.INFO, logI);
    assertIsLevel(Level.DEBUG, logT);
    assertIsLevel(Level.WARN, logD2);
    assertIsLevel(Level.TRACE, logD3);
    assertIsLevel(Level.WARN, logDef);

    doLogs(Level.FATAL, logC);
    doLogs(Level.ERROR, logE);
    doLogs(levelDebug2, logW);
    doLogs(Level.INFO, logI);
    doLogs(Level.DEBUG, logD);
    doLogs(Level.WARN, logD2);
    doLogs(Level.TRACE, logD3);

    expNew =
      ListUtil.list("crit", "crit", "err", "debug", "debug2",
		    "warn", "info", "info", "debug",
		    "err", "warn", "debug2", "trace");

    assertEquals(expNew, getListAppender().getMessages());

    getListAppender().reset();
    setConfig(new HashMap());

    assertIsLevel(Level.FATAL, logC);
    assertIsLevel(Level.ERROR, logE);
    assertIsLevel(Level.WARN, logW);
    assertIsLevel(Level.INFO, logI);
    assertIsLevel(Level.DEBUG, logT);
    assertIsLevel(levelDebug2, logD2);
    assertIsLevel(Level.TRACE, logD3);
    assertIsNullOrLevel(origRootLevel, logDef);

    doLogs(Level.FATAL, logC);
    doLogs(Level.ERROR, logE);
    doLogs(Level.WARN, logW);
    doLogs(Level.INFO, logI);
    doLogs(Level.DEBUG, logT);
    doLogs(levelDebug2, logD2);
    doLogs(Level.TRACE, logD3);
    doLogs(origRootLevel, logDef);

    assertEquals(expDefault, getListAppender().getMessages());
  }

  @Test
  public void testCustomLevels() throws Exception {
    L4JLogger logD2 = getLogger("test.debug2.x.x");
    L4JLogger logSW = getLogger("test.sw.123");
    L4JLogger logSE = getLogger("test.se");
    L4JLogger logC = getLogger("test.critical");

    getListAppender().reset();
    logD2.siteWarning("abc");
    String a123 = "123";
    logSE.siteError(() -> {return a123 + "45";});
    logSE.debug2("nope");
    logSE.siteWarning("nope");
    logD2.siteError("eee");
    logD2.siteError("fff", new Throwable());

    List<String> exp =
      ListUtil.list("SITE_WARNING: abc",
		    "SITE_ERROR: 12345",
		    "SITE_ERROR: eee",
		    "SITE_ERROR: fff"
		    );

    assertEquals(exp, getListAppender().getLevelMessages());
  }
}
