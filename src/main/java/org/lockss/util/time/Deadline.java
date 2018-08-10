/*

Copyright (c) 2000-2018, Board of Trustees of Leland Stanford Jr. University
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

package org.lockss.util.time;

import java.text.*;
import java.util.*;

import org.lockss.util.io.LockssSerializable;
import org.lockss.util.lang.LockssRandom;
import org.slf4j.*;


/** Deadline represents a time (at which some operation must complete).
 */
public class Deadline implements Comparable, LockssSerializable {
 
  /** A long time from now. */
  public static final Deadline MAX =
    new Deadline(new ConstantDate(TimeBase.MAX));

  /** An expired Deadline. */
  public static final Deadline EXPIRED = new Deadline(new ConstantDate(0));

  private static final Logger log = LoggerFactory.getLogger(Deadline.class);

  private static LockssRandom random = null;
  
  protected Date expiration;
  
  protected long duration;                        // only for testing
  
  protected transient List<Callback> subscribers; // those who wish to be notified
                                                  // if/when this Deadline's duration
                                                  // changes

  /** Create a Deadline that expires at the specified Date, with the
   * specified duration.  Done this way so factory methods don't risk a
   * timer tick between getting the current time, and the constructor
   * computing the duration, which would then be different from what was
   * specified.
   * @param at the Date
   * @param duration the duration
   * @param checkReasonable if true, log a warning if the Deadline is
   * either in the past or unreasonably far in the future.
   */
  protected Deadline(Date at, long duration, boolean checkReasonable) {
    expiration = at;
    this.duration = duration;
    if (checkReasonable) {
      checkReasonable();
    }
  }

  /** Create a Deadline that expires at the specified Date, with the
   * specified duration.  Done this way so factory methods don't risk a
   * timer tick between getting the current time, and the constructor
   * computing the duration, which would then be different from what was
   * specified.
   * @param at the Date
   * @param duration the duration
   */
  protected Deadline(Date at, long duration) {
    this(at, duration, true);
  }

  /** Create a Deadline that expires at the specified Date.
   * @param at the Date
   */
  protected Deadline(Date at, boolean checkReasonable) {
    this(at, at.getTime() - nowMs(), checkReasonable);
  }

  /** Create a Deadline that expires at the specified Date.
   * @param at the Date
   */
  protected Deadline(Date at) {
    this(at, false);
  }

  /** Create a Deadline that expires at the specified date.
   * @param at the time in ms
   */
  protected Deadline(long at, boolean checkReasonable) {
    this(new Date(at), checkReasonable);
  }

  /** Create a Deadline that expires at the specified date.
   * @param at the time in ms
   */
  protected Deadline(long at) {
    this(at, true);
  }

  /** Create a Deadline that expires in <code>duration</code> milliseconds.
   * @param duration in ms
   * @return the Deadline
   */
  public static Deadline in(long duration) {
    return new Deadline(new Date(nowMs() + duration), duration);
  }

  /** Create a Deadline representing the specified Date.
   * @param at the Date
   * @return the Deadline
   */
  public static Deadline at(Date at) {
    return new Deadline(at);
  }

  /** Create a Deadline representing the specified date/time.
   * @param at date/time in milliseconds from the epoch.
   * @return the Deadline
   */
  public static Deadline at(long at) {
    return new Deadline(at);
  }

  /** Create a Deadline representing the specified date/time.  This is
   * similar to {@link #at(long)} but suppresses the sanity check.  It is
   * intended to be used when loading or restoring a saved deadline.
   * @param at date/time in milliseconds from the epoch.
   * @return the Deadline
   */
  public static Deadline restoreDeadlineAt(long at) {
    return new Deadline(at, false);
  }

  /** Create a Deadline representing a random time between
   * <code>earliest</code> (inclusive) and <code>latest</code> (exclusive).
   * The random time is uniformly distributed between the endpoints.
   * @param earliest The earliest possible time
   * @param latest The latest possible time
   * @return the Deadline
   */
  public static Deadline atRandomRange(long earliest, long latest) {
    return new Deadline(earliest + getRandom().nextLong(latest - earliest));
  }

  /** Create a Deadline representing a random time between now (inclusive)
   * and <code>before</code> (exclusive).  The random time is uniformly
   * distributed.
   * @param before The time before which the deadline should expire
   * @return the Deadline
   */
  public static Deadline atRandomBefore(long before) {
    return atRandomRange(nowMs(), before);
  }

