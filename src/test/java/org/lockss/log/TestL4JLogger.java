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

  protected static String lockssSysProp;
  protected static String rootSysProp;
  protected static Level origLockssLevel;
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

  @BeforeAll
  public static void beforeAll() {

    // Record the org.lockss.defaultLogLevel and
    // org.lockss.defaultRootLogLevel sysprops at startup so we know what
    // to expect the default org.lockss and default root levels to be.
    // It's not possible to exercise setting these propoerties in this test
    // code, as the LockssLogger class may already have been initialized
    // before this code is executed.

    lockssSysProp = System.getProperty("org.lockss.defaultLogLevel");
    // Ensure that any default level is legal
    if (StringUtils.isBlank(lockssSysProp)) {
      origLockssLevel = Level.INFO;
    } else {
      try {
	origLockssLevel = LockssLogger.getLog4JLevel(lockssSysProp);
      } catch (Exception e) {
	Assertions.fail("org.lockss.defaultLogLevel set to illegal level string: " +
			lockssSysProp);
      }
    }

    rootSysProp = System.getProperty("org.lockss.defaultRootLogLevel");

    if (StringUtils.isBlank(rootSysProp)) {
      origRootLevel = Level.INFO; // must match root level in log4j2-lockss.xml
    } else {
      try {
	origRootLevel = LockssLogger.getLog4JLevel(LockssLogger.levelOf(rootSysProp));
	System.out.println("origRootLevel: " + origRootLevel);
      } catch (Exception e) {
	Assertions.fail("org.lockss.defaultRootLogLevel set to illegal level string: " +
			rootSysProp);
      }
    }
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
    LockssLogger.setLockssConfig(map);
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
    L4JLogger logLockssDef = getLogger("org.lockss.default");
    L4JLogger logRootDef = getLogger("other.package.log");
    
    assertIsLevel(Level.FATAL, logC);
    assertIsLevel(Level.ERROR, logE);
    assertIsLevel(Level.WARN, logW);
    assertIsLevel(Level.INFO, logI);
    assertIsLevel(Level.DEBUG, logD);
    assertIsLevel(levelDebug2, logD2);
    assertIsLevel(Level.TRACE, logD3);
    assertIsLevel(origLockssLevel, logLockssDef);
    assertIsLevel(origRootLevel, logRootDef);

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
    assertIsLevel(origLockssLevel, logLockssDef);
    assertIsNullOrLevel(origRootLevel, logRootDef);

    getListAppender().reset();
    doLogs(Level.FATAL, logC);
    doLogs(Level.ERROR, logE);
    doLogs(levelDebug2, logW);
    doLogs(Level.INFO, logI);
    doLogs(Level.DEBUG, logD);
    doLogs(Level.WARN, logD2);
    doLogs(Level.TRACE, logD3);
    doLogs(origRootLevel, logRootDef);

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
    assertIsLevel(Level.WARN, logLockssDef);
    assertIsLevel(origRootLevel, logRootDef);

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
    assertIsLevel(origLockssLevel, logLockssDef);
    assertIsNullOrLevel(origRootLevel, logRootDef);

    doLogs(Level.FATAL, logC);
    doLogs(Level.ERROR, logE);
    doLogs(Level.WARN, logW);
    doLogs(Level.INFO, logI);
    doLogs(Level.DEBUG, logT);
    doLogs(levelDebug2, logD2);
    doLogs(Level.TRACE, logD3);
    doLogs(origLockssLevel, logLockssDef);
    doLogs(origRootLevel, logRootDef);

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

  @Test
  public void testShortNames() throws Exception {
    L4JLogger log1 = getLogger("noroot.foo.shortname1");
    L4JLogger log2 = getLogger("noroot.foo.shortname2");
    L4JLogger log3 = getLogger("noroot.foo.shortname3");

    assertIsLevel(Level.WARN, log1);
    assertIsLevel(L4JLevel.DEBUG2, log2);
    assertIsLevel(origRootLevel, log3);
  }
}
