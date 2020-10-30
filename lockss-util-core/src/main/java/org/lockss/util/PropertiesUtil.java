/*

Copyright (c) 2020 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lockss.log.L4JLogger;

/**
 * Properties utilities.
 */
public class PropertiesUtil {
  private final static L4JLogger log = L4JLogger.getLogger();

  /**
   * Converts a list of equals-separated key-value pairs into a map.
   * 
   * @param propertiesList A List<String> with the equals-separated key-value
   *                       pairs.
   * @return a {@code Map<String, String>} with the resulting properties map.
   */
  public static Map<String, String> convertListToMap(
      List<String> propertiesList) {
    log.debug2("propertiesList = {}", propertiesList);

    Map<String, String> resultMap = new HashMap<String, String>();

    if (propertiesList != null && propertiesList.size() > 0) {
      for (String property : propertiesList) {
	log.trace("property = {}", property);

	int keyValueSeparator = property.trim().indexOf("=");
	log.trace("keyValueSeparator = {}", keyValueSeparator);

	if (keyValueSeparator > 0) {
	  String key = property.substring(0, keyValueSeparator).trim();
	  log.trace("key = {}", key);

	  String value = property.substring(keyValueSeparator + 1).trim();
	  log.trace("value = {}", value);

	  resultMap.put(key, value);
	} else {
	  throw new IllegalArgumentException(
	      "Missing key/value separator in property '" + property + "'");
	}
      }
    }

    log.debug2("resultMap = {}", resultMap);
    return resultMap;
  }

  /**
   * Converts an array of equals-separated key-value pairs into a map.
   * 
   * @param propertiesArray A String[] with the equals-separated key-value
   *                        pairs.
   * @return a {@code Map<String, String>} with the resulting properties map.
   */
  public static Map<String, String> convertArrayToMap(String[] propertiesArray)
  {
    return convertListToMap(Arrays.asList(propertiesArray));
  }
}