  /** Create a Deadline representing a random time between
   * <code>earliest</code> (inclusive) and <code>latest</code> (exclusive).
   * The random time is uniformly distributed between the endpoints.
   * @param earliest The earliest possible time
   * @param latest The latest possible time
   * @return the Deadline
   */
  public static Deadline atRandomRange(Deadline earliest, Deadline latest) {
    return atRandomRange(earliest.getExpirationTime(),
			 latest.getExpirationTime());
  }

  /** Create a Deadline representing a random time between now (inclusive)
   * and <code>before</code> (exclusive).  The random time is uniformly
   * distributed.
   * @param before The time before which the deadline should expire
   * @return the Deadline
   */
  public static Deadline atRandomBefore(Deadline before) {
    return atRandomRange(nowMs(), before.getExpirationTime());
  }

  /** Create a Deadline representing a random time between
   * <code>minDuration</code> (inclusive) and <code>maxDuration</code>
   * (exclusive) milliseconds from now.  The random time is uniformly
   * distributed between the endpoints.
   * @param minDuration The minimum duration, in milliseconds.
   * @param maxDuration The maximum duration, in milliseconds.
   * @return the Deadline
   */
  public static Deadline inRandomRange(long minDuration, long maxDuration) {
    return atRandomRange(nowMs() + minDuration, nowMs() + maxDuration);
  }

  /** Create a Deadline representing a random time between now (inclusive)
   * and <code>maxDuration</code> (exclusive) milliseconds from now.  The
   * random time is uniformly distributed.
   * @param maxDuration The maximum duration, in milliseconds.
   * @return the Deadline
   */
  public static Deadline inRandomBefore(long maxDuration) {
    return inRandomRange(0, maxDuration);
  }

  /** Create a Deadline representing a random time deviating from the
   * meanDuration by at most delta.  The random time is uniformly distributed.
   * @param meanDuration The mean duration, in milliseconds.
   * @param delta the max deviation
   * @return the Deadline
   */
  public static Deadline inRandomDeviation(long meanDuration, long delta) {
    return inRandomRange(meanDuration - delta, meanDuration + delta);
  }

//   /** Return a timer whose duration is a random, normally distrubuted value
//    * whose mean is <code>meanDuration</code> and standard deviation
//    * <code>stddev</code>.  */
//   public Deadline withinOf(double stddev, long meanDuration) {
//     super(meanDuration + (long)(stddev * getRandom().nextGaussian()));
//   }

  protected void checkReasonable() {
    if (TimeBase.isSimulated()) {
      // don't complain during testing
      return;
    }
    if (duration < minDelta ||
	(duration > maxDelta &&
	 getExpirationTime() != TimeBase.MAX)) {
      log.warn("Unreasonable deadline: " + expiration,
		  new Throwable());
    }
  }

  protected static long minDelta = 0;
  protected static long maxDelta = (4 * TimeUtil.WEEK);

  /** Set the "reasonable" Deadline range.
   * @param maxInPast longest reasonable time in past (as a positive number
   * of milliseconds)
   * @param maxInFuture longest reasonable time in future
   */
  public static void setReasonableDeadlineRange(long maxInPast,
						long maxInFuture) {
    minDelta = -maxInPast;
    maxDelta = maxInFuture;
  }

  /**
   * Return the absolute expiration time, in milliseconds
   * @return the expriation time
   */
  public synchronized long getExpirationTime() {
    return expiration.getTime();
  }

  /**
   * Return the number of milliseconds by which this Deadline exceeds the
   * other.
   * @return the differecne between the two Deadlines.
   */
  public long minus(Deadline other) {
    return expiration.getTime() - other.getExpirationTime();
  }

  /**
   * Return the expiration time as a Date
   * @return the Date
   */
  public Date getExpiration() {
    return expiration;
  }

  /** Return the time remaining until expiration, in milliseconds.  This
   * method should not be used to obtain a duration to sleep, nor a timeout
   * duration for Object.wait(); use {@link #getSleepTime()} for that.
   * @return remaining time
   */
  public synchronized long getRemainingTime() {
    return (expired() ? 0 : expiration.getTime() - nowMs());
  }

