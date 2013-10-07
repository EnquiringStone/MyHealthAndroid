package com.example.myhealthapp.graph;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.myhealthapp.R;
import com.example.myhealthapp.conn.BluetoothListener;
import com.example.myhealthapp.conn.RequestHandler;
import com.example.myhealthapp.db.Database;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

public class XYChartBuilder extends Activity {
	public static final String TYPE = "type";
	public XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	public XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	public XYSeries mCurrentSeries;
	public XYSeries mCurrentSeries2;
	public XYSeriesRenderer mCurrentRenderer;
	public XYSeriesRenderer mCurrentRenderer2;
	public String mDateFormat;
	public boolean newUpdate;

	public GraphicalView mChartView;
	
	private Database localDatabase = new Database(XYChartBuilder.this);

	static double x = 0;
	static double y = 0;
	static double y2 = 0;
	
	protected Update mUpdateTask;

	public XYChartBuilder(){
	  //Log.d("test","Default constructor in XYChartBuilder");
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mDataset = (XYMultipleSeriesDataset) savedState
				.getSerializable("dataset");
		mRenderer = (XYMultipleSeriesRenderer) savedState
				.getSerializable("renderer");
		mCurrentSeries = (XYSeries) savedState
				.getSerializable("current_series");
		mCurrentRenderer = (XYSeriesRenderer) savedState
				.getSerializable("current_renderer");
		mDateFormat = savedState.getString("date_format");
	}
	
