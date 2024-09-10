/*

Copyright (c) 2000-2020, Board of Trustees of Leland Stanford Jr. University
All rights reserved.

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

package org.lockss.util.os;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileStore;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.*;
import org.lockss.util.lang.EncodingUtil;
import org.lockss.log.L4JLogger;
import org.lockss.util.net.IPAddr;
import org.lockss.util.storage.StorageInfo;
import org.lockss.util.time.TimeUtil;
import org.slf4j.*;

/** Utilities to communicate with platform to get info or take action not
 * possible from Java */
public class PlatformUtil {
  
  /** Should be set to the list of allowed TCP ports, based on
   * platform- (and group-) dependent packet filters */
  public static final String SYSPROP_UNFILTERED_TCP_PORTS = "org.lockss.platform.unfilteredTcpPorts";
  public static final String SYSPROP_UNFILTERED_UDP_PORTS = "org.lockss.platform.unfilteredUdpPorts";

  /** Java tmpdir system property.  Java caches this the first time
   * File.createTempFile() is called, so changes to the system
   * property at runtime may not take effect.  Moreover, it should
   * <b>never</b> be set to a dir that may be deleted during that JVM
   * invocation, as even if it's reset when that dir is deleted,
   * createTempFile() may still try to create files there and will
   * fail */
  public static final String SYSPROP_JAVA_IO_TMPDIR = "java.io.tmpdir";

  /** Alternate system property used within LOCKSS code to avoid
   * problems with {@link #SYSPROP_JAVA_IO_TMPDIR} */
  public static final String SYSPROP_LOCKSS_TMPDIR = "org.lockss.tmpdir";

  public static final String SYSPROP_LOCKSS_OS_NAME = "lockss.os.name";
  public static final String SYSPROP_OS_NAME = "os.name";

  public static final String SYSPROP_PLATFORM_HOSTNAME = "org.lockss.platformHostname";

  private static final L4JLogger log = L4JLogger.getLogger();
  
  public enum DiskSpaceSource { Java, DF };

  /** Determines how disk space statistics (total, free, available)
   * are obtained.  If <tt>Java</tt>, the builtin Java library methods
   * are used, if <tt>DF</tt>, the <tt>df</tt> utility is run in a sub
   * process.  The former is normally preferred but returns incorrect
   * results on filesystems larger than 8192PB.  The latter currently
   * works on filesystems up to 8192EB, but is slower and could
   * conceivably fail.  If running with lockss-core, this System
   * property is set from the config param of the same name. */
  public static final String SYSPROP_DISK_SPACE_SOURCE =
    "org.lockss.platform." + "diskSpaceSource";

  public static final DiskSpaceSource DEFAULT_DISK_SPACE_SOURCE =
    DiskSpaceSource.Java;

  private static final DecimalFormat percentFmt = new DecimalFormat("0%");

  public static final File[] FILE_ROOTS = File.listRoots();

  static PlatformUtil instance;

  /** Return the singleton PlatformInfo instance */
  // no harm if happens in two threads, so not synchronized
  public static PlatformUtil getInstance() {
    if (instance == null) {
      String os = System.getProperty(SYSPROP_LOCKSS_OS_NAME);
      if (StringUtils.isEmpty(os)) {
	os = System.getProperty(SYSPROP_OS_NAME);
      }
      if ("linux".equalsIgnoreCase(os)) {
	instance = new Linux();
      }
      if ("openbsd".equalsIgnoreCase(os)) {
	instance = new OpenBSD();
      }
      if ("force_macos".equalsIgnoreCase(os)) {
	instance = new MacOS();
      }
      if ("force_windows".equalsIgnoreCase(os)) {
        instance = new Windows();
      }
      if ("force_solaris".equalsIgnoreCase(os)) {
        instance = new Solaris();
      }
      if ("force_none".equalsIgnoreCase(os)) {
	instance = new PlatformUtil();
      }
      if (SystemUtils.IS_OS_MAC_OSX) {
	instance = new MacOS();
      }
      if (SystemUtils.IS_OS_WINDOWS) {
        instance = new Windows();
      }
      if (/*SystemUtils.IS_OS_SOLARIS*/ "sunos".equalsIgnoreCase(os)) {
        instance = new Solaris();
      }
      if (instance == null) {
	log.warn("No OS-specific PlatformInfo for '" + os + "'");
	instance = new PlatformUtil();
      }
    }
    return instance;
  }

