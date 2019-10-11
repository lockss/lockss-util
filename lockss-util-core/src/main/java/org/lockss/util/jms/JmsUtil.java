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

public class JmsUtil {

  /**
   * This implementation converts a Message to the underlying type.
   * TextMessage back to a String, a ByteMessage back to a byte array,
   * a MapMessage back to a Map, and an ObjectMessage back to a Serializable object. Returns
   * the plain Message object in case of an unknown message type.
   */
  public static Object convertMessage(Message message) throws JMSException {
    if (message instanceof TextMessage) {
      return ((TextMessage) message).getText();
    }
    else if (message instanceof BytesMessage) {
      BytesMessage bytesMessage = (BytesMessage) message;
      byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
      bytesMessage.readBytes(bytes);
      return bytes;
    }
    else if (message instanceof MapMessage) {
      MapMessage mapMessage = (MapMessage) message;
      Map<String, Object> map = new HashMap<String, Object>();
      Enumeration<String> en = mapMessage.getMapNames();
      while (en.hasMoreElements()) {
        String key = en.nextElement();
        map.put(key, mapMessage.getObject(key));
      }
      return map;
    }
    else if (message instanceof ObjectMessage) {
      return ((ObjectMessage) message).getObject();
    }
    else {
      return message;
    }
  }
}
