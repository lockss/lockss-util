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

package org.lockss.util.io;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.apache.commons.collections4.*;
import org.apache.commons.io.IOUtils;
import org.lockss.util.ListUtil;
import org.lockss.util.lang.EncodingUtil;
import org.lockss.util.os.PlatformUtil;
import org.lockss.util.test.*;
import org.slf4j.*;

/**
 * test class for org.lockss.util.TestFileTestUtil
 */

public class TestFileUtil extends LockssTestCase5 {

  private static final Logger log = LoggerFactory.getLogger(TestFileUtil.class);

  String tempDirPath;

  @BeforeEach
  public void setUp() throws Exception {
    tempDirPath = getTempDir().getAbsolutePath() + File.separator;
  }

  @Test
  public void testSysDepPath() {
    String testStr = "test/var\\foo";
    String expectedStr = "test"+File.separator+"var"+File.separator+"foo";
    assertEquals(expectedStr, FileUtil.sysDepPath(testStr));
  }

  @Test
  public void testSysIndepPath() {
    String testStr = "test/var\\foo";
    String expectedStr = "test/var/foo";
    assertEquals(expectedStr, FileUtil.sysIndepPath(testStr));
  }

  boolean isLegal(String x) {
    return FileUtil.isLegalPath(x);
  }

  @Test
  public void testIsLegalPath() {
    assertTrue(isLegal("."));
    assertTrue(isLegal("/"));
    assertTrue(isLegal("/."));
    assertTrue(isLegal("./"));
    assertTrue(isLegal("//"));

    assertFalse(isLegal(".."));
    assertFalse(isLegal("../"));
    assertFalse(isLegal("..//"));
    assertFalse(isLegal("/.."));
    assertFalse(isLegal("//.."));
    assertFalse(isLegal("./.."));
    assertFalse(isLegal("./../"));
    assertFalse(isLegal("/./../"));
    assertFalse(isLegal("/./././.."));
    assertTrue(isLegal("/./././x/.."));

    assertTrue(isLegal("/var"));
    assertTrue(isLegal("/var/"));
    assertTrue(isLegal("/var/foo"));
    assertTrue(isLegal("/var/../foo"));
    assertTrue(isLegal("/var/.."));
    assertTrue(isLegal("/var/../foo/.."));

    assertTrue(isLegal("var/./foo"));
    assertTrue(isLegal("var/."));

    assertFalse(isLegal("/var/../.."));
    assertFalse(isLegal("/var/../../foo"));
    assertFalse(isLegal("/var/.././.."));
    assertFalse(isLegal("/var/.././..///"));

    assertFalse(isLegal("var/../.."));
    assertFalse(isLegal("var/../../foo"));
    assertFalse(isLegal("var/.././.."));
    assertFalse(isLegal("var/.././..///"));
  }

  @Test
  public void testFileContentIsIdentical() throws Exception {
    File file1 = createFile(tempDirPath + "file1", "content 1");
    File file2 = createFile(tempDirPath + "file2", "content 2");
    File file3 = createFile(tempDirPath + "file3", "content 1");
    // shorter length
    File file4 = createFile(tempDirPath + "file4", "con 4");

    assertFalse(FileUtil.isContentEqual(file1, null));
    assertFalse(FileUtil.isContentEqual(null, file1));
    assertFalse(FileUtil.isContentEqual(null, null));
    assertFalse(FileUtil.isContentEqual(file1, file2));
    assertFalse(FileUtil.isContentEqual(file1, file4));

    assertTrue(FileUtil.isContentEqual(file1, file1));
    assertTrue(FileUtil.isContentEqual(file1, file3));
  }

  File createFile(String name, String content) throws Exception {
    File file = new File(name);
    FileOutputStream fos = new FileOutputStream(file);
    InputStream sis = IOUtils.toInputStream(content, EncodingUtil.DEFAULT_ENCODING);
    IOUtils.copy(sis, fos);
    sis.close();
    fos.close();
    return file;
  }

  @Test
  public void testIsTemporaryResourceException() throws IOException {
    String EMFILE = "foo.bar (Too many open files)";
    assertTrue(FileUtil.isTemporaryResourceException(new FileNotFoundException(EMFILE)));
    assertFalse(FileUtil.isTemporaryResourceException(new FileNotFoundException("No such file or directory")));
    assertFalse(FileUtil.isTemporaryResourceException(new IOException(("No such file or directory"))));
  }