  /**
   * <p>
   * Return the system temp directory.
   * </p>
   * 
   * @see #SYSPROP_JAVA_IO_TMPDIR
   * @see #SYSPROP_LOCKSS_TMPDIR
   */
  public static String getSystemTempDir() {
    String lockssTmpDir = System.getProperty(SYSPROP_LOCKSS_TMPDIR);
    return lockssTmpDir != null ? lockssTmpDir
      : System.getProperty(SYSPROP_JAVA_IO_TMPDIR);
  }

  /** Return the current working dir name */
  public static String getCwd() {
    return new File(".").getAbsoluteFile().getParent();
  }

  /**
   * @see #SYSPROP_UNFILTERED_TCP_PORTS
   */
  public List<String> getUnfilteredTcpPorts() {
    return Arrays.asList(StringUtils.split(System.getProperty(SYSPROP_UNFILTERED_TCP_PORTS, ""), ";"));
  }

  /**
   * @see #SYSPROP_UNFILTERED_UDP_PORTS
   */
  public List<String> getUnfilteredUdpPorts() {
    return Arrays.asList(StringUtils.split(System.getProperty(SYSPROP_UNFILTERED_UDP_PORTS, ""), ";"));
  }

  /** Return the PID of the executing process, if possible.
   * @return PID of executing process, which is not necessarily the top
   * java process.
   * @throws UnsupportedException if unsupported on current platform
   */
  public int getPid() throws UnsupportedException {
    throw new UnsupportedException("No OS-independent way to find PID");
  }

  /** Return the PID of the top process of this JVM, if possible.
   * @return PID of top java process, suitable for killing or sending
   * SIGQUIT, etc.
   * @throws UnsupportedException if unsupported on current platform
   */
  public int getMainPid() throws UnsupportedException {
    throw new UnsupportedException("No OS-independent way to find main PID");
  }

  /** Request a thread dump of this JVM using the default method, which is
   * currently {@link #threadDumpSignal(boolean)}.  Dump is output to
   * System.out.  If unsupported on this platform, logs an info message.
   * The alpine jvm does not support jcmd so we use #threadDum
   * @param wait if true will attempt to wait until dump is complete before
   * returning
   */
  public void threadDump(boolean wait) {
    // replace jcmd with signal call since alpine doesn't support jcmd
    //threadDumpJcmd(wait, System.out);
    threadDumpSignal(wait);
  }

  /** Request a thread dump of this JVM by sending it SIGQUIT.  Dump is
   * output to JVM's stderr, which may not be the same as System.err.  Logs
   * a message if unsupported on this platform or fails.  The maven
   * surefire plugin can't handle the low level JVM output this produces.
   * @param wait if true will attempt to wait until dump is complete before
   * returning
   */
  public void threadDumpSignal(boolean wait) {
    int pid;
    try {
      pid = getMainPid();
    } catch (UnsupportedException e) {
      log.info("Thread dump requested, not supported in this environment", e);
      return;
    }
    // thread dump is more likely to appear on System.err than
    // wherever the current log is.
    System.err.println("Thread dump at " + new Date());
    String cmd = "kill -QUIT " + pid;
    try {
      Process p = rt().exec(cmd);
//     InputStream is = p.getInputStream();
//     org.mortbay.util.IO.copy(is, System.out);
      p.waitFor();
      if (wait) {
	try {
	  Thread.sleep(TimeUtil.SECOND);
	} catch (InterruptedException ignore) {}
      }
    } catch (IOException e) {
      log.error("Couldn't exec '" + cmd + "'", e);
    } catch (InterruptedException e) {
      log.error("waitFor()", e);
    }
  }

  /** Request a thread dump of this JVM by invoking jcmd and capturing the
   * output.  Dump is output to System.out.  Logs a message if unsupported
   * on this platform or fails.
   * @param wait if true will attempt to wait until dump is complete before
   * returning
   */
  public void threadDumpJcmd(boolean wait) {
    threadDumpJcmd(wait, System.out);
  }

