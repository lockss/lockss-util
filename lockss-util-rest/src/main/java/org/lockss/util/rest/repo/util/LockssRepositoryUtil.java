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

import org.lockss.util.rest.repo.LockssRepository;
import org.lockss.util.rest.repo.model.*;
import org.lockss.log.L4JLogger;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for {@link LockssRepository} implementations.
 */
public class LockssRepositoryUtil {
  private final static L4JLogger log = L4JLogger.getLogger();

  private static final Pattern NAMESPACE_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9._-]*$");

  /**
   * Validates a namespace.
   *
   * @param namespace A {@link String} containing the namespace to validate.
   * @return A {@code boolean} indicating whether the namespace passed validation.
   */
  public static boolean validateNamespace(String namespace) {
    if (namespace == null) {
      return false;
    }

    Matcher m = NAMESPACE_PATTERN.matcher(namespace);
    return m.matches();
  }

  public static boolean isIdenticalToPreviousVersion(LockssRepository repo,
                                                     Artifact art)
      throws IOException {
    return LockssRepositoryUtil.getIdenticalPreviousVersion(repo, art) != null;
  }

  public static Artifact getIdenticalPreviousVersion(LockssRepository repo,
                                                     Artifact art)
      throws IOException {
    if (art.getCommitted()) {
      throw new IllegalStateException("Can't perform identical check after artifact is committed");
    }
    int ver = art.getVersion();
    if (ver < 2) return null;
    String artHash = art.getContentDigest();
    if (artHash == null) return null;
    // Fetch the latest committed version, if any
    Artifact prev = repo.getArtifact(art.getNamespace(), art.getAuid(), art.getUri());
    if (prev == null) return null;
    if (art.getUuid().equals(prev.getUuid())) {
      log.error("Uncommitted artifact has same UUID as supposedly committed most recent version: " + art);
      // throw?
      return null;
    }
    if (artHash.equals(prev.getContentDigest())) {
      log.debug2("New version identical to old: " + art.getUri());
      return art;
    }
    return null;
  }

}
