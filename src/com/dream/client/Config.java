package com.dream.client;

import java.util.HashSet;
import java.util.Set;

public class Config {

    public static boolean IS_TEST_MODE = true;
    
    public static String VERSION = "0.1.0";
    
    public static String WEB_JS_MODULE = "PushUser";
    
    public static final boolean NOTIFICATION_NEED_SOUND = true;
    public static final boolean NOTIFICATION_NEED_VIBRATE = true;
    
    //public static String SERVER = "http://192.168.1.102:8080/app-web/user/";
    //public static String SERVER2 = "http://192.168.1.102:8080/app-web/task/";
    public static String SERVER = "http://192.168.2.106:8080/app-web/user/";
    public static String SERVER2 = "http://192.168.2.106:8080/app-web/task/";
   // public static String SERVER = "http://127.0.0.1:8080/app-web/user/";
   // public static String SERVER2 = "http://127.0.0.1:8080/app-web/task/";
    public static String udid;
    public static String myName;
    public static String myId;
    public static Set<String> myChannels = new HashSet<String>();
    public static final String TAG = "all";
    
    public static boolean isBackground = true;
    static{
    	myChannels.add(TAG);
    };

}