  /** Request a thread dump of this JVM by invoking jcmd and capturing the
   * output.  Logs a message if unsupported on this platform or fails.
   * @param ostrm PrintStream to which the thread dump is printed
   * @param wait if true will attempt to wait until dump is complete before
   * returning
   */
  public void threadDumpJcmd(boolean wait, PrintStream ostrm) {
    int pid;
    try {
      pid = getMainPid();
    } catch (UnsupportedException e) {
      log.info("Thread dump requested, not supported in this environment", e);
      return;
    }
    ostrm.println("Thread dump at " + new Date());
    // TODO - change this if jcmd does not work
    String cmd = "jcmd " + pid + " Thread.print";
    try {
      Process p = rt().exec(cmd);
      InputStream is = p.getInputStream();
      IOUtils.copy(is, ostrm);
      p.waitFor();
      if (wait) {
	try {
	  Thread.sleep(TimeUtil.SECOND);
	} catch (InterruptedException ignore) {}
      }
    } catch (IOException e) {
      log.error("Error printing thread dump, couldn't exec '" + cmd + "'", e);
    } catch (InterruptedException e) {
      log.error("Error printing thread dump: waitFor()", e);
    }
  }

  /**
   * Determines whether file system is case-sensitive for operations that
   * depend on case sensitivity
   * @return <code>true</code> if the file system is case-sensitive
   */
  public boolean isCaseSensitiveFileSystem() {
    return true;
  }
  
  /**
   * Return the length of the longest filename that can be created in the
   * filesystem.  This is the length of a single directory or filename; for
   * the maximum pathname see maxPathname().  (This should really be
   * filesystem-dependent, not just OS-dependent.) */
  public int maxFilename() {
    return 255;
  }

  /**
   * Return the length of the longest pathname that can be created in the
   * filesystem.  This is total length of an absolute path, including all
   * parent dirs.  (This should really be filesystem-dependent, not just
   * OS-dependent.) */
  public int maxPathname() {
    return 4096;
  }

  /**
   * Return true if the platform includes scripting support */
  public boolean hasScriptingSupport() {
    return true;
  }
  
  /**
   * Return true if the exception was caused by a full filesystem
   */
  public boolean isDiskFullError(IOException e) {
    return StringUtils.indexOfIgnoreCase(e.getMessage(), "No space left on device") >= 0;
  }
  
  static Runtime rt() {
    return Runtime.getRuntime();
  }

  /** Return disk usage below path, in bytes */
  public long getDiskUsage(String path) {
    String cmd = "du -k -s " + path;
    if (log.isTraceEnabled()) log.trace("cmd: " + cmd);
    try {
      Process p = rt().exec(cmd);
      Reader rdr =
	new InputStreamReader(new BufferedInputStream(p.getInputStream()),
			      EncodingUtil.DEFAULT_ENCODING);
      String s;
      try {
	s = IOUtils.toString(rdr);
	int exit = p.waitFor();
	rdr.close();
	// any unreadable dirs cause exit=1; process if got any output
      } catch (IOException e) {
	log.error("Couldn't read from '" + cmd + "'", e);
	return -1;
      }
      List<String> lines = Arrays.asList(StringUtils.split(s, '\n'));
      if (log.isTraceEnabled()) {
	for (String str : lines) {
	  log.trace("DU: " + str);
	}
      }
      if (lines == null || lines.isEmpty()) {
	return -1;
      }
      int brk = StringUtils.indexOfAny(lines.get(0), ' ', '\t', '\n');
      String ks = (brk == -1) ? lines.get(0) : lines.get(0).substring(0, brk);
      return Long.parseLong(ks) * 1024;
    } catch (Exception e) {
      log.warn("DU(" + path + ")", e);
      return -1;
    }
  }