  /** Return the time to sleep, in milliseconds.  This method should be
   * used instead of {@link #getRemainingTime()} to determine how long to
   * sleep.  It differs from getRemainingTime() in two important ways:
   * <ul><li><b>It always returns at least 1</b>, even if the expiration
   * time has already passed.  This is because the value is often used as
   * the timeout argument to Object.wait(), which interprets 0 as "no
   * timeout", not "immediate timeout".  <li>When running in simulated time
   * it returns a small number so that any sleeps will complete quickly, as
   * it is impossible to predict how long it will be until simulated time
   * will be advanced to the deadline.</ul>
   * @return sleep time suitable to pass to Object.wait() or Thread.sleep()
   */
  public synchronized long getSleepTime() {
    if (TimeBase.isSimulated()) {
      return (expired() ? 1 : 5);
    } else {
      long res = getRemainingTime();
      return res >= 1 ? res : 1;
    }
  }

  /**
   * Return true iff the timer has expired
   * @return true if expired
   */
  public synchronized boolean expired() {
    return expiration.getTime() <= TimeBase.nowMs();
  }

  /**
   * Return true iff this deadline expires before <code>other</code>.
   * @param other the other Deadline
   * @return true if expires earlier
   */
  public synchronized boolean before(Deadline other) {
    return expiration.before(other.expiration);
  }

  /**
   * Return the earlier of two deadlines
   * @param d1 first Deadline
   * @param d2 second Deadline
   * @return d1 if it is before d2, else d2
   */
  public static synchronized Deadline earliest(Deadline d1, Deadline d2) {
    return d1.before(d2) ? d1 : d2;
  }

  /**
   * Return the later of two deadlines
   * @param d1 first Deadline
   * @param d2 second Deadline
   * @return d2 if d1 is before it, else d1
   */
  public static synchronized Deadline latest(Deadline d1, Deadline d2) {
    return d1.before(d2) ? d2 : d1;
  }

  /** Cause the deadline to expire immediately */
  public void expire() {
    synchronized (this) {
      expiration.setTime(0);
    }
    changed();
  }

  /**
   * Change expiration time to time n.
   * @param millis new expire time
   */
  public void expireAt(long millis) {
    synchronized (this) {
      expiration.setTime(millis);
      duration = millis - nowMs();
    }
    changed();
  }

  /**
   * Change expiration time to n milliseconds from now.
   * @param millis new expire interval
   */
  public void expireIn(long millis) {
    synchronized (this) {
      expiration.setTime(nowMs() + millis);
      duration = millis;
    }
    changed();
  }

  /**
   * Add <code>delta</code> milliseconds to the deadline.
   * @param delta new ms to add
   */
  public void later(long delta) {
    expireAt(expiration.getTime() + delta);
  }

  /**
   * Subtract <code>delta</code> from the deadline.
   * @param delta new ms to remove
   */
  public void sooner(long delta) {
    expireAt(expiration.getTime() - delta);
  }

  /** Register a callback that will be called if/when the Deadline's
   * duration changes (by a call to expire(), sooner(), etc.)
   * @param callback the Callback
   */
  public synchronized void registerCallback(Callback callback) {
    if (subscribers == null) {
      subscribers = new LinkedList<Callback>();
    }
    subscribers.add(callback);
  }

  /**
   * Unregister a change callback
   * @param callback the Callback
   */
  public synchronized void unregisterCallback(Callback callback) {
    subscribers.remove(callback);
  }

  /** Call deadlineChanged() method of all subscribers.  NB: This must not
   * be synchronized, nor called from a synchronized method  */
  protected void changed() {
    // Make copy so can iterate unsynchronized
    List<Callback> subs = getSubscriberSnapshot();
    if (subs != null) {
      for (Callback cb : subs) {
	// tk - run these in a separate thread
	try {
	  cb.changed(this);
	} catch (Exception e) {
	  log.error("Callback threw", e);
	}
      }
    }
  }

  private synchronized List<Callback> getSubscriberSnapshot() {
    return subscribers == null ? null : new ArrayList<Callback>(subscribers);
  }

  protected static Date now() {
    return TimeBase.nowDate();
//     return new Date();
  }

  protected static long nowMs() {
    return TimeBase.nowMs();
//     return System.currentTimeMillis();
  }

  protected static LockssRandom getRandom() {
    if (random == null) {
      random = new LockssRandom();
    }
    return random;
  }

  // Comparable interface

  public int compareTo(Object o) {
    Deadline other = (Deadline)o;
    long thisTime = expiration.getTime();
    long otherTime = other.getExpirationTime();
    return (thisTime < otherTime ? -1 : (thisTime == otherTime ? 0 : 1));
  }

