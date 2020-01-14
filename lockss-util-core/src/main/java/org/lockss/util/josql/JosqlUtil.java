/*

Copyright (c) 2000-2020 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

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
package org.lockss.util.josql;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import org.lockss.log.L4JLogger;

/**
 * Utility code to support the use of the JOSQL library.
 */
public class JosqlUtil {
  private static L4JLogger log = L4JLogger.getLogger();

  /**
   * Provides the full query that is equivalent to the simplified query for a
   * given class.
   * 
   * A full query looks like
   *   SELECT new org.lockss.ws.entities.SomeWsResult()
   *     {propertyName -> propertyName, ...} 
   *   FROM org.lockss.ws.entities.SomeWsSource
   *   WHERE ...
   * 
   * @param originalQuery   A String withe the original query.
   * @param sourceClassName A String with the fully-qualified class name of the
   *                        objects used as source in the query.
   * @param propertyNames   A {@code Collection<String>} with the names of the
   *                        properties in the query 'select' clause.
   * @param resultClassName A String with the fully-qualified class name of the
   *                        objects returned by the query.
   * @return a String with the full query.
   * @throws Exception if there are problems.
   */
  public static String createFullQuery(String originalQuery,
      String sourceClassName, Set<String> propertyNames, String resultClassName)
	  throws Exception {
    log.debug2("originalQuery = {}", originalQuery);
    log.debug2("sourceClassName = {}", sourceClassName);
    log.debug2("propertyNames = {}", propertyNames);
    log.debug2("resultClassName = {}", resultClassName);

    // Get the property names specified in the query 'select' clause.
    Collection<String> selectPropertyNames =
	getSelectPropertyNames(originalQuery, propertyNames);
    log.trace("selectPropertyNames = {}", selectPropertyNames);

    StringBuilder builder = new StringBuilder("SELECT ")
	.append(createSelectClause(resultClassName, selectPropertyNames))
	.append(" FROM ").append(sourceClassName);

    String whereClause = getWhereClause(originalQuery);
    log.trace("whereClause = {}", whereClause);

    if (whereClause != null && whereClause.length() > 0) {
      builder.append(" WHERE ").append(whereClause);
    }

    String fullQuery = builder.toString();
    log.debug2("fullQuery = {}", fullQuery);
    return fullQuery;
  }

  /**
   * Provides the individual properties used in the 'select' clause of a query.
   * 
   * @param query            A String with the query.
   * @param allPropertyNames A {@code Set<String>} with all the possible
   *                         property names.
   * @return a {@code Collection<String>} with the names of the properties in
   *         the 'select' clause of a query.
   * @throws Exception if there are problems.
   */
  private static Collection<String> getSelectPropertyNames(String query,
      Set<String> allPropertyNames) throws Exception {
    log.debug2("query = {}", query);
    log.debug2("allPropertyNames = {}", allPropertyNames);

    // Locate the beginning of the 'select' clause.
    String lcQuery = query.toLowerCase();
    String beginString = "select ";
    int beginIndex = lcQuery.indexOf(beginString) + beginString.length();
    log.trace("beginIndex = {}", beginIndex);

    // Handle a missing 'select' clause.
    if (beginIndex < beginString.length()) {
      String message = "No SELECT clause in the query '" + query + "'";
      log.debug(message);
      throw new Exception(message);
    }

    // Locate the end of the 'select' clause.
    String endString = "where ";
    int endIndex = lcQuery.indexOf(endString, beginIndex);
    log.trace("endIndex = {}", endIndex);

    // Handle a missing 'where' clause.
    if (endIndex < 0) {
      endIndex = query.length();
    }

    // Handle an empty 'select' clause.
    if (endIndex == beginIndex) {
      String message = "Empty SELECT clause in the query '" + query + "'";
      log.debug(message);
      throw new Exception(message);
    }

    String selectClause = query.substring(beginIndex, endIndex).trim();
    log.trace("selectClause = {}", selectClause);

    // Handle an empty 'select' clause.
    if (selectClause == null || selectClause.length() == 0) {
      String message = "Empty SELECT clause in the query '" + query + "'";
      log.debug(message);
      throw new Exception(message);
    }

    Collection<String> selectNames = null;

    // Check whether the query 'select' clause uses a wildcard.
    if ("*".equals(selectClause)) {
      // Yes: Use all of the property names.
      selectNames = new HashSet<String>(allPropertyNames);
    } else {
      // No: Extract the specified property names.
      // TODO: Replace with a call to the appropriate method in StringUtil
      // once StringUtil has been moved from the lockss-core project to the
      // lockss-util project.
      selectNames = breakAt(selectClause, ",");

      // Validate the property names extracted.
      validatePropertyNames(selectNames, allPropertyNames, query);
    }

    log.debug2("selectNames = {}", selectNames);
    return selectNames;
  }

