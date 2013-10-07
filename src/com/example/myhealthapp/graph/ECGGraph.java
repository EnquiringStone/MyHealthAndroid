package com.example.myhealthapp.graph;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import com.example.myhealthapp.conn.BluetoothHandler;
import com.example.myhealthapp.conn.BluetoothListener;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

public class ECGGraph extends XYChartBuilder {
	
	private BluetoothHandler handler = null;

  public ECGGraph(){
    //Log.d("test","Default constructor in ECGGraph");
    
    XYSeries series = new XYSeries("ECG");
    mDataset.addSeries(series);
    mCurrentSeries = series;
    
    XYSeries series2 = new XYSeries("ECG");
    mDataset.addSeries(series2);
    mCurrentSeries2 = series2;
    
    mRenderer.setXTitle("Time");
    mRenderer.setYTitle("Amplitude");
    mRenderer.setChartTitle("ECG");
    
    mRenderer.setYAxisMin(45);
    mRenderer.setYAxisMax(150);
    
    XYSeriesRenderer renderer2 = new XYSeriesRenderer();
    mRenderer.addSeriesRenderer(renderer2);
    renderer2.setPointStyle(PointStyle.POINT);
    renderer2.setFillPoints(true);
    renderer2.setLineWidth(0);
    renderer2.setColor(Color.BLACK);
  }
  
  protected void onCreate(){
	  handler = BluetoothListener.getInstance(null);
	  Log.d("test", handler.toString());
  }
  
  protected void onRestoreInstanceState(Bundle savedState) {
    super.onRestoreInstanceState(savedState);
    mCurrentSeries2 = (XYSeries) savedState
        .getSerializable("current_series_2");
    mCurrentRenderer2 = (XYSeriesRenderer) savedState
        .getSerializable("current_renderer_2");
  }
  
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable("current_series_2", mCurrentSeries2);
    outState.putSerializable("current_renderer_2", mCurrentRenderer2);
  }
}