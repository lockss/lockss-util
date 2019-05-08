/*

Copyright (c) 2000-2019 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.util;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;
import org.lockss.log.*;
import org.lockss.util.*;
import org.lockss.util.test.*;
import org.lockss.util.test.matcher.*;

/**
 * test class for org.lockss.util.BuildInfo
 */
public class TestBuildInfo extends LockssTestCase5 {
  private static L4JLogger log = L4JLogger.getLogger();

  void assertMatches(String pat, String val) {
    assertThat(val, MatchesPattern.matchesPattern(pat));
  }

  @Test
  public void testBuildInfo() {
    BuildInfo bi =
      new BuildInfo(getClass().getResource("sample-build.properties"));
    assertEquals("Lockss Util", bi.getBuildPropertyInst("build.name"));
    assertEquals("lockss-util", bi.getBuildPropertyInst("build.artifactId"));
    assertEquals("1.7.0-SNAPSHOT", bi.getBuildPropertyInst("build.version"));
    assertEquals("1.75.0", bi.getBuildPropertyInst("build.releasename"));
    assertMatches("1.75.0 built 07-Apr-19 20:05:25 PDT on .*",
		  bi.getBuildInfoStringInst());
    assertMatches("lockss-util 1.75.0 built 07-Apr-19 20:05:25 PDT " +
		  "build.description: Development.* utilities.*," +
		  " on .*",
		  bi.getBuildInfoStringInst(BuildInfo.BUILD_ARTIFACT,
					    BuildInfo.BUILD_RELEASENAME,
					    BuildInfo.BUILD_TIMESTAMP,
					    BuildInfo.BUILD_DESCRIPTION,
					    BuildInfo.BUILD_HOST));
  }

  @Test
  public void testFirst() {
    log.debug("First BuildInfo: {}", BuildInfo.getFirstBuildInfo());
    assertNotNull(BuildInfo.getBuildProperty(BuildInfo.BUILD_TIMESTAMP));
    assertNotNull(BuildInfo.getBuildProperty(BuildInfo.BUILD_HOST));
    assertNull(BuildInfo.getBuildProperty("not a real property"));

    assertEquals("org.lockss", BuildInfo.getBuildProperty("build.groupId"));
    assertEquals("lockss-util", BuildInfo.getBuildProperty("build.artifactId"));
  }

  @Test
  public void testAll() {
    List<BuildInfo> all = BuildInfo.getAllBuildInfo();
    log.debug("All BuildInfo: {}", all);
    assertTrue(all.size() >= 1);
  }

}
