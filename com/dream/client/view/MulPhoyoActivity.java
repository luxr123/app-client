package com.dream.client.view;

import java.util.ArrayList;

import com.dream.client.R;
import com.dream.client.adapter.GridImageAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MulPhoyoActivity extends Activity {

	private GridView gridView;
	private ArrayList<String> dataList = new ArrayList<String>();
	private GridImageAdapter gridImageAdapter;
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mulphotoactivity_main);
		
		Intent intent = getIntent();
		if (null != intent) {
	    	 Bundle extras = intent.getExtras(); 
	    	 id = extras.getString("id");
	     }

		init();
		initListener();

	}

	private void init() {
		gridView = (GridView) findViewById(R.id.myGrid);
		dataList.add("camera_default");
		gridImageAdapter = new GridImageAdapter(this, dataList);
		gridView.setAdapter(gridImageAdapter);
	}

	private void initListener() {

		gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// if (position == dataList.size() - 1) {

				Intent intent = new Intent(MulPhoyoActivity.this, MyAlbumActivity.class);
				Bundle bundle = new Bundle();
				// intent.putArrayListExtra("dataList", dataList);
				bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
				intent.putExtras(bundle);
				startActivityForResult(intent, 0);

				// }

			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				ArrayList<String> tDataList = (ArrayList<String>) bundle.getSerializable("dataList");
				if (tDataList != null) {
					if (tDataList.size() < 8) {
						tDataList.add("camera_default");
					}
					dataList.clear();
					dataList.addAll(tDataList);
					for(String s : dataList)
						Toast.makeText(MulPhoyoActivity.this, s, Toast.LENGTH_SHORT).show();;
					gridImageAdapter.notifyDataSetChanged();
				}
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

}
