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

import java.util.*;
import java.io.*;
import java.net.*;
import java.security.*;
import org.lockss.util.*;
import org.lockss.util.lang.EncodingUtil;

/** Utilities for Files involved in the test hierarchy
 */
public class FileTestUtil {
  /** Create and return the name of a temp file that will be deleted
   * when jvm terminated
   */
  public static File tempFile(String prefix)
      throws IOException {
    return tempFile(prefix, null, null);
  }

  public static File tempFile(String prefix, File dir)
      throws IOException {
    return tempFile(prefix, null, dir);
  }

  public static File tempFile(String prefix, String suffix)
      throws IOException {
    return tempFile(prefix, suffix, null);
  }

  public static File tempFile(String prefix, String suffix, File dir)
      throws IOException {
    File f = File.createTempFile(prefix, suffix, dir);
    if (!LockssTestCase5.isKeepTempFiles()) {
      f.deleteOnExit();
    }
    return f;
  }

  /** Write a temp file containing string and return its name */
  public static File writeTempFile(String prefix, String contents)
      throws IOException {
    return writeTempFile(prefix, null, contents);
  }

  public static File writeTempFile(String prefix, String suffix, String contents)
      throws IOException {
    File file = tempFile(prefix, suffix, null);
    writeFile(file, contents);
    return file;
  }

  public static void writeFile(File file, String contents) throws IOException {
    Writer wrtr = new OutputStreamWriter(new FileOutputStream(file),
					 EncodingUtil.DEFAULT_ENCODING);
    wrtr.write(contents);
    wrtr.close();
  }

  /** Store the string in a temp file and return a file: url for it */
  public static String urlOfString(String s) throws IOException {
    File file = FileTestUtil.writeTempFile("test", s);
    return file.toURI().toURL().toString();
  }

  /** Store the string in a temp file and return a file: url for it */
  public static String urlOfString(String s, String suffix) throws IOException {
    File file = FileTestUtil.writeTempFile("test", suffix, s);
    return file.toURI().toURL().toString();
  }

  /** Return the (absolute) url for the (possibly relative) file name */
  public static String urlOfFile(String s) throws IOException {
    File file = new File(s);
    return file.toURI().toURL().toString();
  }

  /**
   * Generate and return a list of all the files under this directory or file
   * @param file directory or file which to enumerate
   * @return all the files under file, or file if it isn't a directory
   */
  public static List<File> enumerateFiles(File file) {
    if (file == null) {
      throw new IllegalArgumentException("Null file specified");
    }
    if (file.isDirectory()) {
      List<File> list = new ArrayList<File>();
      File[] files = file.listFiles();
      for (int ix=0; ix< files.length; ix++) {
	list.addAll(enumerateFiles(files[ix]));
      }
      return list;
    }
    return ListUtil.list(file);
  }

  /**
   * Return the path of src relative to root
   */
  public static String getPathUnderRoot(File src, File root) {
    if (src == null || root == null) {
      throw new IllegalArgumentException("Null file specified");
    }
    String srcString = src.getPath();
    String rootString = root.getPath();
    if (srcString.startsWith(rootString)) {
      return srcString.substring(rootString.length());
    }
    return null;
  }
}
