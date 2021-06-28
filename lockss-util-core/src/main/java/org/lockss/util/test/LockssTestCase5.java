/*

Copyright (c) 2000-2019, Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.util.test;

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.namespace.NamespaceContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.*;
import org.hamcrest.collection.IsArray;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.CombinableMatcher.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.*;
import org.lockss.util.io.FileUtil;
import org.lockss.util.os.PlatformUtil;
import org.lockss.util.test.matcher.*;
import org.lockss.util.time.TimerUtil;
import org.opentest4j.MultipleFailuresError;
import org.lockss.log.L4JLogger;
import org.w3c.dom.Node;
import org.lockss.log.LockssLogger;

/**
 * <p>
 * A class that has non-static versions of all the methods in
 * {@link Assertions}, {@link MatcherAssert}, {@link Matchers}
 * </p>
 * 
 * @author Thib Guicherd-Callin
 * @since 1.4.0
 * @see Assertions
 * @see MatcherAssert
 * @see Matchers
 */
public class LockssTestCase5 {

  public static final String SYSPROP_KEEP_TEMP_FILES = "org.lockss.keepTempFiles";

  public static final String TEST_ID_FILE_NAME = ".locksstestcase";

  private static final L4JLogger log = L4JLogger.getLogger();
  
  /** Timeout duration for timeouts that are expected to time out.  Setting
   * this higher makes normal tests take longer, setting it too low might
   * cause failing tests to erroneously succeed on slow or busy
   * machines. */
  public static int TIMEOUT_SHOULD = 300;

  /** Timeout duration for timeouts that are expected not to time out.
   * This should be set high to ensure catching failures. */
  public static final int DEFAULT_TIMEOUT_SHOULDNT = 2000;

  public static int TIMEOUT_SHOULDNT = DEFAULT_TIMEOUT_SHOULDNT;

  List<File> tmpDirs;
  
  List<DoLater> doLaters;

  String javaIoTmpdir;
  
  /**
   *
   */
  private static int failures;
  
  /**
   * <p>
   * Sets up {@link PlatformUtil#SYSPROP_JAVA_IO_TMPDIR} before each test.
   * </p>
   * 
   * @see #afterEachJavaIoTmpdir()
   */
  @BeforeEach
  public final void beforeEachJavaIoTmpdir() {
    javaIoTmpdir = System.getProperty(PlatformUtil.SYSPROP_JAVA_IO_TMPDIR);
  }
  
  /**
   * <p>
   * Resets {@link PlatformUtil#SYSPROP_JAVA_IO_TMPDIR} before each test.
   * </p>
   * 
   * @see #afterEachJavaIoTmpdir()
   */
  @AfterEach
  public final void afterEachJavaIoTmpdir() {
    if (!StringUtils.isEmpty(javaIoTmpdir)) {
      System.setProperty(PlatformUtil.SYSPROP_JAVA_IO_TMPDIR, javaIoTmpdir);
    }
  }

  /**
   * <p>
   * Cancels the {@link DoLater} tasks after each test.
   * </p>
   *
   * @see DoLater
   */
  @AfterEach
  public final void afterEachDoLaters() {
    if (doLaters != null) {
      List<DoLater> copy;
      synchronized (this) {
        copy = new ArrayList<DoLater>(doLaters);
      }
      for (DoLater doer : copy) {
        doer.cancel();
      }
      // do NOT set doLaters to null here.  It may be referenced by
      // exiting DoLaters.  It won't hurt anything because the next test
      // will create a new instance of the test case, and get a different
      // doLaters list
    }
  }

  /**
   * <p>
   * Cleans up temporary directories after each test.
   * </p>
   * 
   * @throws Exception
   *           if I/O exceptions occur in the process.
   */
  @AfterEach
  public final void afterEachTempDirs() throws Exception {
    deleteTempFiles(tmpDirs);
  }

  /**
   * Clean up temporary directories.
   * @param tmpList list of temporary files and directories that was passed
   * to getTempDir() and getTempFile()
   *
   * @throws Exception
   *           if I/O exceptions occur in the process.
   */
  public static void deleteTempFiles(List<File> tmpList) throws Exception {
    if (tmpList != null && !isKeepTempFiles()) {
      for (Iterator<File> iter = tmpList.iterator() ; iter.hasNext() ; ) {
        File dir = iter.next();
        File idFile = new File(dir, TEST_ID_FILE_NAME);
        String idContent = null;
        if (idFile.exists()) {
          idContent = IOUtils.toString(new FileReader(idFile));
        }
        if (FileUtil.delTree(dir)) {
          log.trace("deltree(" + dir + ") = true");
          iter.remove();
        } else {
          log.trace("deltree(" + dir + ") = false");
          if (idContent != null) {
            FileTestUtil.writeFile(idFile, idContent);
          }
        }
      }
    }
  }

  /**
   * <p>
   * Sets up a repeated test for {@link #assertSuccessRate(RepetitionInfo, float)}.
   * </p>
   * 
   * @param repetitionInfo
   *          The {@link RepetitionInfo} instance associated with
   *          {@link RepeatedTest}
   * @see #assertSuccessRate(RepetitionInfo, float)
   */
  public void setUpSuccessRate(RepetitionInfo repetitionInfo) {
    if (repetitionInfo.getCurrentRepetition() == 1) {
      failures = 0;
    }
  }
  
  /**
   * <p>
   * In a repeated test that has been set up for counting failures, signals that
   * one of the test tries has failed.
   * </p>
   * 
   * @param repetitionInfo
   *          The {@link RepetitionInfo} instance associated with
   *          {@link RepeatedTest}
   * @see #assertSuccessRate(RepetitionInfo, float)
   */
  public void signalFailure(RepetitionInfo repetitionInfo) {
    ++failures;
    log.warn(String.format("Test failed try %d of %d (%d %s)",
                           repetitionInfo.getCurrentRepetition(),
                           repetitionInfo.getTotalRepetitions(),
                           failures,
                           failures == 1 ? "failure" : "failures"));
  }

  /**
   * <p>
   * In a repeated test that has been set up for counting failures, asserts that
   * the test has succeeded at the given rate or greater (e.g. {@code .8f} means
   * 80% of the time or greater).
   * </p>
   * <p>
   * This is used in conjunction with {@link RepeatedTest},
   * {@link #setUpSuccessRate(RepetitionInfo)} and
   * {@link #signalFailure(RepetitionInfo)} in a
   * {@code try}/{@code catch}/{@code finally} block, in this manner:
   * </p>
<pre>
&#64;RepeatedTest($totalrepetitions)
public void testWithSuccessRate(RepetitionInfo repetitionInfo) {
  try {
    setUpSuccessRate(repetitionInfo);
    // code block to be repeated $totalrepetitions times
  }
  catch (Exception exc) {
    signalFailure(repetitionInfo);
  }
  finally {
    assertSuccessRate(repetitionInfo, $successrate);
  }
}
</pre>
   * 
   * @param repetitionInfo
   *          The {@link RepetitionInfo} instance associated with
   *          {@link RepeatedTest}
   * @param rate
   *          A desired success rate
   * @see #setUpSuccessRate(RepetitionInfo)
   * @see #signalFailure(RepetitionInfo)
   * @see RepeatedTest
   * @see RepetitionInfo
   */
  public void assertSuccessRate(RepetitionInfo repetitionInfo, float rate) {
    if (rate < 0.0f || 1.0f < rate) {
      throw new IllegalArgumentException("Success rate outside the range 0.0-1.0");
    }
    int total = repetitionInfo.getTotalRepetitions();
    if (repetitionInfo.getCurrentRepetition() == total) {
      float achieved = ((float)total - failures) / total;
      if (achieved < rate) {
        fail(String.format("Test failed %d of %d tries, not achieving a %f success rate.", failures, total, rate));
      }
    }
  }

