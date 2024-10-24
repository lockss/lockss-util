/*

Copyright (c) 2000-2022, Board of Trustees of Leland Stanford Jr. University

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

package org.lockss.util.rest.repo.util;

import org.lockss.log.L4JLogger;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;

/**
 * Maintains a semaphore per key for the duration it is needed.
 *
 * @param <T> The class type of the keys.
 */
public class SemaphoreMap<T> {
  private final static L4JLogger log = L4JLogger.getLogger();

  /**
   * Map from key to semaphore and its usage count.
   */
  private Map<T, SemaphoreAndCount> locks = new HashMap<>();

  /**
   * Internal struct containing a semaphore and its usage count.
   */
  private static class SemaphoreAndCount {
    private Semaphore sm;
    private int count;
    private Throwable context;

    /**
     * Constructor.
     */
    public SemaphoreAndCount() {
      this.sm = new Semaphore(1, true);
      this.count = 0;
    }

    /**
     * Returns the semaphore.
     *
     * @return The {@link Semaphore}.
     */
    public Semaphore getSemaphore() {
      return sm;
    }

    /**
     * Increments the usage counter of this semaphore.
     */
    public void incrementCounter() {
      count++;
    }

    /**
     * Decrements the usage counter of this semaphore and returns it.
     *
     * @return A {@code long} containing the usage count.
     */
    public int decrementCounter() {
      return --count;
    }

    public void setContext() {
      context = new Throwable();
    }

    public void eraseContext() {
      context = null;
    }

    public Throwable getContext() {
      return context;
    }

  }

  /**
   * Closeable object representing lock, to allow use in
   * try-with-resources
   */
  public class SemaphoreLock implements Closeable {
    private T key;

    /**
     * Constructor.
     */
    public SemaphoreLock(T key) {
      this.key = key;
    }

    @Override
    public void close() throws IOException {
      releaseLock(key);
    }
  }

  /**
   * Returns the existing internal {@link SemaphoreAndCount} of a key, or creates and returns a new one.
   *
   * Called only from code synchronized on locks map
   *
   * @param key The key of the {@link SemaphoreAndCount} to return.
   * @return The {@link SemaphoreAndCount} of the key.
   */
  private SemaphoreAndCount getSemaphoreAndCount(T key) {
    // Get semaphore and count from internal map
    synchronized (locks) {
      SemaphoreAndCount snc = locks.get(key);

      // Create a new semaphore and count if one did not exist in the map
      if (snc == null) {
        snc = new SemaphoreAndCount();
        locks.put(key, snc);
      }
      return snc;
    }
  }

  /**
   * Acquires the lock (or blocks until it can be acquired) for the semaphore of the provided key.
   *
   * @param key The key of the semaphore to acquire the lock of.
   * @throws InterruptedException Thrown if the thread is interrupted while waiting to acquire.
   */
  public SemaphoreLock getLock(T key) throws InterruptedException {
    SemaphoreAndCount snc;

    // Get the semaphore and increase its usage count
    synchronized (locks) {
      snc = getSemaphoreAndCount(key);
      snc.incrementCounter();
    }

    try {
      // May block until it can be acquired
//       snc.getSemaphore().acquire();
      while (!snc.getSemaphore().tryAcquire(1, TimeUnit.HOURS)) {
        if (snc.getContext() != null) {
          log.fatal("Lock not acquired in an hour: {}", key, snc.getContext());
        } else {
          log.fatal("Lock not acquired in an hour *and* we dnn't think it's held by anybody: {}", key);
        }
        snc.getSemaphore().release();
      }
      snc.setContext();
      log.trace("Acquired lock: {}", key);
    } catch (InterruptedException e) {
      decrementCounter(snc, key);
      log.warn("Interrupted in acquire()", e);
      throw e;
    } catch (Throwable e) {
      decrementCounter(snc, key);
      log.fatal("Unexpected throwable in acquire()", e);
      throw e;
    }
    return new SemaphoreLock(key);
  }

  /**
   * Releases the lock for the semaphore of the provided key.
   *
   * @param key The key of the semaphore to release the lock of.
   */
  public void releaseLock(T key) {
    log.trace("Releasing lock: {}", key);
    synchronized (locks) {
      // Release the semaphore lock
//       SemaphoreAndCount snc = getSemaphoreAndCount(key);
      SemaphoreAndCount snc = locks.get(key);
      if (snc == null) {
        log.error("Attempt to release non-existent lock for {}", key);
        throw new IllegalStateException("No existing semaphore for: " + key);
      }

      if (snc.count < 1) {
        log.warn("Releasing semaphore with usage counter less than one [key: {}]", key, new Throwable());
        throw new IllegalStateException("Releasing semaphore with usage counter less than one for: " + key);
      }

      snc.getSemaphore().release();
      snc.eraseContext();
      log.trace("Released lock: {}", key);

      decrementCounter(snc, key);
    }
  }

  void decrementCounter(SemaphoreAndCount snc, T key) {
    // Decrement the usage count; remove the semaphore from map if no longer in use
    synchronized (locks) {
      if (snc.decrementCounter() < 1) {
        if (snc.getSemaphore().hasQueuedThreads()) {
          log.warn("Semaphore still has queued threads [key: {}]", key);
        }

        locks.remove(key);
      }
    }
  }

  /**
   * Returns an estimate of the usage count of a semaphore. Only intended to be used in testing.
   *
   * @param key The key of the semaphore.
   * @return An {@link Integer} containing the usage count of the semaphore.
   */
  public Integer getCount(T key) {
    synchronized (locks) {
      SemaphoreAndCount snc = locks.get(key);
      return (snc == null) ? null : snc.count;
    }
  }

  /**
   * Returns the number of semaphores in the map. Only intended to be used in testing.
   *
   * @return An {@code int} containing the number of semaphores in the internal map.
   */
  public int getSize() {
    synchronized (locks) {
      return locks.size();
    }
  }
}
