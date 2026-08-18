/*

Copyright (c) 2000-2024 Board of Trustees of Leland Stanford Jr. University,
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
 * @author gaxzerow
 */

package org.lockss.util.io;

import java.io.*;
import java.lang.ref.*;
import java.text.Format;
import java.util.concurrent.atomic.*;

import org.apache.commons.io.output.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.lockss.log.*;
import org.lockss.util.CloseCallbackInputStream;

/** An output stream backed by memory below a specified threshold size,
 * then by a temp file if it grows over the threshold.  If the stream is
 * closed before the threshold is reached, the temp file will not be
 * created.<br>

 * The temp file (if any) should be deleted, either by calling {@link
 * #deleteTempFile()} or by (eventually) closing the stream returned
 * by {@link #getDeleteOnCloseInputStream()}.  (This should be done
 * even if {@link #isInMemory()} is true.)  If the file has not been
 * deleted by the time this is GC'ed, the file fill be deleted and, if
 * this class's log level is debug2 or above, the stack trace showing
 * where it was created will be logged, as a debugging aid.<br>
 *
 * Additionally, if an error occurs writing to the file, the file will
 * be closed and deleted, and that fact logged.
 */
public class DeferredTempFileOutputStream extends ProxyOutputStream {
  private static final L4JLogger log = L4JLogger.getLogger();

  private static final Format TIMESTAMP_DATEFORMAT =
    FastDateFormat.getInstance("HH:mm:ss.SSS");

  private static AtomicInteger counter = new AtomicInteger(0);

  private static boolean avoidExcessiveNeverDeletedLogging = false;

  public static void setAvoidExcessiveNeverDeletedLogging(boolean val) {
    avoidExcessiveNeverDeletedLogging = val;
  }

  protected ThreshStream inner;

  /**
   * The output stream to which data will be written prior to the threshold
   * being reached.
   */
  protected UnsynchronizedByteArrayOutputStream memoryOutputStream;

  /**
   * The output stream to which data will be written at any given time. This
   * will always be one of <code>memoryOutputStream</code> or
   * <code>diskOutputStream</code>.
   */
  protected OutputStream currentOutputStream;

  /**
   * The temp file, if one has been created
   */
  protected File tempFile;

  /** Dir in which to create temp file */
  protected File tmpDir;

  /** Prefix for temp file name */
  protected String tempName;
    
  /** The name of the temp file for log messages */
  protected String logName;

  /**
   * True when close() has been called successfully.
   */
  protected boolean closed = false;

  /**
   * True when deleteTempFile() has been called.
   */
  protected boolean deleted = false;

  // Infrastructure to invoke Cleaner
  private static final Cleaner cleaner = Cleaner.create();
  private Cleaner.Cleanable cleanable = null;
  private DFCleaner dfc;

  private static String defaultName() {
    return "deferred-temp-file-" + counter.incrementAndGet();
  }

  /**
   * Return an OutputStream that will create a tempfile iff the size
   * exceeds threshold.  The tempfile will be named
   * <code>deferred-temp-fileXXX.tmp</code>
   * @param threshold The number of bytes at which to switch from
   * in-memory to disk file
   */
  public DeferredTempFileOutputStream(int threshold) {
    this(threshold, defaultName());
  }

  /**
   * Return an OutputStream that will create a tempfile iff the size
   * exceeds threshold.
   * @param threshold  The number of bytes at which to trigger an event.
   * @param tmpDir  Dir in which temp file will be created.
   */
  public DeferredTempFileOutputStream(int threshold, File tmpDir) {
    this(threshold, defaultName());
    this.tmpDir = tmpDir;
  }

  /**
   * Return an OutputStream that will create a tempfile iff the size
   * exceeds threshold.
   * @param threshold  The number of bytes at which to trigger an event.
   * @param name  Prefix to use for temp file name.
   */
  public DeferredTempFileOutputStream(int threshold, String name) {
    super(null);
    inner = new ThreshStream(threshold);
    out = inner;
//     setReference(inner);
    tempName = name;
    logName = tempName;
    memoryOutputStream = new UnsynchronizedByteArrayOutputStream();
    currentOutputStream = memoryOutputStream;
    dfc = new DFCleaner(logName);
    cleanable = cleaner.register(this, dfc);
  }

  class ThreshStream extends ThresholdingOutputStream {

    // --------------------------------------- ThresholdingOutputStream methods

    ThreshStream(int threshold) {
      super(threshold);
    }

    /**
     * Returns the current output stream. This may be memory based or disk
     * based, depending on the current state with respect to the threshold.
     *
     * @return The underlying output stream.
     *
     * @exception IOException if an error occurs.
     */
    @Override
    protected OutputStream getStream() throws IOException {
      return currentOutputStream;
    }

    /**
     * Switches the underlying output stream from a memory based stream to
     * one that is backed by a temp file on disk.
     *
     * @exception IOException if an error occurs.
     */
    @Override
    protected void thresholdReached() throws IOException {
      tempFile = createTempFile(tempName);
      logName = tempFile.getName();
      dfc.setFile(tempFile);
      OutputStream fos = createFileOutputStream(tempFile);
      BufferedOutputStream bos = new BufferedOutputStream(fos, 100 * 1024);
      memoryOutputStream.writeTo(bos);
      currentOutputStream = bos;
      memoryOutputStream = null;
    }

  }

