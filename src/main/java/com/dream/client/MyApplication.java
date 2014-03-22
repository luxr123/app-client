package com.dream.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import cn.jpush.android.api.JPushInterface;

import com.dream.client.util.Logger;
import com.dream.client.util.MyPreferenceManager;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class MyApplication extends Application {
	
	private HttpClient httpClient;
	public boolean isLogin = false;
	
	private static final String TAG = "MyApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		httpClient = this.createHttpClient();
		
		Logger.i(TAG, "PushUser client version: " + Config.VERSION);
        MyPreferenceManager.init(getApplicationContext());
        Logger.i(TAG, "Current PushUser server: " + Config.SERVER);

//        Config.udid = AndroidUtil.getUdid(getApplicationContext());
        Config.udid = initUDID();
        Logger.d(TAG, "My udid: " + Config.udid);
        JPushInterface.setDebugMode(true); 
        JPushInterface.init(this);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		this.shutdownHttpClient();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		this.shutdownHttpClient();
	}

	// 创建HttpClient实例
	private HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
				params, schReg);

		return new DefaultHttpClient(connMgr, params);
	}

	// 关闭连接管理器并释放资源
	private void shutdownHttpClient() {
		if (httpClient != null && httpClient.getConnectionManager() != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	// 对外提供HttpClient实例
	public HttpClient getHttpClient() {
		return httpClient;
	}
	
	public String initUDID() {
		
		String m_szUniqueID = new String();
		/*
		 * 1. The IMEI: 仅仅只对Android手机有效
		 * 采用此种方法，需要在AndroidManifest.xml中加入一个许可：
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

				Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
				+ Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
				+ Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
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
//		BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
//		m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//		String m_szBTMAC = m_BluetoothAdapter.getAddress();

		/**
		 * 最终,通过拼接，或者拼接后的计算出的MD5值来产生一个结果
		 */
//		String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID + m_szWLANMAC + m_szBTMAC;
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
		
		for (int i = 0; i < p_md5Data.length; i++) {
			int b = (0xFF & p_md5Data[i]);
			// if it is a single digit, make sure it have 0 in front (proper
			// padding)
			if (b <= 0xF)
				m_szUniqueID += "0";
			// add number to string
			m_szUniqueID += Integer.toHexString(b);
		} // hex string to uppercase
		return m_szUniqueID.toUpperCase();
	}
}
