/*

 Copyright (c) 2019 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.lockss.log.L4JLogger;

/**
 * Password utility code.
 */
public class PasswordUtil {
  private static final L4JLogger log = L4JLogger.getLogger();

  /**
   * Provides a password from a resource.
   * 
   * @param passwordResourcePathName A String with the path name of a resource
   *                                 containing the password to be lazy-loaded
   *                                 on the first call and ignored afterwards.
   * @return a String with the password or <code>null</code> if the resource is
   *         not available.
   */
  public static String getPasswordFromResource(String passwordResourcePathName)
  {
    log.debug2("passwordResourcePathName = {}", passwordResourcePathName);
    String passwordFromResource = null;

    // Check whether there is a password resource path name.
    if (passwordResourcePathName != null) {
      // Yes.
      try {
	// Get the classloader.
	ClassLoader cl = Thread.currentThread().getContextClassLoader();

	if (cl == null) {
	  cl = Class.class.getClassLoader();
	}

	// Read the password.
        passwordFromResource = IOUtils.toString(
            cl.getResourceAsStream(passwordResourcePathName),
            StandardCharsets.UTF_8);
      } catch (IOException ioe) {
	// The password could not be obtained from the resource.
	log.warn("Exception caught getting password from resource "
	    + passwordResourcePathName, ioe);
      }
    }

    return passwordFromResource;
  }
}
