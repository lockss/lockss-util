/*
 * Copyright (c) 2018-2019 Board of Trustees of Leland Stanford Jr. University,
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

package org.lockss.util.jms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.jms.*;

import org.lockss.util.*;

public interface JmsProducer {

  public void close() throws JMSException;

  /**
   * Send a text message.
   * @param text the text string to send
   * @throws JMSException if the TextMessage is cannot be created.
   */
  public void sendText(String text) throws JMSException;

  /**
   * Send a map of keys: values in which keys are java Strings and values are
   * a java object for which toString() allows understandable reconstruction.
   * @param map The map to send.
   * @throws JMSException if the map is MapMessage can not be created or set.
   */
  public void sendMap(Map<?,?> map)
      throws JMSException, IllegalArgumentException;

  /**
   * Send a byte array
   * @param bytes the array of bytes to send.
   * @throws JMSException if the BytesMessage can not be created or set.
   */
  public void sendBytes(byte[] bytes) throws JMSException;

  /**
   * Send a serializable java object
   * @param obj the java object to send.
   * @throws JMSException if the ObjectMessage can not be created or set.
   */
  public void sendObject(Serializable obj) throws JMSException;

}
