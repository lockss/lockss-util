/*

 Copyright (c) 2023 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util.rest;

import org.junit.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.lockss.util.rest.status.ApiStatus;
import static org.lockss.util.rest.status.ApiStatus.StartupStatus;
import org.lockss.log.*;
import org.lockss.util.test.*;

/**
 * Test class for org.lockss.util.rest.status.ApiStatus
 */
public class TestApiStatus extends LockssTestCase5 {
  private static L4JLogger log = L4JLogger.getLogger();

  @Test
  public void testStartupStatus() {
    assertFalse(StartupStatus.NONE.arePluginsCollected());
    assertFalse(StartupStatus.PLUGINS_CRAWLING.arePluginsCollected());
    assertTrue(StartupStatus.PLUGINS_COLLECTED.arePluginsCollected());
    assertTrue(StartupStatus.PLUGINS_LOADING.arePluginsCollected());
    assertTrue(StartupStatus.PLUGINS_LOADED.arePluginsCollected());
    assertTrue(StartupStatus.AUS_STARTING.arePluginsCollected());
    assertTrue(StartupStatus.AUS_STARTED.arePluginsCollected());

    assertFalse(StartupStatus.NONE.arePluginsStarted());
    assertFalse(StartupStatus.PLUGINS_CRAWLING.arePluginsStarted());
    assertFalse(StartupStatus.PLUGINS_COLLECTED.arePluginsStarted());
    assertFalse(StartupStatus.PLUGINS_LOADING.arePluginsStarted());
    assertTrue(StartupStatus.PLUGINS_LOADED.arePluginsStarted());
    assertTrue(StartupStatus.AUS_STARTING.arePluginsStarted());
    assertTrue(StartupStatus.AUS_STARTED.arePluginsStarted());

    assertFalse(StartupStatus.NONE.areAusStarted());
    assertFalse(StartupStatus.PLUGINS_CRAWLING.areAusStarted());
    assertFalse(StartupStatus.PLUGINS_COLLECTED.areAusStarted());
    assertFalse(StartupStatus.PLUGINS_LOADING.areAusStarted());
    assertFalse(StartupStatus.PLUGINS_LOADED.areAusStarted());
    assertFalse(StartupStatus.AUS_STARTING.areAusStarted());
    assertTrue(StartupStatus.AUS_STARTED.areAusStarted());
  }

}
