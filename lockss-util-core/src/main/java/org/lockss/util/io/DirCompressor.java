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
import java.nio.file.*;
import java.nio.file.attribute.*;
import org.apache.commons.io.input.*;
import java.util.zip.*;
import org.apache.commons.compress.compressors.gzip.*;
import org.apache.commons.compress.archivers.tar.*;
import org.lockss.log.*;


public abstract class DirCompressor {
  static L4JLogger log = L4JLogger.getLogger();

  File dir;
  File outFile;
  File relativeTo;
  Path srcPath;
  OutputStream outs;

  public static DirCompressor makeZipCompressor() {
    return new Zip();
  }

  public static DirCompressor makeTarCompressor() {
    return new Tar();
  }

  public DirCompressor setOutFile(File outFile) {
    this.outFile = outFile;
    return this;
  }

  public DirCompressor setSourceDir(File srcDir) {
    return setSourceDir(srcDir.toPath());
  }

  public DirCompressor setSourceDir(Path srcDir) {
    this.srcPath = srcDir;
    return this;
  }

  DirCompressor setRelativeTo(File relTo) {
    this.relativeTo = relTo;
    return this;
  }

  protected abstract String getTypeName();
  protected abstract void openCompressedOutputStream(OutputStream out) throws IOException;
  protected abstract void addFile(Path file, Path target) throws IOException;
  protected abstract void finish() throws IOException;

  static class Zip extends DirCompressor {
    ZipOutputStream zout;

    protected String getTypeName() {
      return "zip";
    }

    protected void openCompressedOutputStream(OutputStream out) {
      zout = new ZipOutputStream(out);
    }

    protected void addFile(Path file, Path target) throws IOException {
      ZipEntry ze = new ZipEntry(target.toString());
      zout.putNextEntry(ze);
      Files.copy(file, zout);
      zout.closeEntry();
    }

    protected void finish() throws IOException {
      zout.close();
    }
  }

  static class Tar extends DirCompressor {
    TarArchiveOutputStream tout;

    protected String getTypeName() {
      return "tar";
    }

    protected void openCompressedOutputStream(OutputStream out) throws IOException {
      GzipCompressorOutputStream gzout = new GzipCompressorOutputStream(out);
      tout = new TarArchiveOutputStream(gzout);
    }

    protected void addFile(Path file, Path target) throws IOException {
      TarArchiveEntry tarEntry = new TarArchiveEntry(file.toFile(), target.toString());
      tout.putArchiveEntry(tarEntry);
      Files.copy(file, tout);
      tout.closeArchiveEntry();
    }

    protected void finish() throws IOException {
      tout.finish();
      tout.close();
    }

  }

  public void build() throws IOException {
    if (outFile == null) {
      throw new IllegalArgumentException("outFile must not be null");
    }
    if (srcPath == null) {
      throw new IllegalArgumentException("srcPath must not be null");
    }
    addFiles();
  }

  protected void addFiles() throws IOException {
    if (!Files.isDirectory(srcPath)) {
      throw new IOException("Source dir isn't a directory.");
    }

    // get folder name as zip file name
    try (OutputStream fout = new FileOutputStream(outFile);
         BufferedOutputStream bout = new BufferedOutputStream(fout)) {
      openCompressedOutputStream(bout);
      Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(Path file,
                                           BasicFileAttributes attributes) {

            // only copy files, no symbolic links
            if (attributes.isSymbolicLink()) {
              return FileVisitResult.CONTINUE;
            }

            // get filename
            Path targetFile = srcPath.relativize(file);

            try {
              addFile(file, targetFile);
            } catch (IOException e) {
              log.error("Couldn't add {} to {} {}", file, getTypeName(), outFile);
            }
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFileFailed(Path file, IOException e) {
            log.error("Failure visiting {}", file, e);
            return FileVisitResult.CONTINUE;
          }

        });

      finish();
    }
  
  }
}