	public void onBackPressed()
	  {
		  Log.d("test", "OnbackPressed");
		  mUpdateTask.cancel(true);
		  finish();
	  }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("dataset", mDataset);
		outState.putSerializable("renderer", mRenderer);
		outState.putSerializable("current_series", mCurrentSeries);
		outState.putSerializable("current_renderer", mCurrentRenderer);
		outState.putString("date_format", mDateFormat);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);
		
		mRenderer.setApplyBackgroundColor(true);
		//mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50)); //grey?
		mRenderer.setBackgroundColor(0xff000000);
		mRenderer.setAxisTitleTextSize(16);
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(15);
		mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setPointSize(10);
	  mRenderer.setShowGrid(true);
	    
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setColor(Color.GREEN);
		mCurrentRenderer = renderer;
		
		mUpdateTask = new Update();
    mUpdateTask.execute(this);
		
 }
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			mRenderer.setClickEnabled(true);
			mRenderer.setSelectableBuffer(100);
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					double[] xy = mChartView.toRealPoint(0);
					if (seriesSelection == null) {
						Toast.makeText(XYChartBuilder.this,
								"No chart element was clicked",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								XYChartBuilder.this,
								"Chart element in series index "
										+ seriesSelection.getSeriesIndex()
										+ " data point index "
										+ seriesSelection.getPointIndex()
										+ " was clicked"
										+ " closest point value X="
										+ seriesSelection.getXValue() + ", Y="
										+ seriesSelection.getValue()
										+ " clicked point value X="
										+ (float) xy[0] + ", Y="
										+ (float) xy[1], Toast.LENGTH_SHORT)
								.show();
					}
				}
			});
			mChartView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
				  	
										
						SeriesSelection seriesSelection = mChartView
								.getCurrentSeriesAndPoint();
						if (seriesSelection == null) {
							Toast.makeText(XYChartBuilder.this,
									"No chart element was long pressed",
									Toast.LENGTH_SHORT)
							.show();
							return false; // no chart element was long pressed
						} else {
							/*Toast.makeText(XYChartBuilder.this,
									"Chart element in series index "
											+ seriesSelection.getSeriesIndex()
											+ " data point index "
											+ seriesSelection.getPointIndex()
											+ " was long pressed",
									Toast.LENGTH_SHORT).show();
							return true; // the element was long pressed */

						  AlertDialog.Builder builder = new AlertDialog.Builder(XYChartBuilder.this);

						  // the getXValue() returns a value starting from 0, so 1 lower then the value shown in the graph.
	            builder.setMessage("Do you want to delete the following measurement?" +
	            		" time = " + (seriesSelection.getXValue()+1) + " and value " + seriesSelection.getValue() + "")
	                   .setTitle("Delete measurement?");
	            
	            final SeriesSelection seriesToBeDeleted = seriesSelection;

	            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                     public void onClick(DialogInterface dialog, int id) {
	                         showDeleteMeasurementFromWhereDialog(seriesToBeDeleted);
	                     }
	                 });
	            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                     public void onClick(DialogInterface dialog, int id) {
	                     }
	                 });

	            AlertDialog dialog = builder.create();
	            dialog.show();
	            
	            return true;
						  
						}
				}
			});
			mChartView.addZoomListener(new ZoomListener() {
				public void zoomApplied(ZoomEvent e) {
					String type = "out";
					if (e.isZoomIn()) {
						type = "in";
					}
					System.out.println("Zoom " + type + " rate "
							+ e.getZoomRate());
				}

				public void zoomReset() {
					System.out.println("Reset");
				}
			}, true, true);
			mChartView.addPanListener(new PanListener() {
				public void panApplied() {
					System.out.println("New X range=["
							+ mRenderer.getXAxisMin() + ", "
							+ mRenderer.getXAxisMax() + "], Y range=["
							+ mRenderer.getYAxisMax() + ", "
							+ mRenderer.getYAxisMax() + "]");
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}
	
	public void showDeleteMeasurementFromWhereDialog(SeriesSelection seriesToBeDeleted){
	  AlertDialog.Builder builder = new AlertDialog.Builder(XYChartBuilder.this);

    // the getXValue() returns a value starting from 0, so 1 lower then the value shown in the graph.
    builder.setMessage("Delete following measurement from where?" +
        " time = " + (seriesToBeDeleted.getXValue()+1) + " and value " + seriesToBeDeleted.getValue() + "")
           .setTitle("Delete measurement from where?");
    
    builder.setPositiveButton("Local only", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
             }
         });
    builder.setNegativeButton("Local and server", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
             }
         });

    AlertDialog dialog = builder.create();
    dialog.show();
	}

	protected class Update extends AsyncTask<Context, Integer, String> {
		BluetoothListener handler = BluetoothListener.getInstance(null);	
		
		@Override
		protected String doInBackground(Context... params) {

			int i = 0;
			while (!isCancelled()) {
				try {
					Thread.sleep(1000);
					if(handler.hasNewResult()){
						
						JSONArray arr = null;
						
						String s = handler.getNewResult();
						
						Log.d("test", s);
					    
						/*int testIndex=2;
					    
					    if(testIndex==0){
					    	s="{\"Bloodpressure\":[51,85]}";
					    } else if (testIndex==1){
					    	s="{\"Pulse\":183}";
					    } else if (testIndex==2) {
					    	s="{\"ECG\":[60,59,58,57,56,56,56,56,56,56,56,55,55,56,56,56,56,56,56,57,57,57,56,56,56,56,55,55,55,55,56,56,55,55,55,55,55,55,54,54,54,55,56,57,58,58,58,58,56,55,55,54,54,53,53,54,54,55,56,58,59,59,58,56,55,55,55,56,56,56,56,57,57,57,57,57,57,57,57,57,57,57,57,57,58,58,58,58,58,58,58,57,57,58,59,60,59,58,58,57,57,57,57,58,58,58,58,59,60,61,61,61,62,62,61,61,60,61,61,62,63,63,63,64,64,64,64,64,64,63,62,62,62,62,62,62,62,62,63,63,62,61,60,60,60,59,58,57,56,56,56,56,57,57,57,57,57,57,57,57,56,56,56,56,56,57,57,57,57,56,56,56,56,56,57,57,58,57,57,56,56,55,54,52,49,47,45,44,45,48,52,58,66,75,86,99,111,121,127,129,126,119,108,94,79,66,57,51,46,43,42,44,49,54,58,59,58,57,56,55,56,57,59,61,62,62,61,60,59,58,57,57,57,59,61,62,63,62,62,62,62,62,62,62,62,62,63,64,64,64,64,64,64,65,65,66,67,68,69,69,68,67,66,66,66,67,67,67,67,68,69,71,73,74,74,73,71,70,69,70,70,71,70,70,70,71,72,74,75,76,76,76,77,77,77,77,78,80,81,82,82,82,83,84,85,85,86,87,88,89,91,91,91,92,92,93,93,94,94,95,95,94,94,93,92,90,89,87,86,85,82,79,76,74,73,72,71,69,67,65,64,64,63,63,62,62,62,60,59,58,57,57,57]}";

					    } else {
					    	Log.d("test","This is not supposed to happen..");
					    	s="{\"Pulse\":183}";
					    }*/
						
					    JSONObject jsonObject = new JSONObject(s);
					    String jsonObjectname=jsonObject.names().getString(0);
					    String currentType = null;
					    //String newResult = handler.getNewResult();
					    String newResult = "";

						if(jsonObject.names().getString(0).equals("BloodPressure")){
							currentType = jsonObject.names().getString(0);
							try {
								arr=jsonObject.getJSONArray(jsonObject.names().getString(0));
							} catch(Exception e) {
								e.printStackTrace();
							}
							x = x + 1;
							y = Integer.parseInt(arr.get(0).toString());
							y2 = Integer.parseInt(arr.get(1).toString());
							newResult = "'"+(Integer.parseInt(arr.get(0).toString())+"','"+Integer.parseInt(arr.get(1).toString()))+"'";
							Log.d("test", newResult);
							
							publishProgress(i);
							i++;
							saveResultLocal(newResult, currentType);
						}
						if(jsonObject.names().getString(0).equals("ECG")){
							currentType = jsonObject.names().getString(0);
							try {
								arr=jsonObject.getJSONArray(jsonObject.names().getString(0));
							} catch(Exception e) {
								e.printStackTrace();
							}
							for(int z=0;z<arr.length();z++){
								x = x + 1;
								y = Integer.parseInt(arr.get(z).toString());
								y2 = Integer.parseInt(arr.get(z).toString())+20; // invisible
								Thread.sleep(5);
								newResult += "'"+Integer.parseInt(arr.get(z).toString())+"',";
								publishProgress(i);
								i++;
							}
							newResult = newResult.substring(0, newResult.length()-1);
						}
						
						if(jsonObjectname.equals("Pulse")){
							currentType = jsonObjectname;
							x = x + 1;
							y=Integer.parseInt(jsonObject.get(jsonObjectname).toString())+20;
							y2=Integer.parseInt(jsonObject.get(jsonObjectname).toString());
							newResult = "'"+Integer.parseInt(jsonObject.get(jsonObjectname).toString())+"'";
							publishProgress(i);
							i++;
							saveResultLocal(newResult, currentType);
						}
					    //saveResultLocal(newResult, currentType);
						//saveResultExternal(newResult, currentType);
						
						Log.d("test",newResult);
						Log.d("test",currentType); 
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return "COMPLETE!";
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}
	
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		
			mCurrentSeries.add(x, y);
			mCurrentSeries2.add(x, y2);
			
			if (mChartView != null) {
				mChartView.repaint();
			}
		}
		
		// -- called if the cancel button is pressed
		@Override
		protected void onCancelled() {
			super.onCancelled();
			
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
	
	public void saveResultLocal(String tinput, String ttype){
		final String input = tinput;
		final String type = ttype;
		
		new Thread(){
			public void run(){
				java.util.Date date= new java.util.Date();
				Timestamp timestamp = new Timestamp(date.getTime());
				
				Log.d("test", ""+ input + ", " + type + ", " + timestamp);
				
				localDatabase.executeQuery("INSERT INTO " + type.toLowerCase() + " VALUES('" + timestamp + "'," + input + ")");
				Log.d("test","input:"+input);
				Log.d("test","type:"+type);
				
			}
		}.start();
	}
	
	public void saveResultExternal(String input, String type){
		RequestHandler requestHandler = RequestHandler.getRequestHandler();
		
		String name = "measurement";
		String method = "createMeasurement";

		StringBuffer urlbuffer = new StringBuffer(requestHandler.host);

		try {
			name = requestHandler.encode(name.toString());
			method = requestHandler.encode(method);
			input = requestHandler.encode(input);
			type = requestHandler.encode(type);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		urlbuffer.append("/api?name=").append(name).append("&method=")
				.append(method).append("&login_token=").append(requestHandler.getLoginToken())
				.append("&type=").append(type).append("&value=").append(input);
		
		String url = urlbuffer.toString();
		requestHandler.setURL(url);
		requestHandler.setRunning_flag(true);
		requestHandler.execute(); //done
	}
	
}
