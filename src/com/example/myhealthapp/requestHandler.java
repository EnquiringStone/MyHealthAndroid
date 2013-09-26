package com.example.myhealthapp;

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
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.myhealthapp.conn.MySSLSocketFactory;

import android.util.Base64;

public class requestHandler {

	static URI url = null;
	static String host = "http://145.37.72.146:1234";
	static String params = "";
	static String response = "";
	static Boolean running_flag = false;
	static String token = "";
	static boolean failed;

	Thread thread = new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				failed = false;
				callWebService();
			} catch (Exception e) {
				failed = true;
				e.printStackTrace();
			} finally {
				requestHandler.running_flag = false;
				interupt();
			}
		}
	});

	public void runrequest() {
		thread.start();
	}

	protected void interupt() {
		thread.interrupt();
	}
	
	public void setHost(String host) {
		requestHandler.host = host;
	}

	public void setURL(String apiUrl) {
		try {
			requestHandler.url = new URI(apiUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

		public void setParams(String params) {
		requestHandler.params = params;
	}

	public void callWebService() throws IOException {		
		HttpClient httpclient = MySSLSocketFactory.getNewHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet(
				requestHandler.url));
		StatusLine statusLine = response.getStatusLine();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		response.getEntity().writeTo(out);
		out.close();
		requestHandler.response = out.toString();
		if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
			response.getEntity().getContent().close();
		}
	}

	public String encode(String value) throws UnsupportedEncodingException {
		return Base64.encodeToString(value.getBytes("UTF-8"), Base64.NO_WRAP);
	}

	public static Map<String, String> parseJson() {
		String[] splits = response.toString()
				.substring(1, response.toString().length() - 1).split(",");
		Map<String, String> JsonValues = new HashMap<String, String>();
		for (String split : splits) {
			JsonValues.put(split.split(":")[0].replaceAll("\"", ""),
					split.split(":")[1].replaceAll("\"", ""));
		}
		return JsonValues;
	}
}
