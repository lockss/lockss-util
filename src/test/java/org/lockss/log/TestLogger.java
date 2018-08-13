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

public class TestLogger extends LockssTestCase5 {

  protected static String origSysProp;
  protected static int origDefLevel;

  /** Return the ListAppender created by log4j2-logger-test.xml */
  protected static ListAppender getListAppender() {
    LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
    Configuration config = ctx.getConfiguration();
    return (ListAppender)config.getAppenders().get("ListAppender");
  }

  /** Get the named Logger */
  protected Logger getLogger(String name) {
    Logger res = Logger.getLogger(name);
    return res;
  }

  /** Add ListAppender to record output to loggers below
   * org.lockss.testlogger */
  @BeforeAll
  public static void beforeAll() throws Exception {
    commonBeforeAll();
    Logger.forceReload();
  }

  public static void commonBeforeAll() {

    // Record the org.lockss.defaultLogLevel sysprop at startup so we know
    // what to expect the default (root) log level to be.  Actually varying
    // the initial default log level is beyond the scope of this code, as
    // the Logger class may already have been initialized before this code
    // is executed.
    origSysProp = System.getProperty("org.lockss.defaultLogLevel");

    // Ensure that any default level is legal
    if (StringUtils.isBlank(origSysProp)) {
      origDefLevel = Logger.LEVEL_INFO;
    } else {
      try {
	origDefLevel = Logger.levelOf(origSysProp);
	System.err.println("origDefLevel: " + origDefLevel);
      } catch (Exception e) {
	Assertions.fail("org.lockss.defaultLogLevel set to illegal level string: " +
			origSysProp);
      }
    }
    System.err.println("origDefLevel: " + origDefLevel);

    // Add testing config file to logj4's config.
    System.setProperty("log4j.configurationFile",
		       "log4j2.xml,log4j2-logger-test.xml");
  }

  void assertIsLevel(int level, Logger log) {
    assertEquals(level, log.getLevel());
    assertTrue(log.isLevel(level));
    assertTrue(log.isLevel(level - 1));
    assertFalse(log.isLevel(level + 1));
  }

  @Test
  public void testLevelOf() throws Logger.IllegalLevelException {
    assertEquals(Logger.LEVEL_CRITICAL, Logger.levelOf("critical"));
    assertEquals(Logger.LEVEL_ERROR, Logger.levelOf("error"));
    assertEquals(Logger.LEVEL_SITE_ERROR, Logger.levelOf("siteError"));
    assertEquals(Logger.LEVEL_WARNING, Logger.levelOf("warning"));
    assertEquals(Logger.LEVEL_SITE_WARNING, Logger.levelOf("siteWarning"));
    assertEquals(Logger.LEVEL_DEBUG, Logger.levelOf("debug"));
    assertEquals(Logger.LEVEL_DEBUG1, Logger.levelOf("debug1"));
    assertEquals(Logger.LEVEL_DEBUG2, Logger.levelOf("debug2"));
    assertEquals(Logger.LEVEL_DEBUG3, Logger.levelOf("debug3"));
    assertThrows(Logger.IllegalLevelException.class,
		 () -> {Logger.levelOf("nonesuch");});
  }

  @Test
  public void testNameOf() {
    assertEquals("Critical", Logger.nameOf(Logger.LEVEL_CRITICAL));
    assertEquals("Error", Logger.nameOf(Logger.LEVEL_ERROR));
    assertEquals("SiteError", Logger.nameOf(Logger.LEVEL_SITE_ERROR));
    assertEquals("Warning", Logger.nameOf(Logger.LEVEL_WARNING));
    assertEquals("SiteWarning", Logger.nameOf(Logger.LEVEL_SITE_WARNING));
    assertEquals("Info", Logger.nameOf(Logger.LEVEL_INFO));
    assertEquals("Debug", Logger.nameOf(Logger.LEVEL_DEBUG));
    assertEquals("Debug", Logger.nameOf(Logger.LEVEL_DEBUG1));
    assertEquals("Debug2", Logger.nameOf(Logger.LEVEL_DEBUG2));
    assertEquals("Debug3", Logger.nameOf(Logger.LEVEL_DEBUG3));
  }


  void doLogs(int level, Logger log) {
    switch (level) {
    case Logger.LEVEL_CRITICAL:
      log.critical("crit");
      log.error("err");
      break;
    case Logger.LEVEL_ERROR:
      log.critical("crit");
      log.error("err");
      log.warning("warn");
      break;
    case Logger.LEVEL_WARNING:
      log.error("err");
      log.warning("warn");
      log.info("info");
      break;
    case Logger.LEVEL_INFO:
      log.warning("warn");
      log.info("info");
      log.debug("debug");
      break;
    case Logger.LEVEL_DEBUG:
      log.info("info");
      log.debug("debug");
      log.debug2("debug2");
      log.debug3("debug3");
      break;
    case Logger.LEVEL_DEBUG2:
      log.debug("debug");
      log.debug2("debug2");
      log.debug3("debug3");
      break;
    case Logger.LEVEL_DEBUG3:
      log.debug2("debug2");
      log.debug3("debug3");
      break;
    }
  }

  protected void setConfig(Map<String,String> map) {
    Logger.setLockssConfig(map);
  }

