package com.dream.client.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.dream.client.Config;
import com.dream.client.Constants;
import com.dream.client.util.AndroidUtil;
import com.dream.client.util.Logger;
import com.dream.client.util.StringUtils;
import com.dream.client.view.ShowActivity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器 
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "MyReceiver";
    
    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        
        Bundle bundle = intent.getExtras();
        Logger.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + AndroidUtil.printBundle(bundle));
        
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Logger.d(TAG, "Push用户注册成功");
            processCustomMessage(context, bundle);
            
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Logger.d(TAG, "接受到推送下来的自定义消息");
            
            // Push Talk messages are push down by custom message format
            processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Logger.d(TAG, "接受到推送下来的通知");
            
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Logger.d(TAG, "用户点击打开了通知");
/*        	Intent i = new Intent(context, JpushActivity.class);
        	i.putExtra("msg",bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(i);*/
            
        } else {
            Logger.d(TAG, "Unhandled intent - " + intent.getAction());
        }

    }
    

    
   private void processCustomMessage(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);

        if (StringUtils.isEmpty(title)) {
            Logger.w(TAG, "Unexpected: empty title (friend). Give up");
            return;
        }
        
        //boolean needIncreaseUnread = true;
        
        if (title.equalsIgnoreCase(Config.myName)) {
            Logger.d(TAG, "Message from myself. Give up");
           // needIncreaseUnread = false;
            if (!Config.IS_TEST_MODE) {
                return;
            }
        }
        
        String tag = Config.TAG;
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);     
        
        // Send message to UI (Webview) only when UI is up 
        //if (!Config.isBackground) 
        {
            Intent msgIntent = new Intent(ShowActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(Constants.KEY_MESSAGE, message);
            msgIntent.putExtra(Constants.KEY_TITLE, title);
            if (null != tag) {
                msgIntent.putExtra(Constants.KEY_CHANNEL, tag);
            }
            
            JSONObject all = new JSONObject();
            try {
                all.put(Constants.KEY_TITLE, title);
                all.put(Constants.KEY_MESSAGE, message);
                all.put(Constants.KEY_EXTRAS, new JSONObject(extras));
            } catch (JSONException e) {
            }
            msgIntent.putExtra("all", all.toString());
            
            context.sendBroadcast(msgIntent);
        }       
        
        NotificationHelper.showMessageNotification(context, nm, title, message, tag);
    }
}
