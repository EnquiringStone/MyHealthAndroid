package com.example.myhealthapp;

import java.util.Set;

import com.example.myhealthapp.conn.BluetoothHandler;
import com.example.myhealthapp.conn.BluetoothListener;
import com.example.myhealthapp.conn.BluetoothSender;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.Menu;

public class MeasurementsActivity extends Activity {
	
	BluetoothHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurements);
		handler = new BluetoothListener(this);
		//handler = new BluetoothSender(this);
		handler.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.measurements, menu);
		return true;
	}
	
	@Override
	public void onBackPressed(){
		try{
			handler.DisableBluetooth();
			finish();
		}
		catch (Exception e){
			Log.e("ERROR", "Something went wrong " + e);
		}
		super.onBackPressed();
	}

}