  @Test
  public void testFunc() throws Exception {
    Logger logT = getLogger("test");
    Logger logC = getLogger("test.critical.c1");
    Logger logE = getLogger("test.error");
    Logger logW = getLogger("test.warning.w1");
    Logger logI = getLogger("test.info.foo");
    Logger logD = getLogger("test.debug.d.e.f");
    Logger logD2 = getLogger("test.debug2.lll");
    Logger logD3 = getLogger("test.debug3.fff");
    Logger logDef = getLogger("default");

    assertIsLevel(Logger.LEVEL_CRITICAL, logC);
    assertIsLevel(Logger.LEVEL_ERROR, logE);
    assertIsLevel(Logger.LEVEL_WARNING, logW);
    assertIsLevel(Logger.LEVEL_INFO, logI);
    assertIsLevel(Logger.LEVEL_DEBUG, logD);
    assertIsLevel(Logger.LEVEL_DEBUG2, logD2);
    assertIsLevel(Logger.LEVEL_DEBUG3, logD3);
    assertIsLevel(origDefLevel, logDef);

    doLogs(Logger.LEVEL_CRITICAL, logC);
    doLogs(Logger.LEVEL_ERROR, logE);
    doLogs(Logger.LEVEL_WARNING, logW);
    doLogs(Logger.LEVEL_INFO, logI);
    doLogs(Logger.LEVEL_DEBUG, logD);
    doLogs(Logger.LEVEL_DEBUG2, logD2);
    doLogs(Logger.LEVEL_DEBUG3, logD3);

    List<String> expDefault =
      ListUtil.list("crit", "crit", "err", "err", "warn", "warn",
		    "info", "info", "debug", "debug",
		    "debug2", "debug2", "debug3");

    assertEquals(expDefault, getListAppender().getMessages());

    getListAppender().reset();
    assertEmpty(getListAppender().getMessages());

    Map<String,String> newConfig = new HashMap<String,String>() {{
	put("org.lockss.log.test.debug2.level", "warning");
	put("org.lockss.log.test.warning.level", "debug2");
      }};

    setConfig(newConfig);
    assertEmpty(getListAppender().getMessages());

    assertIsLevel(Logger.LEVEL_CRITICAL, logC);
    assertIsLevel(Logger.LEVEL_ERROR, logE);
    assertIsLevel(Logger.LEVEL_DEBUG2, logW);
    assertIsLevel(Logger.LEVEL_INFO, logI);
    assertIsLevel(Logger.LEVEL_DEBUG, logT);
    assertIsLevel(Logger.LEVEL_WARNING, logD2);
    assertIsLevel(Logger.LEVEL_DEBUG3, logD3);
    assertIsLevel(origDefLevel, logDef);

    doLogs(Logger.LEVEL_CRITICAL, logC);
    doLogs(Logger.LEVEL_ERROR, logE);
    doLogs(Logger.LEVEL_WARNING, logW);
    doLogs(Logger.LEVEL_INFO, logI);
    doLogs(Logger.LEVEL_DEBUG, logT);
    doLogs(Logger.LEVEL_DEBUG2, logD2);
    doLogs(Logger.LEVEL_DEBUG3, logD3);
    doLogs(origDefLevel, logDef);

    List<String> expNew =
      ListUtil.list("crit", "crit", "err", "err", "warn", "info",
		    "warn", "info", "info", "debug",
		    "debug2", "debug3");

    assertEquals(expNew, getListAppender().getMessages());

    getListAppender().reset();
    newConfig.put("org.lockss.log.default.level", "warning");
    setConfig(newConfig);

    assertIsLevel(Logger.LEVEL_CRITICAL, logC);
    assertIsLevel(Logger.LEVEL_ERROR, logE);
    assertIsLevel(Logger.LEVEL_DEBUG2, logW);
    assertIsLevel(Logger.LEVEL_INFO, logI);
    assertIsLevel(Logger.LEVEL_DEBUG, logT);
    assertIsLevel(Logger.LEVEL_WARNING, logD2);
    assertIsLevel(Logger.LEVEL_DEBUG3, logD3);
    assertIsLevel(Logger.LEVEL_WARNING, logDef);

    doLogs(Logger.LEVEL_CRITICAL, logC);
    doLogs(Logger.LEVEL_ERROR, logE);
    doLogs(Logger.LEVEL_WARNING, logW);
    doLogs(Logger.LEVEL_INFO, logI);
    doLogs(Logger.LEVEL_DEBUG, logT);
    doLogs(Logger.LEVEL_DEBUG2, logD2);
    doLogs(Logger.LEVEL_DEBUG3, logD3);
    assertIsLevel(Logger.LEVEL_WARNING, logDef);

    getListAppender().reset();
    setConfig(new HashMap());

    assertIsLevel(Logger.LEVEL_CRITICAL, logC);
    assertIsLevel(Logger.LEVEL_ERROR, logE);
    assertIsLevel(Logger.LEVEL_WARNING, logW);
    assertIsLevel(Logger.LEVEL_INFO, logI);
    assertIsLevel(Logger.LEVEL_DEBUG, logT);
    assertIsLevel(Logger.LEVEL_DEBUG2, logD2);
    assertIsLevel(Logger.LEVEL_DEBUG3, logD3);
    assertIsLevel(origDefLevel, logDef);

    doLogs(Logger.LEVEL_CRITICAL, logC);
    doLogs(Logger.LEVEL_ERROR, logE);
    doLogs(Logger.LEVEL_WARNING, logW);
    doLogs(Logger.LEVEL_INFO, logI);
    doLogs(Logger.LEVEL_DEBUG, logT);
    doLogs(Logger.LEVEL_DEBUG2, logD2);
    doLogs(Logger.LEVEL_DEBUG3, logD3);
    doLogs(origDefLevel, logDef);
  }
}