  @Test
  public void testTempDir() throws IOException {
    try {
      File dir = FileUtil.createTempDir("pre", "suff", new File("/nosuchdir"));
      fail("Shouldn't be able to create temp dir in /nosuchdir");
    } catch (IOException e) {
    }
    File dir = FileUtil.createTempDir("pre", "suff");
    assertTrue(dir.exists());
    assertTrue(dir.isDirectory());
    assertEquals(0, dir.listFiles().length);
    File f = new File(dir, "foo");
    assertFalse(f.exists());
    assertTrue(f.createNewFile());
    assertTrue(f.exists());
    assertEquals(1, dir.listFiles().length);
    assertEquals("foo", dir.listFiles()[0].getName());
    assertTrue(f.delete());
    assertEquals(0, dir.listFiles().length);
    assertTrue(dir.delete());
    assertFalse(dir.exists());
    File parentDir = FileUtil.createTempDir("testTempDir", ".foo");
    assertTrue(parentDir.exists());
    assertTrue(parentDir.isDirectory());
    // Test creating under another directory
    File subDir = FileUtil.createTempDir("subTempDir", ".bar", parentDir);
    assertTrue(subDir.exists());
    assertTrue(subDir.isDirectory());
    FileUtil.delTree(parentDir);

  }

  void writeFile(File dir, String relPath) throws IOException {
    File file = new File(dir, relPath);
    File parent = new File(file.getParent());
    parent.mkdirs();
    FileTestUtil.writeFile(file, relPath);
  }

  String apath = new File("d1/d2/1").toString();

  String[] relPaths = {
    "one",
    "two",
    new File("d1/1").toString(),
    new File("d1/2").toString(),
    apath,
    new File("d1/d2/2").toString(),
    new File("d1/d2/d3/d4/1").toString(),
  };

  String[] dirNames = {
    "d1",
    new File("d1/d2").toString(),
    new File("d1/d2/d3").toString(),
    new File("d1/d2/d3/d4").toString(),
  };

  public void buildTree(File dir) throws IOException {
    for (String rel : relPaths) {
      writeFile(dir, rel);
    }
  }

  List<File> listOfFiles(List<String> lst) {
    return (List<File>)CollectionUtils.collect(lst,
                                               new Transformer<String, File>() {
                                                 public File transform(String str) {
                                                   return new File(str);
                                                 }
                                               });
  }

  List<String> prepend(final String prefix, List<String> lst) {
    return (List<String>)CollectionUtils.collect(lst,
			                         new Transformer<String, String>() {
				                   public String transform(String str) {
				                     return prefix + str;
				                   }
				                 });
  }

  @Test
  public void testListTree() throws IOException {
    File dir = getTempDir();
    buildTree(dir);
    List exp = ListUtil.fromArray(relPaths);
    Collections.sort(exp);
    assertEquals(exp, FileUtil.listTree(dir, dir.getPath(), false));
    assertEquals(prepend(dir.getPath() + File.separator, exp),
		 FileUtil.listTree(dir, false));
    exp.addAll(ListUtil.fromArray(dirNames));
    Collections.sort(exp);
    assertEquals(exp, FileUtil.listTree(dir, dir.getPath(), true));
    assertEquals(prepend(dir.getPath() + File.separator, exp),
		 FileUtil.listTree(dir, true));
  }

  @Test
  public void testEqualTrees() throws IOException {
    File dir1 = getTempDir();
    File dir2 = getTempDir();
    buildTree(dir1);
    buildTree(dir2);
    assertTrue(FileUtil.equalTrees(dir1, dir2));
    writeFile(dir1, "afile");
    File f1 = new File(dir1, "afile");
    assertTrue(f1.exists());
    assertFalse(FileUtil.equalTrees(dir1, dir2));
    f1.delete();
    assertFalse(f1.exists());
    assertTrue(FileUtil.equalTrees(dir1, dir2));
    File f2 = new File(dir1, apath);
    assertTrue(f2.exists());
    FileTestUtil.writeFile(f2, "foobar");
    assertEquals(FileUtil.listTree(dir1, dir1.getPath(), true),
		 FileUtil.listTree(dir2, dir2.getPath(), true));
    assertFalse(FileUtil.equalTrees(dir1, dir2));
  }

