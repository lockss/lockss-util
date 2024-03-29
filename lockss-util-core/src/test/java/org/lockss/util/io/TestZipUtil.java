/*

Copyright (c) 2000-2021 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.util.io;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.apache.commons.io.*;
import org.junit.jupiter.api.*;
import org.lockss.util.*;
import org.lockss.util.io.FileUtil;
import org.lockss.util.test.*;
import org.lockss.test.*;

/**
 * test class for org.lockss.util.ZipUtil
 */
public class TestZipUtil extends LockssTestCase5 {

  File makeZip(Map map) throws IOException {
    File file = FileTestUtil.tempFile("ziptest", ".zip", null);
    OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
    ZipOutputStream z = new ZipOutputStream(out);
    for (Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
      String entname = (String)iter.next();
      z.putNextEntry(new ZipEntry(entname));
      if (!entname.endsWith("/")) {
	String cont = (String)map.get(entname);
        try (InputStream in = IOUtils.toInputStream(cont, "UTF-8")) {
          IOUtils.copy(in, z);
        }
        z.closeEntry();
      }
    }
    z.close();
    return file;
  }

  @Test
  public void testIsZipFile() throws IOException {
    Map map = new HashMap();
    map.put("foo", "bar");
    File zip = makeZip(map);
    assertTrue(ZipUtil.isZipFile(zip));
    try (MyBufferedInputStream in =
         new MyBufferedInputStream(new FileInputStream(zip))) {
      assertEquals(0, in.getPos());
      assertTrue(ZipUtil.isZipFile(in));
      assertEquals(0, in.getPos());
    }
  }

  @Test
  public void testIsZipFileNot() throws IOException {
    File notzip = FileTestUtil.writeTempFile("foo", ".bar", "now is the time");
    assertFalse(ZipUtil.isZipFile(notzip));
    try (MyBufferedInputStream in =
         new MyBufferedInputStream(new FileInputStream(notzip))) {
      assertEquals(0, in.getPos());
      assertFalse(ZipUtil.isZipFile(in));
      assertEquals(0, in.getPos());
    }
  }

  @Test
  public void testIsZipFileShort() throws IOException {
    File zip = FileTestUtil.writeTempFile("foo", ".bar", "PK\003\004");
    assertTrue(ZipUtil.isZipFile(zip));
    File notzip = FileTestUtil.writeTempFile("foo", ".bar", "PK");
    assertFalse(ZipUtil.isZipFile(notzip));
  }

  @Test
  public void testIsZipFileIll() throws IOException {
    File notzip = FileTestUtil.writeTempFile("foo", ".bar", "now is the time");
    try {
      ZipUtil.isZipFile((File)null);
      fail("isZipFile(null) should throw");
    } catch (NullPointerException e) {
    }
    try {
      ZipUtil.isZipFile((BufferedInputStream)null);
      fail("isZipFile(null) should throw");
    } catch (NullPointerException e) {
    }
  }

  public void assertFileMatchesString(String expected, File file) {
    try {
      assertEquals(expected, FileUtils.readFileToString(file));
    } catch (IOException e) {
      fail("Couldn't read file: " + file);
    }
  }

  @Test
  public void testExtract() throws IOException {
    Map map = new HashMap();
    map.put("foo", "bar");
    map.put("a/b/foo", "xxx");
    map.put("a/b/bar", "yyy");
    File zip = makeZip(map);
    File dir = getTempDir();
    ZipUtil.unzip(zip, dir);
    assertEquals(SetUtil.set("foo", "a"),
                 SetUtil.theSet(ListUtil.fromArray(dir.list())));
    assertFileMatchesString("bar", new File(dir, "foo"));
    assertTrue(new File(dir, "a").isDirectory());
    assertTrue(new File(dir, "a/b").isDirectory());
    assertFalse(new File(dir, "a/b/foo").isDirectory());
    assertEquals(SetUtil.set("foo", "bar"),
                 SetUtil.theSet(ListUtil.fromArray(new File(dir, "a/b").list())));
    assertFileMatchesString("xxx", new File(dir, "a/b/foo"));
    assertFileMatchesString("yyy", new File(dir, "a/b/bar"));
  }

  @Test
  public void testExtractIllPath1() throws IOException {
    Map map = new HashMap();
    map.put("/foo", "bar");
    File zip = makeZip(map);
    File dir = getTempDir();
    try {
      ZipUtil.unzip(zip, dir);
      fail("unzip() with absolute path should throw");
    } catch (IOException e) {
      assertMatchesRE("Absolute path.*not allowed", e.getMessage());
    }
  }

  @Test
  public void testExtractIllPath2() throws IOException {
    Map map = new HashMap();
    map.put("foo/../../x", "bar");
    File zip = makeZip(map);
    File dir = getTempDir();
    try {
      ZipUtil.unzip(zip, dir);
      fail("unzip() with dir traversal attack should throw");
    } catch (IOException e) {
      assertMatchesRE("path traversal", e.getMessage());
    }
  }

  void writeFile(File dir, String relPath, String content) throws IOException {
    File file = new File(dir, relPath);
    File parent = new File(file.getParent());
    parent.mkdirs();
    FileTestUtil.writeFile(file, content);
  }

  @Test
  public void testAddDirToZip() throws IOException {
    File zipFile = new File(getTempDir(), "testzip.zip");
    File dir = getTempDir();
    writeFile(dir, "one", "one");
    writeFile(dir, "two", "aaaaaaaaaaaaa");
    writeFile(dir, "d1/1", "d1.1");
    writeFile(dir, "d1/2", "d1.2");
    writeFile(dir, "d1/d2/1", "d1.d2.1");
    writeFile(dir, "d1/d2/2", "d1.d2.2");
    writeFile(dir, "d1/d2/2", "d1.d2.2");
    writeFile(dir, "d1/d2/d3/d4/1", "d1.d2.d3.d4.1");

    OutputStream out = new BufferedOutputStream(new FileOutputStream(zipFile));
    ZipOutputStream z = new ZipOutputStream(out);
    ZipUtil.addDirToZip(z, dir, "");
    z.close();

    File todir = getTempDir();
    ZipUtil.unzip(zipFile, todir);

    assertTrue(FileUtil.equalTrees(dir, todir));
  }

  public void testAddStringToZip() throws IOException {
    File zipFile = new File(getTempDir(), "testzip.zip");
    OutputStream out = new BufferedOutputStream(new FileOutputStream(zipFile));
    ZipOutputStream z = new ZipOutputStream(out);
    String s1 = "ascii string xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    ZipUtil.addStringToZip(z, s1, "ascii");
    String s2 = "ce n'est pas une chaîne";
    ZipUtil.addStringToZip(z, s2, "accented");
    z.close();
    File todir = getTempDir();
    ZipUtil.unzip(zipFile, todir);
    assertFileMatchesString(s1, new File(todir, s1));
    assertFileMatchesString(s2, new File(todir, s2));
  }

  class MyBufferedInputStream extends BufferedInputStream {
    MyBufferedInputStream(InputStream in) {
      super(in);
    }
    long getPos() {
      return pos;
    }
  }

}
