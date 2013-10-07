package com.example.myhealthapp;

import java.io.File;
import java.io.IOException;

import com.example.myhealthapp.conn.RequestHandler;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.util.Log;

public class UrinetestActivity extends Activity {

	private RequestHandler handler;
	static int count=0;
	static int TAKE_PHOTO_CODE = 0;
	final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/myHealth/"; 
    File newdir = new File(dir);
    File newFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_urinetest);
		newdir.mkdirs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.urinetest, menu);
		return true;
	}
	
	public void takePicture(View view)
	{
        count++;
        String file = dir+count+".jpg";
        newFile = new File(file);
        try {
            newFile.createNewFile();
        } catch (IOException e) {}       

        Uri outputFileUri = Uri.fromFile(newFile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
	        Log.d("DEBUG", "Pic saved, trying to send to server now...");
	        handler = RequestHandler.getRequestHandler();
	        handler.setName("urine");
	        handler.execute(newFile);
	        Log.d("DEBUG", "Done!");
	    }
	}

}
