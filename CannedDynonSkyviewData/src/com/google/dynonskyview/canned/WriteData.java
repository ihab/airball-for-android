package com.google.dynonskyview.canned;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.dynonskyview.DummyFlightData;
import com.google.dynonskyview.DynonSerialFormat;

public class WriteData {

  private static final int PERIODS_TO_PRODUCE = 10;
  private static final int PERIOD = 5 * 1000;  // milliseconds
  private static final int DATA_POINTS_PER_SECOND = 64;

  public static void main(String[] argv) throws FileNotFoundException, IOException {
    Writer w = new OutputStreamWriter(new FileOutputStream(new File("adahrs-data.txt")));
    DummyFlightData cfd = new DummyFlightData(PERIOD, 0L);
    for (long i = 0L; i < PERIODS_TO_PRODUCE * PERIOD / 1000 * DATA_POINTS_PER_SECOND; i++) {
      w.write(DynonSerialFormat.dataToWord(cfd.getBlock(i * (1000 / DATA_POINTS_PER_SECOND))) + "\n");
    }
    w.flush();
    w.close();
  }
}