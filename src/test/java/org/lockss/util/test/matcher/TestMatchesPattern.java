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

package org.lockss.util.test.matcher;

import org.junit.jupiter.api.Test;
import org.lockss.util.test.LockssTestCase5;

public class TestMatchesPattern extends LockssTestCase5 {

  @Test
  public void testMatchPattern() {
    assertThat("123", MatchesPattern.matchesPattern("1.3"));
    assertThat("123", not(MatchesPattern.matchesPattern("1.32")));
    assertThat("string string", not(MatchesPattern.matchesPattern("g st")));
    assertThat("string string", MatchesPattern.matchesPattern(".*g st.*"));
    assertThat("string string", not(MatchesPattern.matchesPattern("xxx")));
  }
  
  @Test
  public void testLockssTestCase5() {
    assertThat("123", matchesPattern("1.3"));
    assertThat("123", not(matchesPattern("1.32")));
    assertThat("string string", not(matchesPattern("g st")));
    assertThat("string string", matchesPattern(".*g st.*"));
    assertThat("string string", not(matchesPattern("xxx")));
  }
 
}