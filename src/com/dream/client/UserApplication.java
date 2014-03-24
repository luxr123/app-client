package com.dream.client;

import com.dream.client.util.AndroidUtil;
import com.dream.client.util.Logger;

import cn.jpush.android.api.JPushInterface;
import android.app.Application;
import com.dream.client.util.MyPreferenceManager;

public class UserApplication extends Application{
	private static final String TAG = "UserApplication";
	
	@Override
	public void onCreate() {
		Logger.d(TAG, "onCreate");
        super.onCreate();
        
        Logger.i(TAG, "PushUser client version: " + Config.VERSION);
        MyPreferenceManager.init(getApplicationContext());
        Logger.i(TAG, "Current PushUser server: " + Config.SERVER);

        Config.udid = AndroidUtil.getUdid(getApplicationContext());
        Logger.d(TAG, "My udid: " + Config.udid);
        JPushInterface.setDebugMode(true); 
        JPushInterface.init(this);
	}
	
	
}
