package com.example.myhealthapp.conn;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.example.myhealthapp.conn.BluetoothHandler.ConnectedThread;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

public class BluetoothListener extends BluetoothHandler {
	
	private AcceptThread bluetoothListener = null;
	private ConnectedThread connection = null;

	public BluetoothListener(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
	}
	
	public void cancelConnection(){
		if (connection != null){
			//bluetoothListener.cancel();
			connection.cancel();
			//bluetoothListener = null;
			connection = null;
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (!DeviceHasBluetooth()) {
			Toast.makeText(activity, "device does not support bluetooth",
					Toast.LENGTH_LONG).show();
		} else {
			if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
				MakeDiscoverable(activity);
			}
		}
		while (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		bluetoothListener = new AcceptThread();
		bluetoothListener.start();
		return null;

	}
	
	@Override
	public void manageConnectedSocket(BluetoothSocket socket){
		connection = new ConnectedThread(socket);
		connection.run();
	}

	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			try {
				// MY_UUID is the app's UUID string, also used by the client
				// code
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
						"MyHealth", UUID_RFCOMM_GENERIC);
			} catch (IOException e) {
			}
			mmServerSocket = tmp;
		}

		public void run() {
			BluetoothSocket socket = null;
			while (true) {
				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					break;
				}
				
				if (socket != null) {
					Log.i("DEBUG", "Connection established");
					manageConnectedSocket(socket);
					try {
						mmServerSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
			}
		}
	}
}
