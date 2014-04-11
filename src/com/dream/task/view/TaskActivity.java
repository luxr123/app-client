package com.dream.task.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dream.adapter.GridImageAdapter;
import com.dream.client.Config;
import com.dream.client.R;
import com.dream.client.constants.ErrorCode;
import com.dream.client.entity.UserTask;
import com.dream.client.util.StringUtils;
import com.dream.db.DataHelper;
import com.dream.syncloaderbitmap.cache.ImageLoader;

public class TaskActivity extends Activity {

	private GridView gridView;
	private ArrayList<String> dataList = new ArrayList<String>();
	private GridImageAdapter gridImageAdapter;
	private TextView datetimeView;
	private EditText content;
	private EditText contentinfo;

	private static int ZERO = 0;
	private static int ONE = 1;

	private SimpleDateFormat format;

	private DataHelper dataHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_activity_main);

		init();
		initListener();

	}

	private void init() {
		gridView = (GridView) findViewById(R.id.myGrid);
		dataList.add("camera_default");
		gridImageAdapter = new GridImageAdapter(this, dataList);
		gridView.setAdapter(gridImageAdapter);

		datetimeView = (TextView) findViewById(R.id.datetime);
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		datetimeView.setText(format.format(new Date()));

		content = (EditText) findViewById(R.id.publish_content);
		contentinfo = (EditText) findViewById(R.id.publish_contactinfo);

	}

	private void initListener() {

		gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(TaskActivity.this, AlbumActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
				intent.putExtras(bundle);
				startActivityForResult(intent, ZERO);
			}

		});
	}

	public void setEndDatetime(View v) {
		Intent intent = new Intent(TaskActivity.this, DateTimeActivity.class);
		startActivityForResult(intent, ONE);
	}

	public void publish(View v) {
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
		} else if (requestCode == ONE) {
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

	private static final String TAG = "UserTask";

	private class PublishTask extends AsyncTask<MediaType, Void, String> {
		String url = Config.SERVER2 + "addTask";
		private MultiValueMap<String, Object> formData;
		private UserTask userTask;

		@Override
		protected void onPreExecute() {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			formData = new LinkedMultiValueMap<String, Object>();
			userTask = new UserTask();
			userTask.setContactinfo(contentinfo.getText().toString());
			userTask.setContent(content.getText().toString());
			userTask.setCreatetime(format.format(new Date()));
			userTask.setEndtime(datetimeView.getText().toString());
			userTask.setCreateuserid(Config.myId);
			if (dataList != null && dataList.size() > 0) {
				for (String path : dataList) {
					if (!path.contains("default"))
						formData.add("files", new FileSystemResource(path));
				}
			}

			formData.add("userTask", JSON.toJSONString(userTask));
		}

		@Override
		protected String doInBackground(MediaType... params) {
			try {
				if (params.length <= 0)
					return null;
				MediaType mediaType = params[0];
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.setContentType(mediaType);
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(formData,
						requestHeaders);
				RestTemplate restTemplate = new RestTemplate(true);
				ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
				return responseEntity.getBody();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject jsonObject;
			ErrorCode errorCode;
			JSONArray urls;
			try {
				jsonObject = new JSONObject(result);
				errorCode = com.alibaba.fastjson.JSONObject.parseObject(jsonObject.getString("errorcode"), ErrorCode.class);
				if (errorCode.isSucceed()) {
					urls = jsonObject.getJSONArray("imgUrl");
					dataHelper = new DataHelper(getApplicationContext());
					// 保存任务信息, 之后会进步处理...
					Long id = dataHelper.saveUserTask(userTask);
					StringUtils.toastShow(TaskActivity.this, "发表id:" + id + "success");

					// 如有图片,则缓存任务图片
					ImageLoader imageLoader = null;
					if (dataList != null && dataList.size() > 0) {
						imageLoader = new ImageLoader(getApplicationContext());
						int size = urls.length();
						if (urls.length() == dataList.size())
							for (int i = 0; i < size; i++)
								imageLoader.cacheFile(dataList.get(i), urls.getString(i));
						else
							throw new Exception("上传的图片和返回的图片数量不一致");
							
					}

					Intent intent = getIntent();
					Bundle bundle = new Bundle();
					bundle.putString("result", result.toString());
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					throw new Exception(errorCode.getMsg());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(TaskActivity.this, "发表失败", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

		}

	}

}
