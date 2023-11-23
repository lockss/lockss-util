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

package org.lockss.util.rest.repo.util;

import java.util.*;

import org.springframework.http.HttpHeaders;

/** Utilities for V2 repository
 */
public class RepoUtil {

  /** Build a Spring HttpHeaders from CIProperties */
  public static HttpHeaders httpHeadersFromMap(Map<String, String> map) {
    HttpHeaders res = new HttpHeaders();
    for (String key : map.keySet()) {
      res.set(key, (String)map.get(key));
    }
    return res;
  }

  /** Build a Map from a Spring HttpHeaders */
  // TK should concatenate multi-value keys
  public static Map<String, String> mapFromHttpHeaders(HttpHeaders hdrs) {
    Map<String, String> res = new HashMap<>();
    if (hdrs != null) {
      for (String key : hdrs.keySet()) {
        res.put(key, String.join(",", hdrs.get(key)));
      }
    }
    return res;
  }

//   public static Artifact storeArt(LockssRepository repo, String ns,
// 				  String auid, String url, InputStream in,
// 				  CIProperties props) throws IOException {
//     ArtifactIdentifier id = new ArtifactIdentifier(ns, auid,
// 						   url, null);
//     CIProperties propsCopy  = CIProperties.fromProperties(props);
//     propsCopy.setProperty(CachedUrl.PROPERTY_NODE_URL, url);
//     HttpHeaders metadata = httpHeadersFromProps(propsCopy);

//     // tk
//     BasicStatusLine statusLine =
//       new BasicStatusLine(new ProtocolVersion("HTTP", 1,1), 200, "OK");

//     ArtifactData ad =
//       new ArtifactData(id, metadata,
// 		       new IgnoreCloseInputStream(in),
// 		       statusLine);
//     if (log.isDebug2()) {
//       log.debug2("Creating artifact: " + ad);
//     }
//     Artifact uncommittedArt = repo.addArtifact(ad);
//     return repo.commitArtifact(uncommittedArt);
//   }


}
