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

package org.lockss.util.io;

import java.io.*;
import java.net.URL;
import java.util.*;


public class MockFile extends File {

  String path;
  boolean isDirectory = false;
  boolean isFile = false;
  boolean exists = false;
  boolean mkdirCalled = false;
  List<File> children = new ArrayList<File>();

  public MockFile(String path) {
    super(path);
    this.path = path;
  }

  public String getName() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public String getParent() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public File getParentFile() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public String getPath() {
    return path;
  }

  public boolean isAbsolute() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public String getAbsolutePath() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public File getAbsoluteFile() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public String getCanonicalPath() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public File getCanonicalFile() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public URL toURL() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public boolean canRead() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public boolean canWrite() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public boolean exists() {
    return exists;
  }

  public void setExists(boolean exists) {
    this.exists = exists;
  }

  public boolean isDirectory() {
    return isDirectory;
  }

  public void setIsDirectory(boolean isDirectory) {
    this.isDirectory = isDirectory;
  }

  public boolean isFile() {
    return isFile;
  }

  public void setIsFile(boolean isFile) {
    this.isFile = isFile;
  }

  public boolean isHidden() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public long lastModified() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public long length() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public boolean createNewFile() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public boolean delete() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public void deleteOnExit() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public String[] list() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public String[] list(FilenameFilter filter) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public File[] listFiles() {
    File[] files = new File[children.size()];
    for (int ix = 0; ix < files.length ; ++ix) {
      files[ix] = (File)children.remove(0);
    }
    return files;
  }

  public File[] listFiles(FilenameFilter filter) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public File[] listFiles(FileFilter filter) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public void setChild(File child) {
    children.add(child);
  }

  public boolean mkdir() {
    mkdirCalled = true;
    return true;
  }

  public boolean mkdirs() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public boolean renameTo(File dest) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public boolean setLastModified(long time) {
    throw new UnsupportedOperationException("Not implemented");
  }

  public boolean setReadOnly() {
    throw new UnsupportedOperationException("Not implemented");
  }

//   public int compareTo(File pathname) {
//     throw new UnsupportedOperationException("Not implemented");
//   }

//   public int compareTo(Object o) {
//     throw new UnsupportedOperationException("Not implemented");
//   }

  public boolean equals(Object obj) {
    if (obj instanceof MockFile) {
      return path.equals(((MockFile)obj).getPath());
    }
    return false;
  }

  public int hashCode() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public String toString() {
    return "[MockFile: path="+path+"]";
  }
}
