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

package org.lockss.util.time;

import org.junit.jupiter.api.Test;
import org.lockss.util.test.LockssTestCase5;

public class TestTimerUtil extends LockssTestCase5 {

  @Test
  public void testSleep() {
    long start;
    
    try {
      start = System.currentTimeMillis();
      TimerUtil.sleep(100L);
      assertTrue(System.currentTimeMillis() - start >= 100L);
    }
    catch (InterruptedException ie) {
      fail("Thread was not interrupted but threw InterruptedException");
    }
    
    Thread sleeper = new Thread() {
      @Override
      public void run() {
        long mystart = System.currentTimeMillis();
        try {
          TimerUtil.sleep(100L);
          fail("Thread was supposed to be interrupted but was not");
        }
        catch (InterruptedException ie) {
          assertTrue(System.currentTimeMillis() - mystart < 100L);
        }
      }
    };
    try {
      sleeper.start();
      Thread.sleep(50L);
      sleeper.interrupt();
      sleeper.join();
    }
    catch (InterruptedException ie) {
      fail("Main thread was interrupted");
    }
  }
  
  @Test
  public void testGuaranteedSleep() {
    long start;
    
    start = System.currentTimeMillis();
    TimerUtil.guaranteedSleep(100L);
    assertTrue(System.currentTimeMillis() - start >= 100L);
    
    Thread sleeper = new Thread() {
      @Override
      public void run() {
        long mystart = System.currentTimeMillis();
        TimerUtil.guaranteedSleep(100L);
        assertTrue(System.currentTimeMillis() - mystart < 100L);
      }
    };
    try {
      sleeper.start();
      Thread.sleep(50L);
      sleeper.interrupt();
      sleeper.join();
    }
    catch (InterruptedException ie) {
      fail("Main thread was interrupted");
    }
  }
  
}
