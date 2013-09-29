package com.example.myhealthapp;

import com.example.myhealthapp.conn.BluetoothHandler;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MeasurementsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurements);
		BluetoothHandler btHandler = new BluetoothHandler(this);
		btHandler.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.measurements, menu);
		return true;
	}

}
