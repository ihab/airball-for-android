package org.schmivits.airball.airdata;

import java.io.IOException;
import java.io.InputStream;

public class NonBlockingLineReader {

  private final InputStream mInputStream;
  private StringBuilder mStringBuilder;
  
  public NonBlockingLineReader(InputStream inputStream) {
    mInputStream = inputStream;
    mStringBuilder = new StringBuilder();
  }
  
  public String maybeReadLine() throws IOException {
    while (mInputStream.available() != 0) {
      char c = (char) mInputStream.read();
      if (c == '\n') {
        String s = mStringBuilder.toString();
        mStringBuilder = new StringBuilder();
        return s;
      } else {
        mStringBuilder.append(c);
      }
    }
    return null;
  }
}
