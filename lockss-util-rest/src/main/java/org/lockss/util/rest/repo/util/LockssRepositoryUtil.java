/*

Copyright (c) 2000-2023 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Except as contained in this notice, the name of Stanford University shall not
be used in advertising or otherwise to promote the sale, use or other dealings
in this Software without prior written authorization from Stanford University.

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
