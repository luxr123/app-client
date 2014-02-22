//package com.dream.client.view;
//
///**
// * User: xiaorui.lu
// * Date: 2014年1月14日 下午10:53:46
// */
//import java.io.File;
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import com.dream.client.R;
//
//public class SelectFileActivity2 extends Activity {
//
//	private ListView lvShow;
//
//	private ArrayList<File> fileList = new ArrayList<File>();// 目录下的所有文件list
//	private ArrayList<String> fileNames = new ArrayList<String>();;// 目录下的所有文件名称
//	private String currentFilePath = null;
//
//	public static String FILEPATH = "filepath";
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_select_file);
//		lvShow = (ListView) this.findViewById(R.id.selectImage);
//
//		lvShow.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> adapter, View v, int postion, long id) {
//
//				if (fileList.get(postion).isDirectory()) {
//					loadFile(fileList.get(postion));
//					return;
//				}
//
//				Intent intent = new Intent();
//				intent.putExtra(FILEPATH, fileList.get(postion).getAbsolutePath()); // /将图片的存放路径，传到注册界面。
//				setResult(RegisterActivity.IMAGE_SELECT, intent);
//				finish();
//			}
//		});
//
//		loadFile(Environment.getExternalStorageDirectory());
//	}
//
//	private ArrayAdapter<String> setAdapter() {
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectFileActivity2.this, android.R.layout.simple_list_item_1,
//				fileNames);
//
//		return adapter;
//	}
//
//	/**
//	 * 加载文件
//	 * 
//	 * @param file
//	 */
//	private void loadFile(File file) {
//
//		// 清空list
//		if (fileList.size() > 0) {
//			fileList.clear();
//		}
//		if (fileNames.size() > 0) {
//			fileNames.clear();
//		}
//		// load文件价下所有的文件
//		File[] files = file.listFiles();
//		// 判断是否为空，空说明是一个文件而不是目录
//		if (files == null) {
//			return;
//		}
//
//		for (int i = 0; i < files.length; i++) {
//			fileList.add(files[i]);
//			fileNames.add(files[i].getName());
//		}
//
//		currentFilePath = file.getAbsolutePath();
//		lvShow.setAdapter(setAdapter());
//	};
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//		if (keyCode == KeyEvent.KEYCODE_BACK) { // 当点击返回按钮时
//			if (currentFilePath.equals("/")) {
//				finish();
//			} else {
//				loadFile(new File(currentFilePath).getParentFile());
//			}
//		}
//
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
////		getMenuInflater().inflate(R.menu.select_file, menu);
//		return true;
//	}
//
//}
