/*

Copyright (c) 2020 Board of Trustees of Leland Stanford Jr. University,
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
import org.junit.jupiter.api.*;
import org.lockss.util.test.*;
import org.apache.commons.io.IOUtils;

public class TestEofRememberingInputStream extends LockssTestCase5 {

  @Test
  public void testReadChar() throws IOException {
    InputStream in = IOUtils.toInputStream("foo");
    EofRememberingInputStream ein = new EofRememberingInputStream(in);
    assertFalse(ein.isAtEof());
    assertEquals('f', ein.read());
    assertFalse(ein.isAtEof());
    assertEquals('o', ein.read());
    assertFalse(ein.isAtEof());
    assertEquals('o', ein.read());
    assertFalse(ein.isAtEof());
    assertEquals(-1, ein.read());
    assertTrue(ein.isAtEof());
    assertEquals(-1, ein.read());
    assertTrue(ein.isAtEof());
  }

  @Test
  public void testReadBuf() throws IOException {
    InputStream in = IOUtils.toInputStream("12345678");
    EofRememberingInputStream ein = new EofRememberingInputStream(in);
    byte[] buf = new byte[5];
    assertFalse(ein.isAtEof());
    assertEquals(5, readBytes(ein, buf, 5));
    assertEquals("12345", new String(buf));
    assertFalse(ein.isAtEof());
    assertEquals(3, readBytes(ein, buf, 5));
    assertEquals("67845", new String(buf));
    assertTrue(ein.isAtEof());
  }

  /** Read size bytes from stream into buf.  Keeps trying to read until
   * enough bytes have been read or EOF or error. */
  int readBytes(InputStream ins, byte[] buf, int size)
      throws IOException {
    int off = 0;
    while ( off < size) {
      int nread = ins.read(buf, off, size - off);
      if (nread == -1) {
	return off;
      }
      off += nread;
    }
    return off;
  }

}


