package com.example.myhealthapp.conn;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class RequestHandler extends AsyncTask<File, Void, Boolean> {

	URI url = null;
	public String host = "https://145.37.50.69";
	String params = "";
	String response = "";
	public Boolean running_flag = false;
	public String token = "";
	private String loginToken="";
	private String name="";
	private static RequestHandler instance;

	private RequestHandler(){
		try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
	}
	
	public static RequestHandler getRequestHandler() {
		if(instance == null) {
			instance = new RequestHandler();
		}
		if(instance.getLoginToken() != "") {
			String loginToken = instance.getLoginToken();
			instance = new RequestHandler();
			instance.loginToken = loginToken;
		}
		return instance;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setURL(String apiUrl) {
		try {
			this.url = new URI(apiUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void setParams(String params) {
		this.params = params;
	}
	
	public void setLoginToken(String loginToken)
	{
		try
		{
			this.loginToken=encode(loginToken);
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
	public String getLoginToken() {
		return this.loginToken;
	}
	
	public void setName(String name)
	{
		try
		{
			this.name=encode(name);
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	public void callWebService() throws IOException {
		HttpClient httpclient = MySSLSocketFactory.getNewHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet(this.url));
		StatusLine statusLine = response.getStatusLine();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		response.getEntity().writeTo(out);
		out.close();
		this.response = out.toString();
		this.setRunning_flag(false);
		if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
			response.getEntity().getContent().close();
			this.response = out.toString();
			this.setRunning_flag(false);
		}
	}
	
	public void uploadFile(File file) throws IOException {
		HttpClient httpclient = MySSLSocketFactory.getNewHttpClient();
		HttpPost httpPost = new HttpPost(this.host + "/api");
		if(file.exists()) Log.d("DEBUG", "file exists...");
		MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();        
	    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    multipartEntity.addPart("file", new FileBody(file));
	    multipartEntity.addTextBody("login_token", loginToken);
	    multipartEntity.addTextBody("name", name);
	    multipartEntity.addTextBody("method", encode("uploadFile"));
	    httpPost.setEntity(multipartEntity.build());
		HttpResponse execute = httpclient.execute(httpPost);
		InputStream content = execute.getEntity().getContent();
		BufferedReader buffer = new BufferedReader(
			new InputStreamReader(content));
		String s = "";
		while ((s = buffer.readLine()) != null)
			response += s;
		Log.d("DEBUG", "response: "+response);
	}

	public String encode(String value) throws UnsupportedEncodingException {
		return Base64.encodeToString(value.getBytes("UTF-8"), Base64.NO_WRAP);
	}

	public Map<String, String> parseJson() {
		String[] splits = this.response.toString()
				.substring(1, this.response.toString().length() - 1).split(",");
		Map<String, String> JsonValues = new HashMap<String, String>();
		for (String split : splits) {
			JsonValues.put(split.split(":")[0].replaceAll("\"", ""),
					split.split(":")[1].replaceAll("\"", ""));
		}
		return JsonValues;
	}

	@Override
	protected Boolean doInBackground(File... arg0) {
		try {
			if(arg0.length>0)
				uploadFile(arg0[0]);
			else
				callWebService();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void setRunning_flag(boolean running_flag) {
		this.running_flag = running_flag;

	}

	public boolean getRunning_flag() {
		return this.running_flag;
	}

	@Override
	protected void onPreExecute() {
		setRunning_flag(true);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		setRunning_flag(false);
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	
}
