/*

Copyright (c) 2000-2019 Board of Trustees of Leland Stanford Jr. University,
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
import org.lockss.util.*;
import org.lockss.log.*;

/**
 * An InputStream that wraps all thrown IOExceptions in a {@link
 * org.lockss.util.io.InputIOException}, in order to allow clients to
 * distinguish errors caused by input operations from errors caused by
 * output or other operations, in frameworks such as Spring where such
 * determination would otherwise be difficult.
 */
public class ExceptionWrappingInputStream extends FilterInputStream {

  public ExceptionWrappingInputStream(InputStream in) {
    super(in);
  }

  public int read() throws IOException {
    try {
      return super.read();
    } catch (IOException e) {
      throw new InputIOException(e);
    }
  }

  public int read(byte b[]) throws IOException {
    try {
      return super.read(b);
    } catch (IOException e) {
      throw new InputIOException(e);
    }
  }

  public int read(byte b[], int off, int len) throws IOException {
    try {
      return super.read(b, off, len);
    } catch (IOException e) {
      throw new InputIOException(e);
    }
  }

  public long skip(long n) throws IOException {
    try {
      return super.skip(n);
    } catch (IOException e) {
      throw new InputIOException(e);
    }
  }

  public int available() throws IOException {
    try {
      return super.available();
    } catch (IOException e) {
      throw new InputIOException(e);
    }
  }

  public void close() throws IOException {
    try {
      super.close();
    } catch (IOException e) {
      throw new InputIOException(e);
    }
  }

  public synchronized void reset() throws IOException {
    try {
      super.reset();
    } catch (IOException e) {
      throw new InputIOException(e);
    }
  }
}
