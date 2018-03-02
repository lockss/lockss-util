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

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

/**
 * <p>
 * Unit tests for the {@link EncodingUtil} class.
 * </p>
 * 
 * @author Thib Guicherd-Callin
 * @since 1.4.0
 * @see EncodingUtil
 */
public class TestEncodingUtil {

  @ParameterizedTest
  @MethodSource("testEncodingConstants_source")
  public void testEncodingConstants(String constant, String expectedValue) {
    assertEquals(expectedValue, constant);
  }
  
  public static Stream<Arguments> testEncodingConstants_source() {
    return Stream.of(Arguments.of(EncodingUtil.ENCODING_US_ASCII, "US-ASCII"),
                     Arguments.of(EncodingUtil.ENCODING_ISO_8859_1, "ISO-8859-1"),
                     Arguments.of(EncodingUtil.ENCODING_UTF_8, "UTF-8"));
  }

  @ParameterizedTest
  @MethodSource("testEncodings_source")
  public void testEncodings(String constant, String expectedConstant) {
    assertEquals(expectedConstant, constant);
  }

  public static Stream<Arguments> testEncodings_source() {
    return Stream.of(Arguments.of(EncodingUtil.DEFAULT_ENCODING, EncodingUtil.ENCODING_ISO_8859_1),
                     Arguments.of(EncodingUtil.URL_ENCODING, EncodingUtil.ENCODING_US_ASCII));
  }

}