  /**
   * Create and return the name of a temp dir.  The dir is created within
   * the default temp file dir.  It will be deleted following the test
   * @return The newly created directory
   * @throws IOException
   */
  public File getTempDir() throws IOException {
    File res =  getTempDir("locksstest");
    // To aid in finding the cause of temp dirs that don't get deleted,
    // setting -Dorg.lockss.test.idTempDirs=true will record the name of
    // the test creating the dir in <dir>/.locksstestcase .  This may cause
    // tests to fail (expecting empty dir).
    if (!isKeepTempFiles()
        && Boolean.getBoolean("org.lockss.test.idTempDirs")) {
      FileTestUtil.writeFile(new File(res, TEST_ID_FILE_NAME),
                             StringUtils.substringAfterLast(getClass().getName(), "."));
    }
    return res;
  }

  /**
   * Create and return the name of a temp dir.  The dir is created within
   * the default temp file dir.  It will be deleted following the test.
   * @param prefix the prefix of the name of the directory
   * @return The newly created directory
   * @throws IOException
   */
  public File getTempDir(String prefix) throws IOException {
    if (tmpDirs == null) {
      tmpDirs = new LinkedList<File>();
    }
    return getTempDir(tmpDirs, prefix);
  }

  /**
   * Static method to create and return the name of a temp dir, for callers
   * that must be static.  The dir is created within the default temp file
   * dir. {@link #deleteTempFiles(List<File>)} should be called at the
   * conclusion of the tests to delete the temp dirs..
   * @param tmpList a list where the file will be recorded, to pass to
   * {@link #deleteTempFiles(List<File>)}
   * @return The newly created directory
   * @throws IOException
   */
  public static File getTempDir(List<File> tmpList) throws IOException {
    return getTempDir(tmpList, "locksstest");
  }

  /**
   * Static method to create and return the name of a temp dir, for callers
   * that must be static.  The dir is created within the default temp file
   * dir. {@link #deleteTempFiles(List<File>)} should be called at the
   * conclusion of the tests to delete the temp dirs.
   * @param tmpList a list where the file will be recorded, to pass to
   * {@link #deleteTmpFiles()}
   * @param prefix the prefix of the name of the directory
   * @return The newly created directory
   * @throws IOException
   */
  public static File getTempDir(List<File> tmpList, String prefix)
      throws IOException {
    File tmpdir = FileUtil.createTempDir(prefix, null);
    tmpList.add(tmpdir);
    return tmpdir;
  }

  /**
   * Create and return the name of a temp file.  The file is created within
   * the default temp dir.
   * It will be deleted following the test.
   * @param prefix the prefix of the name of the file
   * @param suffix the suffix of the name of the file
   * @return The newly created file
   * @throws IOException
   */
  public File getTempFile(String prefix, String suffix) throws IOException {
    if (tmpDirs == null) {
      tmpDirs = new LinkedList<File>();
    }
    return getTempFile(tmpDirs, prefix, suffix);
  }

  /**
   * Static method to create and return the name of a temp file.  The file
   * is created within the default temp dir.  {@link
   * #deleteTempFiles(List<File>)} should be called at the conclusion of
   * the tests to delete the temp files.
   * @param tmpList a list where the file will be recorded, to pass to
   * {@link #deleteTempFiles(List<File>)}
   * @param prefix the prefix of the name of the file
   * @param suffix the suffix of the name of the file
   * @return The newly created file
   * @throws IOException
   */
  public static File getTempFile(List<File> tmpList,
				 String prefix, String suffix)
      throws IOException {
    File tmpfile = FileUtil.createTempFile(prefix, suffix);
    if (tmpfile != null) {
      tmpList.add(tmpfile);
    }
    return tmpfile;
  }

  public static boolean isKeepTempFiles() {
    return Boolean.getBoolean(SYSPROP_KEEP_TEMP_FILES);
  }

  /*
   * BEGIN JUnit 5 Assertions non-static copycat
   */

  public <V> V fail() {

    return Assertions.fail();
  }

  public <V> V fail(String message) {

    return Assertions.fail(message);
  }

  public <V> V fail(String message, Throwable cause) {

    return Assertions.fail(message, cause);
  }

  public <V> V fail(Throwable cause) {

    return Assertions.fail(cause);
  }

  public <V> V fail(Supplier<String> messageSupplier) {

    return Assertions.fail(messageSupplier);
  }

  public void assertTrue(boolean condition) {

    Assertions.assertTrue(condition);
  }

  public void assertTrue(boolean condition, Supplier<String> messageSupplier) {

    Assertions.assertTrue(condition, messageSupplier);
  }

  public void assertTrue(BooleanSupplier booleanSupplier) {

    Assertions.assertTrue(booleanSupplier);
  }

  public void assertTrue(BooleanSupplier booleanSupplier, String message) {

    Assertions.assertTrue(booleanSupplier, message);
  }

  public void assertTrue(boolean condition, String message) {

    Assertions.assertTrue(condition, message);
  }

  public void assertTrue(BooleanSupplier booleanSupplier, Supplier<String> messageSupplier) {

    Assertions.assertTrue(booleanSupplier, messageSupplier);
  }

  public void assertFalse(boolean condition) {

    Assertions.assertFalse(condition);
  }

  public void assertFalse(boolean condition, String message) {

    Assertions.assertFalse(condition, message);
  }

  public void assertFalse(boolean condition, Supplier<String> messageSupplier) {

    Assertions.assertFalse(condition, messageSupplier);
  }

  public void assertFalse(BooleanSupplier booleanSupplier) {

    Assertions.assertFalse(booleanSupplier);
  }

  public void assertFalse(BooleanSupplier booleanSupplier, String message) {

    Assertions.assertFalse(booleanSupplier, message);
  }

  public void assertFalse(BooleanSupplier booleanSupplier, Supplier<String> messageSupplier) {

    Assertions.assertFalse(booleanSupplier, messageSupplier);
  }

  public void assertNull(Object actual) {

    Assertions.assertNull(actual);
  }

  public void assertNull(Object actual, String message) {

    Assertions.assertNull(actual, message);
  }

  public void assertNull(Object actual, Supplier<String> messageSupplier) {

    Assertions.assertNull(actual, messageSupplier);
  }

  public void assertNotNull(Object actual) {

    Assertions.assertNotNull(actual);
  }

  public void assertNotNull(Object actual, String message) {

    Assertions.assertNotNull(actual, message);
  }

  public void assertNotNull(Object actual, Supplier<String> messageSupplier) {

    Assertions.assertNotNull(actual, messageSupplier);
  }

