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
import org.apache.commons.io.*;
import org.apache.commons.lang3.*;
import org.apache.commons.compress.archivers.tar.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import org.lockss.log.*;

import java.util.zip.GZIPInputStream;

/**
 * Tar file utilities
 */
public class TarUtil {
  static L4JLogger log = L4JLogger.getLogger();

  /**
   * Expand the tar file to the specified directory.  Does not allow any
   * files to be created outside of specified dir.
   * @param tar tar file
   * @param toDir dir under which to expand tar contents
   * @throws TarException if the tar file is invalid
   * @throws IOException
   */
  public static void untar(File tar, File toDir, boolean isGzipped)
      throws IOException {
    if (!toDir.exists()) {
      toDir.mkdirs();
    }
    if (!toDir.exists()) {
      throw new IOException("Invalid target directory");
    }

    try (InputStream in = new BufferedInputStream(new FileInputStream(tar));
         TarArchiveInputStream tin =
         (isGzipped
          ? new TarArchiveInputStream(new GZIPInputStream(in))
          : new TarArchiveInputStream(new BufferedInputStream(in)))) {
      TarArchiveEntry entry;
      while ((entry = tin.getNextTarEntry()) != null) {
	String relpath = entry.getName();
	if (relpath.startsWith("/")) {
	  throw new IOException("Absolute paths in zip not allowed:" + relpath);
	}
	File file = new File(toDir, relpath);
        if (entry.isDirectory()) {
          if (!file.exists()) {
            file.mkdirs();
          }
        } else {
          File parent = file.getParentFile();
          if (parent != null) {
            if (!parent.exists()) {
              parent.mkdirs();
            }
          }
          try (OutputStream out = new FileOutputStream(file)) {
            long n = IOUtils.copy(tin, out);
            log.trace("Write " + n + " bytes to " + file);
          }
        }
      }
    }
  }
}

