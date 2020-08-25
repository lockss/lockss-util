
package org.lockss.util.io;

import java.io.*;
import javax.activation.*;



public class InputStreamDataSource implements DataSource {

    private final InputStream is;
    private final String type;

    public InputStreamDataSource(InputStream inputStream, String type) {
      this.type = type;
      this.is = inputStream;
    }

    @Override
    public String getContentType() {            
      return type;
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return is;
    }

    @Override
    public String getName() {
      return "input stream";
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
      throw new UnsupportedOperationException("Read-only DataSource");
    }

}
