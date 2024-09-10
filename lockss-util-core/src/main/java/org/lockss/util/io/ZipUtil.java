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
import java.util.*;
import java.util.zip.*;
import java.io.*;
import org.lockss.util.*;
import org.lockss.log.*;

/**
 * Zip file utilities
 */
public class ZipUtil {
  static L4JLogger log = L4JLogger.getLogger();

  /** Magic number of ZIP file.  (Why isn't this public in ZipConstants?) */
  public static final byte ZIP_MAGIC[] =
    new byte[] {(byte)0x50, (byte)0x4b, (byte)0x03, (byte)0x04};

  /**
   * Return true if the file looks like a zip file.  Checks only the magic
   * number, not the whole file for validity.
   * @param file a possible zip file
   * @throws IOException
   */
  public static boolean isZipFile(File file) throws IOException {
    try (BufferedInputStream in =
         new BufferedInputStream(new FileInputStream(file))) {
      return isZipFile(in);
    }
  }

  /**
   * Return true if the stream looks like the contents of a zip file.
   * Checks only the magic number, not the whole file for validity.  The
   * stream will be marked and reset to its current position.
   * @param in a stream open on possible zip file content
   * @throws IOException
   */
  public static boolean isZipFile(BufferedInputStream in) throws IOException {
    byte buf[] = new byte[4];
    in.mark(4);
    IOUtils.read(in, buf, 0, buf.length);
    in.reset();
    return Arrays.equals(ZIP_MAGIC, buf);
  }

  /**
   * Expand the zip file to the specified directory.  Does not allow any
   * files to be created outside of specified dir.
   * @param zip zip file
   * @param toDir dir under which to expand zip contents
   * @throws ZipException if the zip file is invalid
   * @throws IOException
   */
  public static void unzip(File zip, File toDir)
      throws ZipException, IOException {
    try (InputStream in =
         new BufferedInputStream(new FileInputStream(zip))) {
      unzip(in, toDir);
    }
  }

  /**
   * Interpret the stream as the contents of a zip file and Expand it to
   * the specified directory.  Does not allow any files to be created
   * outside of specified dir.
   * @param in InputStream open on zip-like content
   * @param toDir dir under which to expand zip contents
   * @throws ZipException if the zip file is invalid
   * @throws IOException
   */
  public static void unzip(InputStream in, File toDir)
      throws ZipException, IOException {
    if (!toDir.exists()) {
      toDir.mkdirs();
    }
    if (!toDir.exists()) {
      throw new IOException("Invalid target directory");
    }
    try (ZipInputStream zip = new ZipInputStream(in)) {
      ZipEntry entry;

      while ((entry = zip.getNextEntry()) != null) {
	if (entry.isDirectory()) {
	  continue;
	}
	String relpath = entry.getName();
	if (relpath.startsWith("/")) {
	  throw new IOException("Absolute paths in zip not allowed:" + relpath);
	}
	File file = new File(toDir, relpath);
	if (!file.getCanonicalPath().startsWith(toDir.getCanonicalPath())) {
	  throw new IOException("Illegal path traversal");
	}
	File parent = file.getParentFile();
	if (parent != null) {
	  if (!parent.exists()) {
	    parent.mkdirs();
	  }
	}
        try (OutputStream out = new FileOutputStream(file)) {
          long n = IOUtils.copy(zip, out);
          log.trace("Write " + n + " bytes to " + file);
        }
      }
    }
  }

  public static void addStringToZip(ZipOutputStream z,
                                    String str,
                                    String entryName) throws IOException {
    try {
      z.putNextEntry(new ZipEntry(entryName));
      IOUtils.write(str, z, Constants.ENCODING_UTF_8);
    } finally {
      z.closeEntry();
    }
  }

 public static void addFileToZip(ZipOutputStream z,
				  File file) throws IOException {
    addFileToZip(z, file, null);
  }

  public static void addFileToZip(ZipOutputStream z,
				  File file,
				  String entryName) throws IOException {
    try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
      if (entryName == null) {
	entryName = file.getName();
      }
      addFileToZip(z, in, entryName);
    }
  }

  public static void addFileToZip(ZipOutputStream z,
				  InputStream in,
				  String entryName) throws IOException {
    try {
      z.putNextEntry(new ZipEntry(entryName));
      IOUtils.copy(in, z);
    } finally {
      z.closeEntry();
    }
  }

  public static void addDirToZip(ZipOutputStream z, String dirname,
				 String addPrefix) {
    addDirToZip(z, new File(dirname), addPrefix);
  }

  public static void addDirToZip(ZipOutputStream z,
				 File dir,
				 String addPrefix) {
    addDirToZip(z, dir, addPrefix, dir.getPath());
  }

  public static void addDirToZip(ZipOutputStream z,
				 String filename,
				 String addPrefix,
				 String removePrefix) {
    addDirToZip(z, new File(filename), removePrefix);
  }

  public static void addDirToZip(ZipOutputStream z,
				 File file,
				 String addPrefix,
				 String removePrefix) {
    try {
      if (file.isDirectory()) {
	for (String name : file.list()) {
	  addDirToZip(z, new File(file, name), addPrefix, removePrefix);
	}
      } else {
	// regular file
	String entName = file.getPath();
	if (removePrefix != null && file.getPath().startsWith(removePrefix)) {
	  entName =  entName.substring(removePrefix.length() + 1);
	}
	if (!StringUtils.isEmpty(addPrefix)) {
	  entName =  new File(addPrefix, entName).getPath();
	}
	ZipEntry anEntry = new ZipEntry(entName);
	addFileToZip(z, file, entName);
      }
    } catch(Exception e) {
      //handle exception 
    }
  }
}