  public void assertEquals(short expected, short actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(short expected, Short actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Short expected, short actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Short expected, Short actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(short expected, short actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(short expected, Short actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Short expected, short actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Short expected, Short actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(short expected, short actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(short expected, Short actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Short expected, short actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Short expected, Short actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(byte expected, byte actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(byte expected, Byte actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Byte expected, byte actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Byte expected, Byte actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(byte expected, byte actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(byte expected, Byte actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Byte expected, byte actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Byte expected, Byte actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(byte expected, byte actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(byte expected, Byte actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Byte expected, byte actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Byte expected, Byte actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(int expected, int actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(int expected, Integer actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Integer expected, int actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Integer expected, Integer actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(int expected, int actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(int expected, Integer actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Integer expected, int actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Integer expected, Integer actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(int expected, int actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(int expected, Integer actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Integer expected, int actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Integer expected, Integer actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(long expected, long actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(long expected, Long actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Long expected, long actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Long expected, Long actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(long expected, long actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(long expected, Long actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Long expected, long actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Long expected, Long actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(long expected, long actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(long expected, Long actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Long expected, long actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Long expected, Long actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(float expected, float actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(float expected, Float actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Float expected, float actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Float expected, Float actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(float expected, float actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(float expected, Float actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Float expected, float actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Float expected, Float actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(float expected, float actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(float expected, Float actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Float expected, float actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Float expected, Float actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(float expected, float actual, float delta) {

    Assertions.assertEquals(expected, actual, delta);
  }

  public void assertEquals(float expected, float actual, float delta, String message) {

    Assertions.assertEquals(expected, actual, delta, message);
  }

  public void assertEquals(float expected, float actual, float delta, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, delta, messageSupplier);
  }

  public void assertEquals(double expected, double actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(double expected, Double actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Double expected, double actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Double expected, Double actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(double expected, double actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(double expected, Double actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Double expected, double actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Double expected, Double actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(double expected, double actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(double expected, Double actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Double expected, double actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Double expected, Double actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(double expected, double actual, double delta) {

    Assertions.assertEquals(expected, actual, delta);
  }

  public void assertEquals(double expected, double actual, double delta, String message) {

    Assertions.assertEquals(expected, actual, delta, message);
  }

  public void assertEquals(double expected, double actual, double delta, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, delta, messageSupplier);
  }

  public void assertEquals(char expected, char actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(char expected, Character actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Character expected, char actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Character expected, Character actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(char expected, char actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(char expected, Character actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Character expected, char actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Character expected, Character actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(char expected, char actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(char expected, Character actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Character expected, char actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Character expected, Character actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertEquals(Object expected, Object actual) {

    Assertions.assertEquals(expected, actual);
  }

  public void assertEquals(Object expected, Object actual, String message) {

    Assertions.assertEquals(expected, actual, message);
  }

  public void assertEquals(Object expected, Object actual, Supplier<String> messageSupplier) {

    Assertions.assertEquals(expected, actual, messageSupplier);
  }

  public void assertArrayEquals(boolean[] expected, boolean[] actual) {

    Assertions.assertArrayEquals(expected, actual);
  }

  public void assertArrayEquals(boolean[] expected, boolean[] actual, String message) {

    Assertions.assertArrayEquals(expected, actual, message);
  }

  public void assertArrayEquals(boolean[] expected, boolean[] actual, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, messageSupplier);
  }

  public void assertArrayEquals(char[] expected, char[] actual) {

    Assertions.assertArrayEquals(expected, actual);
  }

  public void assertArrayEquals(char[] expected, char[] actual, String message) {

    Assertions.assertArrayEquals(expected, actual, message);
  }

  public void assertArrayEquals(char[] expected, char[] actual, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, messageSupplier);
  }

  public void assertArrayEquals(byte[] expected, byte[] actual) {

    Assertions.assertArrayEquals(expected, actual);
  }

  public void assertArrayEquals(byte[] expected, byte[] actual, String message) {

    Assertions.assertArrayEquals(expected, actual, message);
  }

  public void assertArrayEquals(byte[] expected, byte[] actual, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, messageSupplier);
  }

  public void assertArrayEquals(short[] expected, short[] actual) {

    Assertions.assertArrayEquals(expected, actual);
  }

  public void assertArrayEquals(short[] expected, short[] actual, String message) {

    Assertions.assertArrayEquals(expected, actual, message);
  }

  public void assertArrayEquals(short[] expected, short[] actual, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, messageSupplier);
  }

  public void assertArrayEquals(int[] expected, int[] actual) {

    Assertions.assertArrayEquals(expected, actual);
  }

  public void assertArrayEquals(int[] expected, int[] actual, String message) {

    Assertions.assertArrayEquals(expected, actual, message);
  }

  public void assertArrayEquals(int[] expected, int[] actual, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, messageSupplier);
  }

  public void assertArrayEquals(long[] expected, long[] actual) {

    Assertions.assertArrayEquals(expected, actual);
  }

  public void assertArrayEquals(long[] expected, long[] actual, String message) {

    Assertions.assertArrayEquals(expected, actual, message);
  }

  public void assertArrayEquals(long[] expected, long[] actual, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, messageSupplier);
  }

  public void assertArrayEquals(float[] expected, float[] actual) {

    Assertions.assertArrayEquals(expected, actual);
  }

  public void assertArrayEquals(float[] expected, float[] actual, String message) {

    Assertions.assertArrayEquals(expected, actual, message);
  }

  public void assertArrayEquals(float[] expected, float[] actual, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, messageSupplier);
  }

  public void assertArrayEquals(float[] expected, float[] actual, float delta) {

    Assertions.assertArrayEquals(expected, actual, delta);
  }

  public void assertArrayEquals(float[] expected, float[] actual, float delta, String message) {

    Assertions.assertArrayEquals(expected, actual, delta, message);
  }

  public void assertArrayEquals(float[] expected, float[] actual, float delta, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, delta, messageSupplier);
  }

  public void assertArrayEquals(double[] expected, double[] actual) {

    Assertions.assertArrayEquals(expected, actual);
  }

  public void assertArrayEquals(double[] expected, double[] actual, String message) {

    Assertions.assertArrayEquals(expected, actual, message);
  }

  public void assertArrayEquals(double[] expected, double[] actual, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, messageSupplier);
  }

  public void assertArrayEquals(double[] expected, double[] actual, double delta) {

    Assertions.assertArrayEquals(expected, actual, delta);
  }

  public void assertArrayEquals(double[] expected, double[] actual, double delta, String message) {

    Assertions.assertArrayEquals(expected, actual, delta, message);
  }

  public void assertArrayEquals(double[] expected, double[] actual, double delta, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, delta, messageSupplier);
  }

  public void assertArrayEquals(Object[] expected, Object[] actual) {

    Assertions.assertArrayEquals(expected, actual);
  }

  public void assertArrayEquals(Object[] expected, Object[] actual, String message) {

    Assertions.assertArrayEquals(expected, actual, message);
  }

  public void assertArrayEquals(Object[] expected, Object[] actual, Supplier<String> messageSupplier) {

    Assertions.assertArrayEquals(expected, actual, messageSupplier);
  }

  public void assertIterableEquals(Iterable<?> expected, Iterable<?> actual) {

    Assertions.assertIterableEquals(expected, actual);
  }

  public void assertIterableEquals(Iterable<?> expected, Iterable<?> actual, String message) {

    Assertions.assertIterableEquals(expected, actual, message);
  }

  public void assertIterableEquals(Iterable<?> expected, Iterable<?> actual, Supplier<String> messageSupplier) {

    Assertions.assertIterableEquals(expected, actual, messageSupplier);
  }

  public void assertLinesMatch(List<String> expectedLines, List<String> actualLines) {

    Assertions.assertLinesMatch(expectedLines, actualLines);
  }

  public void assertLinesMatch(List<String> expectedLines, List<String> actualLines, String message) {

    Assertions.assertLinesMatch(expectedLines, actualLines, message);
  }

  public void assertLinesMatch(List<String> expectedLines, List<String> actualLines, Supplier<String> messageSupplier) {

    Assertions.assertLinesMatch(expectedLines, actualLines, messageSupplier);
  }

  public void assertLinesMatch(Stream<String> expectedLines, Stream<String> actualLines) {

    Assertions.assertLinesMatch(expectedLines, actualLines);
  }

  public void assertLinesMatch(Stream<String> expectedLines, Stream<String> actualLines, String message) {

    Assertions.assertLinesMatch(expectedLines, actualLines, message);
  }

  public void assertLinesMatch(Stream<String> expectedLines, Stream<String> actualLines,
      Supplier<String> messageSupplier) {

    Assertions.assertLinesMatch(expectedLines, actualLines, messageSupplier);
  }

  public void assertNotEquals(byte unexpected, byte actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(byte unexpected, Byte actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Byte unexpected, byte actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Byte unexpected, Byte actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(byte unexpected, byte actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(byte unexpected, Byte actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Byte unexpected, byte actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Byte unexpected, Byte actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(byte unexpected, byte actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(byte unexpected, Byte actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Byte unexpected, byte actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Byte unexpected, Byte actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(short unexpected, short actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(short unexpected, Short actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Short unexpected, short actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Short unexpected, Short actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(short unexpected, short actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(short unexpected, Short actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Short unexpected, short actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Short unexpected, Short actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(short unexpected, short actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(short unexpected, Short actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Short unexpected, short actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Short unexpected, Short actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(int unexpected, int actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(int unexpected, Integer actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Integer unexpected, int actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Integer unexpected, Integer actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(int unexpected, int actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(int unexpected, Integer actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Integer unexpected, int actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Integer unexpected, Integer actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(int unexpected, int actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(int unexpected, Integer actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Integer unexpected, int actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Integer unexpected, Integer actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(long unexpected, long actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(long unexpected, Long actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Long unexpected, long actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Long unexpected, Long actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(long unexpected, long actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(long unexpected, Long actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Long unexpected, long actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Long unexpected, Long actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(long unexpected, long actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(long unexpected, Long actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Long unexpected, long actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Long unexpected, Long actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(float unexpected, float actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(float unexpected, Float actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Float unexpected, float actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Float unexpected, Float actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(float unexpected, float actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(float unexpected, Float actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Float unexpected, float actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Float unexpected, Float actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(float unexpected, float actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(float unexpected, Float actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Float unexpected, float actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Float unexpected, Float actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(float unexpected, float actual, float delta) {

    Assertions.assertNotEquals(unexpected, actual, delta);
  }

  public void assertNotEquals(float unexpected, float actual, float delta, String message) {

    Assertions.assertNotEquals(unexpected, actual, delta, message);
  }

  public void assertNotEquals(float unexpected, float actual, float delta, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, delta, messageSupplier);
  }

  public void assertNotEquals(double unexpected, double actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(double unexpected, Double actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Double unexpected, double actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Double unexpected, Double actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(double unexpected, double actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(double unexpected, Double actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Double unexpected, double actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Double unexpected, Double actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(double unexpected, double actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(double unexpected, Double actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Double unexpected, double actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Double unexpected, Double actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(double unexpected, double actual, double delta) {

    Assertions.assertNotEquals(unexpected, actual, delta);
  }

  public void assertNotEquals(double unexpected, double actual, double delta, String message) {

    Assertions.assertNotEquals(unexpected, actual, delta, message);
  }

  public void assertNotEquals(double unexpected, double actual, double delta, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, delta, messageSupplier);
  }

  public void assertNotEquals(char unexpected, char actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(char unexpected, Character actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Character unexpected, char actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Character unexpected, Character actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(char unexpected, char actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(char unexpected, Character actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Character unexpected, char actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Character unexpected, Character actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(char unexpected, char actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(char unexpected, Character actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Character unexpected, char actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Character unexpected, Character actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertNotEquals(Object unexpected, Object actual) {

    Assertions.assertNotEquals(unexpected, actual);
  }

  public void assertNotEquals(Object unexpected, Object actual, String message) {

    Assertions.assertNotEquals(unexpected, actual, message);
  }

  public void assertNotEquals(Object unexpected, Object actual, Supplier<String> messageSupplier) {

    Assertions.assertNotEquals(unexpected, actual, messageSupplier);
  }

  public void assertSame(Object expected, Object actual) {

    Assertions.assertSame(expected, actual);
  }

  public void assertSame(Object expected, Object actual, String message) {

    Assertions.assertSame(expected, actual, message);
  }

  public void assertSame(Object expected, Object actual, Supplier<String> messageSupplier) {

    Assertions.assertSame(expected, actual, messageSupplier);
  }

  public void assertNotSame(Object unexpected, Object actual) {

    Assertions.assertNotSame(unexpected, actual);
  }

  public void assertNotSame(Object unexpected, Object actual, String message) {

    Assertions.assertNotSame(unexpected, actual, message);
  }

  public void assertNotSame(Object unexpected, Object actual, Supplier<String> messageSupplier) {

    Assertions.assertNotSame(unexpected, actual, messageSupplier);
  }

  public void assertAll(Executable... executables) throws MultipleFailuresError {

    Assertions.assertAll(executables);
  }

  public void assertAll(String heading, Executable... executables) throws MultipleFailuresError {

    Assertions.assertAll(heading, executables);
  }

  public void assertAll(Collection<Executable> executables) throws MultipleFailuresError {

    Assertions.assertAll(executables);
  }

  public void assertAll(String heading, Collection<Executable> executables) throws MultipleFailuresError {

    Assertions.assertAll(heading, executables);
  }

  public void assertAll(Stream<Executable> executables) throws MultipleFailuresError {

    Assertions.assertAll(executables);
  }

  public void assertAll(String heading, Stream<Executable> executables) throws MultipleFailuresError {

    Assertions.assertAll(heading, executables);
  }

  public <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {

    return Assertions.assertThrows(expectedType, executable);
  }

  public <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable, String message) {

    return Assertions.assertThrows(expectedType, executable, message);
  }

  public <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable,
      Supplier<String> messageSupplier) {

    return Assertions.assertThrows(expectedType, executable, messageSupplier);
  }

  public void assertDoesNotThrow(Executable executable) {

    Assertions.assertDoesNotThrow(executable);
  }

  public void assertDoesNotThrow(Executable executable, String message) {

    Assertions.assertDoesNotThrow(executable, message);
  }

  public void assertDoesNotThrow(Executable executable, Supplier<String> messageSupplier) {

    Assertions.assertDoesNotThrow(executable, messageSupplier);
  }

  public <T> T assertDoesNotThrow(ThrowingSupplier<T> supplier) {

    return Assertions.assertDoesNotThrow(supplier);
  }

  public <T> T assertDoesNotThrow(ThrowingSupplier<T> supplier, String message) {

    return Assertions.assertDoesNotThrow(supplier, message);
  }

  public <T> T assertDoesNotThrow(ThrowingSupplier<T> supplier, Supplier<String> messageSupplier) {

    return Assertions.assertDoesNotThrow(supplier, messageSupplier);
  }

  public void assertTimeout(Duration timeout, Executable executable) {

    Assertions.assertTimeout(timeout, executable);
  }

  public void assertTimeout(Duration timeout, Executable executable, String message) {

    Assertions.assertTimeout(timeout, executable, message);
  }

  public void assertTimeout(Duration timeout, Executable executable, Supplier<String> messageSupplier) {

    Assertions.assertTimeout(timeout, executable, messageSupplier);
  }

  public <T> T assertTimeout(Duration timeout, ThrowingSupplier<T> supplier) {

    return Assertions.assertTimeout(timeout, supplier);
  }

  public <T> T assertTimeout(Duration timeout, ThrowingSupplier<T> supplier, String message) {

    return Assertions.assertTimeout(timeout, supplier, message);
  }

  public <T> T assertTimeout(Duration timeout, ThrowingSupplier<T> supplier, Supplier<String> messageSupplier) {

    return Assertions.assertTimeout(timeout, supplier, messageSupplier);
  }

  public void assertTimeoutPreemptively(Duration timeout, Executable executable) {

    Assertions.assertTimeoutPreemptively(timeout, executable);
  }

  public void assertTimeoutPreemptively(Duration timeout, Executable executable, String message) {

    Assertions.assertTimeoutPreemptively(timeout, executable, message);
  }

  public void assertTimeoutPreemptively(Duration timeout, Executable executable, Supplier<String> messageSupplier) {

    Assertions.assertTimeoutPreemptively(timeout, executable, messageSupplier);
  }

  public <T> T assertTimeoutPreemptively(Duration timeout, ThrowingSupplier<T> supplier) {

    return Assertions.assertTimeoutPreemptively(timeout, supplier);
  }

  public <T> T assertTimeoutPreemptively(Duration timeout, ThrowingSupplier<T> supplier, String message) {

    return Assertions.assertTimeoutPreemptively(timeout, supplier, message);
  }

  public <T> T assertTimeoutPreemptively(Duration timeout, ThrowingSupplier<T> supplier,
      Supplier<String> messageSupplier) {

    return Assertions.assertTimeoutPreemptively(timeout, supplier, messageSupplier);
  }

  /*
   * END JUnit 5 Assertions non-static copycat
   */
  
  
  public <T> void assertThat(T actual, Matcher<? super T> matcher) {

    MatcherAssert.assertThat(actual, matcher);
  }

  
  public <T> void assertThat(String reason, T actual,
                             Matcher<? super T> matcher) {

    MatcherAssert.assertThat(reason, actual, matcher);
  }


  public void assertThat(String reason, boolean assertion) {

    MatcherAssert.assertThat(reason, assertion);
  }


  public <T> Matcher<T> allOf(Iterable<Matcher<? super T>> matchers) {

    return Matchers.allOf(matchers);
  }


  public <T> Matcher<T> allOf(Matcher<? super T>... matchers) {

    return Matchers.allOf(matchers);
  }


  public <T> Matcher<T> allOf(Matcher<? super T> first,
                              Matcher<? super T> second) {

    return Matchers.allOf(first, second);
  }


  public <T> Matcher<T> allOf(Matcher<? super T> first,
                              Matcher<? super T> second,
                              Matcher<? super T> third) {

    return Matchers.allOf(first, second, third);
  }


  public <T> Matcher<T> allOf(Matcher<? super T> first,
                              Matcher<? super T> second,
                              Matcher<? super T> third,
                              Matcher<? super T> fourth) {

    return Matchers.allOf(first, second, third, fourth);
  }


  public <T> Matcher<T> allOf(Matcher<? super T> first,
                              Matcher<? super T> second,
                              Matcher<? super T> third,
                              Matcher<? super T> fourth,
                              Matcher<? super T> fifth) {

    return Matchers.allOf(first, second, third, fourth, fifth);
  }


  public <T> Matcher<T> allOf(Matcher<? super T> first,
                              Matcher<? super T> second,
                              Matcher<? super T> third,
                              Matcher<? super T> fourth,
                              Matcher<? super T> fifth,
                              Matcher<? super T> sixth) {

    return Matchers.allOf(first, second, third, fourth, fifth, sixth);
  }


  public <T> AnyOf<T> anyOf(Iterable<Matcher<? super T>> matchers) {

    return Matchers.anyOf(matchers);
  }


  public <T> AnyOf<T> anyOf(Matcher<T> first, Matcher<? super T> second,
                            Matcher<? super T> third) {

    return Matchers.anyOf(first, second, third);
  }


  public <T> AnyOf<T> anyOf(Matcher<T> first, Matcher<? super T> second,
                            Matcher<? super T> third,
                            Matcher<? super T> fourth) {

    return Matchers.anyOf(first, second, third, fourth);
  }


  public <T> AnyOf<T> anyOf(Matcher<T> first, Matcher<? super T> second,
                            Matcher<? super T> third, Matcher<? super T> fourth,
                            Matcher<? super T> fifth) {

    return Matchers.anyOf(first, second, third, fourth, fifth);
  }


  public <T> AnyOf<T> anyOf(Matcher<T> first, Matcher<? super T> second,
                            Matcher<? super T> third, Matcher<? super T> fourth,
                            Matcher<? super T> fifth,
                            Matcher<? super T> sixth) {

    return Matchers.anyOf(first, second, third, fourth, fifth, sixth);
  }


  public <T> AnyOf<T> anyOf(Matcher<T> first, Matcher<? super T> second) {

    return Matchers.anyOf(first, second);
  }


  public <T> AnyOf<T> anyOf(Matcher<? super T>... matchers) {

    return Matchers.anyOf(matchers);
  }


  public <LHS> CombinableBothMatcher<LHS> both(Matcher<? super LHS> matcher) {

    return Matchers.both(matcher);
  }


  public <LHS> CombinableEitherMatcher<LHS> either(Matcher<? super LHS> matcher) {

    return Matchers.either(matcher);
  }


  public <T> Matcher<T> describedAs(String description, Matcher<T> matcher,
                                    Object... values) {

    return Matchers.describedAs(description, matcher, values);
  }


  public <U> Matcher<Iterable<U>> everyItem(Matcher<U> itemMatcher) {

    return Matchers.everyItem(itemMatcher);
  }


  public <T> Matcher<T> is(T value) {

    return Matchers.is(value);
  }


  public <T> Matcher<T> is(Matcher<T> matcher) {

    return Matchers.is(matcher);
  }


  public <T> Matcher<T> is(Class<T> type) {

    return Matchers.is(type);
  }


  public <T> Matcher<T> isA(Class<T> type) {

    return Matchers.isA(type);
  }


  public Matcher<Object> anything() {

    return Matchers.anything();
  }


  public Matcher<Object> anything(String description) {

    return Matchers.anything(description);
  }


  public <T> Matcher<Iterable<? super T>> hasItem(T item) {

    return Matchers.hasItem(item);
  }


  public <T> Matcher<Iterable<? super T>> hasItem(Matcher<? super T> itemMatcher) {

    return Matchers.hasItem(itemMatcher);
  }


  public <T> Matcher<Iterable<T>> hasItems(T... items) {

    return Matchers.hasItems(items);
  }


  public <T> Matcher<Iterable<T>> hasItems(Matcher<? super T>... itemMatchers) {

    return Matchers.hasItems(itemMatchers);
  }


  public <T> Matcher<T> equalTo(T operand) {

    return Matchers.equalTo(operand);
  }


  public <T> Matcher<T> any(Class<T> type) {

    return Matchers.any(type);
  }


  public <T> Matcher<T> instanceOf(Class<?> type) {

    return Matchers.instanceOf(type);
  }


  public <T> Matcher<T> not(Matcher<T> matcher) {

    return Matchers.not(matcher);
  }


  public <T> Matcher<T> not(T value) {

    return Matchers.not(value);
  }


  public Matcher<Object> nullValue() {

    return Matchers.nullValue();
  }


  public <T> Matcher<T> nullValue(Class<T> type) {

    return Matchers.nullValue(type);
  }


  public Matcher<Object> notNullValue() {

    return Matchers.notNullValue();
  }


  public <T> Matcher<T> notNullValue(Class<T> type) {

    return Matchers.notNullValue(type);
  }


  public <T> Matcher<T> sameInstance(T target) {

    return Matchers.sameInstance(target);
  }


  public <T> Matcher<T> theInstance(T target) {

    return Matchers.theInstance(target);
  }


  public Matcher<String> containsString(String substring) {

    return Matchers.containsString(substring);
  }


  public Matcher<String> startsWith(String prefix) {

    return Matchers.startsWith(prefix);
  }


  public Matcher<String> endsWith(String suffix) {

    return Matchers.endsWith(suffix);
  }


  public <T> IsArray<T> array(Matcher<? super T>... elementMatchers) {

    return Matchers.array(elementMatchers);
  }


  public <T> Matcher<T[]> hasItemInArray(T element) {

    return Matchers.hasItemInArray(element);
  }


  public <T> Matcher<T[]> hasItemInArray(Matcher<? super T> elementMatcher) {

    return Matchers.hasItemInArray(elementMatcher);
  }


  public <E> Matcher<E[]> arrayContaining(List<Matcher<? super E>> itemMatchers) {

    return Matchers.arrayContaining(itemMatchers);
  }


  public <E> Matcher<E[]> arrayContaining(E... items) {

    return Matchers.arrayContaining(items);
  }


  public <E> Matcher<E[]> arrayContaining(Matcher<? super E>... itemMatchers) {

    return Matchers.arrayContaining(itemMatchers);
  }


  public <E> Matcher<E[]> arrayContainingInAnyOrder(E... items) {

    return Matchers.arrayContainingInAnyOrder(items);
  }


  public <E> Matcher<E[]> arrayContainingInAnyOrder(Matcher<? super E>... itemMatchers) {

    return Matchers.arrayContainingInAnyOrder(itemMatchers);
  }


  public <E> Matcher<E[]> arrayContainingInAnyOrder(Collection<Matcher<? super E>> itemMatchers) {

    return Matchers.arrayContainingInAnyOrder(itemMatchers);
  }


  public <E> Matcher<E[]> arrayWithSize(Matcher<? super Integer> sizeMatcher) {

    return Matchers.arrayWithSize(sizeMatcher);
  }


  public <E> Matcher<E[]> arrayWithSize(int size) {

    return Matchers.arrayWithSize(size);
  }


  public <E> Matcher<E[]> emptyArray() {

    return Matchers.emptyArray();
  }


  public <E> Matcher<Collection<? extends E>> hasSize(Matcher<? super Integer> sizeMatcher) {

    return Matchers.hasSize(sizeMatcher);
  }


  public <E> Matcher<Collection<? extends E>> hasSize(int size) {

    return Matchers.hasSize(size);
  }


  public <E> Matcher<Collection<? extends E>> empty() {

    return Matchers.empty();
  }


  public <E> Matcher<Collection<E>> emptyCollectionOf(Class<E> type) {

    return Matchers.emptyCollectionOf(type);
  }


  public <E> Matcher<Iterable<? extends E>> emptyIterable() {

    return Matchers.emptyIterable();
  }


  public <E> Matcher<Iterable<E>> emptyIterableOf(Class<E> type) {

    return Matchers.emptyIterableOf(type);
  }


  public <E> Matcher<Iterable<? extends E>> contains(Matcher<? super E>... itemMatchers) {

    return Matchers.contains(itemMatchers);
  }


  public <E> Matcher<Iterable<? extends E>> contains(E... items) {

    return Matchers.contains(items);
  }


  public <E> Matcher<Iterable<? extends E>> contains(Matcher<? super E> itemMatcher) {

    return Matchers.contains(itemMatcher);
  }


  public <E> Matcher<Iterable<? extends E>> contains(List<Matcher<? super E>> itemMatchers) {

    return Matchers.contains(itemMatchers);
  }


  public <T> Matcher<Iterable<? extends T>> containsInAnyOrder(T... items) {

    return Matchers.containsInAnyOrder(items);
  }


  public <T> Matcher<Iterable<? extends T>> containsInAnyOrder(Collection<Matcher<? super T>> itemMatchers) {

    return Matchers.containsInAnyOrder(itemMatchers);
  }


  public <T> Matcher<Iterable<? extends T>> containsInAnyOrder(Matcher<? super T>... itemMatchers) {

    return Matchers.containsInAnyOrder(itemMatchers);
  }


  public <E> Matcher<Iterable<? extends E>> containsInAnyOrder(Matcher<? super E> itemMatcher) {

    return Matchers.containsInAnyOrder(itemMatcher);
  }


  public <E> Matcher<Iterable<E>> iterableWithSize(Matcher<? super Integer> sizeMatcher) {

    return Matchers.iterableWithSize(sizeMatcher);
  }


  public <E> Matcher<Iterable<E>> iterableWithSize(int size) {

    return Matchers.iterableWithSize(size);
  }


  public <K, V> Matcher<Map<? extends K, ? extends V>> hasEntry(K key,
                                                                V value) {

    return Matchers.hasEntry(key, value);
  }


  public <K, V> Matcher<Map<? extends K, ? extends V>> hasEntry(Matcher<? super K> keyMatcher,
                                                                Matcher<? super V> valueMatcher) {

    return Matchers.hasEntry(keyMatcher, valueMatcher);
  }


  public <K> Matcher<Map<? extends K, ?>> hasKey(Matcher<? super K> keyMatcher) {

    return Matchers.hasKey(keyMatcher);
  }


  public <K> Matcher<Map<? extends K, ?>> hasKey(K key) {

    return Matchers.hasKey(key);
  }


  public <V> Matcher<Map<?, ? extends V>> hasValue(V value) {

    return Matchers.hasValue(value);
  }


  public <V> Matcher<Map<?, ? extends V>> hasValue(Matcher<? super V> valueMatcher) {

    return Matchers.hasValue(valueMatcher);
  }


  public <T> Matcher<T> isIn(Collection<T> collection) {

    return Matchers.isIn(collection);
  }


  public <T> Matcher<T> isIn(T[] param1) {

    return Matchers.isIn(param1);
  }


  public <T> Matcher<T> isOneOf(T... elements) {

    return Matchers.isOneOf(elements);
  }


  public Matcher<Double> closeTo(double operand, double error) {

    return Matchers.closeTo(operand, error);
  }


  public Matcher<BigDecimal> closeTo(BigDecimal operand, BigDecimal error) {

    return Matchers.closeTo(operand, error);
  }


  public <T extends Comparable<T>> Matcher<T> comparesEqualTo(T value) {

    return Matchers.comparesEqualTo(value);
  }


  public <T extends Comparable<T>> Matcher<T> greaterThan(T value) {

    return Matchers.greaterThan(value);
  }


  public <T extends Comparable<T>> Matcher<T> greaterThanOrEqualTo(T value) {

    return Matchers.greaterThanOrEqualTo(value);
  }


  public <T extends Comparable<T>> Matcher<T> lessThan(T value) {

    return Matchers.lessThan(value);
  }


  public <T extends Comparable<T>> Matcher<T> lessThanOrEqualTo(T value) {

    return Matchers.lessThanOrEqualTo(value);
  }


  public Matcher<String> equalToIgnoringCase(String expectedString) {

    return Matchers.equalToIgnoringCase(expectedString);
  }


  public Matcher<String> equalToIgnoringWhiteSpace(String expectedString) {

    return Matchers.equalToIgnoringWhiteSpace(expectedString);
  }


  public Matcher<String> isEmptyString() {

    return Matchers.isEmptyString();
  }


  public Matcher<String> isEmptyOrNullString() {

    return Matchers.isEmptyOrNullString();
  }


  public Matcher<String> stringContainsInOrder(Iterable<String> substrings) {

    return Matchers.stringContainsInOrder(substrings);
  }


  public <T> Matcher<T> hasToString(Matcher<? super String> toStringMatcher) {

    return Matchers.hasToString(toStringMatcher);
  }


  public <T> Matcher<T> hasToString(String expectedToString) {

    return Matchers.hasToString(expectedToString);
  }


  public <T> Matcher<Class<?>> typeCompatibleWith(Class<T> baseType) {

    return Matchers.typeCompatibleWith(baseType);
  }


  public Matcher<EventObject> eventFrom(Class<? extends EventObject> eventClass,
                                        Object source) {

    return Matchers.eventFrom(eventClass, source);
  }


  public Matcher<EventObject> eventFrom(Object source) {

    return Matchers.eventFrom(source);
  }


  public <T> Matcher<T> hasProperty(String propertyName) {

    return Matchers.hasProperty(propertyName);
  }


  public <T> Matcher<T> hasProperty(String propertyName,
                                    Matcher<?> valueMatcher) {

    return Matchers.hasProperty(propertyName, valueMatcher);
  }


  public <T> Matcher<T> samePropertyValuesAs(T expectedBean) {

    return Matchers.samePropertyValuesAs(expectedBean);
  }


  public Matcher<Node> hasXPath(String xPath,
                                NamespaceContext namespaceContext) {

    return Matchers.hasXPath(xPath, namespaceContext);
  }


  public Matcher<Node> hasXPath(String xPath) {

    return Matchers.hasXPath(xPath);
  }


  public Matcher<Node> hasXPath(String xPath, NamespaceContext namespaceContext,
                                Matcher<String> valueMatcher) {

    return Matchers.hasXPath(xPath, namespaceContext, valueMatcher);
  }


  public Matcher<Node> hasXPath(String xPath, Matcher<String> valueMatcher) {

    return Matchers.hasXPath(xPath, valueMatcher);
  }

  /** Assert that Iterable has no elements */
  public void assertEmpty(Iterable iter) {
    assertNotNull(iter);
    assertFalse(iter.iterator().hasNext(), "Expected empty, wasn't");
  }

  /** Assert that Iterator has no elements */
  public void assertEmpty(Iterator iter) {
    assertNotNull(iter);
    assertFalse(iter.hasNext());
  }

  /** Assert that the Executable throws an instance of the expected class,
   * and that the expected pattern is found in the Throwable's message . */
  public <T extends Throwable> T assertThrowsMatch(Class<T> expectedType,
                                                   String pattern,
                                                   Executable executable) {
    return assertThrowsMatch(expectedType, pattern, executable, null);
  }

  /** Assert that the Executable throws an instnace of the expected class,
   * and that the expected pattern is found in the Throwable's message . */
  public <T extends Throwable> T assertThrowsMatch(Class<T> expectedType,
                                                   String pattern,
                                                   Executable executable,
                                                   String message) {
    T th = assertThrows(expectedType, executable, message);
    assertThat(message,
               th.getMessage(), findPattern(pattern));
    return th;
  }

  /** Read a byte, fail with a detailed message if an IOException is
   * thrown. */
  int paranoidRead(InputStream in, String streamName, long cnt, long expLen,
                   String message) {
    try {
      return in.read();
    } catch (IOException e) {
      fail( ( buildPrefix(message) + "after " + cnt + " bytes" +
              (expLen >= 0 ? " of " + expLen : "") +
              ", " + streamName + " stream threw " + e.toString()),
            e);
      // compiler doesn't know fail() doesn't return
      throw new IllegalStateException("can't happen");
    }
  }

  /** Assert that the two InputStreams return the same sequence of bytes,
   * of the expected length.  Displays a detailed message if a mistmatch is
   * found, one stream runs out before the other, the length doesn't match
   * or an IOException is thrown while reading. */
  public void assertSameBytes(InputStream expected,
                              InputStream actual,
                              long expLen) {
    assertSameBytes(expected, actual, expLen, null);
  }
  
  /** Assert that the two InputStreams return the same sequence of bytes.
   * Displays a detailed message if a mistmatch is found, one stream runs
   * out before the other, the length doesn't match or an IOException is
   * thrown while reading. */
  public void assertSameBytes(InputStream expected,
                              InputStream actual) {
    assertSameBytes(expected, actual, null);
  }
  
  /** Assert that the two InputStreams return the same sequence of bytes.
   * Displays a detailed message if a mistmatch is found, one stream runs
   * out before the other, the length doesn't match or an IOException is
   * thrown while reading. */
  public void assertSameBytes(InputStream expected,
                              InputStream actual,
                              String message) {
    assertSameBytes(expected, actual, -1, message);
  }

  /** Assert that the two InputStreams return the same sequence of bytes,
   * of the expected length.  Displays a detailed message if a mistmatch is
   * found, one stream runs out before the other, the length doesn't match
   * or an IOException is thrown while reading. */
  public void assertSameBytes(InputStream expected,
                              InputStream actual,
                              long expLen,
                              String message) {
    if (expected == actual) {
      throw new IllegalArgumentException("assertSameBytes() called with same stream for both expected and actual.");
    }
    // XXX This could obscure the byte count at which an error occurs
    if (!(expected instanceof BufferedInputStream)) {
      expected = new BufferedInputStream(expected);
    }
    if (!(actual instanceof BufferedInputStream)) {
      actual = new BufferedInputStream(actual);
    }
    long cnt = 0;
    int ch = paranoidRead(expected, "expected", cnt, expLen, message);
    while (-1 != ch) {
      int ch2 = paranoidRead(actual, "actual", cnt, expLen, message);
      if (-1 == ch2) {
        fail(buildPrefix(message) +
             "actual stream ran out early, at byte position " + cnt);
      }
      cnt++;

      if (ch != ch2) {      // Avoid building fail message unless necessary
	assertEquals(ch, ch2,
		     buildPrefix(message) + "at byte position " + cnt);
      }
      ch = paranoidRead(expected, "expected", cnt, expLen, message);
    }

    int ch2 = paranoidRead(actual, "actual", cnt, expLen, message);
    if (-1 != ch2) {
      fail(buildPrefix(message) +
           "expected stream ran out early, at byte position " + cnt);
    }
    if (expLen >= 0) {
      assertEquals(expLen, cnt, "Both streams were wrong length");
    }
  }
  
  static String buildPrefix(String message) {
    return (StringUtils.isNotBlank(message) ? message + " ==> " : "");
  }

  public void assertSameCharacters(Reader expected,
                                   Reader actual,
                                   String message) {
    assertSameCharacters(expected, actual, -1, message);
  }
  
  /** Assert that the two Readers return the same sequence of
   * characters */
  public void assertSameCharacters(Reader expected,
                                   Reader actual) {
    assertSameCharacters(expected, actual, null);
  }
  
  /** Read a byte, fail with a detailed message if an IOException is
   * thrown. */
  int paranoidReadChar(Reader in, String streamName, long cnt, long expLen,
                       String message) {
    try {
      return in.read();
    } catch (IOException e) {
      fail( ( buildPrefix(message) + "after " + cnt + " chars" +
              (expLen >= 0 ? " of " + expLen : "") +
              ", " + streamName + " stream threw " + e.toString()),
            e);
      // compiler doesn't know fail() doesn't return
      throw new IllegalStateException("can't happen");
    }
  }

  /** Assert that the two Readers return the same sequence of characters,
   * of the expected length.  Displays a detailed message if a mistmatch is
   * found, one stream runs out before the other, the length doesn't match
   * or an IOException is thrown while reading. */
  public void assertSameCharacters(Reader expected,
                                   Reader actual,
                                   long expLen,
                                   String message) {
    if (expected == actual) {
      throw new IllegalArgumentException("assertSameBytes() called with same reader for both expected and actual.");
    }
    // XXX This could obscure the char count at which an error occurs
    if (!(expected instanceof BufferedReader)) {
      expected = new BufferedReader(expected);
    }
    if (!(actual instanceof BufferedReader)) {
      actual = new BufferedReader(actual);
    }
    long cnt = 0;
    int ch = paranoidReadChar(expected, "expected", cnt, expLen, message);
    while (-1 != ch) {
      int ch2 = paranoidReadChar(actual, "actual", cnt, expLen, message);
      if (-1 == ch2) {
        fail(buildPrefix(message) +
             "actual stream ran out early, at char position " + cnt);
      }
      cnt++;
      assertEquals((char)ch, (char)ch2,
                   buildPrefix(message) + "at char position " + cnt);
      ch = paranoidReadChar(expected, "expected", cnt, expLen, message);
    }

    int ch2 = paranoidReadChar(actual, "actual", cnt, expLen, message);
    if (-1 != ch2) {
      fail(buildPrefix(message) +
           "expected stream ran out early, at char position " + cnt);
    }
    if (expLen >= 0) {
      assertEquals(expLen, cnt, "Both streams were wrong length");
    }
  }

  /**
   * Asserts that a string matches the content of an InputStream
   */
  public void assertInputStreamMatchesString(String expected,
                                             InputStream in)
      throws IOException {
    assertInputStreamMatchesString(expected, in, "UTF-8");
  }

  /**
   * Asserts that a string matches the content of an InputStream
   */
  public void assertInputStreamMatchesString(String expected,
                                             InputStream in,
                                             String encoding)
      throws IOException {
    Reader rdr = new InputStreamReader(in, encoding);
    assertReaderMatchesString(expected, rdr);
  }

  /**
   * Asserts that a string matches the content of a reader read using the
   * specified buffer size.
   */
  public void assertInputStreamMatchesString(String expected,
                                             InputStream in,
                                             int bufsize)
      throws IOException {
    Reader rdr = new InputStreamReader(in, "UTF-8");
    assertReaderMatchesString(expected, rdr, bufsize);
  }

  /**
   * Asserts that a string matches the content of a reader
   */
  public void assertReaderMatchesString(String expected, Reader reader)
      throws IOException {
    int len = Math.max(1, expected.length() * 2);
    char[] ca = new char[len];
    StringBuilder actual = new StringBuilder(expected.length());

    int n;
    while ((n = reader.read(ca)) != -1) {
      actual.append(ca, 0, n);
    }
    assertEquals(expected, actual.toString());
  }

  /**
   * Asserts that a string matches the content of a reader read using the
   * specified buffer size.
   */
  public void assertReaderMatchesString(String expected, Reader reader,
                                               int bufsize)
      throws IOException {
    char[] ca = new char[bufsize];
    StringBuilder actual = new StringBuilder(expected.length());

    int n;
    while ((n = reader.read(ca)) != -1) {
      actual.append(ca, 0, n);
    }
    assertEquals("With buffer size " + bufsize + ",",
                 expected, actual.toString());
  }

  /** Log the start of each test class */
  @BeforeAll
  public static void logTestClass(TestInfo info) {
    log.info("Start test class: " + info.getDisplayName());
  }

  /** Log the end of each test class */
  @AfterAll
  public static void logTestClassEnd(TestInfo info) {
    log.info("End test class: " + info.getDisplayName());
  }

  /** Log each test method */
  @BeforeEach
  public void beforeEachLog(TestInfo info) {
    LockssLogger.resetLogs();
    log.info("Testcase: " + info.getDisplayName());
  }

  /** Called by the &#64;VariantTest mechanism to set up the named
   * variant */
  protected void setUpVariant(String vName) {
  }
  
  public Matcher<String> findPattern(Pattern pattern) {
    return FindPattern.findPattern(pattern);
  }

  public Matcher<String> findPattern(String regex) {
    return FindPattern.findPattern(regex);
  }

  public Matcher<String> matchesPattern(Pattern pattern) {
    return MatchesPattern.matchesPattern(pattern);
  }

  public Matcher<String> matchesPattern(String regex) {
    return MatchesPattern.matchesPattern(regex);
  }
  
  /** For historical reasons, this is not anchored - uses FindPatter, not
   * Matchespattern */
  public void assertMatchesRE(String regexp, String string) {
    assertThat(string, FindPattern.findPattern(regexp));
  }

  /** For historical reasons, this is not anchored - uses FindPatter, not
   * Matchespattern */
  public void assertMatchesRE(String msg,
			      String regexp, String string) {
    assertThat(msg, string, FindPattern.findPattern(regexp));
  }

  public void assertClass(Class expClass, Object obj) {
    assertClass(null, expClass, obj);
  }

  public void assertClass(String msg, Class expClass, Object obj) {
    if (! expClass.isInstance(obj)) {
      StringBuffer sb = new StringBuffer();
      if (msg != null) {
        sb.append(msg);
        sb.append(" ");
      }
      sb.append(obj);
      if (obj != null) {
        sb.append(" (a ");
        sb.append(obj.getClass().getName());
        sb.append(")");
      }
      sb.append(" is not a ");
      sb.append(expClass.getName());
      fail(sb.toString());
    }
  }

  /** Abstraction to do something in another thread, after a delay,
   * unless cancelled.  If the scheduled activity is still pending when the
   * test completes, it is cancelled by tearDown().
   * <br>For one-off use:<pre>
   *  final Object obj = ...;
   *  DoLater doer = new DoLater(1000) {
   *      protected void doit() {
   *        obj.method(...);
   *      }
   *    };
   *  doer.start();</pre>
   *
   * Or, for convenient repeated use of a particular delayed operation,
   * define a class that extends <code>DoLater</code>,
   * with a constructor that calls
   * <code>super(wait)</code> and stores any other necessary args into
   * instance vars, and a <code>doit()</code> method that does whatever needs
   * to be done.  And a convenience method to create and start it.
   * For example, <code>Interrupter</code> is defined as:<pre>
   *  public class Interrupter extends DoLater {
   *    private Thread thread;
   *    Interrupter(long waitMs, Thread thread) {
   *      super(waitMs);
   *      this.thread = thread;
   *    }
   *
   *    protected void doit() {
   *      thread.interrupt();
   *    }
   *  }
   *
   *  public Interrupter interruptMeIn(long ms) {
   *    Interrupter i = new Interrupter(ms, Thread.currentThread());
   *    i.start();
   *    return i;
   *  }</pre>
   *
   * Then, to protect a test with a timeout:<pre>
   *  Interrupter intr = null;
   *  try {
   *    intr = interruptMeIn(1000);
   *    // perform a test that should complete in less than one second
   *    intr.cancel();
   *  } finally {
   *    if (intr.did()) {
   *      fail("operation failed to complete in one second");
   *    }
   *  }</pre>
   * The <code>cancel()</code> ensures that the interrupt will not
   * happen after the try block completes.  (This is not necessary at the
   * end of a test case, as any pending interrupters will be cancelled
   * by tearDown.)
   */
  public abstract class DoLater extends Thread {
    
    private long wait;
    
    private boolean want = true;
    
    private boolean did = false;
    
    private boolean threadDump = false;

    protected DoLater(long waitMs) {
      wait = waitMs;
    }

    /** Must override this to perform desired action */
    protected abstract void doit();

    /**
     * Return true iff action was taken
     * @return true iff taken
     */
    public boolean did() {
      return did;
    }

    /** Cancel the action iff it hasn't already started.  If it has started,
     * wait until it completes.  (Thus when <code>cancel()</code> returns, it
     * is safe to destroy any environment on which the action relies.)
     */
    public synchronized void cancel() {
      if (want) {
        want = false;
        this.interrupt();
      }
    }

    public final void run() {
      try {
        synchronized (LockssTestCase5.this) {
          if (doLaters == null) {
            doLaters = new LinkedList<DoLater>();
          }
          doLaters.add(this);
        }
        if (wait != 0) {
          TimerUtil.sleep(wait);
        }
        synchronized (this) {
          if (want) {
            want = false;
            did = true;
            if (threadDump) {
              try {
                PlatformUtil.getInstance().threadDump(true);
              } catch (Exception e) {
              }
            }
            doit();
          }
        }
      } catch (InterruptedException e) {
        // exit thread
      } finally {
        synchronized (LockssTestCase5.this) {
          doLaters.remove(this);
        }
      }
    }

    /** Get a thread dump before triggering the event */
    public void setThreadDump() {
      threadDump = true;
    }

  }
  
  /** Interrupter interrupts a thread in a while */
  public class Interrupter extends DoLater {
  
    private Thread thread;

    Interrupter(long waitMs, Thread thread) {
      super(waitMs);
      setPriority(thread.getPriority() + 1);
      this.thread = thread;
    }

    /** Interrupt the thread */
    protected void doit() {
      log.debug("Interrupting");
      thread.interrupt();
    }

  }

  /**
   * Interrupt current thread in a while
   * @param ms interval to wait before interrupting
   * @return an Interrupter
   */
  public Interrupter interruptMeIn(long ms) {
    Interrupter i = new Interrupter(ms, Thread.currentThread());
    i.start();
    return i;
  }

  /**
   * Interrupt current thread in a while, first printing a thread dump
   * @param ms interval to wait before interrupting
   * @param threadDump true if thread dump wanted
   * @return an Interrupter
   */
  public Interrupter interruptMeIn(long ms, boolean threadDump) {
    Interrupter i = new Interrupter(ms, Thread.currentThread());
    if (threadDump) {
      i.setThreadDump();
    }
    i.start();
    return i;
  }
  
}
