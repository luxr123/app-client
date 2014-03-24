package com.dream.mulimage.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.alibaba.fastjson.JSON;
import com.dream.adapter.GridImageAdapter;
import com.dream.client.Config;
import com.dream.client.R;
import com.dream.client.constants.ErrorCode;
import com.dream.client.entity.UserTask;
import com.dream.client.util.Logger;
import com.dream.client.util.StringUtils;
import com.dream.client.view.JpushActivity;
import com.dream.client.view.LoginActivity;
import com.dream.client.view.MainActivity;

public class MulMainActivity extends Activity {

	private GridView gridView;
	private ArrayList<String> dataList = new ArrayList<String>();
	private GridImageAdapter gridImageAdapter;
	private TextView datetimeView;
	private EditText content;
	private EditText contentinfo;
	
	public static int ZERO = 0;
	public static int ONE = 1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mul_activity_main);

		init();
		initListener();

	}

	private void init() {
		gridView = (GridView) findViewById(R.id.myGrid);
		dataList.add("camera_default");
		gridImageAdapter = new GridImageAdapter(this, dataList);
		gridView.setAdapter(gridImageAdapter);
		
		datetimeView = (TextView) findViewById(R.id.datetime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		datetimeView.setText(format.format(new Date()));
		
		content = (EditText) findViewById(R.id.publish_content);
		contentinfo = (EditText) findViewById(R.id.publish_contactinfo);

	}

	private void initListener() {

		gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MulMainActivity.this, AlbumActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
				intent.putExtras(bundle);
				startActivityForResult(intent, ZERO);
			}

		});
	}
	
	public void setEndDatetime(View v){
		Intent intent = new Intent(MulMainActivity.this, DateTimeActivity.class);
		startActivityForResult(intent, ONE);
	}
	
	public void publish(View v){
		new PublishTask().execute(MediaType.MULTIPART_FORM_DATA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ZERO) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				ArrayList<String> tDataList = (ArrayList<String>) bundle.getSerializable("dataList");
				if (tDataList != null && tDataList.size() > 0) {
					if (tDataList.size() < 8) {
						tDataList.add("camera_default");
					}
					dataList.clear();
					dataList.addAll(tDataList);
					gridImageAdapter.notifyDataSetChanged();
				}
			}
		} else if(requestCode == ONE){
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				String datetime = bundle.getString("datetime");
				datetimeView.setText(datetime);
			}
		}

	}

	private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

		ArrayList<String> tDataList = new ArrayList<String>();

		for (String s : dataList) {
			if (!s.contains("default")) {
				tDataList.add(s);
			}
		}

		return tDataList;

	}
	
	private static final String TAG = "MultiPhoto";
	
	private class PublishTask extends AsyncTask<MediaType, Void, ErrorCode> {
		String url = Config.SERVER2+"addTask";
		private MultiValueMap<String, Object> formData;
		private UserTask userTask;
		
		@Override
		protected void onPreExecute() {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			formData = new LinkedMultiValueMap<String, Object>();
			userTask = new UserTask();
			userTask.setContactinfo(contentinfo.getText().toString());
			userTask.setContent(content.getText().toString());
			userTask.setCreatetime(format.format(new Date()));
			userTask.setEndtime(datetimeView.getText().toString());
			userTask.setCreateuserid(Long.parseLong(Config.myId));
			if(dataList != null && dataList.size()>0){
				for(String path : dataList){
					if(!path.contains("default"))
						formData.add("files", new FileSystemResource(path));
				}
			}
		
			formData.add("userTask", JSON.toJSONString(userTask));
		}

		@Override
		protected ErrorCode doInBackground(MediaType... params) {
			try {
				if (params.length <= 0) 
					return null;
				MediaType mediaType = params[0];
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.setContentType(mediaType);
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(formData, requestHeaders);
				RestTemplate restTemplate = new RestTemplate(true);
				ResponseEntity<ErrorCode> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ErrorCode.class);
				return responseEntity.getBody();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ErrorCode result) {
			if (result.isSucceed()) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("result", result.toString());
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();							
			} else {
				Toast.makeText(MulMainActivity.this, "发表失败", Toast.LENGTH_LONG).show();
			}
		}
		
	}

}