  /** Get disk usage info from Java.  This fails on filesystems that
   * are 8192PB or larger, because Java returns the size, in bytes, in
   * a long.
   */
  public DF getJavaDF(String path) {
    File f = null;
    try {
      f = new File(path).getCanonicalFile();
    } catch (IOException e) {
      f = new File(path).getAbsoluteFile();
    }
    // mirror the df behaviour of returning null if path doesn't exist
    if (!f.exists()) {
      return null;
    }
    DF df = new DF();
    df.path = path;
    df.size = f.getTotalSpace() / 1024;
    df.avail = f.getUsableSpace() / 1024;
    df.used = df.size - (f.getFreeSpace() /1024);
    df.percent = Math.ceil((df.size -df.avail) * 100.00 / df.size);
    df.percentString =  String.valueOf(Math.round(df.percent)) + "%";
    df.percent /= 100.00;
    df.fs = null;
    try {
      df.mnt = mountOf(f);
    } catch (IOException e) {
      log.warn("Error finding mount point of: {}", path);
      df.mnt = "Unknown";
    }
    df.source = DiskSpaceSource.Java;
    if (log.isTraceEnabled()) log.trace(df.toString());
    return df;
  }

  public static String mountOf(File f) throws IOException {
    return mountOf(f.toPath());
  }

  public static String mountOf(Path p) throws IOException {
    FileStore fs = Files.getFileStore(p);
    Path temp = p.toAbsolutePath();
    Path mountp = temp;

    while( (temp = temp.getParent()) != null && fs.equals(Files.getFileStore(temp)) ) {
      mountp = temp;
    }
    return mountp.toString();
  }

  /** Get disk space statistics for the filesystem containing the
   * path, either directly from Java or by invoking 'df', according to
   * {@value PARAM_} */

  public DF getDF(String path) {
    DiskSpaceSource source = DEFAULT_DISK_SPACE_SOURCE;
    String propval = System.getProperty(SYSPROP_DISK_SPACE_SOURCE);
    if (!StringUtils.isEmpty(propval)) {
      try {
        source = Enum.valueOf(DiskSpaceSource.class, propval);
      } catch (IllegalArgumentException e) {
        log.warn("Illegal value for System property {}: {}",
                 SYSPROP_DISK_SPACE_SOURCE, propval);
      }
    }
    switch (source) {
    case Java:
    default:
      return getJavaDF(path);
    case DF:
      return getPlatformDF(path);
    }
  }


  /** Get disk usage info by running 'df' */
  public DF getPlatformDF(String path) {
    return getPlatformDF(path, "-k -P");
  }

  public DF getPlatformDF(String path, String dfArgs) {
    String cmd = "df " + dfArgs + " " + path;
    if (log.isTraceEnabled()) log.trace("cmd: " + cmd);
    try {
      Process p = rt().exec(cmd);
      Reader rdr =
	new InputStreamReader(new BufferedInputStream(p.getInputStream()),
			      EncodingUtil.DEFAULT_ENCODING);
      String s;
      try {
	s = IOUtils.toString(rdr);
	int exit = p.waitFor();
	rdr.close();
	if (exit != 0) {
	  if (log.isDebugEnabled()) log.debug("cmd: " + cmd + " exit code " + exit);
	}
      } catch (IOException e) {
	log.error("Couldn't read from '" + cmd + "'", e);
	return null;
      }

      List<String> lines = Arrays.asList(StringUtils.split(s, '\n'));
      if (log.isTraceEnabled()) {
	for (String str : lines) {
	  log.trace("DF: " + str);
	}
      }
      if (lines == null || lines.size() < 2) {
	return null;
      }
      return makeDFFromLine(path, lines.get(1));
    } catch (Exception e) {
      log.warn("DF(" + path + ")", e);
      return null;
    }
  }

  DF makeDFFromLine(String path, String line) {
    String[] tokens = new String[6];
    StringTokenizer st = new StringTokenizer(line, " \t");
    int ntok = 0;
    while (st.hasMoreTokens()) {
      String tok = st.nextToken();
      if (ntok > 5) {
	break;
      }
      tokens[ntok++] = tok;
    }
    if (ntok < 6) {
      return null;
    }
    DF df = new DF();
    df.path = path;
    df.fs = tokens[0];
    df.size = getLong(tokens[1]);
    df.used = getLong(tokens[2]);
    df.avail = getLong(tokens[3]);
    df.percentString = tokens[4];
    df.mnt = tokens[5];
    df.source = DiskSpaceSource.DF;
    try {
      df.percent = percentFmt.parse(df.percentString).doubleValue();
    } catch (ParseException e) {
    }
    if (log.isTraceEnabled()) log.trace(df.toString());
    return df;
  }

