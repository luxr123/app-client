package com.dream.client.view;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.dream.client.Config;
import com.dream.client.R;
import com.dream.client.constants.ErrorCode;

public class ShowActivity extends Activity{
	private TextView mText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	     setContentView(R.layout.activity_main);
	     mText = (TextView)findViewById(R.id.show);
	     Intent intent = getIntent(); 
	     if (null != intent) {
	    	 Bundle extras = intent.getExtras(); 
		     mText.setText(extras.getString("login"));
	     }
	     registerMessageReceiver();
	     new LoginJpush().execute();
	     
	}
	

	
	
	private class LoginJpush extends AsyncTask<Void, Void, ErrorCode> {
		private String url = Config.SERVER+ "loginJpush";
		private MultiValueMap<String, String> requestParams;

		@Override
		protected void onPreExecute() {
			System.out.println("============start jpush==========");
			requestParams = new LinkedMultiValueMap<String, String>();
			requestParams.add("udid", Config.udid);
		}
		
		@Override
		protected ErrorCode doInBackground(Void... params) {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestParams, requestHeaders);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<ErrorCode> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ErrorCode.class);
			return responseEntity.getBody();
		}
		
		@Override
		protected void onPostExecute(ErrorCode result) {
			/*if(result.isSucceed()){
				System.out.println("=====jpush success =====");
			}*/
		}
		
	}
	
    @Override
	protected void onResume() {
		Config.isBackground = false;
		super.onResume();
	}


	@Override
	protected void onPause() {
		Config.isBackground = true;
		super.onPause();
	}


	@Override
	protected void onDestroy() {
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}
	
	//for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.dream.client.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	
	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {	
				//do something...
				/*String messge = intent.getStringExtra(KEY_MESSAGE);
	            
	            //String extras = intent.getStringExtra(KEY_EXTRAS);
				Intent intent1 = new Intent(ShowActivity.this, JpushActivity.class);
				intent1.putExtra("msg", messge);				
				startActivity(intent1); */
			}
		}
	}
	


	
}
