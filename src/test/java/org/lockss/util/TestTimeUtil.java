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

package org.lockss.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

/**
 * <p>
 * Unit tests for the {@link TimeUtil} class.
 * </p>
 * 
 * @author Thib Guicherd-Callin
 * @since 1.4.0
 * @see TimeUtil
 */
public class TestTimeUtil {

  @ParameterizedTest
  @MethodSource("testTimeConstants_source")
  public void testTimeConstants(long constant, long expectedValue) {
    assertEquals(expectedValue, constant);
  }
  
  public static Stream<Arguments> testTimeConstants_source() {
    return Stream.of(Arguments.of(TimeUtil.SECOND, 1_000L),
                     Arguments.of(TimeUtil.MINUTE, 60_000L),
                     Arguments.of(TimeUtil.HOUR, 3_600_000L),
                     Arguments.of(TimeUtil.DAY, 86_400_000L),
                     Arguments.of(TimeUtil.WEEK, 604_800_000L),
                     Arguments.of(TimeUtil.YEAR, 31_536_000_000L));
  }

}
