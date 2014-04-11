package com.dream.client.view;

/**
 * User: xiaorui.lu
 * Date: 2014年1月14日 下午11:05:31
 */
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.json.JSONObject;
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
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.alibaba.fastjson.JSON;
import com.dream.client.Config;
import com.dream.client.R;
import com.dream.client.constants.ErrorCode;
import com.dream.client.entity.Gender;
import com.dream.client.entity.User;
import com.dream.client.util.StringUtils;
import com.dream.db.DataHelper;
import com.dream.syncloaderbitmap.cache.ImageLoader;

import encode.BASE64Encoder;

public class RegisterActivity extends Activity {

	private static Uri iconUri;
	private static String iconPath;

	private static final String TAG = "Register";

	private EditText name = null;
	private EditText password = null;
	private EditText confirmpassword = null;
	private EditText code = null;
	private RadioGroup genderGroup = null;
	private Button uploadBtn = null;
	private Button ok = null;

	private ImageView imgShow;

	// 属性
	private String checkcode = null;
	private String guid = null;
	private Gender gender = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		checkcode = bundle.getString("checkcode");
		guid = bundle.getString("guid");

		imgShow = (ImageView) this.findViewById(R.id.img_show);
		imgShow.setAdjustViewBounds(true); // 调整图片的大小

		name = (EditText) findViewById(R.id.user);
		code = (EditText) findViewById(R.id.code);
		code.setHint(checkcode);
		password = (EditText) findViewById(R.id.password);
		confirmpassword = (EditText) findViewById(R.id.confirmpassword);
		genderGroup = (RadioGroup) findViewById(R.id.radioGroup);
		uploadBtn = (Button) findViewById(R.id.uploadIcon);
		ok = (Button) findViewById(R.id.ok);

		genderGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() { // 选择性别

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == R.id.male) {
							gender = Gender.male;
						} else if (checkedId == R.id.female) {
							gender = Gender.female;
						}
					}

				});

		uploadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/***
				 * 这个是调用android内置的intent，来过滤图片文件 ，同时也可以过滤其他的
				 */
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_PICK);
				startActivityForResult(intent, 1);
			}
		});

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (iconUri == null) {
					toastShow("您尚未选择等待上传的图片");
					return;
				}
				if (!password.getText().toString().equals(confirmpassword.getText().toString())) {
					toastShow("the password is wrong");
					return;
				}
				if (!code.getText().toString().equals(checkcode)) {
					toastShow("the checkcode is wrong");
					return;
				}

				new MyRegister().execute(MediaType.MULTIPART_FORM_DATA);
//				Intent intent = new Intent(RegisterActivity.this, LoginActivity.class); // 返回到登录界面
//				startActivity(intent);
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			/**
			 * 当选择的图片不为空的话，在获取到图片的途径
			 */
			iconUri = data.getData();
			Log.e(TAG, "uri = " + iconUri);
			// 通过路径加载图片
			// 图片缩放的实现，请看：http://blog.csdn.net/reality_jie_blog/article/details/16891095
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				((BitmapDrawable) imgShow.getDrawable()).getBitmap().compress(CompressFormat.PNG, 100, baos);// 压缩为PNG格式,100表示跟原图大小一样
				baos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.imgShow.setImageURI(iconUri);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
	@SuppressWarnings("deprecation")
	public String GetImageStr(Uri u) {
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		byte[] data = null;
		// 读取图片字节数组
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor actualimagecursor = managedQuery(u, proj, null, null, null);
			int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			String img_path = actualimagecursor.getString(actual_image_column_index);
			InputStream in = new FileInputStream(img_path);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码

		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);// 返回Base64编码过的字节数组字符串
	}

	public String getRealPathFromURI(Uri contentUri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		if (cursor.moveToFirst()) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}

	private class MyRegister extends AsyncTask<MediaType, Void, String> {
		private User user = null;

		String url = Config.SERVER + "register";
		private MultiValueMap<String, Object> formData;

		// onPreExecute方法用于在执行后台任务前做一些UI操作
		@Override
		protected void onPreExecute() {
			System.out.println("============start register url==========");
			user = new User();
			Date date = new Date();
			user.setCheckcode(checkcode);
			user.setGuid(guid);
			user.setCreatetime(date);
			user.setUpdatetime(date);
			user.setGender(gender);
			user.setName(name.getText().toString());
			user.setPassword(password.getText().toString());
			user.setUdid(Config.udid);
			user.setTags(Config.TAG);

			iconPath = getRealPathFromURI(iconUri);

			Resource resource = new FileSystemResource(iconPath);

			// populate the data to post
			formData = new LinkedMultiValueMap<String, Object>();
			formData.add("userJson", JSON.toJSONString(user));
			formData.add("file", resource);
		}

		// doInBackground方法内部执行后台任务,不可在此方法内修改UI
		@Override
		protected String doInBackground(MediaType... params) {
			try {
				if (params.length <= 0) {
					return null;
				}
				MediaType mediaType = params[0];
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.setContentType(mediaType);
				// HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(formData,
						requestHeaders);
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate(true);

				// Make the network request, posting the message, expecting a
				// String in response from the server
				ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
				return responseEntity.getBody();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

		// onPostExecute方法用于在执行完后台任务后更新UI,显示结果
		@Override
		protected void onPostExecute(String result) {
			JSONObject jsonObject;
			ErrorCode errorCode;
			try {
				jsonObject = new JSONObject(result);
				errorCode = com.alibaba.fastjson.JSONObject.parseObject(jsonObject.getString("errorcode"), ErrorCode.class);
				if (errorCode.isSucceed()) {
					Config.myId = jsonObject.getLong("id");
					Config.myName = jsonObject.getString("username");
					Config.myIconUrl = jsonObject.getString("headIconUrl");

					// 缓存头像
					ImageLoader imageLoader = new ImageLoader(getApplicationContext());
					imageLoader.cacheFile(iconPath, Config.myIconUrl);

					// 保存此用户入数据库
					Long id = new DataHelper(getApplicationContext()).saveUser();
					StringUtils.toastShow(RegisterActivity.this, "注册id:" + id + "success");

					Intent registrtIntent = new Intent(RegisterActivity.this, MainActivity.class);
					JPushInterface.setAliasAndTags(RegisterActivity.this, Config.myName, Config.myChannels);
					startActivity(registrtIntent);
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// 封装Toast,一方面调用简单,另一方面调整显示时间只要改此一个地方即可.
	public void toastShow(String text) {
		Toast.makeText(RegisterActivity.this, text, Toast.LENGTH_LONG).show();
	}

}
