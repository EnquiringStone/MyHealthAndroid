package com.example.myhealthapp.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class BluetoothHandler extends AsyncTask<Void, Void, Void> {
	Activity activity;
	BluetoothAdapter mBluetoothAdapter;
	BluetoothServerSocket mBluetoothSocket;
	UUID UUID_RFCOMM_GENERIC = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	final private String TAG = "BluetoothHandler";

	public BluetoothHandler(Activity a) {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		activity = a;

		try {
			mBluetoothSocket = mBluetoothAdapter
					.listenUsingRfcommWithServiceRecord("Bluetooth service",
							UUID_RFCOMM_GENERIC);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Boolean DeviceHasBluetooth() {
		Boolean b = true;

		if (mBluetoothAdapter == null) {
			b = false;
		}
		return b;
	}

	public void Accept() throws IOException {
		mBluetoothSocket.accept();
	}

	public void EnableBluetooth() {
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(enableBtIntent, 0);
		}
	}

	public void MakeDiscoverable(Activity a) {
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
		activity.startActivity(discoverableIntent);
	}



	@Override
	protected Void doInBackground(Void... params) {
		Log.i("DEBUG", ""+mBluetoothAdapter.getScanMode());
		if (!DeviceHasBluetooth()){
			Toast.makeText(activity, "device does not support bluetooth",
					Toast.LENGTH_LONG).show();
		}
		else{
			if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
				MakeDiscoverable(activity);
			}			
		}
		while(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.i("DEBUG", "I'm here with var: "+mBluetoothAdapter.getScanMode());
		AcceptThread bluetoothListener = new AcceptThread();
		bluetoothListener.start();
		return null;
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
					// TODO work to manage the connection (in a separate thread)
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

	public void manageConnectedSocket(BluetoothSocket socket) {

	}

	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(UUID_RFCOMM_GENERIC);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	        manageConnectedSocket(mmSocket);
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	class ConnectedThread extends Thread {

	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInput;
	    private final OutputStream mmOutput;

	    public ConnectedThread(BluetoothSocket socket)
	    {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;

	        try
	        {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        }
	        catch(IOException e) { }

	        mmInput = tmpIn;
	        mmOutput = tmpOut;
	    }

	    public void run()
	    {
	        byte[] buffer = new byte[1024];
	        int bytes;

	        while(true)
	        {
	            try
	            {
	                bytes = mmInput.read(buffer);
	                Log.d(TAG, "Received : "+bytes);
	            }
	            catch(IOException e) { break; }
	        }
	    }

	    public void write(byte[] bytes)
	    {  
	        try
	        {
	            mmOutput.write(bytes);
	            Log.d(TAG, "Bytes Sent");
	        }
	        catch (IOException e) { Log.d(TAG, "Bytes Not Sent"); }
	    }

	    public void cancel()
	    {
	        try
	        {
	            Log.d(TAG, "Attempting to Close ConnectedThread Socket");
	            mmSocket.close();
	            Log.d(TAG, "ConnectedThread Socket Closed");
	        }
	        catch(IOException e) { Log.d(TAG, "ConnectedThread Failed To Close"); }
	    }

	}
}