  @Test
  public void testDelTree() throws IOException {
    File dir = getTempDir("deltree");
    File d1 = new File(dir, "foo");
    assertTrue(d1.mkdir());
    File d2 = new File(d1, "bar");
    assertTrue(d2.mkdir());
    assertTrue(new File(dir, "f1").createNewFile());
    assertTrue(new File(d1, "d1f1").createNewFile());
    assertTrue(new File(d2, "d2f1").createNewFile());
    assertFalse(dir.delete());
    assertTrue(FileUtil.delTree(dir));
    assertFalse(dir.exists());
  }

  @Test
  public void testDelTreeNoDir() throws IOException {
    File dir = getTempDir("deltree");
    File d1 = new File(dir, "foo");
    assertFalse(d1.exists());
    assertTrue(FileUtil.delTree(d1));
  }

  @Test
  public void testEmptyDir() throws IOException {
    File dir = getTempDir("deltree");
    File d1 = new File(dir, "foo");
    assertTrue(d1.mkdir());
    File d2 = new File(d1, "bar");
    assertTrue(d2.mkdir());
    assertTrue(new File(dir, "f1").createNewFile());
    assertTrue(new File(d1, "d1f1").createNewFile());
    assertTrue(new File(d2, "d2f1").createNewFile());
    assertFalse(dir.delete());
    assertTrue(FileUtil.emptyDir(dir));
    String files[] = dir.list();
    assertEquals(0, files.length);
  }

  @Test
  public void testEmptyDirNoDir() throws IOException {
    File dir = getTempDir("deltree");
    File d1 = new File(dir, "foo");
    assertFalse(d1.exists());
    assertFalse(FileUtil.emptyDir(d1));
  }

  @Test
  public void testSafeDeleteFile() throws IOException {
    assertFalse(FileUtil.safeDeleteFile(null));
    File dir = getTempDir("safeDelete");
    writeFile(dir, "existingFile");
    File f1 = new File(dir, "existingFile");
    assertTrue(f1.exists());
    assertTrue(FileUtil.safeDeleteFile(f1));
    File f2 = new File(dir, "missingFile");
    assertFalse(f2.exists());
    assertFalse(FileUtil.safeDeleteFile(f2));
  }

  @Test
  public void testNewFileOutputStream() throws IOException {
    File dir = getTempDir("longtest");
    File shortName = new File(dir, "shortpath");
    IOUtils.write("a content", FileUtil.newFileOutputStream(shortName), EncodingUtil.DEFAULT_ENCODING);
    assertInputStreamMatchesString("a content",
				   FileUtil.newFileInputStream(shortName));
    // Ensure can overwrite existing file
    IOUtils.write("bee content season", FileUtil.newFileOutputStream(shortName), EncodingUtil.DEFAULT_ENCODING);
    assertInputStreamMatchesString("bee content season",
				   FileUtil.newFileInputStream(shortName));

    File noFile = new File(dir, "nosuchfile");
    try {
      FileUtil.newFileInputStream(noFile);
      fail("FileUtil.newFileInputStream() non-existent file should throw");
    } catch (FileNotFoundException e) {
      assertEquals(noFile.getPath(), e.getMessage());
    }
  }

  // These tests ensure correct behavior of long file and path names.  Skip
  // them on less capable filesystems.
  @Test
  public void testNewFileOutputStreamLongPath() throws IOException {
    File dir = getTempDir("longtest");
    int pad = dir.getPath().length();
    PlatformUtil pi = PlatformUtil.getInstance();
    if (pi.maxFilename() < 251 ||
	pi.maxPathname() < (2510 + pad)) {
      log.debug("Skipping long path tests");
      return;
    }

    String s250 = StringUtils.repeat("1234567890", 25);
    assertEquals(250, s250.length());
    String longStr = StringUtils.repeat(s250 + "/", 10);
    assertEquals(2510, longStr.length());
    File longDir = new File(dir, longStr);
    assertTrue(longDir.mkdirs());
    File longName = new File(longDir, "longpath");
    IOUtils.write("b content", FileUtil.newFileOutputStream(longName), EncodingUtil.DEFAULT_ENCODING);
    assertInputStreamMatchesString("b content",
				   FileUtil.newFileInputStream(longName));
  }

