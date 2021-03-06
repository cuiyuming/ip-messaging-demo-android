package com.twilio.ipmessaging.demo;

import java.io.IOException;
import java.net.URLEncoder;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.twilio.ipmessaging.Constants.StatusListener;
import com.twilio.ipmessaging.TwilioIPMessagingSDK;

import com.twilio.ipmessaging.demo.BasicIPMessagingClient.LoginListener;
import com.twilio.ipmessaging.demo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity implements LoginListener {
	private static final Logger logger = Logger.getLogger(LoginActivity.class);
	private static final String AUTH_PHP_SCRIPT = "http://884f2ec7.ngrok.io/ipmToken";

	private static final String DEFAULT_CLIENT_NAME = "f096c989-7102-4126-b232-24d178677595";
	private static final String DEFAULT_CONTACT_NAME = "48ac0082-867a-457f-8828-bf68d4d86719";

	private ProgressDialog progressDialog;
	private Button login;
	private Button logout;
	private CheckBox gcmCxbx;
	private Button stopGCM;
	private String capabilityToken = null;
	private EditText clientNameTextBox;
	private BasicIPMessagingClient chatClient;
	private String endpoint_id = "";
	public static String local_author = DEFAULT_CLIENT_NAME;
    private String PROJECT_NUMBER = "538672672293";
    private EditText etRegId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		
		this.clientNameTextBox = (EditText) findViewById(R.id.client_name);
		this.clientNameTextBox.setText(DEFAULT_CLIENT_NAME);
		this.endpoint_id = Secure.getString(this.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

		this.login = (Button) findViewById(R.id.register);
		this.login.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				String idChosen = clientNameTextBox.getText().toString();
				String device_name = "android";

				StringBuilder url = new StringBuilder();
				url.append(AUTH_PHP_SCRIPT);
				url.append("?identity=");
				url.append(URLEncoder.encode(idChosen));
				url.append("&device_id=" + URLEncoder.encode(device_name));
//				url.append(clientNameTextBox.getText().toString());
//				url.append("&endpoint_id=" + LoginActivity.this.endpoint_id);
//				logger.e("url string : " + url.toString());
				new GetCapabilityTokenAsyncTask().execute(url.toString());
				TwilioApplication application = (TwilioApplication) LoginActivity.this.getApplication();
				application.setUser(idChosen);

			}
		});

		this.logout = (Button) findViewById(R.id.logout);
		etRegId = (EditText) findViewById(R.id.etRegId);
		chatClient = TwilioApplication.get().getBasicClient();

		gcmCxbx = (CheckBox) findViewById(R.id.gcmcxbx);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_about) {
			showAboutDialog();
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAboutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		builder.setTitle("About").setMessage("Version: " + TwilioIPMessagingSDK.getVersion()).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog aboutDialog = builder.create();
		aboutDialog.show();
	}

	private class GetCapabilityTokenAsyncTask extends AsyncTask<String, Void, String> {
		private String urlString;
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			LoginActivity.this.chatClient.doLogin(capabilityToken, LoginActivity.this, urlString);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			LoginActivity.this.progressDialog = ProgressDialog.show(LoginActivity.this, "",
					"Logging in. Please wait...", true);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				urlString = params[0];
				capabilityToken = HttpHelper.httpGet(params[0]);
				chatClient.setCapabilityToken(capabilityToken);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return capabilityToken;
		}
	}

	@Override
	public void onLoginStarted() {
		logger.d("Log in started");
	}
	
		@Override
		public void onLoginFinished() {
			if (gcmCxbx.isChecked()) {
				getGCMRegistrationToken();
			}
			LoginActivity.this.progressDialog.dismiss();
			Intent intent = new Intent(this, ChannelActivity.class);
			this.startActivity(intent);
		}

	@Override
	public void onLoginError(String errorMessage) {
		LoginActivity.this.progressDialog.dismiss();
		logger.e("Error logging in : " + errorMessage);
		Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onLogoutFinished() {
		// TODO Auto-generated method stub
	}
	
	public void getGCMRegistrationToken() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String token = "";
				InstanceID instanceId = InstanceID.getInstance(getApplicationContext());
				try {
					token = instanceId.getToken(PROJECT_NUMBER, null);
					chatClient.setGCMToken(token);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return token;
			}

			@Override
			protected void onPostExecute(final String token) {
				etRegId.setText(token);
				chatClient.getIpMessagingClient().registerGCMToken(token, new StatusListener() {

					@Override
					public void onError() {
						logger.w("GCM registration not successful");
					}

					@Override
					public void onSuccess() {
						logger.d("GCM registration successful");
					}
				});
			}
	        }.execute(null, null, null);
	    }

}
