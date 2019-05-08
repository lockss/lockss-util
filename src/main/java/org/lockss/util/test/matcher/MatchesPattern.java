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

import java.util.regex.Pattern;

import org.hamcrest.*;

/** Regexp matcher to use with assertThat.  Pattern must match entire
 * input string.  <i>Eg</i>, <code>assertThat("reason", "abc123",
 * MatchPattern.matchPattern("a.*23"))</code> */
// XXX Copied from hamcrest 2.0.  If we upgrade, this can be removed
public class MatchesPattern extends TypeSafeMatcher<String> {
  
  private final Pattern pattern;

  public MatchesPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  @Override
  protected boolean matchesSafely(String item) {
    return pattern.matcher(item).matches();
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("a string matching the pattern '" + pattern + "'");
  }

  /**
   * Creates a matcher of {@link java.lang.String} that matches when the examined string
   * exactly matches the given {@link java.util.regex.Pattern}.
   */
  public static Matcher<String> matchesPattern(Pattern pattern) {
    return new MatchesPattern(pattern);
  }

  /**
   * Creates a matcher of {@link java.lang.String} that matches when the examined string
   * exactly matches the given regular expression, treated as a {@link java.util.regex.Pattern}.
   */
  public static Matcher<String> matchesPattern(String regex) {
    return new MatchesPattern(Pattern.compile(regex));
  }
  
}