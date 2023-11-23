/*

Copyright (c) 2000-2023, Board of Trustees of Leland Stanford Jr. University

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.util.rest;

import java.util.*;

import org.springframework.http.HttpHeaders;

/**
 * Static utilities for working with HTTP headers in Spring-related code
 */
public class SpringHeaderUtil {

  /** Merge two sets of headers.  Multi-valued fields are handled -
   * additional values for a field will be added.  However, redundant
   * values will *not* be added.
   * @Param headers the headers to add.  If null, toHeaders is returned
   * unmodified
   * @Param toHeaders the headers to be added to.  If null, a new
   * HttpHeaders instance is created, added to, and returned.
   * @Return toHeaders, modified by the addition of headers
   */
  public static HttpHeaders addHeaders(HttpHeaders headers,
				       HttpHeaders toHeaders) {
    return  addHeaders(headers, toHeaders, false);
  }

  /** Merge two sets of headers.  Multi-valued fields are handled -
   * additional values for a field will be added.  However, redundant
   * values will *not* be added.
   * @Param headers the headers to add.  If null, toHeaders is returned
   * unmodified
   * @Param toHeaders the headers to be added to.  If null, a new
   * HttpHeaders instance is created, added to, and returned.
   * @Param replace if true, values for a key in headers (including
   * multiple values) will replace any existing values for that key in
   * toHeaders.  If false all values for a key in headers will be added to
   * that key in toHeaders, unless already present.  If toHeaders is null,
   * a new HttpHeaders instance will be created and added to.
   * @Return toHeaders, modified by the addition of headers
   */
  public static HttpHeaders addHeaders(HttpHeaders headers,
				       HttpHeaders toHeaders,
				       boolean replace) {
    if (toHeaders == null) {
      toHeaders = new HttpHeaders();
    }
    if (headers != null) {
      for (Map.Entry<String,List<String>> ent : headers.entrySet()) {
	for (String val : ent.getValue()) {
	  if (replace) {
	    toHeaders.put(ent.getKey(), ent.getValue());
	  } else {
	    List<String> oldval = toHeaders.get(ent.getKey());
	    if (oldval == null || !oldval.contains(val)) {
	      toHeaders.add(ent.getKey(), val);
	    }
	  }
	}
      }
    }
    return toHeaders;
  }

}
