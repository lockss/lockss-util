/*

Copyright (c) 2000-2023, Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.util.rest.repo.model;

import java.io.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.lockss.log.L4JLogger;
import org.lockss.util.*;
import org.lockss.util.rest.repo.util.*;
import org.lockss.util.test.LockssTestCase5;

/**
 * Test class for {@link ArtifactData}.
 */
public class TestArtifactData extends LockssTestCase5 {
  private final static L4JLogger log = L4JLogger.getLogger();

  private static String URL1 = "u1";
  private static String ARTID1 = "1a-2b-3c";
  private final static String NS1 = "ns1";
  private final static String AUID1 = "auid1";

  ArtifactSpec makeAS(String url, String id, String content) {
    return makeAS(url, id, content, null);
  }

  ArtifactSpec makeAS(String url, String id, String content, Map headers) {
    if (headers == null) {
      headers = MapUtil.map("H1", "v1", "h2", "V2");
    }
    ArtifactSpec s1 = ArtifactSpec.forNsAuUrl(NS1, AUID1, url)
      .setArtifactUuid(id)
      .setContent(content)
      .setHeaders(headers)
      .setCollectionDate(0);
    return s1;
  }

  @Test
  public void testStats() throws Exception {
    ArtifactSpec as1 = makeAS(URL1, ARTID1, "content");
    ArtifactData ad1 = as1.getArtifactData();
    // read InputStream, release
    as1.assertArtifactData(ad1);
    ArtifactData.Stats st = ad1.getStats();
    assertEquals(1, st.getTotalAllocated());
    assertEquals(1, st.getWithContent());
    assertEquals(0, st.getInputUsed());
    assertEquals(0, st.getInputUnused());
    assertEquals(0, st.getUnreleased());
    ad1.release();
    assertEquals(1, st.getTotalAllocated());
    assertEquals(1, st.getWithContent());
    assertEquals(1, st.getInputUsed());
    assertEquals(0, st.getInputUnused());
    assertEquals(0, st.getUnreleased());

    // get InputStream, close
    ArtifactData ad2 = as1.getArtifactData();
    InputStream is2 = ad2.getInputStream();
    assertEquals(2, st.getTotalAllocated());
    assertEquals(2, st.getWithContent());
    assertEquals(1, st.getInputUsed());
    assertEquals(0, st.getInputUnused());
    assertEquals(0, st.getUnreleased());
    is2.close();
    assertEquals(2, st.getTotalAllocated());
    assertEquals(2, st.getWithContent());
    assertEquals(2, st.getInputUsed());
    assertEquals(0, st.getInputUnused());
    assertEquals(0, st.getUnreleased());

    // don't get InputStream, release
    ad2 = as1.getArtifactData();
    assertEquals(3, st.getTotalAllocated());
    assertEquals(3, st.getWithContent());
    assertEquals(2, st.getInputUsed());
    assertEquals(0, st.getInputUnused());
    assertEquals(0, st.getUnreleased());
    ad2.release();
    assertEquals(3, st.getTotalAllocated());
    assertEquals(3, st.getWithContent());
    assertEquals(2, st.getInputUsed());
    assertEquals(1, st.getInputUnused());
    assertEquals(0, st.getUnreleased());
  }
}
