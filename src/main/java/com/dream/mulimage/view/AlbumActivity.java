package com.dream.mulimage.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dream.adapter.AlbumGridViewAdapter;
import com.dream.client.R;
import com.dream.mulimage.util.ImageManager;

public class AlbumActivity extends Activity {

	private GridView gridView;
	private ArrayList<String> dataList = new ArrayList<String>();
	private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();
	private ArrayList<String> selectedDataList = new ArrayList<String>();
	// private String cameraDir = "/DCIM/";
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	private LinearLayout selectedImageLayout;
	private Button okButton;
	private HorizontalScrollView scrollview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		selectedDataList = (ArrayList<String>) bundle.getSerializable("dataList");

		init();
		initListener();

	}

	private void init() {

		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setVisibility(View.GONE);
		gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList, selectedDataList);
		gridView.setAdapter(gridImageAdapter);
		refreshData();
		selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
		okButton = (Button) findViewById(R.id.ok_button);
		scrollview = (HorizontalScrollView) findViewById(R.id.scrollview);

		initSelectImage();

	}

	private void initSelectImage() {
		if (selectedDataList == null)
			return;
		for (final String path : selectedDataList) {
			ImageView imageView = (ImageView) LayoutInflater.from(AlbumActivity.this).inflate(R.layout.choose_imageview,
					selectedImageLayout, false);
			selectedImageLayout.addView(imageView);
			hashMap.put(path, imageView);
			ImageManager.from(AlbumActivity.this).displayImage(imageView, path, R.drawable.camera_default, 100, 100);
			imageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					removePath(path);
					gridImageAdapter.notifyDataSetChanged();
				}
			});
		}
		okButton.setText("完成(" + selectedDataList.size() + "/8)");
	}

	private void initListener() {

		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(final ToggleButton toggleButton, int position, final String path, boolean isChecked) {

				if (selectedDataList.size() >= 8) {
					toggleButton.setChecked(false);
					if (!removePath(path)) {
						Toast.makeText(AlbumActivity.this, "只能选择8张图片", 200).show();
					}
					return;
				}

				if (isChecked) {
					if (!hashMap.containsKey(path)) {
						ImageView imageView = (ImageView) LayoutInflater.from(AlbumActivity.this).inflate(R.layout.choose_imageview,
								selectedImageLayout, false);
						selectedImageLayout.addView(imageView);
						imageView.postDelayed(new Runnable() {

							@Override
							public void run() {

								int off = selectedImageLayout.getMeasuredWidth() - scrollview.getWidth();
								if (off > 0) {
									scrollview.smoothScrollTo(off, 0);
								}
							}
						}, 100);

						hashMap.put(path, imageView);
						selectedDataList.add(path);
						ImageManager.from(AlbumActivity.this).displayImage(imageView, path, R.drawable.camera_default, 100, 100);
						imageView.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								toggleButton.setChecked(false);
								removePath(path);

							}
						});
						okButton.setText("完成(" + selectedDataList.size() + "/8)");
					}
				} else {
					removePath(path);
				}

			}
		});

		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				// intent.putArrayListExtra("dataList", dataList);
				bundle.putStringArrayList("dataList", selectedDataList);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();

			}
		});

	}

	private boolean removePath(String path) {
		if (hashMap.containsKey(path)) {
			selectedImageLayout.removeView(hashMap.get(path));
			hashMap.remove(path);
			removeOneData(selectedDataList, path);
			okButton.setText("完成(" + selectedDataList.size() + "/8)");
			return true;
		} else {
			return false;
		}
	}

	private void removeOneData(ArrayList<String> arrayList, String s) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).equals(s)) {
				arrayList.remove(i);
				return;
			}
		}
	}

	private void refreshData() {

		new AsyncTask<Void, Void, ArrayList<String>>() {

			@Override
			protected void onPreExecute() {
				progressBar.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected ArrayList<String> doInBackground(Void... params) {
				ArrayList<String> tmpList = new ArrayList<String>();

				final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
				final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
				Cursor imagecursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
						orderBy + " DESC");

				for (int i = 0; i < imagecursor.getCount(); i++) {
					imagecursor.moveToPosition(i);
					int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
					tmpList.add(imagecursor.getString(dataColumnIndex));

					System.out.println("=====> Array path => " + tmpList.get(i));
				}
				return tmpList;
			}

			protected void onPostExecute(ArrayList<String> tmpList) {

				if (AlbumActivity.this == null || AlbumActivity.this.isFinishing()) {
					return;
				}
				progressBar.setVisibility(View.GONE);
				dataList.clear();
				dataList.addAll(tmpList);
				System.out.println("dataList.size=>" + dataList.size());
				gridImageAdapter.notifyDataSetChanged();
				return;

			};

		}.execute();

	}

	@Override
	public void onBackPressed() {
		finish();
		// super.onBackPressed();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		// ImageManager2.from(AlbumActivity.this).recycle(dataList);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

}
