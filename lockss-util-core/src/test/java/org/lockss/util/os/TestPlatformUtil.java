/*

Copyright (c) 2000-2018, Board of Trustees of Leland Stanford Jr. University
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

package org.lockss.util.os;

import java.io.*;
import org.lockss.util.*;
import org.lockss.util.lang.EncodingUtil;
import org.lockss.util.test.*;
import org.slf4j.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.*;

/**
 * test class for org.lockss.util.PlatformInfo
 */
public class TestPlatformUtil extends LockssTestCase5 {

  private static final Logger log = LoggerFactory.getLogger(TestPlatformUtil.class);
  
  PlatformUtil info;

  @BeforeEach
  public void setUp() throws Exception {
    info = PlatformUtil.getInstance();
  }

  @Test
  public void testEnsureRuntime_execDoesntImplicitlyInvokeShell() {
    // This command echoes a number.
    String[] cmd1 = {"sh", "-c", "echo $$"};
    // This command should not echo a number
    String[] cmd2 = {"echo", "$$"};

    if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
      assertThat(exec(cmd1), findPattern("^[0-9]+"));
    }
    assertThat(exec(cmd2), not(findPattern("^[0-9]+")));
  }

  String exec(String cmd[]) {
    try {
      Process p = Runtime.getRuntime().exec(cmd);
      Reader rdr =
	new InputStreamReader(new BufferedInputStream(p.getInputStream()),
			      EncodingUtil.DEFAULT_ENCODING);
      try {
	String s = IOUtils.toString(rdr);
	rdr.close();
	return s;
      } catch (IOException e) {
	log.warn("Couldn't read from process stream", e);
	return null;
      }
    } catch (Exception e) {
      log.warn("exec() failed", e);
      return null;
    }
  }

  public void testGetSystemTempDir() throws IOException {
    String javatmp = System.getProperty(PlatformUtil.SYSPROP_JAVA_IO_TMPDIR);
    assertEquals(javatmp, PlatformUtil.getSystemTempDir());
    String parmtmp = new File(getTempDir(), "another/tmp/dir").toString();
    System.setProperty(PlatformUtil.SYSPROP_JAVA_IO_TMPDIR, parmtmp);
    assertEquals(new File(parmtmp, "dtmp").toString(),
		 PlatformUtil.getSystemTempDir());
  }

  @Test
  public void testGetCwd() {
    log.info("cwd: " + info.getCwd());
  }

  public void testGetUnfilteredTcpPorts() throws Exception {
    assertEmpty(info.getUnfilteredTcpPorts());
    System.setProperty(PlatformUtil.SYSPROP_UNFILTERED_TCP_PORTS, "9909");
    assertEquals(ListUtil.list("9909"), info.getUnfilteredTcpPorts());
    System.setProperty(PlatformUtil.SYSPROP_UNFILTERED_TCP_PORTS, "9900;1234");
    assertEquals(ListUtil.list("9900", "1234"), info.getUnfilteredTcpPorts());
// Dropped support for comma when moving from lockss-core to lockss-util
//    ConfigurationUtil.setFromArgs(PlatformUtil.PARAM_UNFILTERED_TCP_PORTS,
//				  "9900,1234,333");
//    assertEquals(ListUtil.list("9900", "1234", "333"),
//		 info.getUnfilteredTcpPorts());
  }

  @Test
  public void testDiskUsageNonexistentPath() throws Exception {
    long du = info.getDiskUsage("/very_unlik_elyd_irect_oryname/4x2");
    assertEquals(-1, du);
  }

  public void testDiskUsage() throws Exception {
    long du;
    File tmpdir = getTempDir();
    du = info.getDiskUsage(tmpdir.toString());
    assertTrue(du >= 0);
    StringBuffer sb = new StringBuffer(1500);
    while (sb.length() < 1200) {
      sb.append("01234567890123456789012345678901234567890123456789");
    }
    FileTestUtil.writeFile(new File(tmpdir, "foobar"), sb.toString());
    long du2 = info.getDiskUsage(tmpdir.toString());
    assertTrue(du2 > du);
  }

  @Test
  public void testNonexistentPathNullDF() throws Exception {
    String javatmp = System.getProperty("java.io.tmpdir");
    PlatformUtil.DF df =
      info.getPlatformDF(javatmp);
    assertNotNull(df, javatmp + " is null");
    javatmp = "/very_unlik_elyd_irect_oryname/4x3";
    df = info.getPlatformDF(javatmp);
    assertNull(df, javatmp);
  }

  @Test
  public void testNonexistentPathNullJavaDF() throws Exception {
    String javatmp = System.getProperty("java.io.tmpdir");
    PlatformUtil.DF df = info.getJavaDF(javatmp);
    assertNotNull(df, javatmp + " is null");
    javatmp = "/very_unlik_elyd_irect_oryname/4x3";
    df = info.getJavaDF(javatmp);
    assertNull(df, javatmp);
  }

  @RepeatedTest(10)
  public void testJavaDFEqualsDF(RepetitionInfo repetitionInfo) throws Exception {
    try {
      setUpSuccessRate(repetitionInfo);
      String javatmp = System.getProperty(PlatformUtil.SYSPROP_JAVA_IO_TMPDIR);
      PlatformUtil.DF df = info.getPlatformDF(javatmp);
      PlatformUtil.DF jdf = info.getJavaDF(javatmp);  
      assertEquals(df.getAvail(), jdf.getAvail());
      assertEquals(df.getSize(), jdf.getSize());
      assertEquals(df.getUsed(), jdf.getUsed());
      assertEquals(df.getPercent(), jdf.getPercent(), 1.0);
      assertEquals(df.getPath(), jdf.getPath());
      assertEquals(PlatformUtil.DiskSpaceSource.DF, df.getSource());
      assertEquals(PlatformUtil.DiskSpaceSource.Java, jdf.getSource());
    }
    catch (Throwable thr) {
      signalFailure(repetitionInfo);
    }
    finally {
      assertSuccessRate(repetitionInfo, 0.1f);
    }
  }

  @Test
  public void testGetDFSource() throws Exception {
    String javatmp = System.getProperty("java.io.tmpdir");
    assertEquals(PlatformUtil.DiskSpaceSource.Java,
                 info.getDF(javatmp).getSource());
    System.setProperty(PlatformUtil.SYSPROP_DISK_SPACE_SOURCE, "DF");
    assertEquals(PlatformUtil.DiskSpaceSource.DF,
                 info.getDF(javatmp).getSource());
  }

  @Test
  public void testMakeDF() throws Exception {
    String str = "/dev/hda2  26667896   9849640  15463576    39% /";
    PlatformUtil.DF df = info.makeDFFromLine("/mnt", str);
    assertNotNull(df);
    assertEquals("/mnt", df.getPath());
    assertEquals(26667896, df.getSize());
    assertEquals(9849640, df.getUsed());
    assertEquals(15463576, df.getAvail());
    assertEquals("39%", df.getPercentString());
    assertEquals(.39, df.getPercent(), .0000001);
  }

  @Test
  public void testMakeDFLong() throws Exception {
    String str = "/dev/md0     2826607136 411558468 2269149176      16% /";
    PlatformUtil.DF df = info.makeDFFromLine("/cache.wd3", str);
    assertNotNull(df);
    assertEquals("/cache.wd3", df.getPath());
    assertEquals(2826607136L, df.getSize());
    assertEquals(411558468, df.getUsed());
    assertEquals(2269149176L, df.getAvail());
    assertEquals("16%", df.getPercentString());
    assertEquals(.16, df.getPercent(), .0000001);
  }

  @Test
  public void testMakeDFIll1() throws Exception {
    String str = "/dev/hda2  26667896   9849640  -1546    39% /";
    PlatformUtil.DF df = info.makeDFFromLine("/mnt", str);
    assertNotNull(df);
    assertEquals("/mnt", df.getPath());
    assertEquals(26667896, df.getSize());
    assertEquals(9849640, df.getUsed());
    assertEquals(-1546, df.getAvail());
    assertEquals("39%", df.getPercentString());
    assertEquals(.39, df.getPercent(), .0000001);
  }

  @Test
  public void testMakeDFIll2() throws Exception {
    // linux df running under linux emul on OpenBSD can produce this
    String str = "-  26667896   9849640  4294426204    101% /";
    PlatformUtil.DF df = info.makeDFFromLine("/mnt", str);
    assertNotNull(df);
    assertEquals("/mnt", df.getPath());
    assertEquals(26667896, df.getSize());
    assertEquals(9849640, df.getUsed());
    assertEquals(4294426204L, df.getAvail());
    assertEquals("101%", df.getPercentString());
    assertEquals(1.01, df.getPercent(), .0000001);
  }

  PlatformUtil.DF makeThresh(int minFreeMB, double minFreePercent) {
    return PlatformUtil.DF.makeThreshold(minFreeMB, minFreePercent);
  }

  @Test
  public void testIsFullerThan() throws Exception {
    String str = "/dev/hda2  26667896   9849640  15463576    73% /";
    PlatformUtil.DF df = info.makeDFFromLine("/mnt", str);
    assertFalse(df.isFullerThan(makeThresh(100, 0)));
    assertFalse(df.isFullerThan(makeThresh(15000, 0)));
    assertTrue(df.isFullerThan(makeThresh(16000, 0)));
    assertTrue(df.isFullerThan(makeThresh(16000, .3)));
    assertTrue(df.isFullerThan(makeThresh(100, .30)));
    assertFalse(df.isFullerThan(makeThresh(100, .20)));
    assertFalse(df.isFullerThan(makeThresh(0, 0)));
  }

  @Test
  public void testisDiskFullError() throws Exception {
    assertFalse(info.isDiskFullError(new IOException("jjjjj: No such file or directory")));
    assertTrue(info.isDiskFullError(new IOException("No space left on device")));
    assertTrue(info.isDiskFullError(new IOException("disk: No space left on device")));
  }

  // maven surefire plugin is incompatible with lowlevel JVM output
  public void xtestThreadDumpSignal() throws Exception {
    info.threadDumpSignal(true);
  }

  @Test
  public void testThreadDumpJcmd() throws Exception {
    info.threadDumpJcmd(false);
  }

  boolean isBuggy(String str) {
    return PlatformUtil.isBuggyDoubleString(str);
  }

  double parseDouble(String str) {
    return PlatformUtil.parseDouble(str);
  }

  @Test
  public void testIsBuggyDoubleString() {
    assertTrue(isBuggy("2.2250738585072012e-308"));
    assertTrue(isBuggy("0.00022250738585072012E-304"));
    assertTrue(isBuggy("0.0000000022250738585072012E-299"));
    assertTrue(isBuggy("0.000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000022250738585072012"));
    assertTrue(isBuggy("0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002225073858507201212345"));
    assertTrue(isBuggy("22.250738585072012e-309"));
    assertTrue(isBuggy("22.250738585072012e-0309"));
    assertTrue(isBuggy("00000000002.2250738585072012e-308"));
    assertTrue(isBuggy("2.225073858507201200000e-308"));
    assertTrue(isBuggy("2.2250738585072012e-00308"));
    assertTrue(isBuggy("2.2250738585072012997800001e-308"));

    assertFalse(isBuggy("0"));
    assertFalse(isBuggy("4.5"));
    assertFalse(isBuggy("2.2e-308"));
    assertFalse(isBuggy("2.2E-308"));
    assertFalse(isBuggy("2.2250738585072011e-308"));

    // BaseServletManager.CompressingFilterWrapper checks the
    // Accept-Encoding: header without parsing out the qvalue
    assertTrue(isBuggy("gzip;q=1.0, identity; q=2.2250738585072012997800001e-308, *;q=0"));
    assertFalse(isBuggy("gzip;q=1.0, identity; q=2.2, *;q=0"));
  }

  @Test
  public void testParseDouble() {
    assertEquals(0.0, parseDouble("0"));
    assertEquals(4.5, parseDouble("4.5"));
    try {
      parseDouble("2.2250738585072012e-308");
      fail("should throw");
    } catch (NumberFormatException e) {
    }
    try {
      parseDouble("0.00022250738585072012E-304");
      fail("should throw");
    } catch (NumberFormatException e) {
    }
  }

  @Test
  public void testLinux() {
    PlatformUtil pi = new PlatformUtil.Linux();
    assertTrue(pi.isCaseSensitiveFileSystem());
    assertEquals(255, pi.maxFilename());
    assertEquals(4096, pi.maxPathname());
    assertTrue(pi.hasScriptingSupport());
  }

  @Test
  public void testMacOS() {
    PlatformUtil pi = new PlatformUtil.MacOS();
    assertFalse(pi.isCaseSensitiveFileSystem());
    assertEquals(255, pi.maxFilename());
    assertEquals(1016, pi.maxPathname());
    assertTrue(pi.hasScriptingSupport());
  }

  @Test
  public void testWindows() {
    PlatformUtil pi = new PlatformUtil.Windows();
    assertFalse(pi.isCaseSensitiveFileSystem());
    assertEquals(255, pi.maxFilename());
    assertEquals(260, pi.maxPathname());
    assertTrue(pi.hasScriptingSupport());
  }

}
