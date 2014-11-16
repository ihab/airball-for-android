package org.schmivits.airball.dataproxy.data;

import android.content.Context;

public class AirballActualServer extends AirballServer {

  public AirballActualServer(Context context, AbstractProcess parent, String title) {
    super(context, parent, title);
  }
  
  @Override
  protected DataSourceStream makeDataSourceStream() {
    return new ActualDataSourceStream(getContext(), this, "");        
  }
}
