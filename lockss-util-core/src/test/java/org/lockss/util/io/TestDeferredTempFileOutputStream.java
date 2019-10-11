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

// Portions of this code are:
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author <a href="mailto:martinc@apache.org">Martin Cooper</a>
 */

package org.lockss.util.io;

import java.io.*;
import org.junit.jupiter.api.*;
import org.lockss.util.test.*;
import org.lockss.util.test.matcher.*;
import org.apache.commons.io.output.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;

public class TestDeferredTempFileOutputStream extends LockssTestCase5 {

  /**
   * The test data as a string (which is the simplest form).
   */
  private String testString = "0123456789";

  /**
   * The test data as a byte array, derived from the string.
   */
  private byte[] testBytes = testString.getBytes();

  /**
   * Tests the case where the amount of data falls below the threshold, and
   * is therefore confined to memory.
   */
  @Test
  public void testBelowThreshold() throws IOException {
    DeferredTempFileOutputStream dfos =
      new MyDeferredTempFileOutputStream(testBytes.length + 42);
    dfos.write(testBytes, 0, testBytes.length);
    dfos.close();
    assertTrue(dfos.isInMemory());
    assertArrayEquals(testBytes, dfos.getData());
    verifyResultStream(dfos.getInputStream());
    verifyResultStream(dfos.getDeleteOnCloseInputStream());
    // ensure this is harmless
    dfos.deleteTempFile();
  }

  /**
   * Tests the case where the amount of data is exactly the same as the
   * threshold. The behavior should be the same as that for the amount of
   * data being below (i.e. not exceeding) the threshold.
   */
  @Test
  public void testAtThreshold() throws IOException {
    DeferredTempFileOutputStream dfos =
      new MyDeferredTempFileOutputStream(testBytes.length);
    dfos.write(testBytes, 0, testBytes.length);
    dfos.close();
    assertTrue(dfos.isInMemory());
    assertArrayEquals(testBytes, dfos.getData());
    verifyResultStream(dfos.getInputStream());
    verifyResultStream(dfos.getDeleteOnCloseInputStream());
  }

  /**
   * Tests the case where the amount of data exceeds the threshold, and is
   * therefore written to disk. The actual data written to disk is verified,
   * as is the file itself.
   */
  @Test
  public void testAboveThreshold() throws IOException {
    DeferredTempFileOutputStream dfos =
      new MyDeferredTempFileOutputStream(testBytes.length - 5);
    dfos.write(testBytes, 0, testBytes.length);
    dfos.close();
    File testFile = dfos.getFile();
    assertFalse(dfos.isInMemory());
    assertNull(dfos.getData());
    verifyResultFile(testFile);
    assertThat(testFile.getName(),
	       FindPattern.findPattern("deferred-temp-file"));
    verifyResultStream(dfos.getInputStream());
    assertTrue(testFile.exists());
    verifyResultStream(dfos.getDeleteOnCloseInputStream());
  }

  @Test
  public void testAboveThresholdNamed() throws IOException {
    DeferredTempFileOutputStream dfos =
      new MyDeferredTempFileOutputStream(testBytes.length - 5, "betelgeuse");
    dfos.write(testBytes, 0, testBytes.length);
    dfos.close();
    File testFile = dfos.getFile();
    assertFalse(dfos.isInMemory());
    assertNull(dfos.getData());
    verifyResultFile(testFile);
    assertThat(testFile.getName(),
	       FindPattern.findPattern("betelgeuse"));
    assertTrue(testFile.exists());
    dfos.deleteTempFile();
    assertFalse(testFile.exists());
  }

  @Test
  public void testDeleteTempFileNotClosed() throws IOException {
    DeferredTempFileOutputStream dfos =
      new MyDeferredTempFileOutputStream(testBytes.length - 5);
    dfos.write(testBytes, 0, testBytes.length);
    assertFalse(dfos.isInMemory());
    File testFile = dfos.getFile();
    assertTrue(testFile.exists());
    dfos.deleteTempFile();
    assertFalse(testFile.exists());
  }

  /**
   * Tests the case where there are multiple writes beyond the threshold, to
   * ensure that the <code>thresholdReached()</code> method is only called
   * once, as the threshold is crossed for the first time.
   */
  @Test
  public void testThresholdReached() throws IOException {
    DeferredTempFileOutputStream dfos =
      new MyDeferredTempFileOutputStream(testBytes.length / 2);
    int chunkSize = testBytes.length / 3;

    dfos.write(testBytes, 0, chunkSize);
    dfos.write(testBytes, chunkSize, chunkSize);
    dfos.write(testBytes, chunkSize * 2,
	       testBytes.length - chunkSize * 2);
    dfos.close();
    assertFalse(dfos.isInMemory());
    assertNull(dfos.getData());

    File testFile = dfos.getFile();
    verifyResultFile(testFile);

    verifyResultStream(dfos.getInputStream());
    assertTrue(testFile.exists());
    verifyResultStream(dfos.getDeleteOnCloseInputStream());
    assertFalse(testFile.exists());
  }

  /**
   * Verifies that the specified file contains the same data as the original
   * test data.
   *
   * @param testFile The file containing the test output.
   */
  private void verifyResultFile(File testFile) throws IOException {
    assertTrue(testFile.exists());
    FileInputStream fis = new FileInputStream(testFile);
    verifyResultStream(fis);
  }

  private void verifyResultStream(InputStream is) throws IOException {

    assertTrue(is.available() == testBytes.length);

    byte[] resultBytes = new byte[testBytes.length];
    assertTrue(is.read(resultBytes) == testBytes.length);

    assertArrayEquals(testBytes, resultBytes);
    assertTrue(is.read(resultBytes) == -1);

    try {
      is.close();
    } catch (IOException e) {
      // Ignore an exception on close
    }
  }

  static class MyDeferredTempFileOutputStream
    extends DeferredTempFileOutputStream {
    public MyDeferredTempFileOutputStream(int threshold) {
      super(threshold);
    }
    public MyDeferredTempFileOutputStream(int threshold, String name) {
      super(threshold, name);
    }
    protected File createTempFile(String name) throws IOException {
      File file = super.createTempFile(name);
      file.deleteOnExit();
      return file;
    }
  }
}
