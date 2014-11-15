package com.google.dynonskyview.canned;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Field;

import com.google.dynonskyview.ADAHRSDataBlock;
import com.google.dynonskyview.DynonSerialFormat;

public class CapturedDataTest {

  private boolean compare(String expected, String actual) {
    if (!expected.equals(actual)) {
      System.out.println("len = " + expected.length() + "\n" + expected + " != \n" + actual);
      return false;
    } else {
      return true;
    }
  }

  private boolean compare(String expected, String actual, int i, int j) {
    return compare(expected.substring(i, j), actual.substring(i, j));
  }

  private boolean compareDataWords(String expected, String actual) {
    return compare(expected, actual, 0, 41);
  }

  private String toString(ADAHRSDataBlock block) {
    StringBuilder sb = new StringBuilder();
    for (Field f : ADAHRSDataBlock.class.getFields()) {
      try {
        sb.append(f.getName());
        sb.append("=");
        sb.append(f.get(block));
        sb.append(",");
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return sb.toString();
  }

  private static void printHeader(Writer w) throws IOException {
    w.write("DATA,");
    w.write("EXCEPTION,");
    for (Field f : ADAHRSDataBlock.class.getFields()) {
      w.write(f.getName() + ",");
    }
    w.write("\n");
  }

  private static void printLine(Writer w, String dataWord, String exception, ADAHRSDataBlock block) throws IOException {
    w.write(dataWord + ",");
    w.write(exception + ",");
    for (Field f : ADAHRSDataBlock.class.getFields()) {
      try {
        w.write((block == null ? "X" : "" + f.get(block)) + ",");
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    w.write("\n");
  }

  public static void tabulateCapturedData() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(CapturedDataTest.class.getResourceAsStream("dynon-capture.txt")));
    Writer writer = new FileWriter(new File("dynon-capture-decode.csv"));
    printHeader(writer);
    for (String dataWord; (dataWord = reader.readLine()) != null; ) {
      try {
        printLine(writer, dataWord, "ok", DynonSerialFormat.wordToData(dataWord));
      } catch (Exception e) {
        printLine(writer, dataWord, e.toString(), null);
      }
    }
  }

  public static void main(String[] argv) throws IOException {
    tabulateCapturedData();
  }
}
