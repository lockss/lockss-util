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

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.commons.lang3.*;

@Plugin(name = "ListAppender", category = "Core", elementType = "appender", printObject = true)

public class ListAppender extends AbstractAppender {
  private List<String> messages = new ArrayList<>();
  private List<String> levelMessages = new ArrayList<>();

  public ListAppender(String name, Filter filter,
		      Layout<? extends Serializable> layout) {
    super(name, filter, layout);
  }

  @Override
  public void append(LogEvent event) {
    messages.add(event.getMessage().getFormattedMessage());
    levelMessages.add(event.getLevel() + ": " +
		 event.getMessage().getFormattedMessage());
  }

  public List<String> getMessages() {
    return messages;
  }

  public List<String> getLevelMessages() {
    return levelMessages;
  }

//   public void setMessages(List<String> messages) {
//     this.messages = messages;
//   }

  public void reset() {
    this.messages = new ArrayList<>();;
    this.levelMessages = new ArrayList<>();
  }

  @PluginFactory
  public static ListAppender
    createAppender(@PluginAttribute("name") String name,
		   @PluginElement("Layout") Layout<? extends Serializable> layout, 
		   @PluginElement("Filter") final Filter filter,
		   @PluginAttribute("otherAttribute") String otherAttribute) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Missing appender name");
    }
    if (layout == null) {
      layout = PatternLayout.createDefaultLayout();
    }
    return new ListAppender(name, filter, layout);
  }
}
