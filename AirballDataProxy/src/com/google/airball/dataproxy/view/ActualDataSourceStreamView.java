package com.google.airball.dataproxy.view;

import tw.com.prolific.driver.pl2303.PL2303Driver.BaudRate;
import tw.com.prolific.driver.pl2303.PL2303Driver.DataBits;
import tw.com.prolific.driver.pl2303.PL2303Driver.FlowControl;
import tw.com.prolific.driver.pl2303.PL2303Driver.Parity;
import tw.com.prolific.driver.pl2303.PL2303Driver.StopBits;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.airball.dataproxy.R;
import com.google.airball.dataproxy.data.ActualDataSourceStream;
import com.google.airball.dataproxy.data.ActualDataSourceStream.SerialParameters;

public class ActualDataSourceStreamView extends ProcessNodeView {

  private EnumModel<BaudRate> mBaudRate;
  private EnumModel<DataBits> mDataBits;
  private EnumModel<StopBits> mStopBits;
  private EnumModel<Parity> mParity;
  private EnumModel<FlowControl> mFlowControl;
  
  class EnumModel<T extends Enum<?>> {
    private final Class<T> mClazz;
    private final Spinner mSpinner;
    
    public EnumModel(Class<T> clazz, int id, T selected) {
      mClazz = clazz;
      mSpinner = (Spinner) findViewById(id);      
      mSpinner.setAdapter((SpinnerAdapter) new ArrayAdapter<Object>(
          getContext(),
          R.layout.serial_parameter_item, 
          mClazz.getEnumConstants()));
      mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
          updateFromSpinners();
        }
        @Override public void onNothingSelected(AdapterView<?> parent) {
          updateFromSpinners();
        }
      });
      set(selected);
    }
    
    public T get() {
      return mClazz.getEnumConstants()[mSpinner.getSelectedItemPosition()];
    }
    
    public void set(T value) {
      T[] constants = mClazz.getEnumConstants();
      for (int i = 0; i < constants.length; i++) {
        if (value == constants[i]) {
          mSpinner.setSelection(i);
          return;
        }
      }
    }
  }
  
  private <Q extends Enum<?>> EnumModel<Q> make(Class<Q> clazz, int id, Q selected) {
    return new EnumModel<Q>(clazz, id, selected);
  }

  public ActualDataSourceStreamView(Context context, AttributeSet attrs) {
    this(null, context, attrs);
  }
  
  public ActualDataSourceStreamView(ActualDataSourceStream model, Context context, AttributeSet attrs) {
    super(model, context, attrs);
  }
  
  @Override protected void addAdditional(ViewGroup parent) {
    LayoutInflater.from(getContext()).inflate(
        R.layout.actual_data_source,
        parent);
    SerialParameters sp = ((ActualDataSourceStream) getModel()).getSerialParameters();
    mBaudRate = make(BaudRate.class, R.id.actual_data_source_baud_rate, sp.mBaudRate);
    mDataBits = make(DataBits.class, R.id.actual_data_source_data_bits, sp.mDataBits);
    mStopBits = make(StopBits.class, R.id.actual_data_source_stop_bits, sp.mStopBits);
    mParity = make(Parity.class, R.id.actual_data_source_parity, sp.mParity);
    mFlowControl = make(FlowControl.class, R.id.actual_data_source_flow_control, sp.mFlowControl);
  }

  private void updateFromSpinners() {
    ((ActualDataSourceStream) getModel()).setSerialParameters(
        new ActualDataSourceStream.SerialParameters(
            mBaudRate.get(), 
            mDataBits.get(),
            mStopBits.get(),
            mParity.get(),
            mFlowControl.get()));
  }
}
