/*

Copyright (c) 2020-2021 Board of Trustees of Leland Stanford Jr. University,
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
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.lockss.log.*;
import org.lockss.util.*;
import org.lockss.util.test.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.*;

public class TestDirArchiver extends LockssTestCase5 {
  static L4JLogger log = L4JLogger.getLogger();

  static List<String> filenames = ListUtil.list("foo",
                                                "bar.baz",
                                                "subdir1/foo",
                                                "subdir1/f1.aaa",
                                                "subdi2/file37");

  static final Path TEST_PREFIX = Paths.get("foo/bar");

  File srcdir;
  File tgtdir;
  File tgtfile;

  @BeforeEach
  public void makeDir() throws IOException {
    srcdir = getTempDir("compressor-src-dir");
    tgtdir = getTempDir("compressor-tgt-dir");

    System.out.println("ddddddddddddd: " + srcdir);
    for (String f : filenames) {
      File file = new File(srcdir, f);
      File parent = file.getParentFile();
      parent.mkdirs();
      FileTestUtil.writeFile(file, StringUtils.repeat(f, 20));
    }
  }

  @Test
  public void testZip() throws IOException {
    tgtfile = getTempFile("foo", ".zip");
    DirArchiver da = DirArchiver.makeZipArchiver()
      .setSourceDir(srcdir)
      .setOutFile(tgtfile);
    da.build();
    ZipUtil.unzip(tgtfile, tgtdir);
    assertTrue(FileUtil.equalTrees(srcdir, tgtdir),
               "Comparing " + srcdir + " with " + tgtdir);
  }

  @Test
  public void testZipWithPrefix() throws IOException {
    try {
      DirArchiver.makeZipArchiver().setPrefix(new File("/foo/bar"));
      fail("Should be illegal to set absolute prefix");
    } catch (IllegalArgumentException e) {}

    tgtfile = getTempFile("foo", ".zip");
    DirArchiver da = DirArchiver.makeZipArchiver()
      .setSourceDir(srcdir)
      .setPrefix(new File(TEST_PREFIX.toString()))
      .setOutFile(tgtfile);
    da.build();
    ZipUtil.unzip(tgtfile, tgtdir);
    File tgtsub = tgtdir.toPath().resolve(TEST_PREFIX).toFile();
    assertTrue(tgtsub.toString().endsWith(TEST_PREFIX.toString()));
    assertTrue(FileUtil.equalTrees(srcdir, tgtsub),
               "Comparing " + srcdir + " with " + tgtsub);
  }

  @Test
  public void testTar() throws IOException {
    tgtfile = getTempFile("foo", ".tgz");
    DirArchiver da = DirArchiver.makeTarArchiver()
      .setSourceDir(srcdir)
      .setOutFile(tgtfile);
    da.build();
    TarUtil.untar(tgtfile, tgtdir, true);
    assertTrue(FileUtil.equalTrees(srcdir, tgtdir),
               "Comparing " + srcdir + " with " + tgtdir);
  }

  @Test
  public void testTarWithPrefix() throws IOException {
    try {
      DirArchiver.makeTarArchiver().setPrefix(new File("/foo/bar"));
      fail("Should be illegal to set absolute prefix");
    } catch (IllegalArgumentException e) {}

    tgtfile = getTempFile("foo", ".tar");
    DirArchiver da = DirArchiver.makeTarArchiver()
      .setSourceDir(srcdir)
      .setPrefix(new File(TEST_PREFIX.toString()))
      .setOutFile(tgtfile);
    da.build();
    TarUtil.untar(tgtfile, tgtdir, true);
    File tgtsub = tgtdir.toPath().resolve(TEST_PREFIX).toFile();
    assertTrue(tgtsub.toString().endsWith(TEST_PREFIX.toString()));
    assertTrue(FileUtil.equalTrees(srcdir, tgtsub),
               "Comparing " + srcdir + " with " + tgtsub);
  }

}


