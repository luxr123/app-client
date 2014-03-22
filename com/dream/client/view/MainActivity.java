package com.dream.client.view;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

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
import com.dream.client.entity.UserTask;

import encode.BASE64Decoder;


public class MainActivity extends Activity {

	private TextView mText;
	private ImageView iconShow;
	
	private ImageButton taskWrite;
	
	private static String id;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		iconShow = (ImageView) this.findViewById(R.id.icon);
		iconShow.setAdjustViewBounds(true); // 调整图片的大小

		mText = (TextView)this.findViewById(R.id.username);
		taskWrite = (ImageButton) this.findViewById(R.id.activity_card_write);
		Intent intent = getIntent(); 
	     if (null != intent) {
	    	 Bundle extras = intent.getExtras(); 
	    	 id = extras.getString("id");
		     mText.setText("Hi," + extras.getString("username"));
	     }
	     registerMessageReceiver();
	     new LoginJpush().execute();
	     
	     new MyIcon(id).execute();
	     
	     taskWrite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(MainActivity.this, MulPhoyoActivity.class);
//				// 传参数
//				Bundle bundle = new Bundle();
//				bundle.putString("id", id);
//				// 在调用intent的方法代表批量添加
//				intent.putExtras(bundle);
//				startActivityForResult(intent, 1);
			}
		});
	     
	}
	
	private class PublishTask extends AsyncTask<Void, Void, ErrorCode> {
		UserTask userTask = new UserTask();
		
		@Override
		protected void onPreExecute() {
			userTask.setCreatetime(new Date());
			
			super.onPreExecute();
		}

		@Override
		protected ErrorCode doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(ErrorCode result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
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
	



	private class MyIcon extends AsyncTask<Void, Void, String> {
		private String id;
		
		public MyIcon(String id) {
			super();
			this.id = id;
		}

		public String getId() {
			return id;
		}

		String url = Config.SERVER + "showIcon";
		private MultiValueMap<String, String> requestParams;

		@Override
		protected void onPreExecute() {
			requestParams = new LinkedMultiValueMap<String, String>();
			requestParams.add("id", this.getId());
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpHeaders requestHeaders = new HttpHeaders();
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestParams, requestHeaders);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
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
}