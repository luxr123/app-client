package com.dream.client.view;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.view.Window;
import cn.jpush.android.api.JPushInterface;

import com.dream.client.Config;
import com.dream.client.R;
import com.dream.client.constants.ErrorCode;

/** 欢迎动画activity */
public class WelcomeActivity extends Activity {
	/** Called when the activity is first created. */
	protected static final String TAG = MainActivity.class.getSimpleName();

	private String m_szUniqueID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		//initID();
		newThreadToReset();
		
	}

	public void initID() {
		/*
		 * 1. The IMEI: 仅仅只对Android手机有效 采用此种方法，需要在AndroidManifest.xml中加入一个许可：
		 * android.permission.READ_PHONE_STATE，并且用户应当允许安装此应用
		 */
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String m_szImei = TelephonyMgr.getDeviceId();

		/*
		 * 2. Pseudo-Unique ID, 这个在任何Android手机中都有效 产生13 位digits
		 * 有一些特殊的情况，一些如平板电脑的设置没有通话功能，或者你不愿加入READ_PHONE_STATE许可。而你仍然想获得唯一序列号之类的东西
		 */
		String m_szDevIDShort = "35"
				+ // we make this look like a valid IMEI

				Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
				+ Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
				+ Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
				+ Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
				+ Build.USER.length() % 10;

		/*
		 * 3. The Android ID 通常被认为不可信，因为它有时为null
		 */
		String m_szAndroidID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

		/*
		 * 4. The WLAN MAC Address string
		 * 是另一个唯一ID。但是你需要为你的工程加入android.permission.ACCESS_WIFI_STATE
		 * 权限，否则这个地址会为null。
		 */
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();

		/*
		 * 5. The BT MAC Address string
		 * 只在有蓝牙的设备上运行。并且要加入android.permission.BLUETOOTH 权限.
		 */
		// BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth
		// adapter
		// m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// String m_szBTMAC = m_BluetoothAdapter.getAddress();

		/**
		 * 最终,通过拼接，或者拼接后的计算出的MD5值来产生一个结果
		 */
		// String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID +
		// m_szWLANMAC + m_szBTMAC;
		String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID + m_szWLANMAC;
		// compute md5
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
		// get md5 bytes
		byte p_md5Data[] = m.digest();
		// create a hex string
		m_szUniqueID = new String();
		for (int i = 0; i < p_md5Data.length; i++) {
			int b = (0xFF & p_md5Data[i]);
			// if it is a single digit, make sure it have 0 in front (proper
			// padding)
			if (b <= 0xF)
				m_szUniqueID += "0";
			// add number to string
			m_szUniqueID += Integer.toHexString(b);
		} // hex string to uppercase
		m_szUniqueID = m_szUniqueID.toUpperCase();
		// 9DDDF85AFF0A87974CE4541BD94D5F55
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
			//Config.udid = m_szUniqueID;
			requestParams.add("udid", Config.udid);
			
			System.out.println(requestParams.getFirst("udid"));
//			requestParams.add("udid", m_szUniqueID);
		}

		@Override
		protected ErrorCode doInBackground(Void... params) {
			HttpHeaders requestHeaders = new HttpHeaders();
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(
					requestParams, requestHeaders);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<ErrorCode> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ErrorCode.class);
			return responseEntity.getBody();
		}

		@Override
		protected void onPostExecute(ErrorCode result) {
			if (result.isSucceed()) {
				Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
				String id = result.getMsg().split("--")[1].split(",")[1];
				String userName = result.getMsg().split("--")[1].split(",")[0];
				JPushInterface.setAliasAndTags(WelcomeActivity.this, userName, Config.myChannels);
				intent.putExtra("id", id);
				intent.putExtra("username", userName);
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