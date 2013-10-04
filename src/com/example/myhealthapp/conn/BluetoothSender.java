package com.example.myhealthapp.conn;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

public class BluetoothSender extends BluetoothHandler {

	private ConnectedThread connection = null;
	String data = "";

	public BluetoothSender(Activity a) {
		super(a);
	}

	public void cancelConnection() {
		if (connection != null) {
			connection.cancel();
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

		Set<BluetoothDevice> devices = GetDevices(mBluetoothAdapter);
		for (BluetoothDevice bt : devices) {
			if (bt.getName().equals("GT-I9505")
					|| bt.getName().equalsIgnoreCase("Galaxy S4 Arjan")) {
				ConnectThread bluetoothconnector = new ConnectThread(bt);
				bluetoothconnector.start();
			}
		}
		return null;

	}

	/*
	 * Returns wheter it passed or failed
	 */
	public boolean setData(String data) {
		if (this.data.equals("")) {
			this.data = data;
			return true;
		}
		return false;
	}

	public String getData() {
		String tmp = new String(this.data);
		this.data = "";
		return tmp;
	}

	@Override
	public void manageConnectedSocket(BluetoothSocket socket) {
		connection = new ConnectedThread(socket);
		while (socket.isConnected()) {			
			String s = getData();
			if (!s.equals("")) {
				connection.write(s.getBytes());
			} else {
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device
						.createRfcommSocketToServiceRecord(UUID_RFCOMM_GENERIC);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
				Log.i("DEBUG", "Connected to listening device");
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				cancel();
				return;
			}

			// Do work to manage the connection (in a separate thread)
			manageConnectedSocket(mmSocket);
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

}
