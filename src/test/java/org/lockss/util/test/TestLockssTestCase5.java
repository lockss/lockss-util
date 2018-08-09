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

package org.lockss.util.test;

import java.io.*;

import org.apache.commons.io.input.ReaderInputStream;
import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;


public class TestLockssTestCase5 extends LockssTestCase5 {
  
  InputStream stringInputStream(String str) {
    return new ReaderInputStream(new StringReader(str));
  }

  static String STREAM_CLOSED_PAT =
    "java\\.io\\.IOException: [Ss]tream ?[Cc]losed";

  @Test
  public void testAssertSameBytes() {
    assertThrowsMatch(AssertionFailedError.class,
		      "expected stream ran out early, at byte position 3",
		      () -> {assertSameBytes(stringInputStream("123"),
					     stringInputStream("1234"));});
    assertThrowsMatch(AssertionFailedError.class,
		      "actual stream ran out early, at byte position 4",
		      () -> {assertSameBytes(stringInputStream("foooo"),
					     stringInputStream("fooo"));});

    assertThrowsMatch(AssertionFailedError.class,
		      "at byte position 4 ==> expected: <52> but was: <53>",
		      () -> {assertSameBytes(stringInputStream("12345"),
					     stringInputStream("12354"));});
    InputStream closedStream = stringInputStream("123");
    try {
      closedStream.close();
    } catch (IOException e) {
      fail("Couldn't close stream", e);
    }
    assertThrowsMatch(AssertionFailedError.class,
		      "after 0 bytes, expected stream threw " + STREAM_CLOSED_PAT,
		      () -> {assertSameBytes(closedStream,
					     stringInputStream("foo"));});
    assertThrowsMatch(AssertionFailedError.class,
		      "after 0 bytes, actual stream threw " + STREAM_CLOSED_PAT,
		      () -> {assertSameBytes(stringInputStream("foo"),
					     closedStream);});
  }

  @Test
  public void testAssertSameCharacters() {
    assertThrowsMatch(AssertionFailedError.class,
		      "expected stream ran out early, at char position 3",
		      () -> {assertSameCharacters(new StringReader("foo"),
						  new StringReader("fooo"));});
    assertThrowsMatch(AssertionFailedError.class,
		      "actual stream ran out early, at char position 4",
		      () -> {assertSameCharacters(new StringReader("foooo"),
						  new StringReader("fooo"));});

    assertThrowsMatch(AssertionFailedError.class,
		      "at char position 4 ==> expected: <6> but was: <o>",
		      () -> {assertSameCharacters(new StringReader("foo6"),
						  new StringReader("fooo"));});
    Reader closedRdr = new StringReader("123");
    try {
      closedRdr.close();
    } catch (IOException e) {
      fail("Couldn't close reader", e);
    }
    assertThrowsMatch(AssertionFailedError.class,
		      "after 0 chars, expected stream threw " + STREAM_CLOSED_PAT,
		      () -> {assertSameCharacters(closedRdr,
						  new StringReader("foo"));});
    assertThrowsMatch(AssertionFailedError.class,
		      "after 0 chars, actual stream threw " + STREAM_CLOSED_PAT,
		      () -> {assertSameCharacters(new StringReader("foo"),
						  closedRdr);});
  }
  
  @RepeatedTest(5)
  public void testSuccessRateFails(RepetitionInfo repetitionInfo) {
    try {
      setUpSuccessRate(repetitionInfo);
      if (repetitionInfo.getCurrentRepetition() <= 4) {
        fail("4 intentional failures: " + repetitionInfo.getCurrentRepetition());
      }
    }
    catch (Throwable thr) {
      signalFailure(repetitionInfo);
    }
    finally {
      if (repetitionInfo.getCurrentRepetition() < repetitionInfo.getTotalRepetitions()) {
        try {
          assertSuccessRate(repetitionInfo, 0.5f);
        }
        catch (AssertionFailedError afe) {
          fail("Assertion should not have failed");
        }
      }
      else {
        assertThrowsMatch(AssertionFailedError.class,
                          "4 of 5 tries, not achieving a 0.50* success rate",
                          () -> assertSuccessRate(repetitionInfo, 0.5f));
      }
    }
  }

  @RepeatedTest(5)
  public void testSuccessRateSucceeds(RepetitionInfo repetitionInfo) {
    try {
      setUpSuccessRate(repetitionInfo);
      if (repetitionInfo.getCurrentRepetition() == 2) {
        fail("1 intentional failure: " + repetitionInfo.getCurrentRepetition());
      }
    }
    catch (Throwable thr) {
      signalFailure(repetitionInfo);
    }
    finally {
      try {
        assertSuccessRate(repetitionInfo, 0.5f);
      }
      catch (AssertionFailedError afe) {
        fail("Assertion should not have failed");
      }
    }
  }

  @RepeatedTest(5)
  public void testSuccessRateExactRateSucceeds(RepetitionInfo repetitionInfo) {
    try {
      setUpSuccessRate(repetitionInfo);
      if (repetitionInfo.getCurrentRepetition() == 2) {
        fail("1 intentional failure: " + repetitionInfo.getCurrentRepetition());
      }
    }
    catch (Throwable thr) {
      signalFailure(repetitionInfo);
    }
    finally {
      try {
        assertSuccessRate(repetitionInfo, 0.8f);
      }
      catch (AssertionFailedError afe) {
        fail("Assertion should not have failed");
      }
    }
  }

}
