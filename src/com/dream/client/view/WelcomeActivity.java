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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import cn.jpush.android.api.JPushInterface;

import com.dream.client.Config;
import com.dream.client.R;
import com.dream.client.constants.ErrorCode;

/** 欢迎动画activity */
public class WelcomeActivity extends Activity {
	/** Called when the activity is first created. */
	protected static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		//initID();
		newThreadToReset();
	}

	

	private void resetAliasAndTags() {
		// download from pushtalk server
		new MyLogin().execute();
	}

	private void newThreadToReset() {
		new Thread() {
			public void run() {
				resetAliasAndTags();
			}
		}.start();
	}

	private class MyLogin extends AsyncTask<Void, Void, ErrorCode> {
		private String url = Config.SERVER + "udidLogin";
		private MultiValueMap<String, String> requestParams;

		@Override
		protected void onPreExecute() {
			System.out.println("============start login==========");
			requestParams = new LinkedMultiValueMap<String, String>();
			requestParams.add("udid", Config.udid);
			System.out.println("Welcome uuid:" +  Config.udid);
		}

		@Override
		protected ErrorCode doInBackground(Void... params) {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestParams,
					requestHeaders);
			RestTemplate restTemplate = new RestTemplate(true);
			ResponseEntity<ErrorCode> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ErrorCode.class);
			return responseEntity.getBody();
		}

		@Override
		protected void onPostExecute(ErrorCode result) {
			if (result.isSucceed()) {
				Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
				Config.myId = result.getMsg().split("--")[1].split(",")[1];
				Config.myName = result.getMsg().split("--")[1].split(",")[0];
				JPushInterface.setAliasAndTags(WelcomeActivity.this, Config.myName, Config.myChannels);
				startActivity(intent);
			} else {
				final Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
				// 系统会为需要启动的activity寻找与当前activity不同的task;
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// 创建一个新的线程来显示欢迎动画，指定时间后结束，跳转至指定界面
				new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(3000);
							// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
							getApplicationContext().startActivity(intent);
							finish();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
	}

}