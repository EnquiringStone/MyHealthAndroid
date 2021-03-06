package com.example.myhealthapp;

import java.util.Random;

import com.example.myhealthapp.conn.BluetoothHandler;
import com.example.myhealthapp.conn.BluetoothListener;
import com.example.myhealthapp.conn.BluetoothSender;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MeasurementsActivity extends Activity {

	BluetoothHandler handler;
	MeasurementsActivity self;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurements);
		self = this;

		findViewById(R.id.sendbtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						handler = new BluetoothSender(self);
						handler.execute();
					}
				});

		findViewById(R.id.recievebtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						handler = new BluetoothListener(self);
						handler.execute();
					}
				});
		
		findViewById(R.id.randomBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Random rndm = new Random();
						
						String string = "" + rndm.nextInt();
						handler.setData(string);
						Log.i("DEBUG", "Were setting the data");
					}
				});
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
			if (handler != null){
				handler.cancelConnection();
				handler.DisableBluetooth();
				handler = null;
				finish();
			}
		}
		catch (Exception e){
			Log.e("ERROR", "Something went wrong " + e);
		}
		super.onBackPressed();
	}

}
