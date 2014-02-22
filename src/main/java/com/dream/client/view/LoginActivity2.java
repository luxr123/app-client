//package com.dream.client.view;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
//import org.springframework.web.client.RestTemplate;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.dream.client.R;
//import com.dream.client.entity.User;
//
///**登陆界面activity*/
//@SuppressLint("NewApi")
//public class LoginActivity extends Activity implements OnClickListener{
//	protected static final String TAG = LoginActivity.class.getSimpleName();
//	
//	/**更多登陆项的显示View*/
//	private View view_more;
//	/**更多菜单，默认收起，点击后展开，再点击收起*/
//	private View menu_more;
//	private ImageView img_more_up;//更多登陆项箭头图片
//	private Button btn_login;//登录按钮
//	private Button btn_regist;//注册按钮
//	/**更所登陆项的菜单是否展开，默认收起*/
//	private boolean isShowMenu = false;
//	
//	public static final int MENU_PWD_BACK = 1;
//	public static final int MENU_HELP = 2;
//	public static final int MENU_EXIT = 3;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.login);
//		initView();
//	}
//	
//	private void initView(){
//		img_more_up = (ImageView) findViewById(R.id.img_more_up);
//		
//		btn_login = (Button) findViewById(R.id.btn_login);
//		btn_login.setOnClickListener(this);
//		btn_regist = (Button) findViewById(R.id.btn_login_regist);
//		btn_regist.setOnClickListener(this);
//		
//		menu_more = findViewById(R.id.menu_more);
//		view_more =  findViewById(R.id.view_more);
//		view_more.setOnClickListener(this);
//	}
//	
//	
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.view_more:
//			showMoreMenu(isShowMenu);
//			break;
//		case R.id.btn_login:
//			Intent loginIntent = new Intent(LoginActivity.this,LoginActivity.class);
//			startActivity(loginIntent);
//		case R.id.btn_login_regist:
//			btn_regist.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					new MyTask().execute();
//				}
//			});
//			
//			break;
//
//		}
//	}
//	
//	private class MyTask extends AsyncTask<Void, Void, User> {
//		
//		@Override
//		protected void onPreExecute() {
//			System.out.println("============start==========");
//		}
//
//		@Override
//		protected User doInBackground(Void... params) {
//			HttpHeaders requestHeaders = new HttpHeaders();
//			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
//			HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);
//			String url = "http://10.100.50.38:8080/app-web/user/register";
//			RestTemplate restTemplate = new RestTemplate();
//			restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
//			ResponseEntity<User> responseEntity = restTemplate.exchange(url,HttpMethod.GET, requestEntity, User.class);
//			return responseEntity.getBody();
//		}
//		
//		@Override
//		protected void onPostExecute(User result) {
//			System.out.println("===================" + result.getCheckcode() + "================" + result.getGuid());
//			System.out.println("===================end================");
//			Intent registrtIntent = new Intent(LoginActivity.this,RegisterActivity.class);
//			//传参数
//			Bundle bundle = new Bundle();
//			bundle.putString("checkcode", result.getCheckcode());
//			bundle.putString("guid", result.getGuid());
//			//在调用intent的方法代表批量添加
//			registrtIntent.putExtras(bundle);
//			startActivity(registrtIntent);
//		}
//		
//	}
//	
//	/**
//	 * 展示更多菜单的方法,boolean的默认值是false
//	 * @param show
//	 */
//	public void showMoreMenu(boolean show){
//		if(show){//如果菜单不展开的时候
//			menu_more.setVisibility(View.GONE);
//			img_more_up.setImageResource(R.drawable.login_more_up);
//			isShowMenu = false;
//		}else{//菜单展开的时候
//			menu_more.setVisibility(View.VISIBLE);
//			img_more_up.setImageResource(R.drawable.login_more);
//			isShowMenu = true;
//		}
//	}
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {//创建系统功能菜单
//		// TODO Auto-generated method stub
//		menu.add(0, MENU_PWD_BACK, 1, "密码找回").setIcon(R.drawable.menu_findkey);
//		menu.add(0,MENU_HELP,2,"帮助").setIcon(R.drawable.menu_setting);
//		menu.add(0, MENU_EXIT, 3, "退出").setIcon(R.drawable.menu_exit);
//		return super.onCreateOptionsMenu(menu);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		switch(item.getItemId()){
//		case MENU_PWD_BACK:
//			break;
//		case MENU_HELP:
//			break;
//		case MENU_EXIT:
//			finish();
//			break;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	 // 封装Toast,一方面调用简单,另一方面调整显示时间只要改此一个地方即可.  
//    public void toastShow(String text) {  
//        Toast.makeText(LoginActivity.this, text, 1).show();  
//    }
//
//}
