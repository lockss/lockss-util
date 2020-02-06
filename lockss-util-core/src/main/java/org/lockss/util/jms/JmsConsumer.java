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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.jms.*;

import org.lockss.util.*;

public interface JmsConsumer {

  public void close() throws JMSException;

  public void setListener(MessageListener listener) throws JMSException;

  public Object receive(int timeout) throws JMSException;

  /**
   * Receive a text message from the message queue.
   *
   * @param timeout the time to wait for the message to be received.
   * @return the resulting String message.
   * @throws JMSException if thrown by JMS methods
   */
  public String receiveText(int timeout) throws JMSException;

  /**
   * Return a Map with string keys and object values from the message queue.
   *
   * @param timeout the time to wait for the message to be received.
   * @return the resulting Map
   * @throws JMSException if thrown by JMS methods
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> receiveMap(int timeout) throws JMSException;

  /**
   * Return a byte array from the message queue
   *
   * @param timeout the time to wait for the message to be received.
   * @return the byte array.
   * @throws JMSException if thrown by JMS methods
   */
  public byte[] receiveBytes(int timeout) throws JMSException;

  /**
   * Return a serializable object from the message queue.
   *
   * @param timeout for the message consumer receive
   * @return the resulting Serializable object
   * @throws JMSException if thrown by JMS methods
   */
  public Serializable receiveObject(int timeout) throws JMSException;

}