  @Test
  public void testSetOwnerRWX() throws IOException {
    File dir = getTempDir("setperm");
    FileUtil.setOwnerRWX(dir);
    assertEquals(EnumSet.of(PosixFilePermission.OWNER_READ,
			    PosixFilePermission.OWNER_WRITE,
			    PosixFilePermission.OWNER_EXECUTE),
		 Files.getPosixFilePermissions(dir.toPath()),
		 "Dir: " + dir);
  }

  @Test
  public void testListDirFilesWithExtension() throws IOException {
    try {
      FileUtil.listDirFilesWithExtension(null, "123");
      fail("FileUtil.listDirFilesWithExtension(null, ext) should throw");
    } catch (IOException e) {
      assertEquals("Invalid directory 'null'", e.getMessage());
    }

    File dir = getTempDir();

    try {
      FileUtil.listDirFilesWithExtension(new File(dir, "ne"), "123");
      fail("FileUtil.listDirFilesWithExtension(nonexistentdir, ext) should "
	  + "throw");
    } catch (IOException e) {
      assertTrue(e.getMessage().startsWith("Invalid directory '"));
      assertTrue(e.getMessage().endsWith("/ne'"));
    }

    try {
      FileUtil.listDirFilesWithExtension(dir, null);
      fail("FileUtil.listDirFilesWithExtension(dir, null) should throw");
    } catch (IOException e) {
      assertEquals("Invalid required extension 'null'", e.getMessage());
    }

    try {
      FileUtil.listDirFilesWithExtension(dir, "");
      fail("FileUtil.listDirFilesWithExtension(dir, \"\") should throw");
    } catch (IOException e) {
      assertEquals("Invalid required extension ''", e.getMessage());
    }

    assertEquals(0, FileUtil.listDirFilesWithExtension(dir, "123").size());

    writeFile(dir, "123");

    try {
      FileUtil.listDirFilesWithExtension(new File(dir, "123"), "123");
      fail("FileUtil.listDirFilesWithExtension(nondir, ext) should throw");
    } catch (IOException e) {
      assertTrue(e.getMessage().startsWith("Invalid directory '"));
      assertTrue(e.getMessage().endsWith("/123'"));
    }

    writeFile(dir, "abc.xyz");
    writeFile(dir, "xyz.abc");
    writeFile(dir, new File("xyz2.abc/abc.xyz").toString());
    writeFile(dir, new File("abc2.xyz/xyz.abc").toString());

    assertEquals(1, FileUtil.listDirFilesWithExtension(dir, "abc").size());
    assertEquals("xyz.abc",
	FileUtil.listDirFilesWithExtension(dir, "abc").get(0));

    assertEquals(1, FileUtil.listDirFilesWithExtension(
	new File(dir, "xyz2.abc"), "xyz").size());
    assertEquals("abc.xyz", FileUtil.listDirFilesWithExtension(
	new File(dir, "xyz2.abc"), "xyz").get(0));

    assertEquals(1, FileUtil.listDirFilesWithExtension(
	new File(dir, "abc2.xyz"), "abc").size());
    assertEquals("xyz.abc", FileUtil.listDirFilesWithExtension(
	new File(dir, "abc2.xyz"), "abc").get(0));
  }

  /**
   * Tests for readPasswdFile().
   *
   * @throws IOException if there are problems running the tests.
   */
  @Test
  public void testReadPasswdFile() throws Exception {
    try {
      FileUtil.readPasswdFile(null);
      fail("FileUtil.testReadPasswdFile(null) should throw");
    } catch (IOException e) {
      assertEquals("Null password file", e.getMessage());
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      sb.append("13 characters");
    }

    try {
      FileUtil.readPasswdFile(createFile(tempDirPath + "tooLong", sb.toString())
	  .getAbsolutePath());
      fail("FileUtil.testReadPasswdFile(null) should throw");
    } catch (IOException e) {
      assertEquals("Unreasonably large password file: "
	  + sb.toString().length(), e.getMessage());
    }

    String password = "supersecret";

    assertEquals(password, FileUtil.readPasswdFile(
	createFile(tempDirPath + "supsec", password).getAbsolutePath()));
  }
}
