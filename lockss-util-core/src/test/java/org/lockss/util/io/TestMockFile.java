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

import org.junit.jupiter.api.Test;
import org.lockss.util.test.LockssTestCase5;

public class TestMockFile extends LockssTestCase5 {

  @Test
  public void testUnsupportedOperationException() {
    MockFile f = new MockFile("/some/unlikely/path/testUnsupportedOperationException");
    assertThrows(UnsupportedOperationException.class, () -> f.getName());
    assertThrows(UnsupportedOperationException.class, () -> f.getParent());
    assertThrows(UnsupportedOperationException.class, () -> f.getParentFile());
    assertThrows(UnsupportedOperationException.class, () -> f.isAbsolute());
    assertThrows(UnsupportedOperationException.class, () -> f.getAbsolutePath());
    assertThrows(UnsupportedOperationException.class, () -> f.getAbsoluteFile());
    assertThrows(UnsupportedOperationException.class, () -> f.getCanonicalPath());
    assertThrows(UnsupportedOperationException.class, () -> f.getCanonicalFile());
    assertThrows(UnsupportedOperationException.class, () -> f.toURL());
    assertThrows(UnsupportedOperationException.class, () -> f.canRead());
    assertThrows(UnsupportedOperationException.class, () -> f.canWrite());
    assertThrows(UnsupportedOperationException.class, () -> f.isHidden());
    assertThrows(UnsupportedOperationException.class, () -> f.lastModified());
    assertThrows(UnsupportedOperationException.class, () -> f.length());       
    assertThrows(UnsupportedOperationException.class, () -> f.createNewFile());
    assertThrows(UnsupportedOperationException.class, () -> f.delete());
    assertThrows(UnsupportedOperationException.class, () -> f.deleteOnExit());
    assertThrows(UnsupportedOperationException.class, () -> f.list());
    assertThrows(UnsupportedOperationException.class, () -> f.list((FilenameFilter)null));
    assertThrows(UnsupportedOperationException.class, () -> f.listFiles((FilenameFilter)null));
    assertThrows(UnsupportedOperationException.class, () -> f.listFiles((FileFilter)null));
    assertThrows(UnsupportedOperationException.class, () -> f.mkdirs());
    assertThrows(UnsupportedOperationException.class, () -> f.renameTo(null));
    assertThrows(UnsupportedOperationException.class, () -> f.setLastModified(0L));
    assertThrows(UnsupportedOperationException.class, () -> f.setReadOnly());
    assertThrows(UnsupportedOperationException.class, () -> f.hashCode());
  }

  public void assertMkdirCalled(MockFile mockFile) {
    if (!mockFile.mkdirCalled) {
      fail("mkdir not called");
    }
  }

}
