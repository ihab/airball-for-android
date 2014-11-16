package org.schmivits.airball.dataproxy.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tw.com.prolific.driver.pl2303.PL2303Driver;
import tw.com.prolific.driver.pl2303.PL2303Driver.BaudRate;
import tw.com.prolific.driver.pl2303.PL2303Driver.DataBits;
import tw.com.prolific.driver.pl2303.PL2303Driver.FlowControl;
import tw.com.prolific.driver.pl2303.PL2303Driver.Parity;
import tw.com.prolific.driver.pl2303.PL2303Driver.StopBits;
import android.content.Context;
import android.hardware.usb.UsbManager;

public class ActualDataSourceStream extends ConcurrentProcess implements DataSourceStream {

  private static final String USB_PERMISSION = "org.schmivits.airball.dataproxy.USB_PERMISSION";
  private static final int READ_BUFFER_SIZE = 2048;    
  private static final long ENUMERATE_PAUSE = 1000L;
  private static final long SETUP_PAUSE = 500L;  
  private static final long READ_PAUSE = 10L;    
  
  public static class SerialParameters {
    public final BaudRate mBaudRate;
    public final DataBits mDataBits;
    public final StopBits mStopBits;
    public final Parity mParity;
    public final FlowControl mFlowControl;
    public SerialParameters(
        BaudRate baudRate,
        DataBits dataBits,
        StopBits stopBits,
        Parity parity,
        FlowControl flowControl) {
      mBaudRate = baudRate;
      mDataBits = dataBits;
      mStopBits = stopBits;
      mParity = parity;
      mFlowControl = flowControl;
    }
  }
  
  private final List<Listener> mListeners = new ArrayList<Listener>();
  private SerialParameters mSerialParameters = new SerialParameters(
      BaudRate.B115200,
      DataBits.D8,
      StopBits.S1,
      Parity.NONE,
      FlowControl.OFF);
  private PL2303Driver mDriver;
  private final byte[] mReadBuffer = new byte[READ_BUFFER_SIZE];  
  private final StringBuilder mLineBuilder = new StringBuilder();
  private boolean mSerialChanged = false;
  
  public ActualDataSourceStream(Context context, ProcessNode parent, String title) {
    super(context, parent, title);
  }
  
  @Override protected void doRun() throws InterruptedException {
    mDriver = new PL2303Driver(
        (UsbManager) getContext().getSystemService(Context.USB_SERVICE),
        getContext(),
        USB_PERMISSION);
    boolean doneSetup = false;
    while (isRunning()) {
      if (mSerialChanged) {
        synchronized (this) {
          if (mSerialChanged) {
            doneSetup = false;
            mSerialChanged = false;
          }
        }
      }
      if (!mDriver.isConnected()) {
        doneSetup = false;
        enumerateDriver();
      } else if (mDriver.isConnected() && !doneSetup) {
        doneSetup = setupDriver();
      } else {
        readFromDriver();
      }
    }
  }
  
  private void enumerateDriver() throws InterruptedException {
    setStatus("Attempting enumerate ...");        
    if (mDriver.enumerate()) {
      setStatus("Enumerate successful");
    } else {
      setStatus("Enumerate failed, sleeping ...");
      Thread.sleep(ENUMERATE_PAUSE); 
    }
  }
  
  private boolean setupDriver() throws InterruptedException {
    setStatus("Setting up driver ...");
    boolean success;
    try {
      int setup = mDriver.setup(
          mSerialParameters.mBaudRate,
          mSerialParameters.mDataBits,
          mSerialParameters.mStopBits,
          mSerialParameters.mParity,
          mSerialParameters.mFlowControl);
      boolean initByBaudRate = mDriver.InitByBaudRate(
          mSerialParameters.mBaudRate);
      success = setup == 0 && initByBaudRate;
    } catch (IOException e) {
      success = false;
      setStatus(e.toString());
    }
    if (success) {
      setStatus("Setup succeeded");
      return true;
    } else {
      setStatus("Setup failed, sleeping ...");
      Thread.sleep(SETUP_PAUSE);
      return false;
    }    
  }
  
  private void readFromDriver() throws InterruptedException {
    for (int n; (n = mDriver.read(mReadBuffer)) > 0; ) {
      receivedData(n);
    }
    Thread.sleep(READ_PAUSE);
  }
  
  private boolean isLineSeparator(char c) {
    return c == '\r' || c == '\n';
  }
  
  private void receivedData(int n) {
    for (int i = 0; i < n; i++) {
      char c = (char) mReadBuffer[i];
      if (isLineSeparator(c)) {
        if (mLineBuilder.length() > 0) {
          notifyListeners(mLineBuilder.toString());
          setStatus(mLineBuilder.toString());
          mLineBuilder.setLength(0);
        }
      } else {
        mLineBuilder.append(c);
      }
    }
  }
  
  private void notifyListeners(String line) {
    for (Listener l : mListeners) {
      l.data(line);
    }
  }

  @Override
  public void addListener(Listener l) {
    synchronized (mListeners) {
      mListeners.add(l);
    }
  }

  @Override
  public void removeListener(Listener l) {
    synchronized (mListeners) {
      mListeners.remove(l);
    }
  }
  
  public void setSerialParameters(SerialParameters p) {
    synchronized (this) {
      mSerialParameters = p;
      mSerialChanged = true;
    }
  }
  
  public SerialParameters getSerialParameters() {
    return mSerialParameters;
  }
}