  /**
   * Provides a query 'select' clause for a class and its property names.
   * 
   * @param className     A String with the fully-qualified class name.
   * @param propertyNames A {@code Collection<String>} with the names of the
   *                      properties in the query 'select' clause.
   * @return a String with the query 'select' clause.
   */
  private static String createSelectClause(String className,
      Collection<String> propertyNames) {
    log.debug2("className = {}", className);
    log.debug2("propertyNames = {}", propertyNames);

    // Initialize the 'select' clause with the class name.
    StringBuilder builder =
	new StringBuilder("new ").append(className).append("() {");

    boolean isFirst = true;

    // Loop through all the property names that must appear in the 'select'
    // clause.
    for (String name : propertyNames) {
      if (!isFirst) {
	builder.append(", ");
      } else {
	isFirst = false;
      }

      // Append this property name to the 'select' clause.
      builder.append(name).append(" -> ").append(name);
    }

    // Finish the 'select' clause.
    String selectClause = builder.append("}").toString();
    log.debug2("selectClause = {}", selectClause);
    return selectClause;
  }

  /**
   * Provides the 'where' clause of a query.
   * 
   * @param query
   *          A String with the query containing the 'where' clause.
   * @return A String with the query 'where' clause.
   * @throws LockssWebServicesFault
   */
  private static String getWhereClause(String query) throws Exception {
    log.debug2("query = {}", query);
    String whereClause = "";

    // Locate the beginning of the 'where' clause.
    String beginString = " where ";
    int beginIndex =
	query.toLowerCase().indexOf(beginString) + beginString.length();
    log.trace("beginIndex = {}", beginIndex);

    // Check whether a 'where' clause exists.
    if (beginIndex >= beginString.length()) {
      // Yes: Extract the contents of the 'where' clause.
      whereClause = query.substring(beginIndex).trim();

      // Handle an empty 'where' clause.
      if (whereClause == null || whereClause.length() == 0) {
	String message = "Empty WHERE clause in the query '" + query + "'";
	log.debug(message);
	throw new Exception(message);
      }
    }

    log.debug2("whereClause = {}", whereClause);
    return whereClause;
  }

  /**
   * Validates property names.
   * 
   * @param propertyNames      A {@code Collection<String>} with the names of
   *                           the properties to be validated.
   * @param validPropertyNames A {@code Set<String>} with the valid property
   *                           names.
   * @param query              A String with the query.
   * @throws Exception if the validation fails.
   */
  private static void validatePropertyNames(Collection<String> propertyNames,
      Set<String> validPropertyNames, String query) throws Exception {
    log.debug2("propertyNames = {}", propertyNames);
    log.debug2("validPropertyNames = {}", validPropertyNames);
    log.debug2("query = {}", query);

    Set<String> invalidNames = new HashSet<String>();

    // Loop through all the property names to be validated.
    for (String name : propertyNames) {
      // Check whether the name of this property is not among those that are
      // valid.
      if (!validPropertyNames.contains(name)) {
	// Yes: Place it in the list of names that are not valid.
	invalidNames.add(name);
	log.debug("Property '" + name + "' not in set " + validPropertyNames);
      }
    }

    // Check whether any property names were found invalid.
    if (invalidNames.size() > 0) {
      // Yes: Report the problem.
      StringBuilder builder = new StringBuilder("Invalid name(s) ");
      boolean isFirst = true;

      for (String name : invalidNames) {
	if (!isFirst) {
	  builder.append(", ");
	} else {
	  isFirst = false;
	}

	builder.append("'").append(name).append("'");
      }

      String message = builder.append(" in the query '").append(query)
	  .append("'").toString();
      log.debug(message);
      throw new Exception(message);
    }
  }

  /**
   * Breaks a string at a separator string.
   * 
   * @param s   A String containing zero or more occurrences of the separator
   *            string.
   * @param sep A String with the separator string.
   */
  // TODO: Remove the method below (mostly copied from StringUtil) once
  // StringUtil has been moved from the lockss-core project to the lockss-util
  // project.
  private static Vector<String> breakAt(String s, String sep) {
    Vector<String> res = new Vector<>();
    int len;
    if (s == null || (len = s.length()) == 0) {
      return res;
    }
    int maxItems = Integer.MAX_VALUE;
    for (int pos = 0; maxItems > 0; maxItems-- ) {
      int end = s.indexOf(sep, pos);
      if (end == -1) {
	if (pos > len) {
	  break;
	}
	end = len;
      }
      if (pos != end) {
	String str = s.substring(pos, end);
	str = str.trim();
	if (str.length() != 0) {
	  res.addElement(str);
	}
      }
      pos = end + sep.length();
    }
    return res;
  }
}