  int getInt(String s) throws NumberFormatException{
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      log.warn("Illegal number in DF output: " + s);
      return 0;
    }
  }

  long getLong(String s) throws NumberFormatException{
    try {
      return Long.parseLong(s);
    } catch (NumberFormatException e) {
      log.warn("Illegal number in DF output: " + s);
      return 0;
    }
  }

  /**
   * <p>Convenience call to the current {@link PlatformUtil}
   * instance's {@link #updateFileAtomically} method.</p>
   * @param updated An updated version of the target file.
   * @param target  A target file, which is to be updated by the
   *                updated file.
   * @return True if and only if the update succeeded; false
   *         otherwise.
   * @see #updateFileAtomically
   */
  public static boolean updateAtomically(File updated, File target) {
    return getInstance().updateFileAtomically(updated, target);
  }
  
  /**
   * <p>Attempts to update <code>target</code> atomically by renaming
   * <code>updated</code> to <code>target</code> if possible or by
   * using any similar filesystem-specific mechanism.</p>
   * <p>Atomicity is not guaranteed. An atomic update is more likely
   * if the files are in the same directory. Even if they are, some
   * platforms may not support atomic renames.</p>
   * <p>When this method returns, <code>updated</code>will exist if
   * and only if the update failed.</p>
   * @param updated An updated version of the target file.
   * @param target  A target file, which is to be updated by the
   *                updated file.
   * @return True if and only if the update succeeded; false
   *         otherwise.
   * @see #updateAtomically
   * @see PlatformUtil.Windows#updateFileAtomically
   */
  public boolean updateFileAtomically(File updated, File target) {
    try {
      return updated.renameTo(target); // default
    }
    catch (SecurityException se) {
      // Just log and rethrow
      log.warn("Security exception reported in atomic update", se);
      throw se;
    }
  }
  
  public static String getLocalHostname() {
    String host = System.getProperty(SYSPROP_PLATFORM_HOSTNAME);
    if (host == null) {
      try {
        host = IPAddr.getLocalHost().getHostName();
      } catch (UnknownHostException ex) {
        log.error("Couldn't determine localhost.", ex);
        return null;
      }
    }
    return host;
  }
  
  public static double parseDouble(String str) {
    if (isBuggyDoubleString(str)) {
      throw new NumberFormatException("Buggy double string");
    }
    return Double.parseDouble(str);
  }

  // Double.parseDouble("2.2250738585072012e-308") loops.  Disallow it and
  // variants, such as:
  //
  //   0.00022250738585072012e-304 (decimal point placement) (and similar
  //   strings with the decimal point shifted farther to the left,
  //   including far enough that the exponent goes to zero)
  //   22.250738585072012e-309 (decimal point placement)
  //   00000000002.2250738585072012e-308 (leading zeros)
  //   2.225073858507201200000e-308 (trailing zeros)
  //   2.2250738585072012e-00308 (leading zeros in the exponent)
  //   2.2250738585072012997800001e-308 (superfluous digits beyond digit
  //   17)

  // Match the bad sequence of digits, allowing for an embedded decimal
  // point, followed by a large negative three digit exponent
  private static Pattern BUGGY_DOUBLE_PAT1 =
    Pattern.compile("2\\.?2\\.?2\\.?5\\.?0\\.?7\\.?3\\.?8\\.?5\\.?8\\.?5\\.?0\\.?7\\.?2\\.?0\\.?1\\.?2\\d*[eE]-0*[23]\\d\\d");

  // Match the bad sequence of digits preceded by a decimal point and at
  // least 100 zeroes.
  private static Pattern BUGGY_DOUBLE_PAT2 =
    Pattern.compile("\\.0{100,}22250738585072012");

  public static boolean isBuggyDoubleString(String str) {
    Matcher m1 = BUGGY_DOUBLE_PAT1.matcher(str);
    if (m1.find()) {
      return true;
    }
    Matcher m2 = BUGGY_DOUBLE_PAT2.matcher(str);
    return m2.find();
 }

  /** Linux implementation of platform-specific code */
  public static class Linux extends PlatformUtil {
    // offsets into /proc/<n>/stat
    static final int STAT_OFFSET_PID = 0;
    static final int STAT_OFFSET_PPID = 3;
    static final int STAT_OFFSET_FLAGS = 8;
    // flag bits
    static final int PF_FORKNOEXEC = 0x40;	// forked but didn't exec

    /** Get PID of current process */
    public int getPid() throws UnsupportedException {
      return getProcPid();
    }

    /** Get PID of main java process */
    public int getMainPid() throws UnsupportedException {
      String pid;
      String ppid = "self";
      int flags;
      do {
	List<String> v = getProcStats(ppid);
	pid = v.get(STAT_OFFSET_PID);
	ppid = v.get(STAT_OFFSET_PPID);
	flags = getInt(v, STAT_OFFSET_FLAGS);
//  	log.debug("getMainPid: pid = " + pid + ", ppid = " + ppid +
//  		  ", flags = 0x" + Integer.toHexString(flags));
      } while ((flags & PF_FORKNOEXEC) != 0);
      return Integer.parseInt(pid);
    }

    /** Get PID from linux /proc/self/stat */
    private int getProcPid() throws UnsupportedException {
      List<String> v = getMyProcStats();
      String pid = v.get(STAT_OFFSET_PID);
      return Integer.parseInt(pid);
    }

    // return int from string in vector
    private int getInt(List<String> v, int pos) {
      String s = v.get(pos);
      return Integer.parseInt(s);
    }

    /** Get stat vector of this java process from /proc/self/stat .
   * Read the stat file with java code so the executing process (self) is
   * java. */
    List<String> getMyProcStats() throws UnsupportedException {
      return getProcStats("self");
    }

    /** Get stat vector for specified process from /proc/<n>/stat .
     * @param pid the process for which to get stats, or "self"
     * @return vector of strings of values in stat file
     */
    public List<String> getProcStats(String pid) throws UnsupportedException {
      String filename = "/proc/" + pid + "/stat";
      try {
	Reader r = new FileReader(new File(filename));
	String s = IOUtils.toString(r);
	return Arrays.asList(StringUtils.split(s, ' '));
      } catch (FileNotFoundException e) {
	throw new UnsupportedException("Can't open " + filename, e);
      } catch (IOException e) {
	throw new UnsupportedException("Error reading " + filename, e);
      }
    }

  }

  /** OpenBSD implementation of platform-specific code */
  public static class OpenBSD extends PlatformUtil {
    // offsets into /proc/<n>/status
    static final int STAT_OFFSET_CMD = 1;
    static final int STAT_OFFSET_PID = 1;
    static final int STAT_OFFSET_PPID = 2;

    /** Get PID of current process */
    public int getPid() throws UnsupportedException {
      return getProcPid();
    }

    /** Get PID of main java process */
    public int getMainPid() throws UnsupportedException {
      // tk - not sure how to find top process on OpenBSD.
      // This is right for green threads only.
      return getProcPid();
    }

    /** Get PID from OpenBSD /proc/curproc/status */
    private int getProcPid() throws UnsupportedException {
      List<String> v = getMyProcStats();
      String pid = v.get(STAT_OFFSET_PID);
      return Integer.parseInt(pid);
    }

    /** Get stat vector of this java process from /proc/curproc.status .
     * Read the stat file with java code so the executing process (self) is
     * java. */
    List<String> getMyProcStats() throws UnsupportedException {
      return getProcStats("curproc");
    }

    /** Get stat vector for specified process from /proc/<n>/status .
     * @param pid the process for which to get stats, or "self"
     * @return vector of strings of values in stat file
     */
    public List<String> getProcStats(String pid) throws UnsupportedException {
      String filename = "/proc/" + pid + "/status";
      try {
	Reader r = new FileReader(new File(filename));
	String s = IOUtils.toString(r);
	return Arrays.asList(StringUtils.split(s, ' '));
      } catch (FileNotFoundException e) {
	throw new UnsupportedException("Can't open " + filename, e);
      } catch (IOException e) {
	throw new UnsupportedException("Error reading " + filename, e);
      }
    }
  }

  public static class Solaris extends PlatformUtil {
    public String dfArgs = "-k";

    public DF getPlatformDF(String path) {
      return (super.getPlatformDF(path, dfArgs));
    }

    public int getPid() throws UnsupportedException {
      throw new UnsupportedException("Don't know how to get PID on Solaris");
    }

    /** Get PID of main java process */
    public int getMainPid() throws UnsupportedException {
      throw new UnsupportedException("Don't know how to get PID on Solaris");
    }
    
    public Vector getProcStats(String pid) throws UnsupportedException {
      throw new
	UnsupportedException("Don't know how to get proc state on Solaris");
    }
  }


  public static class MacOS extends PlatformUtil {
    public String dfArgs = "-k -I";

    /**
     * Determines whether file system is case-sensitive for operations that
     * depend on case sensitivity
     * @return <code>true</code> if the file system is case-sensitive
     */
    public boolean isCaseSensitiveFileSystem() {
      return false; // MacOS FS is not case sensitive
    }

    public int maxPathname() {
      return 1016;
    }

    public DF getPlatformDF(String path) {
      return (super.getPlatformDF(path, dfArgs));
    }

    public int getPid() throws UnsupportedException {
      // see http://stackoverflow.com/questions/35842/process-id-in-java
      String pidprop = System.getProperty("pid");
      if (!StringUtils.isEmpty(pidprop)) {
        try {
          int pid = Integer.parseInt(pidprop);
          return pid;
        } catch (NumberFormatException ex) {
          System.setProperty("pid", "");
          // shouldn't happen, so fall through and reset it
        }
      }

      // Note: may fail in some JVM implementations
      // therefore fallback has to be provided

      // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
      final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
      final int index = jvmName.indexOf('@');

      if (index < 1) {
          // part before '@' empty (index = 0) / '@' not found (index = -1)
        throw new UnsupportedException("Don't know how to get PID on MacOS");
      }

      try {
          pidprop = jvmName.substring(0, index);
          int pid = Integer.parseInt(pidprop);
          System.setProperty("pid", pidprop);
          return pid;
      } catch (NumberFormatException e) {
        throw new UnsupportedException("Don't know how to get PID on MacOS");
      }
    }

    /** Get PID of main java process */
    public int getMainPid() throws UnsupportedException {
      return getPid();
    }
    
    public Vector getProcStats(String pid) throws UnsupportedException {
      throw new
	UnsupportedException("Don't know how to get proc state on MacOS");
    }
  }


  public static class Windows extends PlatformUtil {
    
    public DF getPlatformDF(String path, String dfArgs) {
      String cmd = "df " + dfArgs + " " + path;
      if (log.isTraceEnabled()) log.trace("cmd: " + cmd);
      	try {
          Process p = rt().exec(cmd);
          Reader rdr =
            new InputStreamReader(new BufferedInputStream(p.getInputStream()),
                  EncodingUtil.DEFAULT_ENCODING);
          String s;
          try {
            s = IOUtils.toString(rdr);
            // ignore exit status because GnuWin32 'df' reports
            // "df: `NTFS': No such file or directory" even though
            // it seems to operate correctly
            int exit = p.waitFor();
            rdr.close();
          } catch (IOException e) {
            log.error("Couldn't read from '" + cmd + "'", e);
            return null;
          }

          List<String> lines = Arrays.asList(StringUtils.split(s, '\n'));
          if (log.isTraceEnabled()) {
            for (String str : lines) {
              log.trace("DF: " + str);
            }
          }
          if (lines == null || lines.size() < 2) {
            return null;
	  }
	  return makeDFFromLine(path, lines.get(1));
      	} catch (Exception e) {
      	  log.warn("DF(" + path + ")", e);
          return null;
	}
    }
    
    /**
     * Determines whether file system is case-sensitive for operations that
     * depend on case sensitivity
     * @return <code>true</code> if the file system is case-sensitive
     */
    public boolean isCaseSensitiveFileSystem() {
      return false; // Windows FS is not case sensitive
    }
    
    public int maxPathname() {
      return 260;
    }

    public synchronized boolean updateFileAtomically(File updated, File target) {
      try {
        File saveTarget = null;

        // Move target out of the way if necessary
        if (target.exists()) {
          // Create temporary file
          saveTarget = new File(target.getParent(),
                                target.getName() + ".windows." + System.currentTimeMillis());

          // Rename target
          if (!target.renameTo(saveTarget)) {
            log.error("Windows platform: "
                      + target.toString()
                      + " exists but could not be renamed to "
                      + saveTarget.toString());
            return false; // fail unconditionally
          }
        }

        // Update target
        if (updated.renameTo(target)) {
          // Delete original if needed if the update is successful
          if (saveTarget != null) {
            if (!saveTarget.delete()) {
              log.warn("Windows platform: "
                          + saveTarget.toString()
                          + " could not be deleted at the end of an update");
            }
          }
          
          return true; // succeed
        }
        else {
          // Log an error message if the update is unsuccessful
          log.error("Windows platform: "
                    + updated.toString()
                    + " could not be renamed to "
                    + target.toString());
          
          // Try to restore the original (unlikely to succeed)
          if (!saveTarget.renameTo(target)) {
            log.error("Windows platform: "
                      + target.toString()
                      + " could not be restored from "
                      + saveTarget.toString());
          }
          
          return false; // fail
        }
      }
      catch (SecurityException se) {
        // Log and rethrow
        log.warn("Windows Platform: security exception reported in atomic update", se);
        throw se;
      }
    }
  }

  /** Struct holding disk space info (from df) */
  public static class DF {
    protected String path;
    protected String fs;
    protected long size;
    protected long used;
    protected long avail;
    protected String percentString;
    protected double percent = -1.0;
    protected String mnt;
    protected DiskSpaceSource source;

    public static DF makeThreshold(long minFreeMB, double minFreePercent) {
      DF df = new DF();
      df.avail = minFreeMB * 1024;
      df.percent = minFreePercent == 0.0 ? -1.0 : 1.0 - minFreePercent;
      return df;
    }

    /**
     * Create a DF using the information in a StorageInfo object.
     * 
     * @param storageInfo A StorageInfo with the source data.
     * @return a DF populated wih the data from the StorageInfo object.
     */
    public static DF fromStorageInfo(StorageInfo storageInfo) {
      DF df = new DF();

      if (storageInfo != null) {
        df.mnt = storageInfo.getName();
        df.size = storageInfo.getSizeKB();
        df.used = storageInfo.getUsedKB();
        df.avail = storageInfo.getAvailKB();
        df.percentString = storageInfo.getPercentUsedString();
        df.percent = storageInfo.getPercentUsed();
      } else {
	throw new NullPointerException(
	    "Platform.DF received a null StorageInfo");
      }

      return df;
    }

    public String getFs() {
      return fs;
    }
    public String getPath() {
      return path;
    }
    public long getSize() {
      return size;
    }
    public long getUsed() {
      return used;
    }
    public long getAvail() {
      return avail;
    }
    public String getPercentString() {
      return percentString;
    }
    public double getPercent() {
      return percent;
    }
    public String getMnt() {
      return mnt;
    }
    public DiskSpaceSource getSource() {
      return source;
    }
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[DF: ");
      if (fs != null) {
	sb.append("fs: ");
	sb.append(fs);
	sb.append(", ");
      }
      if (path != null) {
	sb.append("path: ");
	sb.append(path);
	sb.append(", ");
      }
      if (mnt != null) {
	sb.append("mnt: ");
	sb.append(mnt);
	sb.append(", ");
      }
      sb.append("size: ");
      sb.append(size);
      sb.append("K, ");
      sb.append("used: ");
      sb.append(used);
      sb.append("K, ");
      sb.append("avail: ");
      sb.append(avail);
      sb.append("K, ");
      sb.append(percentString);
      sb.append(" used]");
      return sb.toString();
    }
    public boolean isFullerThan(DF threshold) {
      if (threshold.getAvail() > 0 &&
	  threshold.getAvail() >= getAvail()) {
	return true;
      }
      if (threshold.getPercent() > 0 &&
	  threshold.getPercent() <= getPercent()) {
	return true;
      }
      return false;
    }
  }

  /** Exception thrown if no implementation is available for the current
   * platform, or a platform-dependent error occurs.
   * In the case of an error, the original exception is available. */
  public static class UnsupportedException extends Exception {
    Throwable e;

    public UnsupportedException(String msg) {
      super(msg);
    }

    public UnsupportedException(String msg, Throwable e) {
      super(msg);
      this.e = e;
    }

    /** Return the nested Throwable */
    public Throwable getError() {
      return e;
    }
  }
}
