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

package org.lockss.util.lang;

import java.util.Random;

/** Extension of {@link Random} that adds some missing methods. */
public class LockssRandom extends Random {

  public LockssRandom() {
    super();
  }

  public LockssRandom(long seed) {
    super(seed);
  }

  /** Return the next pseudorandom number with <code>bits</code> random
   * bits. */
  public long nextBits(int bits) {
    if (bits <= 32) {
      return next(bits) & (long)0xffffffffL;
    }
    return (((long)next(bits - 32)) << 32) | next(32) & (long)0xffffffffL;
  }

  /** Returns a pseudorandom, uniformly distributed int value between 0
   * (inclusive) and the specified value (exclusive), drawn from this
   * random number generator's sequence.  The algorithm is similar to that
   * given in the javadoc for {@link java.util.Random#nextInt(int)}.
   */
  public long nextLong(long n) {
    if (n<=0) {
      throw new IllegalArgumentException("n must be > 0");
    }
    long bits, val;
    do {
      bits = (nextLong() >>> 1);
      val = bits % n;
    } while(bits - val + (n-1) < 0);
    return val;
  }
}
