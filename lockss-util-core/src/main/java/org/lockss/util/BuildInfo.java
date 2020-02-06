/*

Copyright (c) 2000-2019 Board of Trustees of Leland Stanford Jr. University,
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
import java.util.*;
import java.io.*;
import java.net.*;
import org.lockss.log.*;

/**
 * Loads and provides access to info about build objects and
 * environment.
 */
public class BuildInfo {
  private static L4JLogger log = L4JLogger.getLogger();

  /** Resource dir */
  public static String RESOURCE_PATH = "org/lockss/componentresources/";

  /** Name of the property file into which the build writes build
      properties.  This must be the same as file.buildInfo in the parent
      pom */
  public static final String PROPERTY_RESOURCE =
      RESOURCE_PATH + "build.properties";

  /** Build artifact name property. */
  public static final String BUILD_ARTIFACT = "build.artifactId";
  /** Build name name property. */
  public static final String BUILD_NAME = "build.name";
  /** Build description property. */
  public static final String BUILD_DESCRIPTION = "build.description";
  /** Build version property. */
  public static final String BUILD_VERSION = "build.version";
  /** Build parent version property. */
  public static final String BUILD_PARENT_VERSION = "build.parent.version";
  /** git branch property. */
  public static final String BUILD_GIT_BRANCH = "build.git.branch";
  /** git commit property. */
  public static final String BUILD_GIT_COMMIT = "build.git.commit";
  /** git dirty property. */
  public static final String BUILD_GIT_DIRTY = "build.git.dirty";
  /** Build timestamp property. */
  public static final String BUILD_TIMESTAMP = "build.timestamp";
  /** Build raw timestamp property (ms since epoch). */
  public static final String BUILD_RAWTIMESTAMP = "build.rawtimestamp";
  /** Build user name property. */
  public static final String BUILD_USER_NAME = "build.user.name";
  /** Build host property. */
  public static final String BUILD_HOST = "build.host";
  /** Release name property. */
  public static final String BUILD_RELEASENAME = "build.releasename";

  // The primary BuildInfo for the component (first on the classpath)
  private static BuildInfo FIRST = null;
  // List of all BuildInfo found on the classpath
  private static List<BuildInfo> ALL = null;

  private Properties buildProps = null;

  /** Create a BuildInfo from a Properties */
  public BuildInfo(Properties props) {
    if (props == null) {
      throw new IllegalArgumentException("props must not be null");
    }
    buildProps = props;
  }

  /** Create a BuildInfo from a build.properties file */
  public BuildInfo(URL url) {
    if (url == null) {
      throw new IllegalArgumentException("url must not be null");
    }
    buildProps = loadPropsFrom(url);
  }

  /**
   * Return the value of a build property from the primary BuildInfo
   * @param prop the name of the property
   * @return the value of the property
   */
  public static String getBuildProperty(String prop) {
    return getFirstBuildInfo().getBuildPropertyInst(prop);
  }

  /**
   * Return the value of a build property
   * @param prop the name of the property
   * @return the value of the property
   */
  public String getBuildPropertyInst(String prop) {
    String s = buildProps.getProperty(prop);
    if (s != null && !s.startsWith("${")) {
      return s;
    }
    return null;
  }

  /**
   * Return all the build properties
   */
  public Properties getBuildPropertiesInst() {
    return buildProps;
  }

  /** Return a build info summary string containing the listed fields from
   * the primary BuildInfo */
  public static String getBuildInfoString(String ... fields) {
    return getFirstBuildInfo().getBuildInfoStringInst(fields);
  }

  /** Return the default build info summary string */
  public String getBuildInfoStringInst() {
    return getBuildInfoStringInst(BUILD_RELEASENAME,
				  BUILD_TIMESTAMP,
				  BUILD_HOST);
  }

  /** Return a build info summary string containing the listed fields.
   * Fields can be a field name, or <tt><i>label</i>:<i>field</i></tt> */
  public String getBuildInfoStringInst(String ... fields) {
    boolean needComma = false;

    StringBuffer sb = new StringBuffer();
    for (String field : fields) {
      if (sb.length() != 0) {
	if (needComma) {
	  sb.append(",");
	  needComma = false;
	}
	sb.append(" ");
      }
      // If foo:bar, foo is label, bar is field name
      if (field.indexOf(":") > 0) {
	String[] pair = field.split(":");
	sb.append(pair[0]);
	if (pair.length >= 2) {
	  field = pair[1];
	} else {
	  field = null;
	}
      }
      if (field != null) {
	switch (field) {
	case BUILD_NAME:
	case BUILD_ARTIFACT:
	case BUILD_VERSION:
	case BUILD_RELEASENAME:
	  String val1 = getBuildPropertyInst(field);
	  if (val1 != null) {
	    sb.append(val1);
	  }
	  break;
	case BUILD_TIMESTAMP:
	  sb.append("built ");
	  sb.append(getBuildPropertyInst(BUILD_TIMESTAMP));
	  break;
	case BUILD_HOST:
	  String buildHost = getBuildPropertyInst(BUILD_HOST);
	  if (buildHost != null) {
	    sb.append("on ");
	    sb.append(buildHost);
	  }
	  break;
	default:
	  String val2 = getBuildPropertyInst(field);
	  if (val2 != null) {
	    sb.append(field);
	    sb.append(": ");
	    sb.append(val2);
	    needComma = true;
	  }
	}
      }
    }
    return sb.toString();
  }

  /** Return the primary BuildInfo (first on the classpath) */
  public static synchronized BuildInfo getFirstBuildInfo() {
    if (FIRST == null) {
      Properties props = new Properties();
      try {
	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	InputStream istr = loader.getResourceAsStream(PROPERTY_RESOURCE);
	props.load(istr);
	log.debug2(props.toString());
	istr.close();
      } catch (Exception e) {
	log.warn("Can't load build info", e);
	props = new Properties();
      }
      FIRST = new BuildInfo(props);
    }
    return FIRST;
  }

  /** Return a list of all the BuildInfo found on the classpath */
  public static List<BuildInfo> getAllBuildInfo() {
    if (ALL == null) {
      List<BuildInfo> res = new ArrayList<>();
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      try {
	for (URL url : Collections.list(loader.getResources(PROPERTY_RESOURCE))) { 
	  try {
	    res.add(new BuildInfo(url));
	  } catch (Exception e) {
	    log.warn("Can't load build info: {}", url, e);
	  }
	}
      } catch (IOException e) {
	log.warn("Can't find build info files", e);
      }
      ALL = res;
    }
    return ALL;
  }

  private Properties loadPropsFrom(URL url) {
    Properties props = new Properties();
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      try (InputStream istr = url.openStream()) {
	props.load(istr);
	log.debug2("Loaded props: {}", props);
      }
    } catch (Exception e) {
      log.warn("Can't load build info", e);
      props = new Properties();
    }
    return props;
  }

  public String toString() {
    return "[BuildInfo: " + buildProps + "]";
  }
}
