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

/**
 * <p>
 * Utilities and constants related to encodings.
 * </p>
 * <p>
 * The beginnings of this class came from the larger
 * {@code org.lockss.util.Constants} class.
 * </p>
 * 
 * @author Thib Guicherd-Callin
 * @since 1.4.0
 */
public class EncodingUtil {

  /**
   * <p>
   * The US ASCII encoding ({@value}).
   * </p>
   * 
   * @since 1.4.0
   */
  public static final String ENCODING_US_ASCII = "US-ASCII";
  
  /**
   * <p>
   * The UTF-8 encoding ({@value}).
   * </p>
   * 
   * @since 1.4.0
   */
  public static final String ENCODING_UTF_8 = "UTF-8";
  
  /**
   * <p>
   * The ISO-8859-1 encoding ({@value}).
   * </p>
   * 
   * @since 1.4.0
   */
  public static final String ENCODING_ISO_8859_1 = "ISO-8859-1";
  
  /**
   * <p>
   * The default encoding used when none is detected.
   * </p>
   * 
   * @since 1.4.0
   * @see #ENCODING_ISO_8859_1
   */
  public static String DEFAULT_ENCODING = ENCODING_ISO_8859_1;

  /**
   * <p>
   * The encoding of URLs.
   * </p>
   * 
   * @since 1.4.0
   * @see #ENCODING_US_ASCII
   */
  public static final String URL_ENCODING = ENCODING_US_ASCII;

}
