package org.schmivits.airball.util;

public interface ValueModel <T> {
  T getValue();
  boolean isValid();
}
