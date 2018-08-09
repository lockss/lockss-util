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

package org.lockss.util.test;

import java.io.*;

import org.junit.jupiter.api.Test;
import org.lockss.util.*;
import org.lockss.util.io.*;

/**
 * test class for org.lockss.util.TestFileTestUtil
 */
public class TestFileTestUtil extends LockssTestCase5 {

  @Test
  public void testTempFile() throws IOException {
    String testStr = "test string";
    File file = FileTestUtil.tempFile("prefix");
    FileWriter fw = new FileWriter(file);
    fw.write(testStr);
    fw.close();
    assertTrue(file.exists());
    FileReader fr = new FileReader(file);
    char in[] = new char[40];
    int len = fr.read(in);
    assertEquals(len, testStr.length());
    String res = new String(in, 0, len);
    assertEquals(testStr, res);
  }

  @Test
  public void testWriteTempFile() throws IOException {
    String testStr = "multi-line\ntest string\n";
    File file = FileTestUtil.writeTempFile("prefix", testStr);
    assertTrue(file.exists());
    FileReader fr = new FileReader(file);
    char in[] = new char[80];
    int len = fr.read(in);
    assertEquals(len, testStr.length());
    String res = new String(in, 0, len);
    assertEquals(testStr, res);
  }

  @Test
  public void testEnumerateFilesNullFile() {
    try {
      FileTestUtil.enumerateFiles(null);
      fail("FileTestUtil.enumerateFiles() should have thrown when a null file "
	   +"was specified");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testEnumerateFilesOneFile() {
    File file = new MockFile("blah");
    assertEquals(ListUtil.list(file), FileTestUtil.enumerateFiles(file));
  }

  @Test
  public void testEnumerateFilesDirectory() {
    MockFile file = new MockFile("Path_to_directory");
    MockFile child1 = new MockFile("child1");
    MockFile child2 = new MockFile("child2");

    file.setIsDirectory(true);
    file.setChild(child1);
    file.setChild(child2);

    assertEquals(ListUtil.list(child1, child2), FileTestUtil.enumerateFiles(file));
  }

  @Test
  public void testEnumerateFilesMultiLevelDirectory() {
    MockFile file = new MockFile("Path_to_directory");
    MockFile child1 = new MockFile("child1");
    MockFile child2 = new MockFile("child2");
    MockFile childDir1 = new MockFile("childDir1");
    MockFile child11 = new MockFile("child11");


    file.setIsDirectory(true);
    file.setChild(child1);
    file.setChild(child2);
    file.setChild(childDir1);

    childDir1.setIsDirectory(true);
    childDir1.setChild(child11);

    assertEquals(ListUtil.list(child1, child2, child11),
		 FileTestUtil.enumerateFiles(file));
  }

  @Test
  public void testGetPathUnderRootNullParams() {
    try {
      FileTestUtil.getPathUnderRoot(null, null);
      fail("FileTestUtil.getPathUnderRoot() should have thrown when a null file "
	   +"was specified");
    } catch (IllegalArgumentException e) {
    }
    try {
      FileTestUtil.getPathUnderRoot(new File("blah"), null);
      fail("FileTestUtil.getPathUnderRoot() should have thrown when a null file "
	   +"was specified");
    } catch (IllegalArgumentException e) {
    }
    try {
      FileTestUtil.getPathUnderRoot(null, new File("blah"));
      fail("FileTestUtil.getPathUnderRoot() should have thrown when a null file "
	   +"was specified");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testGetPathUnderRootTrailingSlash() {
    File src = new MockFile("/tmp/dir/test/file/path");
    File root = new MockFile("/tmp/dir/");
    assertEquals("test/file/path", FileTestUtil.getPathUnderRoot(src, root));
  }

  @Test
  public void testGetPathUnderRootNoCommonPath() {
    File src = new File("/tmp/dir/test/file/path");
    File root = new File("/other/directory");
    assertNull(FileTestUtil.getPathUnderRoot(src, root));
  }

  @Test
  public void testUrlOfFile() throws IOException {
    String s = "foo/bar.txt";
    StringBuffer buffer = new StringBuffer("file:");
    String path = FileUtil.sysIndepPath(new File(s).getAbsolutePath());
    if (!path.startsWith("/")) {
      buffer.append("/");
    }
    buffer.append(path);
    assertEquals(buffer.toString(), FileTestUtil.urlOfFile(s));
  }
}
