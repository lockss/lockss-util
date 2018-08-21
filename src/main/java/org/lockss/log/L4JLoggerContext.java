/*

Copyright (c) 2000-2018 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.log;

import java.net.URI;
import java.util.*;

import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.message.MessageFactory;


/**
 * This LoggerContext:<ul>
 *
 * <li>arranges for Loggers returned by LogManager.getLogger() to be
 * instances of L4JLogger.</li>
 *
 * <li>maintains a map (nameMap) of logger short name to fully-qualified
 * name, added to as loggers are created.  This facilitates setting log
 * levels via the LOCKSS config, using only the short name.</li>
 *
 * <li>maintains a map (levelMap) of logger name to LOCKSS-configured log
 * level, newly-created loggers can get their level set if their short name
 * matches an existing configured level. Keys are whatever name appeared in
 * the LOCKSS config, generally but not necessarily a short name.</li>
 *
 * <li>levelMap also contains {@value PARAM_STACKTRACE_LEVEL} and {@value
 * PARAM_STACKTRACE_SEVERITY}, because this is an easy place for {@link
 * L4JContextDataInjector} to find them.</li>
 * </ul>
 */
public class L4JLoggerContext extends LoggerContext {

  // Each short name may map to more than one fq name.  <i>Eg</i>, creating
  // loggers <tt>org.lockss.log.Logger</tt> and
  // <tt>org.lockss.util.Logger</tt> results in <tt>Logger</tt> being
  // mapped to both fq names
  private SetValuedMap<String,String> nameMap = new HashSetValuedHashMap<>();

  private Map<String,Level> levelMap = null;

  public L4JLoggerContext(final String name,
			  final Object externalContext,
			  final URI configLocn) {
    super(name, externalContext, configLocn);
  }

  public L4JLoggerContext(final String name) {
    super(name);
  }

  @Override
  protected org.apache.logging.log4j.core.Logger
    newInstance(final LoggerContext ctx,
		final String name,
		final MessageFactory messageFactory) {
    updateNameMap(name);
    org.apache.logging.log4j.core.Logger res =
      new L4JLogger(ctx, name, messageFactory);
    return res;
  }

  /** Return Collection of fully-qualified names of loggers whose last
   * component matches <tt>shortName</tt>.
   */
  public synchronized Collection<String> getFQNames(String shortName) {
    Set<String> res = nameMap.get(shortName);
    return res != null
      ? Collections.unmodifiableSet(res)
      : Collections.emptyList();
  }

  /** Set the configured logger levels. */
  public void setLevelMap(Map<String,Level> levelMap) {
    this.levelMap = levelMap;
  }

  /** Return the configured logger levels (and {@value
 * PARAM_STACKTRACE_LEVEL} and {@value PARAM_STACKTRACE_SEVERITY}). */
  public Map<String,Level> getLevelMap() {
    return levelMap;
  }

  /** Add a shortName -> fqName mapping for a newly created logger.  If
   * this is the first time we've see this fqName (why wouldn't it be?),
   * and the shortname have a configured level, set the level for the new
   * fq name. */
  private synchronized void updateNameMap(String fqName) {
    if (fqName.indexOf(".") > 0) {
      String shortName = fqName.substring(fqName.lastIndexOf('.')+1);
      if (nameMap.put(shortName, fqName) && levelMap != null) {

	Level shortLevel = levelMap.get(shortName);
	if (shortLevel != null) {
	  Configurator.setLevel(fqName, shortLevel);
	}
      }
    }
  }
}