  // Overridable for testing
  protected File createTempFile(String name) throws IOException {
    return FileUtil.createTempFile(name, ".tmp", tmpDir);
  }

  // Overridable for testing
  protected OutputStream createFileOutputStream(File fileName)
      throws IOException {
    return new FileOutputStream(tempFile);
  }

  // --------------------------------------------------------- Public methods

  /**
   * Determines whether or not the data for this output stream has been
   * retained in memory.
   *
   * @return <code>true</code> if the data is available in memory;
   *         <code>false</code> otherwise.
   */
  public boolean isInMemory() {
    return (!inner.isThresholdExceeded());
  }

  public String tempFileName() {
    return logName;
  }

  /**
   * Returns the data for this output stream as an array of bytes, assuming
   * that the data has been retained in memory. If the data was written to
   * disk, this method returns <code>null</code>.
   *
   * @return The data for this output stream, or <code>null</code> if no such
   *         data is available.
   * @deprecated
   */
  @Deprecated
  public byte[] getData() {
    if (memoryOutputStream != null) {
      return memoryOutputStream.toByteArray();
    }
    return null;
  }

  /**
   * Returns the number of bytes that have been written to this output stream.
   *
   * @return The number of bytes written.
   */
  public long getByteCount() {
    return inner.getByteCount();
  }

  /**
   * Return an InputStream open on the contents written to the
   * OutputStream.
   *
   * @return An InputStream open on the data written to the OutputStream
   */
  public InputStream getInputStream() throws IOException {
    if (isInMemory()) {
      return new CloseCallbackInputStream(new ByteArrayInputStream(getData()),
                                          null, this);
    } else {
      return
        new BufferedInputStream(new CloseCallbackInputStream(new FileInputStream(getFile()),
                                                             null, this));
    }
  }

  /**
   * Return an InputStream open on the contents written to the
   * OutputStream.  If a file is present it will be deleted when this
   * stream is closed.
   *
   * @return An InputStream open on the data written to the OutputStream
   */
  public InputStream getDeleteOnCloseInputStream() throws IOException {
    // Always use a CloseCallbackInputStream with a callback in order
    // to avoid false "never deleted" warnings when the file is in memory
    CloseCallbackInputStream.Callback cb =
      new CloseCallbackInputStream.Callback() {
        @Override
        public void streamClosed(Object cookie) {
          deleteTempFile();
        }
      };
    return
      new BufferedInputStream(new CloseCallbackInputStream(getInputStream(),
                                                           cb, this));
  }

  /**
   * Returns the temp file, if any
   *
   * @return The temp file behind this output stream, if the threshold
   * has been reached, else null
   */
  public File getFile() {
    return tempFile;
  }
    
  /**
   * Close the output stream
   *
   * @exception IOException if an error occurs.
   */
  public void close() throws IOException {
    super.close();
    closed = true;
  }
    
  /**
   * Delete the temp file, if it exists; closes the stream first if it's
   * still open.
   */
  public void deleteTempFile() {
    if (!closed) {
      log.error("Deleted while still open: {}", logName, new Throwable());
      IOUtils.closeQuietly(this);
    }
    if (tempFile != null) {
      FileUtils.deleteQuietly(tempFile);
      tempFile = null;
    }
    if (dfc != null) {
      dfc.setDeleted();                // Tell cleaner delete was called
      log.debug2("Deleted {}", logName);
      cleanable.clean();               // ??? Cleaner doc suggests this
    }
  }

  @Override
  protected void handleIOException(IOException e) throws IOException {
    log.warn("Exception thrown while writing: {}", logName, e);
    if (!isInMemory()) {
      log.warn("Closing and deleting tempfile: {}", logName);
      IOUtils.closeQuietly(this);
      deleteTempFile();
    }
    throw e;
  }

  /** The Cleaner state and Runnable */
  private static class DFCleaner implements Runnable {

    private volatile boolean isDeleted = false;
    private volatile File file;
    private volatile String createStack;
    private volatile String name;
    private volatile long openTime;

    private DFCleaner() {
      this(null);
    }

    private DFCleaner(String name) {
      this.name = name;

      if (log.isDebug2Enabled()) {
        // Record the current stack
        Throwable th = new Exception("Create");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);
        createStack = sw.toString();
      }

      openTime = System.currentTimeMillis();
    }

    private void setFile(File file) {
      this.file = file;
    }

    private void setDeleted() {
      isDeleted = true;
    }

    public void run() {
      if (isDeleted) {
        return;
      }
      StringBuilder sb = new StringBuilder();
      if (file == null) {
        sb.append("Never deleted (in mem): ");
        sb.append(name);
      } else {
        sb.append("Never deleted: ");
        sb.append(file.getName());
        try {
          if (!file.delete()) {
            sb.append(" DELETE FAILED");
          }
        } catch (Exception e) {
            sb.append(" DELETE FAILED: ");
            sb.append(e.toString());
        }
      }
      sb.append(".  Created at ");
      sb.append(TIMESTAMP_DATEFORMAT.format(openTime));
      if (createStack != null) {
        sb.append(" at ");
        sb.append(createStack);
      }
      if (avoidExcessiveNeverDeletedLogging) {
        log.debug2(sb.toString());
      } else {
        log.warn(sb.toString());
      }
    }
  }
}
