package com.dream.client.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dream.client.Config;
import com.dream.client.R;
import com.dream.client.constants.ErrorCode;
import com.dream.client.util.StringUtils;
import com.dream.mulimage.view.MulMainActivity;

import encode.BASE64Decoder;

public class MainActivity extends Activity {

	private TextView mText;
	private ImageView iconShow;

	private ImageButton taskWrite;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		iconShow = (ImageView) this.findViewById(R.id.icon);
		iconShow.setAdjustViewBounds(true); // 调整图片的大小

		mText = (TextView) this.findViewById(R.id.username);
		taskWrite = (ImageButton) this.findViewById(R.id.activity_card_write);
		mText.setText(" Welcome, " + Config.myName);
		registerMessageReceiver();
		new LoginJpush().execute();

		new MyIcon().execute();

		taskWrite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						MulMainActivity.class);		
				startActivityForResult(intent, 1);
			}
		});

	}

	private class LoginJpush extends AsyncTask<Void, Void, ErrorCode> {
		private String url = Config.SERVER + "loginJpush";
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
			requestHeaders
					.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(
					requestParams, requestHeaders);
			RestTemplate restTemplate = new RestTemplate(true);
			ResponseEntity<ErrorCode> responseEntity = restTemplate.exchange(
					url, HttpMethod.POST, requestEntity, ErrorCode.class);
			return responseEntity.getBody();
		}

		@Override
		protected void onPostExecute(ErrorCode result) {
			/*
			 * if(result.isSucceed()){
			 * System.out.println("=====jpush success ====="); }
			 */
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

	// for receive customer msg from jpush server
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
				// do something...
			}
		}
	}

	private class MyIcon extends AsyncTask<Void, Void, String> {

		String url = Config.SERVER + "getCompressIcon";
		private MultiValueMap<String, String> requestParams;

		@Override
		protected void onPreExecute() {
			requestParams = new LinkedMultiValueMap<String, String>();
			requestParams.add("id", Config.myId);
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpHeaders requestHeaders = new HttpHeaders();
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(
					requestParams, requestHeaders);
			RestTemplate restTemplate = new RestTemplate(true);
System.out.println("---------url2-----------"+url);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url,HttpMethod.POST, requestEntity, String.class);
			return responseEntity.getBody();
		}

		@Override
		protected void onPostExecute(String result) {
			byte[] b = null;
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(result);
				ByteArrayInputStream bais = null;
				bais = new ByteArrayInputStream(b);
				iconShow.setImageDrawable(Drawable.createFromStream(bais, "b"));// 把图片设置到ImageView对象中
				bais.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			StringUtils.toastShow(MainActivity.this, "task publish success");
		}

	}
}