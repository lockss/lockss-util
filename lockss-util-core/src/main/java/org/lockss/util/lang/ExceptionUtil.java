/*

Copyright (c) 2000-2021, Board of Trustees of Leland Stanford Jr. University
All rights reserved.

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

package org.lockss.util.lang;
import java.util.*;
import java.util.stream.*;
import org.apache.commons.lang3.exception.*;

/**
 * <p>
 * Utilities related to {@link Exception} and {@link Throwable}.
 * </p>
 * 
 * @since 1.75.8
 */
public class ExceptionUtil {

  /**
   * <p>
   * Initializes the cause of a {@Throwable} instance and returns it, for
   * one-step creation and initialization when a {@Throwable) subclass does not
   * have a constructor that accepts a cause.
   * </p>
   * <p>
   * Before:
   * </p>
   * 
   * <pre>
     }
     catch (Exception1 c) {
       Exception2 e = new Exception(); // assume no constructor with cause
       e.initCause(c);
       throw e;
     }
   * </pre>
   * <p>
   * After:
   * </p>
   * 
   * <pre>
     }
     catch (Exception1 c) {
       throw ExceptionUtil.initCause(new Exception2(), c);
     }
   * </pre>
   * 
   * @param <T>
   *          A {@link Throwable} type.
   * @param throwable
   *          A {@link Throwable} instance.
   * @param cause
   *          A {@link Throwable} cause (can be null).
   * @return The throwable instance with {@link Throwable#initCause(Throwable)}
   *         called with the given cause. If the given cause is null, returns
   *         the throwable instance unchanged.
   * @since 1.75.8
   * @see Throwable#initCause(Throwable)
   */
  public static <T extends Throwable> T initCause(T throwable,
                                                  Throwable cause) {
    if (cause != null) {
      throwable.initCause(cause);
    }
    return throwable;
  }
  
  /** If one of the (transitive) causes of the exception is of the
   * given type, return it, else return null */
  public static <T extends Throwable> T getNestedExceptionOfType(Throwable ex,
                                                                 Class<T> type) {
    int ix = ExceptionUtils.indexOfType(ex, type);
    if (ix >= 0) {
      Throwable[] throwables = ExceptionUtils.getThrowables(ex);
      return (T)throwables[ix];
    } else {
      return null;
    }
  }

  /** If one of the (transitive) causes of the exception is of any of
   * the given types, return it, else return null */
  public static Throwable getNestedExceptionOfType(Throwable ex,
                                                   Class... types) {
    for (Class t : types) {
      Throwable res = getNestedExceptionOfType(ex, t);
      if (res != null) {
        return res;
      }
    }
    return null;
  }
//     return Stream.of(types)
//       .map(t -> getNestedExceptionOfType(ex, t))
//       .filter(x -> x != null)
//       .findFirst().orElse(/*(Throwable)null*/new Throwable("foo"));
}
