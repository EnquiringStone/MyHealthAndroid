package com.example.myhealthapp;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	// Values for username and password at the time of the login attempt.
	String url;
	String result;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private EditText mHostView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUsernameView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);
		mHostView = (EditText) findViewById(R.id.host);
		
		mHostView.setText(requestHandler.host);

		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin(textView);
							return true;
						}
						return false;
					}
				});

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin(view);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void attemptLogin(View v) {
		String username = mUsernameView.getText().toString();
		String password = mPasswordView.getText().toString();
		requestHandler.host = mHostView.getText().toString();
				
		String url = "";
		String name = "user";
		String method = "login";

		requestHandler handler = new requestHandler();

		StringBuffer urlbuffer = new StringBuffer(requestHandler.host);

		try {
			username = handler.encode(username);
			password = handler.encode(password);
			name = handler.encode(name.toString());
			method = handler.encode(method);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		urlbuffer.append("/api?name=").append(name).append("&method=")
				.append(method).append("&username=").append(username)
				.append("&password=").append(password);

		url = urlbuffer.toString();
		handler.setURL(url);
		requestHandler.running_flag = true;
		handler.runrequest();

		Boolean act = true;
		while (act) {
			if (!requestHandler.running_flag) {
				if (!requestHandler.failed) {
					Log.i("DEBUG",
							"Start handling loginrequest results and runningflag = "
									+ requestHandler.running_flag);

					Map<String, String> JsonValues = requestHandler.parseJson();
					if (JsonValues.get("error").equals("false")
							&& JsonValues.get("login_token").length() > 3) {
						Log.i("DEBUG", "Generating token");
						requestHandler.token = JsonValues.get("login_token");
						Intent myIntent = new Intent(v.getContext(),
								MenuActivity.class);
						Toast.makeText(LoginActivity.this, "Login Successful",
								Toast.LENGTH_LONG).show();
						startActivityForResult(myIntent, 0);
					} else if (JsonValues.get("error").equals("true")) {
						Log.i("DEBUG", "Display error message");
						Toast.makeText(LoginActivity.this,
								JsonValues.get("message"), Toast.LENGTH_LONG)
								.show();
					} else {
						Log.i("DEBUG",
								"error value = " + JsonValues.get("error"));
						Toast.makeText(LoginActivity.this,
								"Something went wrong :(", Toast.LENGTH_LONG)
								.show();
					}

					act = false;

				} else if (requestHandler.failed) {
					act = false;

					try {
						Map<String, String> JsonValues = requestHandler.parseJson();
						Toast.makeText(LoginActivity.this,
								JsonValues.get("message"), Toast.LENGTH_LONG)
								.show();
					} catch (Exception e) {
						Toast.makeText(LoginActivity.this,
								"Something went wrong", Toast.LENGTH_LONG)
								.show();
					}
				}

			}
		}
	}

}
