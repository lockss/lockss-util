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

package org.lockss.log;

import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.layout.*;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Selects the pattern to use based on the prefix of the Logger name.
 */
@Plugin(name = "LoggerNamePatternSelector", category = Node.CATEGORY, elementType = PatternSelector.ELEMENT_TYPE, printObject = true)
public class LoggerNamePatternSelector implements PatternSelector {

  /**
   * Custom LoggerNamePatternSelector builder. Use the {@link LoggerNamePatternSelector#newBuilder() builder factory method} to create this.
   */
  public static class Builder implements org.apache.logging.log4j.core.util.Builder<LoggerNamePatternSelector> {

    @PluginElement("PatternMatch")
    private PatternMatch[] properties;
        
    @PluginBuilderAttribute("defaultPattern")
    private String defaultPattern;
        
    @PluginBuilderAttribute(value = "alwaysWriteExceptions") 
    private boolean alwaysWriteExceptions = true;
        
    @PluginBuilderAttribute(value = "disableAnsi")
    private boolean disableAnsi;
        
    @PluginBuilderAttribute(value = "noConsoleNoAnsi")
    private boolean noConsoleNoAnsi;

    @PluginConfiguration
    private Configuration configuration;

    @Override
    public LoggerNamePatternSelector build() {
      if (defaultPattern == null) {
        defaultPattern = PatternLayout.DEFAULT_CONVERSION_PATTERN;
      }
      if (properties == null || properties.length == 0) {
        LOGGER.warn("No marker patterns were provided with PatternMatch");
        return null;
      }
      return new LoggerNamePatternSelector(properties, defaultPattern,
                                           alwaysWriteExceptions, disableAnsi,
                                           noConsoleNoAnsi, configuration);
    }

    public Builder setProperties(final PatternMatch[] properties) {
      this.properties = properties;
      return this;
    }

    public Builder setDefaultPattern(final String defaultPattern) {
      this.defaultPattern = defaultPattern;
      return this;
    }

    public Builder setAlwaysWriteExceptions(final boolean alwaysWriteExceptions) {
      this.alwaysWriteExceptions = alwaysWriteExceptions;
      return this;
    }

    public Builder setDisableAnsi(final boolean disableAnsi) {
      this.disableAnsi = disableAnsi;
      return this;
    }

    public Builder setNoConsoleNoAnsi(final boolean noConsoleNoAnsi) {
      this.noConsoleNoAnsi = noConsoleNoAnsi;
      return this;
    }

    public Builder setConfiguration(final Configuration configuration) {
      this.configuration = configuration;
      return this;
    }

  }
    
  private final Map<String, PatternFormatter[]> formatterMap = new HashMap<>();

  private final Map<String, String> patternMap = new HashMap<>();

  private final PatternFormatter[] defaultFormatters;

  private final String defaultPattern;

  private static Logger LOGGER = StatusLogger.getLogger();


  /**
   * @deprecated Use {@link #newBuilder()} instead. This will be private in a future version.
   */
  @Deprecated
  public LoggerNamePatternSelector(final PatternMatch[] properties, final String defaultPattern,
                               final boolean alwaysWriteExceptions, final boolean noConsoleNoAnsi,
                               final Configuration config) {
    this(properties, defaultPattern, alwaysWriteExceptions, false, noConsoleNoAnsi, config);
  }

  private LoggerNamePatternSelector(final PatternMatch[] properties, final String defaultPattern,
                                final boolean alwaysWriteExceptions, final boolean disableAnsi,
                                final boolean noConsoleNoAnsi, final Configuration config) {
    final PatternParser parser = PatternLayout.createPatternParser(config);
    for (final PatternMatch property : properties) {

      try {
        final List<PatternFormatter> list = parser.parse(property.getPattern(), alwaysWriteExceptions,
                                                         disableAnsi, noConsoleNoAnsi);
        formatterMap.put(property.getKey(), list.toArray(new PatternFormatter[list.size()]));
        patternMap.put(property.getKey(), property.getPattern());
      } catch (final RuntimeException ex) {
        throw new IllegalArgumentException("Cannot parse pattern '" + property.getPattern() + "'", ex);
      }
    }
    try {
      final List<PatternFormatter> list = parser.parse(defaultPattern, alwaysWriteExceptions, disableAnsi,
                                                       noConsoleNoAnsi);
      defaultFormatters = list.toArray(new PatternFormatter[list.size()]);
      this.defaultPattern = defaultPattern;
    } catch (final RuntimeException ex) {
      throw new IllegalArgumentException("Cannot parse pattern '" + defaultPattern + "'", ex);
    }
  }

  @Override
  public PatternFormatter[] getFormatters(final LogEvent event) {
    final String name = event.getLoggerName();
    if (name == null) {
      return defaultFormatters;
    }
    for (final String key : formatterMap.keySet()) {
      if (name.startsWith(key)) {
        return formatterMap.get(key);
      }
    }
    return defaultFormatters;
  }

  /**
   * Creates a builder for a custom ScriptPatternSelector.
   *
   * @return a ScriptPatternSelector builder.
   */
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Deprecated, use {@link #newBuilder()} instead.
   * @param properties
   * @param defaultPattern
   * @param alwaysWriteExceptions
   * @param noConsoleNoAnsi
   * @param configuration
   * @return a new LoggerNamePatternSelector.
   * @deprecated Use {@link #newBuilder()} instead.
   */
  @Deprecated
  public static LoggerNamePatternSelector createSelector(
                                                     final PatternMatch[] properties,
                                                     final String defaultPattern,
                                                     final boolean alwaysWriteExceptions,
                                                     final boolean noConsoleNoAnsi,
                                                     final Configuration configuration) {
    final Builder builder = newBuilder();
    builder.setProperties(properties);
    builder.setDefaultPattern(defaultPattern);
    builder.setAlwaysWriteExceptions(alwaysWriteExceptions);
    builder.setNoConsoleNoAnsi(noConsoleNoAnsi);
    builder.setConfiguration(configuration);
    return builder.build();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (final Map.Entry<String, String> entry : patternMap.entrySet()) {
      if (!first) {
        sb.append(", ");
      }
      sb.append("key=\"").append(entry.getKey()).append("\", pattern=\"").append(entry.getValue()).append("\"");
      first = false;
    }
    if (!first) {
      sb.append(", ");
    }
    sb.append("default=\"").append(defaultPattern).append("\"");
    return sb.toString();
  }
}
