/*
 * Copyright (c) 2018 Board of Trustees of Leland Stanford Jr. University,
 * all rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 * STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Stanford University shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization from Stanford University.
 */

package org.lockss.util.jobq;

import static org.junit.jupiter.api.Assertions.*;

import javax.print.attribute.standard.JobState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lockss.util.jobq.Result.Listener;
import org.lockss.util.test.LockssTestCase5;

class TestJobImpl extends LockssTestCase5 {
  String inString = "This is an input.";
  String outString = "This is a result.";
  MyListener<String> listener = new MyListener<>();

  JobImpl<String, String> job = new JobImpl<>(inString, listener);
  @Test
  void onExecute() throws Exception {
    job.setResult(outString);
    String res =  job.onExecute();
    assertEquals(res, outString);
  }

  @Test
  void cancel() {
    assertTrue(job.isPending());
    job.cancel();
    assertTrue(job.isCanceled());
  }

  @Test
  void deliverError() {
    job.deliverError(new JobQException("exception"));
  }

  @Test
  void deliverResult() {
    job.deliverResult(outString);
  }

  static class MyListener<T> implements Listener<T> {

    /**
     * respond to a result.
     *
     * @param res the result
     */
    @Override
    public void onResult(T res) {
      Assertions.assertNotNull(res);
    }

    /**
     * respond to an error.
     *
     * @param err the error
     */
    @Override
    public void onError(Throwable err) {
      Assertions.assertNotNull(err);
    }
  }
}