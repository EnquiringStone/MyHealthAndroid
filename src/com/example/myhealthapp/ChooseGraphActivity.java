package com.example.myhealthapp;

import java.io.Serializable;

import com.example.myhealthapp.conn.BluetoothHandler;
import com.example.myhealthapp.conn.BluetoothListener;

import com.example.myhealthapp.graph.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class ChooseGraphActivity extends Activity {
  
  BluetoothHandler handler;
 //ChooseGraphActivity self;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_graph);
    handler = BluetoothListener.getInstance(this);
    handler.execute();

    /*findViewById(R.id.sendbtn).setOnClickListener(
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
        });*/
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
      handler.cancelConnection();
      handler.cancel(true);
      finish();
    }
    catch (Exception e){
      Log.e("ERROR", "Something went wrong " + e);
    }
    super.onBackPressed();
  }
  
  public void showECGGraph(View view){
    Intent intent = new Intent(getApplicationContext(), ECGGraph.class);
    startActivity(intent);
  }
  
  public void showBloodpressureGraph(View view){
    Intent intent = new Intent(getApplicationContext(), BloodpressureGraph.class);
    startActivity(intent);
  }
  
  public void showPulseGraph(View view){
    Intent intent = new Intent(getApplicationContext(), PulseGraph.class);
    startActivity(intent);
  }
  
  public void sendAllData(View view){
    Toast.makeText(ChooseGraphActivity.this, "Your data has been uploaded.", Toast.LENGTH_SHORT).show();
  } 

}
