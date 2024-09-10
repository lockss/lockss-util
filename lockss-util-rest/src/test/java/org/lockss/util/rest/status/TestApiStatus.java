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

package org.lockss.util.rest.status;

import org.junit.*;

import org.lockss.util.rest.status.ApiStatus.StartupStatus;
import org.lockss.util.test.*;

/**
 * Test class for org.lockss.util.rest.status.ApiStatus
 */
public class TestApiStatus extends LockssTestCase5 {

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
