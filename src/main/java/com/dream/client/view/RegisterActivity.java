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

import com.alibaba.fastjson.JSON;
import com.dream.client.Config;
import com.dream.client.R;
import com.dream.client.constants.ErrorCode;
import com.dream.client.entity.User;

import encode.BASE64Encoder;

public class RegisterActivity extends Activity {
	
	private static Uri imgPath;

	private static final String TAG = "uploadImage";

	private EditText name = null;
	private EditText password = null;
	private EditText confirmpassword = null;
	private EditText code = null;
	private RadioGroup genderGroup = null;
	private Button uploadBtn = null;
	private Button ok = null;

	private ImageView imgShow;

//	private SharedPreferences preferences;
//	private SharedPreferences.Editor editor;

	private User user = null;

	// 属性
	private String checkcode = null;
	private String guid = null;
	private String gender = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		// preferences = getSharedPreferences("dream-app", MODE_PRIVATE);
		// editor = preferences.edit();

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
							gender = "male";
						} else if (checkedId == R.id.female) {
							gender = "female";
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
				if (imgPath == null) {
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
				Intent intent = new Intent(RegisterActivity.this, LoginActivity.class); // 返回到登录界面
				startActivity(intent);
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			/**
			 * 当选择的图片不为空的话，在获取到图片的途径
			 */
			imgPath = data.getData();
			Log.e(TAG, "uri = " + imgPath);
			// 通过路径加载图片
			// 图片缩放的实现，请看：http://blog.csdn.net/reality_jie_blog/article/details/16891095
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				((BitmapDrawable) imgShow.getDrawable()).getBitmap().compress(CompressFormat.PNG, 100, baos);// 压缩为PNG格式,100表示跟原图大小一样
				baos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.imgShow.setImageURI(imgPath);
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
	    if(cursor.moveToFirst()){
	       int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	       res = cursor.getString(column_index);
	    }
	    cursor.close();
	    return res;
	}

	private class MyRegister extends AsyncTask<MediaType, Void, ErrorCode> {

		String url = "http://10.100.50.38:8080/app-web/user/register";
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
			user.setUdid(Config.udid);user.setTags(Config.TAG);
			
			Resource resource = new FileSystemResource(getRealPathFromURI(imgPath));

			// populate the data to post
			formData = new LinkedMultiValueMap<String, Object>();
			formData.add("userJson", JSON.toJSONString(user));
			formData.add("file", resource);
		}

		// doInBackground方法内部执行后台任务,不可在此方法内修改UI
		@Override
		protected ErrorCode doInBackground(MediaType... params) {
			try {
				if (params.length <= 0) {
					return null;
				}
				MediaType mediaType = params[0];
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.setContentType(mediaType);
				// HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(formData, requestHeaders);
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate(true);
				
				// Make the network request, posting the message, expecting a String in response from the server
				ResponseEntity<ErrorCode> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
						ErrorCode.class);
				

				// Create a new RestTemplate instance
//				RestTemplate restTemplate = new RestTemplate();
//				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//				if (mediaType == MediaType.APPLICATION_JSON)
//					restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
//				ResponseEntity<ErrorCode> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ErrorCode.class);
				return responseEntity.getBody();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}
/*		@Override
		protected ErrorCode doInBackground(MediaType... params) {
			try {
				if (params.length <= 0) {
					return null;
				}
				MediaType mediaType = params[0];
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.setContentType(mediaType);
				// HttpEntity object to use for the request
				HttpEntity<User> requestEntity = new HttpEntity<User>(user, requestHeaders);
				
				
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
				if (mediaType == MediaType.APPLICATION_JSON)
					restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
				ResponseEntity<ErrorCode> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ErrorCode.class);
				return responseEntity.getBody();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}
*/
		// onProgressUpdate方法用于更新进度信息
		// @Override
		// protected void onProgressUpdate(Integer... progresses) {
		// Log.i(TAG, "onProgressUpdate(Progress... progresses) called");
		// progressBar.setProgress(progresses[0]);
		// textView.setText("loading..." + progresses[0] + "%");
		// }

		// onPostExecute方法用于在执行完后台任务后更新UI,显示结果
		@Override
		protected void onPostExecute(ErrorCode result) {
			System.out.println("=========s==========" + result.toString() + "==========s======");
			// System.out.println("===================end================");
			 Intent registrtIntent = new Intent(RegisterActivity.this,LoginActivity.class);
			// // 传参数
			// Bundle bundle = new Bundle();
			// bundle.putString("checkcode", result.getCheckcode());
			// bundle.putString("guid", result.getGuid());
			// // 在调用intent的方法代表批量添加
			// registrtIntent.putExtras(bundle);
			 startActivity(registrtIntent);
		}

		// onCancelled方法用于在取消执行中的任务时更改UI
		// @Override
		// protected void onCancelled() {
		// Log.i(TAG, "onCancelled() called");
		// textView.setText("cancelled");
		// progressBar.setProgress(0);
		// }

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
