#!/usr/bin/python

import serial
import string
import time

with serial.Serial('/dev/cu.usbserial', 19200, timeout=1) as ser:
  with open('adahrs-data.txt') as f:
    for line in f:
      ser.write(line)
      time.sleep(1.0 / 16.0);
