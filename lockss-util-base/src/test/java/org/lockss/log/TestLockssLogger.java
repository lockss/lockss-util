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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.config.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestLockssLogger {

  protected static String lockssSysProp;
  protected static String rootSysProp;
  protected static int origLockssLevel;
  protected static int origRootLevel;

  /** Return the ListAppender created by log4j2-logger-test.xml */
  protected static ListAppender getListAppender() {
    LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
    Configuration config = ctx.getConfiguration();
    return (ListAppender)config.getAppenders().get("ListAppender");
  }

  /** Get the named LockssLogger */
  protected LockssLogger getLogger(String name) {
    LockssLogger res = LockssLogger.getLogger(name);
    return res;
  }

  @BeforeAll
  public static void beforeAll() throws Exception {
    commonBeforeAll();
    LockssLogger.forceReload();
  }

  public static void commonBeforeAll() {

    // Record the org.lockss.defaultLogLevel and
    // org.lockss.defaultRootLogLevel sysprops at startup so we know what
    // to expect the default org.lockss and default root levels to be.
    // It's not possible to exercise setting these properties in this test
    // code, as the LockssLogger class may already have been initialized
    // before this code is executed.

    lockssSysProp = System.getProperty("org.lockss.defaultLogLevel");
    // Ensure that any default level is legal
    if (StringUtils.isBlank(lockssSysProp)) {
      origLockssLevel = LockssLogger.LEVEL_INFO;
    } else {
      try {
        origLockssLevel = LockssLogger.levelOf(lockssSysProp);
      } catch (Exception e) {
        fail("org.lockss.defaultLogLevel set to illegal level string: " +
                        lockssSysProp);
      }
    }

    rootSysProp = System.getProperty("org.lockss.defaultRootLogLevel");

    if (StringUtils.isBlank(rootSysProp)) {
      origRootLevel = LockssLogger.LEVEL_INFO; System.err.println("DEFAULT: INFO");
    } else {
      try {
        origRootLevel = LockssLogger.levelOf(rootSysProp); System.err.println("SYSPROP: " + rootSysProp);
      } catch (Exception e) {
        fail("org.lockss.defaultRootLogLevel set to illegal level string: " +
                        rootSysProp);
      }
    }
  }

  /*
   * When our logging and testing utilities were both in lockss-util-core, this
   * was part of the behind-the-scenes work of LockssTestCase5, but testFunc()
   * doesn't quite work without this (assertIsLevel(origRootLevel, logRootDef)
   * fails).
   */
  @BeforeEach
  public void beforeEach_resetLogs() {
    LockssLogger.resetLogs();
  }
  
  void assertIsLevel(int level, LockssLogger log) {
    assertEquals(level, log.getLevel());
    assertTrue(log.isLevel(level));
    assertTrue(log.isLevel(level - 1));
    assertFalse(log.isLevel(level + 1));
  }

  @Test
  public void testLevelOf() throws LockssLogger.IllegalLevelException {
    assertEquals(LockssLogger.LEVEL_CRITICAL, LockssLogger.levelOf("critical"));
    assertEquals(LockssLogger.LEVEL_ERROR, LockssLogger.levelOf("error"));
    assertEquals(LockssLogger.LEVEL_SITE_ERROR, LockssLogger.levelOf("siteError"));
    assertEquals(LockssLogger.LEVEL_WARNING, LockssLogger.levelOf("warning"));
    assertEquals(LockssLogger.LEVEL_SITE_WARNING, LockssLogger.levelOf("siteWarning"));
    assertEquals(LockssLogger.LEVEL_INFO, LockssLogger.levelOf("info"));
    assertEquals(LockssLogger.LEVEL_DEBUG, LockssLogger.levelOf("debug"));
    assertEquals(LockssLogger.LEVEL_DEBUG1, LockssLogger.levelOf("debug1"));
    assertEquals(LockssLogger.LEVEL_DEBUG2, LockssLogger.levelOf("debug2"));
    assertEquals(LockssLogger.LEVEL_DEBUG3, LockssLogger.levelOf("debug3"));
    assertThrows(LockssLogger.IllegalLevelException.class,
                 () -> {LockssLogger.levelOf("nonesuch");});
  }

  @Test
  public void testNameOf() {
    assertEquals("Critical", LockssLogger.nameOf(LockssLogger.LEVEL_CRITICAL));
    assertEquals("Error", LockssLogger.nameOf(LockssLogger.LEVEL_ERROR));
    assertEquals("SiteError", LockssLogger.nameOf(LockssLogger.LEVEL_SITE_ERROR));
    assertEquals("Warning", LockssLogger.nameOf(LockssLogger.LEVEL_WARNING));
    assertEquals("SiteWarning", LockssLogger.nameOf(LockssLogger.LEVEL_SITE_WARNING));
    assertEquals("Info", LockssLogger.nameOf(LockssLogger.LEVEL_INFO));
    assertEquals("Debug", LockssLogger.nameOf(LockssLogger.LEVEL_DEBUG));
    assertEquals("Debug", LockssLogger.nameOf(LockssLogger.LEVEL_DEBUG1));
    assertEquals("Debug2", LockssLogger.nameOf(LockssLogger.LEVEL_DEBUG2));
    assertEquals("Debug3", LockssLogger.nameOf(LockssLogger.LEVEL_DEBUG3));
  }


  void doLogs(int level, LockssLogger log) {
    switch (level) {
    case LockssLogger.LEVEL_CRITICAL:
      log.critical("crit");
      log.error("err");
      break;
    case LockssLogger.LEVEL_ERROR:
      log.critical("crit");
      log.error("err");
      log.warning("warn");
      break;
    case LockssLogger.LEVEL_WARNING:
      log.error("err");
      log.warning("warn");
      log.info("info");
      break;
    case LockssLogger.LEVEL_INFO:
      log.warning("warn");
      log.info("info");
      log.debug("debug");
      break;
    case LockssLogger.LEVEL_DEBUG:
      log.info("info");
      log.debug("debug");
      log.debug2("debug2");
      log.debug3("debug3");
      break;
    case LockssLogger.LEVEL_DEBUG2:
      log.debug("debug");
      log.debug2("debug2");
      log.debug3("debug3");
      break;
    case LockssLogger.LEVEL_DEBUG3:
      log.debug2("debug2");
      log.debug3("debug3");
      break;
    }
  }

  protected void setConfig(Map<String,String> map) {
    LockssLogger.setLockssConfig(map);
  }

  @Test
  public void testFactories() throws Exception {
    LockssLogger l1 = LockssLogger.getLogger("name1");
    assertEquals("name1", l1.getName());
    assertSame(l1, LockssLogger.getLogger("name1"));
    assertSame(l1, getLogger("name1"));

    LockssLogger l2 = LockssLogger.getLogger();
    assertEquals("org.lockss.log.TestLockssLogger", l2.getName());
    assertSame(l2, getLogger(l2.getName()));
    assertSame(l2, LockssLogger.getLogger(TestLockssLogger.class));

  }

  @Test
  public void testStackTrace() throws Exception {
    LockssLogger logD = getLogger("test.debug.DDD");
    LockssLogger logI = getLogger("test.info.III");
    logI.info("should not have stack trace", new Throwable("Umm"));
    logD.debug("should have stack trace", new Throwable("Err"));
  }

  @Test
  public void testFunc() throws Exception {
    LockssLogger logT = getLogger("test");
    LockssLogger logC = getLogger("test.critical.c1");
    LockssLogger logE = getLogger("test.error");
    LockssLogger logW = getLogger("test.warning.w1");
    LockssLogger logI = getLogger("test.info.foo");
    LockssLogger logD = getLogger("test.debug.d.e.f");
    LockssLogger logD2 = getLogger("test.debug2.lll.long.name.to.exercise layout");
    LockssLogger logD3 = getLogger("test.debug3.fff");
    LockssLogger logLockssDef = getLogger("org.lockss.Random");
    LockssLogger logRootDef = getLogger("other.package.FooLog");

    assertIsLevel(LockssLogger.LEVEL_CRITICAL, logC);
    assertIsLevel(LockssLogger.LEVEL_ERROR, logE);
    assertIsLevel(LockssLogger.LEVEL_WARNING, logW);
    assertIsLevel(LockssLogger.LEVEL_INFO, logI);
    assertIsLevel(LockssLogger.LEVEL_DEBUG, logD);
    assertIsLevel(LockssLogger.LEVEL_DEBUG2, logD2);
    assertIsLevel(LockssLogger.LEVEL_DEBUG3, logD3);
    assertIsLevel(origLockssLevel, logLockssDef);
    assertIsLevel(origRootLevel, logRootDef);

    doLogs(LockssLogger.LEVEL_CRITICAL, logC);
    doLogs(LockssLogger.LEVEL_ERROR, logE);
    doLogs(LockssLogger.LEVEL_WARNING, logW);
    doLogs(LockssLogger.LEVEL_INFO, logI);
    doLogs(LockssLogger.LEVEL_DEBUG, logD);
    doLogs(LockssLogger.LEVEL_DEBUG2, logD2);
    doLogs(LockssLogger.LEVEL_DEBUG3, logD3);

    List<String> expDefault =
      Arrays.asList("crit", "crit", "err", "err", "warn", "warn",
                    "info", "info", "debug", "debug",
                    "debug2", "debug2", "debug3");

    assertEquals(expDefault, getListAppender().getMessages());

    getListAppender().reset();
    assertTrue(getListAppender().getMessages().isEmpty());

    Map<String,String> newConfig = new HashMap<>(Map.of(
        "org.lockss.log.test.debug2.level", "warning",
        "org.lockss.log.test.warning.level", "debug2"
    ));

    setConfig(newConfig);
    assertTrue(getListAppender().getMessages().isEmpty());

    assertIsLevel(LockssLogger.LEVEL_CRITICAL, logC);
    assertIsLevel(LockssLogger.LEVEL_ERROR, logE);
    assertIsLevel(LockssLogger.LEVEL_DEBUG2, logW);
    assertIsLevel(LockssLogger.LEVEL_INFO, logI);
    assertIsLevel(LockssLogger.LEVEL_DEBUG, logT);
    assertIsLevel(LockssLogger.LEVEL_WARNING, logD2);
    assertIsLevel(LockssLogger.LEVEL_DEBUG3, logD3);
    assertIsLevel(origLockssLevel, logLockssDef);
    assertIsLevel(origRootLevel, logRootDef);

    doLogs(LockssLogger.LEVEL_CRITICAL, logC);
    doLogs(LockssLogger.LEVEL_ERROR, logE);
    doLogs(LockssLogger.LEVEL_DEBUG2, logW);
    doLogs(LockssLogger.LEVEL_INFO, logI);
    doLogs(LockssLogger.LEVEL_DEBUG, logT);
    doLogs(LockssLogger.LEVEL_WARNING, logD2);
    doLogs(LockssLogger.LEVEL_DEBUG3, logD3);
    doLogs(origLockssLevel, logLockssDef);
    doLogs(origRootLevel, logRootDef);

    List<String> expNew =
      Arrays.asList("crit", "crit", "err", "debug", "debug2",
                    "warn", "info", "info", "debug",
                    "err", "warn", "debug2", "debug3");

    assertEquals(expNew, getListAppender().getMessages());

    getListAppender().reset();
    newConfig.put("org.lockss.log.default.level", "warning");
    newConfig.put("org.lockss.log.root.level", "warning");
    newConfig.put(LockssLogger.PARAM_STACKTRACE_SEVERITY, "error");
    newConfig.put(LockssLogger.PARAM_STACKTRACE_LEVEL, "warning");
    setConfig(newConfig);

    assertIsLevel(LockssLogger.LEVEL_CRITICAL, logC);
    assertIsLevel(LockssLogger.LEVEL_ERROR, logE);
    assertIsLevel(LockssLogger.LEVEL_DEBUG2, logW);
    assertIsLevel(LockssLogger.LEVEL_INFO, logI);
    assertIsLevel(LockssLogger.LEVEL_DEBUG, logT);
    assertIsLevel(LockssLogger.LEVEL_WARNING, logD2);
    assertIsLevel(LockssLogger.LEVEL_DEBUG3, logD3);
    assertIsLevel(LockssLogger.LEVEL_WARNING, logLockssDef);
    assertIsLevel(LockssLogger.LEVEL_WARNING, logRootDef);

    doLogs(LockssLogger.LEVEL_CRITICAL, logC);
    doLogs(LockssLogger.LEVEL_ERROR, logE);
    doLogs(LockssLogger.LEVEL_DEBUG2, logW);
    doLogs(LockssLogger.LEVEL_INFO, logI);
    doLogs(LockssLogger.LEVEL_DEBUG, logT);
    doLogs(LockssLogger.LEVEL_WARNING, logD2);
    doLogs(LockssLogger.LEVEL_DEBUG3, logD3);

    expNew =
      Arrays.asList("crit", "crit", "err", "debug", "debug2",
                    "warn", "info", "info", "debug",
                    "err", "warn", "debug2", "debug3");

    assertEquals(expNew, getListAppender().getMessages());

    getListAppender().reset();
    setConfig(new HashMap<>());

    assertIsLevel(LockssLogger.LEVEL_CRITICAL, logC);
    assertIsLevel(LockssLogger.LEVEL_ERROR, logE);
    assertIsLevel(LockssLogger.LEVEL_WARNING, logW);
    assertIsLevel(LockssLogger.LEVEL_INFO, logI);
    assertIsLevel(LockssLogger.LEVEL_DEBUG, logT);
    assertIsLevel(LockssLogger.LEVEL_DEBUG2, logD2);
    assertIsLevel(LockssLogger.LEVEL_DEBUG3, logD3);
    assertIsLevel(origLockssLevel, logLockssDef);
    assertIsLevel(origRootLevel, logRootDef);

    doLogs(LockssLogger.LEVEL_CRITICAL, logC);
    doLogs(LockssLogger.LEVEL_ERROR, logE);
    doLogs(LockssLogger.LEVEL_WARNING, logW);
    doLogs(LockssLogger.LEVEL_INFO, logI);
    doLogs(LockssLogger.LEVEL_DEBUG, logT);
    doLogs(LockssLogger.LEVEL_DEBUG2, logD2);
    doLogs(LockssLogger.LEVEL_DEBUG3, logD3);
    doLogs(origLockssLevel, logLockssDef);
    doLogs(origRootLevel, logRootDef);

    assertEquals(expDefault, getListAppender().getMessages());
  }

  @Test
  public void testLevel() throws Exception {
    LockssLogger logT = getLogger("test");
    LockssLogger logD1 = getLogger("test.debug");
    LockssLogger logD2 = getLogger("test.debug.aaa");
    LockssLogger logD3 = getLogger("test.debug.w.xxx");

    assertIsLevel(LockssLogger.LEVEL_DEBUG, logT);
    assertIsLevel(LockssLogger.LEVEL_DEBUG, logD1);
    assertIsLevel(LockssLogger.LEVEL_DEBUG, logD2);
    assertIsLevel(LockssLogger.LEVEL_WARNING, logD3);

    LockssLogger.setRootLevel(LockssLogger.LEVEL_WARNING);

    assertIsLevel(LockssLogger.LEVEL_DEBUG, logT);
    assertIsLevel(LockssLogger.LEVEL_DEBUG, logD1);
    assertIsLevel(LockssLogger.LEVEL_DEBUG, logD2);
    assertIsLevel(LockssLogger.LEVEL_WARNING, logD3);

  }

  @Test
  public void testShortNameLevel() throws Exception {
    Map<String,String> newConfig = new HashMap<>(Map.of(
        "org.lockss.log.default.level", "info"
    ));
    setConfig(newConfig);

    LockssLogger logPollManager = getLogger("org.lockss.poll.PollManager");
    LockssLogger logPoller = getLogger("org.lockss.poll.v3.V3Poller");
    LockssLogger logVoter = getLogger("org.lockss.poll.v3.V3Voter");
    LockssLogger logVoter2 = getLogger("org.lockss.other.V3Voter");

    assertIsLevel(LockssLogger.LEVEL_INFO, logPollManager);
    assertIsLevel(LockssLogger.LEVEL_INFO, logPoller);
    assertIsLevel(LockssLogger.LEVEL_INFO, logVoter);
    assertIsLevel(LockssLogger.LEVEL_INFO, logVoter2);

    newConfig.put("org.lockss.log.PollManager.level", "warning");
    setConfig(newConfig);
    assertIsLevel(LockssLogger.LEVEL_WARNING, logPollManager);

    newConfig.put("org.lockss.log.org.lockss.poll.v3.level", "debug2");
    newConfig.put("org.lockss.log.V3Voter.level", "debug3");
    setConfig(newConfig);
    assertIsLevel(LockssLogger.LEVEL_WARNING, logPollManager);
    assertIsLevel(LockssLogger.LEVEL_DEBUG2, logPoller);
    assertIsLevel(LockssLogger.LEVEL_DEBUG3, logVoter);
    assertIsLevel(LockssLogger.LEVEL_DEBUG3, logVoter2);
    assertIsLevel(LockssLogger.LEVEL_DEBUG2, getLogger("org.lockss.poll.v3.Tally"));

  }

}
