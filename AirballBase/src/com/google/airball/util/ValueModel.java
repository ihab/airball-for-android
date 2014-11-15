package com.google.airball.util;

public interface ValueModel <T> {
  T getValue();
  boolean isValid();
}
