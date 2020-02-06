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

package org.lockss.util.net;

import org.junit.jupiter.api.Test;
import org.lockss.util.ListUtil;
import org.lockss.util.test.LockssTestCase5;

import java.net.*;

/**
 * Test class for <code>org.lockss.util.IPAddr</code>
 */

public class TestIPAddr extends LockssTestCase5 {

  @Test
  public void testConstructor() throws Exception {
    try {
      IPAddr a1 = new IPAddr(null);
      fail("Should have thrown NullPointerException");
    } catch (NullPointerException e) {
    }
  }

  @Test
  public void testAccessors() throws Exception {
    IPAddr a1 = IPAddr.getByName("1.2.3.4");
    InetAddress i1 = InetAddress.getByName("1.2.3.4");
    assertArrayEquals(i1.getAddress(), a1.getAddress());
    assertEquals(i1.getHostAddress(), a1.getHostAddress());
    assertEquals(i1.hashCode(), a1.hashCode());
  }

  @Test
  public void testEquals() throws Exception {
    IPAddr a1 = IPAddr.getByName("1.2.3.4");
    IPAddr a2 = IPAddr.getByName("1.2.3.4");
    IPAddr a3 = IPAddr.getByName("1.2.3.5");
    assertEquals(a1, a2);
    assertNotEquals(a1, a3);
    assertNotEquals(a1, "1.2.3.4");
  }

  @Test
  public void testGetAllByName() throws Exception {
    IPAddr a[] = IPAddr.getAllByName("1.2.3.4");
    assertEquals(ListUtil.list(IPAddr.getByName("1.2.3.4")),
		 ListUtil.fromArray(a));
  }

  @Test
  public void testIsLoopbackAddress() throws Exception {
    assertTrue(IPAddr.getByName("127.0.0.1").isLoopbackAddress());
    assertTrue(IPAddr.getByName("127.0.0.255").isLoopbackAddress());
    assertFalse(IPAddr.getByName("127.0.0.0").isLoopbackAddress());
    assertFalse(IPAddr.getByName("1.2.3.4").isLoopbackAddress());

    // static version
    assertTrue(IPAddr.isLoopbackAddress("127.0.0.1"));
    assertFalse(IPAddr.isLoopbackAddress("127.0.1.1"));
    assertFalse(IPAddr.isLoopbackAddress("127.0.0.0.1"));
  }
}