  public boolean equals(Object o) {
    return (o instanceof Deadline) &&
      expiration.getTime() == ((Deadline)o).getExpirationTime();
  }

  /** Return a suitable hashCode  */
  public int hashCode() {
    long t = expiration.getTime();
    return (int)t ^ (int)(t >> 32);
  }

  private static final DateFormat dfsec = new SimpleDateFormat("HH:mm:ss");
  private static final DateFormat dfms = new SimpleDateFormat("HH:mm:ss.SSS");

  public String toString() {
    if (expiration.getTime() == TimeBase.MAX) {
      return "[deadline: never]";
    }
    boolean isSim = TimeBase.isSimulated();
    StringBuffer sb = new StringBuffer();
    sb.append("[deadline: dur ");
    sb.append(isSim ? Long.toString(duration)
	      : TimeUtil.timeIntervalToString(duration));
    sb.append(", at ");
    if (isSim) {
      sb.append("sim ");
      sb.append(expiration.getTime());
    } else {
      sb.append(dfsec.format(expiration));
    }
    sb.append("]");
    return sb.toString();
  }

  public String shortString() {
    long expMs = getExpirationTime();
    if (expMs == TimeBase.MAX) {
      return "never";
    }
    if (TimeBase.isSimulated()) {
      return Long.toString(expMs);
    } else {
      StringBuffer sb = new StringBuffer();
      long nowMs = TimeBase.nowMs();
      long fromNow = expMs - nowMs;
      long dayDiff = (expMs / TimeUtil.DAY) - (nowMs / TimeUtil.DAY);
      if (fromNow >= (-2 * TimeUtil.SECOND) &&
	  fromNow <= (2 * TimeUtil.SECOND)) {
	sb.append(dfms.format(expiration));
      } else {
	sb.append(dfsec.format(expiration));
      }
      if (dayDiff > 0) {
	sb.append("+");
	sb.append(dayDiff);
	sb.append("D");
      } else if	(dayDiff < 0) {
	sb.append("-");
	sb.append(-dayDiff);
	sb.append("D");
      }
      return sb.toString();
    }
  }

  /**
   * The Deadline.Callback interface defines the
   * method that will be called if/when a deadline changes.
   */
  public interface Callback {
    /**
     * Called when the deadline's duration is changed.
     * @param deadline  the Deadline that changed.
     */
    public void changed(Deadline deadline);
  }

  /**
   * A Deadline.Callback that interrupts a thread.  Example use:<pre>
    Deadline.InterruptCallback cb = new Deadline.InterruptCallback();
    try {
      deadline.registerCallback(cb);
      while (queue.isEmpty() && !deadline.expired()) {
	this.wait(deadline.getSleepTime());
      }
    } finally {
      cb.disable();
      deadline.unregisterCallback(cb);
    }<pre>
  */
  public static class InterruptCallback implements Callback {
    private Thread thread;

    public InterruptCallback() {
      thread = Thread.currentThread();
    }
    public synchronized void changed(Deadline deadline) {
      if (thread != null) thread.interrupt();
    }
    /** Assumed to be called from the thread that no longer wants to be
     * interrupted if the deadline changes */
    public synchronized void disable() {
      thread = null;
      // Guarantee that thread's interrupted status is false when this
      // returns.  This is necessary in the case whee a deadline was
      // changed just after the wait() returns, but before disable is
      // called.  If the interrup status were allowed to persist, an
      // ensuing IO operation would be erroneously interrupted.  Note that
      // this won't (and shouldn't) interfere with any InterruptedException
      // that's already in the process of being thrown.
      Thread.interrupted();
    }
  }

  /** Sleep, returning when the deadline is reached, or possibly earlier.
   * In order to guarantee that the deadline has actually been reached, this
   * must be called in a <code>while (!deadline.expired()) { ... }</code>
   * loop.
   * @throws InterruptedException if either the timer duration is changed
   * (<i>eg</i>, by {@link #expire()} or {@link #sooner(long)}) or the
   * thread is otherwise interrupted.
   */
  public void sleep() throws InterruptedException {
    if (expired()) {
      return;
    }
    InterruptCallback cb = new InterruptCallback();
    long nap;
    try {
      registerCallback(cb);
      while (!expired()) {
	Thread.sleep(getSleepTime());
      }
    } finally {
      cb.disable();
      unregisterCallback(cb);
    }
  }

  // for testing
  protected long getDuration() {
    return duration;
  }
}
