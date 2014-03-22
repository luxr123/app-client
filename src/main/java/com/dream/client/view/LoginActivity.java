package com.dream.client.view;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import cn.jpush.android.api.JPushInterface;

import com.dream.client.Config;
import com.dream.client.R;
import com.dream.client.constants.ErrorCode;
import com.dream.client.entity.User;

public class LoginActivity extends Activity{
	protected static final String TAG = LoginActivity.class.getSimpleName();
	private Button btn_login;// 登录按钮
	private Button btn_regist;// 注册按钮
	private EditText user;
	private EditText pwd;
	private CheckBox remPwd;
	private CheckBox autoLogin;

	SharedPreferences preferences = null;// 记录一些信息，使用SharePreferences更方便。
	SharedPreferences.Editor editor = null;// 对SharePreferences进行操作。

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		initView(); // 初始化任务

		remPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {// 点击记住密码复选框

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				editor.putBoolean("rememberpassword", isChecked);
			}

		});
		autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {// 点击自动登录复选框

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						editor.putBoolean("autologin", isChecked);
						if (isChecked) {
							// 如果自动登录，记住密码也要选上。
							remPwd.setChecked(isChecked);
							editor.putBoolean("rememberpassword", isChecked);
						}
					}

				});
		btn_regist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new MyRegister().execute();
			}
		});
		
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new MyLogin().execute();
			}
		});
	}

	private void initView() {
		preferences = getSharedPreferences("dream-app", MODE_PRIVATE);
		editor = preferences.edit();
		user = (EditText) findViewById(R.id.user);// 获取login的用户输入框
		pwd = (EditText) findViewById(R.id.password);// 获取login的密码输入框
		remPwd = (CheckBox) findViewById(R.id.rememberpassword);
		autoLogin = (CheckBox) findViewById(R.id.autologin);

		btn_login = (Button) findViewById(R.id.login);
		btn_regist = (Button) findViewById(R.id.register);

		String name = preferences.getString("name", null);// 获取保存过的用户名。
		Boolean isRemPsd = preferences.getBoolean("rememberpassword", false);// 获取是否记住密码
		Boolean isAutoLogin = preferences.getBoolean("autologin", false);// 获取是否自动登录
		if (name != null && isRemPsd && isAutoLogin) {
			// 如果自动登录，并且记住用户名，就直接进入到MainActivity
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
		} else if (isRemPsd && name != null) {
			// 如果只是记住密码，则在用户框中输入用户名，密码框中输入密码。
			user.setText(name);
			String psd = preferences.getString("password", null);
			pwd.setText(psd);
		}
	}

	private class MyLogin extends AsyncTask<Void, Void, ErrorCode> { 
		
		private String name;
		private String password;
		String url = Config.SERVER + "login";
		private MultiValueMap<String, String> requestParams;
		
		@Override
		protected void onPreExecute() {
			System.out.println("============start login==========");
			this.name = user.getText().toString();
			this.password = pwd.getText().toString();
			
			requestParams = new LinkedMultiValueMap<String, String>();
			requestParams.add("udid", Config.udid);
			requestParams.add("name", this.name);
			requestParams.add("password", this.password);
		}

		@Override
		protected ErrorCode doInBackground(Void... params) {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestParams, requestHeaders);
			RestTemplate restTemplate = new RestTemplate(true);
			ResponseEntity<ErrorCode> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ErrorCode.class);
			return responseEntity.getBody();
		}

		@Override
		protected void onPostExecute(ErrorCode result) {
			if(ErrorCode.CODE_SUCCESS.equals(result.getErr())){
				System.out.println("============login success==========");
				toastShow("login success");
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				Config.myId = result.getMsg().split("--")[1].split(",")[1];
				Config.myName = result.getMsg().split("--")[1].split(",")[0];
				JPushInterface.setAliasAndTags(LoginActivity.this, Config.myName, Config.myChannels);
				startActivity(intent);
			}
		}

	}

	private class MyRegister extends AsyncTask<Void, Void, User> {

		@Override
		protected void onPreExecute() {
			System.out.println("============start register==========");
		}

		@Override
		protected User doInBackground(Void... params) {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);
			String url = Config.SERVER + "register";
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<User> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, User.class);
			return responseEntity.getBody();
		}

		@Override
		protected void onPostExecute(User result) {
			Intent registrtIntent = new Intent(LoginActivity.this, RegisterActivity.class);
			// 传参数
			Bundle bundle = new Bundle();
			bundle.putString("checkcode", result.getCheckcode());
			bundle.putString("guid", result.getGuid());
			// 在调用intent的方法代表批量添加
			registrtIntent.putExtras(bundle);
			startActivity(registrtIntent);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	// 封装Toast,一方面调用简单,另一方面调整显示时间只要改此一个地方即可.
	public void toastShow(String text) {
		Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
	}
}