/*

Copyright (c) 2018 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

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
package org.lockss.util;

import org.junit.jupiter.api.Test;
import org.lockss.util.test.LockssTestCase5;

/**
 * Test class for org.lockss.util.AccessType3.
 */
public class TestAccessType3 extends LockssTestCase5 {
  @Test
  public void testIntegrity() {
    // Expect only the values READ, WRITE and READ_WRITE.
    for (AccessType3 at : AccessType3.values()) {
      switch(at) {
      case READ:
	break;
      case WRITE:
	break;
      case READ_WRITE:
	break;
      default:
	throw new IllegalArgumentException(at.toString());
      }
    }

    // Accept the values READ, WRITE and READ_WRITE.
    for (String ats : new String[]{"READ", "WRITE", "READ_WRITE"}) {
      assertNotNull(AccessType3.valueOf(ats));
    }

    // Do not accept other values.
    assertThrows(NullPointerException.class, () -> AccessType3.valueOf(null));
    assertThrows(IllegalArgumentException.class, () -> AccessType3.valueOf(""));
    assertThrows(IllegalArgumentException.class,
	() -> AccessType3.valueOf("READ "));
    assertThrows(IllegalArgumentException.class,
	() -> AccessType3.valueOf(" READ"));
    assertThrows(IllegalArgumentException.class,
	() -> AccessType3.valueOf("WRITE "));
    assertThrows(IllegalArgumentException.class,
	() -> AccessType3.valueOf(" WRITE"));
    assertThrows(IllegalArgumentException.class,
	() -> AccessType3.valueOf("READ_WRITE "));
    assertThrows(IllegalArgumentException.class,
	() -> AccessType3.valueOf(" READ_WRITE"));
    assertThrows(IllegalArgumentException.class,
	() -> AccessType3.valueOf("invalid"));
  }
}
