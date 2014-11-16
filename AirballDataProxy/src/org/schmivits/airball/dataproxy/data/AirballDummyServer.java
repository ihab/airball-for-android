package org.schmivits.airball.dataproxy.data;

import android.content.Context;

public class AirballDummyServer extends AirballServer {

  public AirballDummyServer(Context context, AbstractProcess parent, String title) {
    super(context, parent, title);
  }
  
  @Override
  protected DataSourceStream makeDataSourceStream() {
    return new DummyDataSourceStream(getContext(), this, "");    
  }
}
