package com.dream.client.view;


import com.dream.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class JpushActivity extends Activity{
	private TextView mText;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	     setContentView(R.layout.activity_main);
	     mText = (TextView)findViewById(R.id.show);
	     mText.setText("显示对应数据...");
	    /* Intent intent = getIntent(); 
	     if (null != intent) {
	    	 Bundle extras = intent.getExtras(); 
		     mText.setText(extras.getString("message"));
	     }*/
	     
	}
	

}
