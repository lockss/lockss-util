/**
<pre>

This package provides a framework for LOCKSS logging, built on top of
log4j, as well as binary compatibility with the legacy LOCKSS logger.

Configuration:

Logs are controlled by a chain of checked-in log4j config files and
optionally a local file, a System property, and LOCKSS config params.

Log4j config files are loded in this order, with later settings overriding
eariler settings.  The exact merge rules are at
https://logging.apache.org/log4j/2.x/manual/configuration.html#CompositeConfiguration

  - log4j2-lockss.xml (in lockss-util:src/main/resources/) contains
    standard Logger and Appender setup.  No other project should contain a
    file with this name.

  - log4j2-lockss-test.xml (in lockss-util:src/test/resources/) is
    used by the logger tests.  If you need special logger setup for tests
    in some other project, put them in a file with this name in the
    project's test/resources/ dir.

  - log4j2-lockss-project.xml is intended for downstream project-wide
    config.  It should be checked into the project's main/resources/ dir.

  - If the environemt variable LOG4J_LOCKSS_CONFIG is set, the file it
    points to be will be loaded last.  This is intended for user-specific
    config.

log4j2-lockss.xml defines several properties to make it easier for
downstream configs to make certain changes without repeating entire Logger
or Appender elements:

  &lt;Properties&gt;
    &lt;!-- Default log level --&gt;
    &lt;Property name="root.level"&gt;INFO&lt;/Property&gt;

    &lt;!-- Default log dir --&gt;
    &lt;Property name="log.dir"&gt;logs&lt;/Property&gt;

    &lt;!-- Default pattern for log output to console --&gt;
    &lt;Property name="layout.console"&gt;
      %ld{HH:mm:ss.SSS} [%t] %-5level %logger{36}: %msg%lex%n
    &lt;/Property&gt;

    &lt;!-- Default pattern for log output to console from org.lockss.**
         loggers.  Uses simple logger name as we're familiar with these
         names. --&gt;
    &lt;Property name="layout.console.lockss"&gt;
      %ld{HH:mm:ss.SSS} [%t] %-5level %logger{1}: %msg%lex%n
    &lt;/Property&gt;

    &lt;!-- Patterns for file output and lockss file output default to
         corresponding console patterns. --&gt;
    &lt;Property name="layout.file"&gt;
      ${layout.console}
    &lt;/Property&gt;
    &lt;Property name="layout.file.lockss"&gt;
      ${layout.console.lockss}
    &lt;/Property&gt;

  &lt;/Properties&gt;


The sysprop org.lockss.defaultLogLevel (-Dloglevel= in maven) overrides the
default log level set in the log4j config file(s).

LOCKSS config params (in lockss.txt, lockss.opt, expert config, etc.) of
the form org.lockss.log.&lt;name&gt;.level can be used to override the log levels
set in the log4j config files.  &lt;name&gt; can be simple or fq.
org.lockss.log.default.level sets the default (root) level (overrides
sysprop).

LOCKSS loggers created with Logger.getLogger() or Logger.getLogger(Class)
now get fully qualified names.  Those created with Logger.getLogger(String)
still have an unqualified name if that's what's supplied.  The default log
layout includes only the last component so they'll all look the same.
Their log level can be set using config params with either the unqualified
name or the fq name.  Levels for all loggers (not just those obtained with
Logger.getLogger() or L4JLogger.getLogger() can be set with LOCKSS config
params


Using loggers:

New code should use the log4j API
(https://logging.apache.org/log4j/2.x/log4j-api/apidocs/org/apache/logging/log4j/Logger.html),
via the L4JLogger subclass which includes the additional LOCKSS log levels
(siteError, siteWarning, debug2 and debug3 (same as trace).

  private static L4JLogger log = L4JLogger.getLogger();
  ...
  log.debug2("msg: {}", arg);

The constants L4JLevel.SITE_ERROR, SITE_WARNING, DEBUG2, and DEBUG3 are
analogous to log4j's Level.XXX .


Implementation:

- Does not support the old LogTargets.  Targets (Appenders) must be
  configured using log4j directly.

- Define log4j custom log levels corresponding to Debug2, Debug3,
  SiteError, SiteWarning.

- Extend the use of the custom levels to code that uses log4j directly, via
  a subclass of org.apache.logging.log4j.core.Logger which implements the
  full suite of logging methods corresponding to the custom levels.

- Allow configuration of logger levels via the LOCKSS config mechanism.

- Duplicate the feature whereby stack traces can be included in the log
  only when they're logged at a sufficiently high level, or when the logger
  is set at a sufficiently low level.

- Provide a framework for users, and downstream projects, to customize the
  logging setup.  A sequence of config files is loaded:

  - log4j2-lockss-util.xml has a standard Logger and Appender setup and
    should not be overridden.

  - log4j2-lockss-test.xml is used by the logger tests in this package.
    main/resources/log4j2-lockss-test.xml is empty; the logger setup needed
    for the tests is in test/resources/log4j2-lockss-test.xml, which gets
    loaded when the tests are running because appears on the classpath
    before the main classes/files.

  - log4j2-lockss-project.xml is intended for downstream projects.
    main/resources/log4j2-project.xml is empty; if a downstream project
    includes one in its resource directory it will normally be first on the
    classpath so will be loaded.

  - the file pointed to by the environemt variable LOG4J_LOCKSS_CONFIG, if
    it is set.

- log4j2.component.properties is used to specify:

  - The list of config files to be loaded (above).

  - Factories required by org.lockss.log.Logger & friends:

    - The factory class L4JLoggerContextSelector causes the LoggerContext
      to be a L4JLoggerContext.

    - L4JContextDataInjector makes the configured values for
      o.l.log.stackTraceLevel and stackTraceSeverity available to the
      PatternConverter that needs them.

- The plugin L4JThrowablePatternConverter is a PatternConverter bound to
  %lex, which is similar to %ex but which suppresses the stack trace if
  neither the message level nor the logger's configured level satisfies
  stackTraceSeverity and stackTraceLevel, respectively.  The Logger's level
  is obtained from the ThreadContextStack, which L4JLogger pushes and pops
  around log calls.

- The plugin L4JDatePatternConverter is a PatternConverter bound to %ld,
  which outputs the date like %d and appends " (sim XXX)" when the LOCKSS
  TimeBase is in simulated mode.

- The custom log levels can be used directly with the log4j api in one of
  two ways:

  - by calling the log() method and passing in one of the L4JLevel
    constants.

  - All the loggers returned by LogManager.getLogger() are instances of
    L4JLogger, which implements the full suite of debug2(), siteWarning(),
    etc.  methods, as well as (inheriting) the standard methods.  In order
    to call those methods the result of LogManager.getLogger() must be
    downcast to L4JLogger.  Convenience methods are provided to do that:

      L4JLogger log = L4JLogger.getLogger();  // log name is caller's class name
      L4JLogger log = L4JLogger.getLogger(Class);  // log name is class name
      L4JLogger log = L4JLogger.getLogger("name"); // log name is "name"

      L4JLogger log = L4JLogger.cast(LogManager.getFormatterLogger());

    If log4j has not been configured as expected (with the required
    factories), the logger won't actually be a L4JLogger, and a
    ClassCaseException will result.  The cast() and other convenience
    methods above issue a more informative message if that happens.

- Classpath issues:

  - Most config files are loaded as standard resources, using the first one
    found on the classpath.  lockss-util should be as early as possible in
    the dependency list of dependent projects to decrease the chance that
    files from other packages will be found instead of ours.

 (- log4j2.component.properties works differently.  Log4j finds all
    occurrences on the classpath and loads them all, in order.  The result
    is that the *last* value set for each property takes effect.)

  - For similar reasons, log4j adapters for other logging systems are
    included in lockss-util's pom, even though they're not used by lockss
    util.  If it were left to downstream projects that need them to include
    them in their poms they'd be later in the classpath, leading to a
    greater chance that some possibly less desirable adapter would be used
    instead.

    - slf4j, log4j 1.2, Commons Logging bridges require only their jar on
      the classpath.

    - For CXF logging (used by lockss-core web services SOAP framework),
      only the config file to cause it to use log4j is here
      (src/main/resources/META-INF/cxf/org.apache.cxf.Logger).  Downstream
      projects can include cxf-bundle with no classpath problems.

    An exception is java.util.logging.  log4j-jul-2.x requires the sysprop
    java.util.logging.manager to be set to
    org.apache.logging.log4j.jul.LogManager at startup.  Not bothering with
    that unless there's some indication it's needed.

  - None of our config files (specified in log4j2.component.properties) is
    named log4j2.xml.  It's likely other packages contain a file with that
    name; avoiding it means we're not solely dependent on classpath
    ordering to ensure our files are used.

  - However, *not* having a file named log4j2.xml is also a problem. Spring
    Boot runs its own log initialization which, by default, forces
    log4j2.xml to be loaded.  We disable that initialization where possible
    (by setting the System property
    org.springframework.boot.logging.LoggingSystem = none), but that can't
    be counted on because it depends on the execution environment.  In
    order to make things work when their log init does run, log4j2.xml is
    symlinked to our main config file, log4j2-lockss.xml (in
    main/resources).  That way, if the standard file name does get loaded
    by some mechanism we can't control, it will at least be ours.  This
    seems to work well enough with Spring Boot.
</pre>
*/
package org.lockss.log;
