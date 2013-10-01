package com.example.myhealthapp.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
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
	
	public BluetoothAdapter getAdapter(){
		return this.mBluetoothAdapter;
	}
	
	public Set<BluetoothDevice> GetDevices(BluetoothAdapter adapter){
		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		return devices;
	}

	@Override
	protected Void doInBackground(Void... params) {
		return null;
	}

	public void manageConnectedSocket(BluetoothSocket socket) {
		
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
	        	Log.d("DEBUG", "Trying to read");
	            try
	            {
	                bytes = mmInput.read(buffer);
	                String string = new String(buffer);
	                string = string.split(""+string.charAt(string.length()-1))[0];
	                Log.d("DEBUG", "Received : "+ string);
	            }
	            
	            catch(IOException e) { break; }
	        }
	    }

	    public void write(byte[] bytes)
	    {  
	        try
	        {
	            mmOutput.write(bytes);
	            Log.d("DEBUG", "Bytes Sent");
	        }
	        catch (IOException e) { Log.d("DEBUG", "Bytes Not Sent"); }
	    }

	    public void cancel()
	    {
	        try
	        {
	            Log.d("DEBUG", "Attempting to Close ConnectedThread Socket");
	            mmSocket.close();
	            Log.d("DEBUG", "ConnectedThread Socket Closed");
	        }
	        catch(IOException e) { Log.d("DEBUG", "ConnectedThread Failed To Close"); }
	    }

	}
}
