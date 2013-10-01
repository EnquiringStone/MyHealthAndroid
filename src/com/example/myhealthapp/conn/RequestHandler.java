package com.example.myhealthapp.conn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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


import android.os.AsyncTask;
import android.util.Base64;

public class RequestHandler extends AsyncTask<String, Void, Boolean> {

	URI url = null;
	public String host = "https://145.37.72.146:1234";
	String params = "";
	String response = "";
	public Boolean running_flag = false;
	public String token = "";

	public RequestHandler(){
		try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
	protected Boolean doInBackground(String... arg0) {
		try {
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
