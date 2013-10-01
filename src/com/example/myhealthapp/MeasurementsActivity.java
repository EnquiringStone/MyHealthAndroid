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
	
	BluetoothListener btListener;
	BluetoothSender btSender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurements);
		BluetoothListener btListener = new BluetoothListener(this);
		BluetoothSender btSender = new BluetoothSender(this);
		btListener.execute();
//		btSender.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.measurements, menu);
		return true;
	}

}
