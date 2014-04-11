package com.dream.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.dream.client.Config;
import com.dream.client.R;
import com.dream.client.constants.ErrorCode;
import com.dream.client.entity.UserTask;
import com.dream.client.util.ImageUtil;
import com.dream.client.util.StringUtils;
import com.dream.client.util.TimeUtil;
import com.dream.client.view.CustomListView.OnLoadMoreListener;
import com.dream.client.view.CustomListView.OnRefreshListener;
import com.dream.syncloaderbitmap.cache.ImageLoader;
import com.dream.task.view.TaskActivity;

public class MainActivity extends AbsListViewBaseActivity implements OnItemClickListener, OnItemLongClickListener {

	private TextView mText;
	private ImageView headIcon;
	private ImageButton taskWrite;
	
	private ImageLoader mImageLoader;  // 加载头像
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		headIcon = (ImageView) this.findViewById(R.id.icon);
		headIcon.setAdjustViewBounds(true); // 调整图片的大小
		
		mImageLoader = new ImageLoader(this.getApplicationContext());

		mText = (TextView) this.findViewById(R.id.username);
		taskWrite = (ImageButton) this.findViewById(R.id.activity_card_write);
		mText.setText(" Welcome, " + Config.myName);
		registerMessageReceiver();
		new LoginJpush().execute();

		new Thread(new Runnable() {
			public void run() {
				// 加载头像
				getHeadIcon();
			}
		}).start();
		

		taskWrite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, TaskActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		//new InitAppData().execute();
		initView();

	}

	private class LoginJpush extends AsyncTask<Void, Void, ErrorCode> {
		private String url = Config.SERVER + "loginJpush";
		private MultiValueMap<String, String> requestParams;

		@Override
		protected void onPreExecute() {
			System.out.println("============start jpush==========");
			requestParams = new LinkedMultiValueMap<String, String>();
			requestParams.add("udid", Config.udid);
		}

		@Override
		protected ErrorCode doInBackground(Void... params) {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestParams,
					requestHeaders);
			RestTemplate restTemplate = new RestTemplate(true);
			ResponseEntity<ErrorCode> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ErrorCode.class);
			return responseEntity.getBody();
		}

		@Override
		protected void onPostExecute(ErrorCode result) {
			/*
			 * if(result.isSucceed()){
			 * System.out.println("=====jpush success ====="); }
			 */
		}

	}

	@Override
	public void onResume() {
		Config.isBackground = false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		Config.isBackground = true;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	// for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.dream.client.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				// do something...
			}
		}
	}
	
	private void getHeadIcon() {
		System.out.println("myIconUrl" + Config.myIconUrl);
		mImageLoader.DisplayImage(Config.myIconUrl, headIcon, false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			StringUtils.toastShow(MainActivity.this, "refresh data ...");
			loadData(0);
		}

	}

	private JSONArray array;

	private static final String TAG = "MainActivity";

	private static final int LOAD_DATA_FINISH = 10;
	private static final int REFRESH_DATA_FINISH = 11;

	private List<UserTask> mList = null;
	private HomeAdapter mAdapter;
	private CustomListView mListView;
	private int mCount = 10;

	@SuppressWarnings("unchecked")
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_DATA_FINISH:
				if (mAdapter != null) {
					mAdapter.mlist = (ArrayList<UserTask>) msg.obj;
					mAdapter.notifyDataSetChanged();
				}
				mListView.onRefreshComplete(); // 下拉刷新完成
				break;
			case LOAD_DATA_FINISH:
				if (mAdapter != null) {
					mAdapter.mlist.addAll((ArrayList<UserTask>) msg.obj);
					mAdapter.notifyDataSetChanged();
				}
				mListView.onLoadMoreComplete(); // 加载更多完成
				break;
			default:
				break;
			}
		};
	};

	private void initView() {
		mAdapter = new HomeAdapter(this, mList, URLS);
		mListView = (CustomListView) findViewById(R.id.mListView);
		mListView.setAdapter(mAdapter);

		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO 下拉刷新
				Log.e(TAG, "onRefresh");
				loadData(0);
			}
		});

		mListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				// TODO 加载更多
				Log.e(TAG, "onLoad");
				loadData(1);
			}
		});

		mListView.setOnItemLongClickListener(this);
		mListView.setOnItemClickListener(this);

	}

	public void loadData(final int type) {
		new Thread() {
			@Override
			public void run() {
				List<JSONObject> _List = null;
				Map<String, Object> map = null;
				List<Map<String, Object>> listjson = null;
				switch (type) {
				case 0:
					mCount = 10;
					_List = new ArrayList<JSONObject>();
					listjson = new ArrayList<Map<String, Object>>();
					for (int i = 0; i < mCount; i++) {
						map = new HashMap<String, Object>();
						map.put("id", i);
						map.put("nick", "nick" + i);
						map.put("origtext", "origtext:" + i);
						map.put("isvip", "1");
						map.put("timestamp", System.currentTimeMillis() / 1000);
						map.put("image", "xx");
						listjson.add(map);
					}
					array = new JSONArray(listjson);

					if (array != null && array.length() > 0) {
						_List.clear();
						int lenth = array.length();
						for (int i = 0; i < lenth; i++) {
							_List.add(array.optJSONObject(i));
						}
					}

					break;

				case 1:
					_List = new ArrayList<JSONObject>();
					int _Index = mCount + 10;
					listjson = new ArrayList<Map<String, Object>>();

					for (int i = mCount + 1; i <= _Index; i++) {
						map = new HashMap<String, Object>();
						map.put("id", i);
						map.put("nick", "nick" + i);
						map.put("origtext", "origtext:" + i);
						map.put("isvip", "1");
						map.put("timestamp", System.currentTimeMillis() / 1000);
						map.put("image", "xx");
						listjson.add(map);
					}

					array = new JSONArray(listjson);

					if (array != null && array.length() > 0) {
						_List.clear();
						int lenth = array.length();
						for (int i = 0; i < lenth; i++) {
							_List.add(array.optJSONObject(i));
						}
					}
					mCount = _Index;
					break;
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (type == 0) { // 下拉刷新
					// Collections.reverse(mList); //逆序
					Message _Msg = mHandler.obtainMessage(REFRESH_DATA_FINISH, _List);
					mHandler.sendMessage(_Msg);
				} else if (type == 1) {
					Message _Msg = mHandler.obtainMessage(LOAD_DATA_FINISH, _List);
					mHandler.sendMessage(_Msg);
				}
			}
		}.start();
	}
	
	private class InitAppData extends AsyncTask<Void, Void, List<UserTask>> {
		long maxId = 0;
		long oldestId = 0;
		String url = Config.SERVER2 + "getFirstPage";
		private MultiValueMap<String, String> requestParams;
		@Override
		protected void onPreExecute() {
			maxId = 0;
			oldestId = 0;
			requestParams = new LinkedMultiValueMap<String, String>();
			requestParams.add("maxId", String.valueOf(maxId));
			requestParams.add("oldestId", String.valueOf(oldestId));
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected List<UserTask> doInBackground(Void... params) {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestParams,requestHeaders);
			RestTemplate restTemplate = new RestTemplate(true);
			ResponseEntity<List> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, List.class);
			return responseEntity.getBody();
		}

		@Override
		protected void onPostExecute(List<UserTask> list) {
			if(!list.isEmpty()){
				mList = list;
				initView();
			}
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		StringUtils.toastShow(MainActivity.this, "onItemLongClick");
		CharSequence[] items = null;
		try {
			items = new CharSequence[] { "转播", "对话", "点评", "收藏", ((JSONObject) array.opt(position)).getString("nick"), "取消" };
		} catch (JSONException e) {
			e.printStackTrace();
		}
		new AlertDialog.Builder(MainActivity.this).setTitle("选项").setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0: {
					StringUtils.toastShow(MainActivity.this, "0");
				}
					break;
				case 1: {
					StringUtils.toastShow(MainActivity.this, "1");
				}
					break;
				case 2: {
					StringUtils.toastShow(MainActivity.this, "2");
				}
					break;
				case 3: {
					StringUtils.toastShow(MainActivity.this, "3");
				}
					break;
				case 4: {
					StringUtils.toastShow(MainActivity.this, "4");
				}
					break;
				case 5: {
					StringUtils.toastShow(MainActivity.this, "5");
				}
					break;
				default: {
					StringUtils.toastShow(MainActivity.this, "break");
					break;
				}
				}
			}
		}).show();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		StringUtils.toastShow(MainActivity.this, "onItemClick");
		// 此处传回来的position和mAdapter.getItemId()获取的一致;
		Log.e(TAG, "click position:" + position);
		// Log.e(TAG,
		// "__ mAdapter.getItemId() = "+mAdapter.getItemId(position)

	}
	
	private static final String[] URLS = {
		"http://lh5.ggpht.com/_mrb7w4gF8Ds/TCpetKSqM1I/AAAAAAAAD2c/Qef6Gsqf12Y/s144-c/_DSC4374%20copy.jpg",
		"http://lh5.ggpht.com/_Z6tbBnE-swM/TB0CryLkiLI/AAAAAAAAVSo/n6B78hsDUz4/s144-c/_DSC3454.jpg",
		"http://lh3.ggpht.com/_GEnSvSHk4iE/TDSfmyCfn0I/AAAAAAAAF8Y/cqmhEoxbwys/s144-c/_MG_3675.jpg",
		"http://lh6.ggpht.com/_Nsxc889y6hY/TBp7jfx-cgI/AAAAAAAAHAg/Rr7jX44r2Gc/s144-c/IMGP9775a.jpg",
		"http://lh3.ggpht.com/_lLj6go_T1CQ/TCD8PW09KBI/AAAAAAAAQdc/AqmOJ7eg5ig/s144-c/Juvenile%20Gannet%20despute.jpg",
		"http://lh6.ggpht.com/_ZN5zQnkI67I/TCFFZaJHDnI/AAAAAAAABVk/YoUbDQHJRdo/s144-c/P9250508.JPG",
		"http://lh4.ggpht.com/_XjNwVI0kmW8/TCOwNtzGheI/AAAAAAAAC84/SxFJhG7Scgo/s144-c/0014.jpg",
		"http://lh6.ggpht.com/_lnDTHoDrJ_Y/TBvKsJ9qHtI/AAAAAAAAG6g/Zll2zGvrm9c/s144-c/000007.JPG",
		"http://lh6.ggpht.com/_qvCl2efjxy0/TCIVI-TkuGI/AAAAAAAAOUY/vbk9MURsv48/s144-c/DSC_0844.JPG",
		"http://lh4.ggpht.com/_TPlturzdSE8/TBv4ugH60PI/AAAAAAAAMsI/p2pqG85Ghhs/s144-c/_MG_3963.jpg",
		"http://lh4.ggpht.com/_4f1e_yo-zMQ/TCe5h9yN-TI/AAAAAAAAXqs/8X2fIjtKjmw/s144-c/IMG_1786.JPG",
		"http://lh6.ggpht.com/_iFt5VZDjxkY/TB9rQyWnJ4I/AAAAAAAADpU/lP2iStizJz0/s144-c/DSCF1014.JPG",
		"http://lh5.ggpht.com/_hepKlJWopDg/TB-_WXikaYI/AAAAAAAAElI/715k4NvBM4w/s144-c/IMG_0075.JPG",
		"http://lh6.ggpht.com/_OfRSx6nn68g/TCzsQic_z3I/AAAAAAABOOI/5G4Kwzb2qhk/s144-c/EASTER%20ISLAND_Hanga%20Roa_31.5.08_46.JPG",
		"http://lh6.ggpht.com/_ZGv_0FWPbTE/TB-_GLhqYBI/AAAAAAABVxs/cVEvQzt0ke4/s144-c/IMG_1288_hf.jpg",
		"http://lh6.ggpht.com/_a29lGRJwo0E/TBqOK_tUKmI/AAAAAAAAVbw/UloKpjsKP3c/s144-c/31012332.jpg",
		"http://lh3.ggpht.com/_55Lla4_ARA4/TB6xbyxxJ9I/AAAAAAABTWo/GKe24SwECns/s144-c/Bluebird%20049.JPG",
		"http://lh3.ggpht.com/_iVnqmIBYi4Y/TCaOH6rRl1I/AAAAAAAA1qg/qeMerYQ6DYo/s144-c/Kwiat_100626_0016.jpg",
		"http://lh6.ggpht.com/_QFsB_q7HFlo/TCItd_2oBkI/AAAAAAAAFsk/4lgJWweJ5N8/s144-c/3705226938_d6d66d6068_o.jpg",
		"http://lh5.ggpht.com/_JTI0xxNrKFA/TBsKQ9uOGNI/AAAAAAAChQg/z8Exh32VVTA/s144-c/CRW_0015-composite.jpg",
		"http://lh6.ggpht.com/_loGyjar4MMI/S-InVNkTR_I/AAAAAAAADJY/Fb5ifFNGD70/s144-c/Moving%20Rock.jpg",
		"http://lh4.ggpht.com/_L7i4Tra_XRY/TBtxjScXLqI/AAAAAAAAE5o/ue15HuP8eWw/s144-c/opera%20house%20II.jpg",
		"http://lh3.ggpht.com/_rfAz5DWHZYs/S9cstBTv1iI/AAAAAAAAeYA/EyZPUeLMQ98/s144-c/DSC_6425.jpg",
		"http://lh6.ggpht.com/_iGI-XCxGLew/S-iYQWBEG-I/AAAAAAAACB8/JuFti4elptE/s144-c/norvig-polar-bear.jpg",
		"http://lh3.ggpht.com/_M3slUPpIgmk/SlbnavqG1cI/AAAAAAAACvo/z6-CnXGma7E/s144-c/mf_003.jpg",
		"http://lh4.ggpht.com/_loGyjar4MMI/S-InQvd_3hI/AAAAAAAADIw/dHvCFWfyHxQ/s144-c/Rainbokeh.jpg",
		"http://lh4.ggpht.com/_yy6KdedPYp4/SB5rpK3Zv0I/AAAAAAAAOM8/mokl_yo2c9E/s144-c/Point%20Reyes%20road%20.jpg",
		"http://lh5.ggpht.com/_6_dLVKawGJA/SMwq86HlAqI/AAAAAAAAG5U/q1gDNkmE5hI/s144-c/mobius-glow.jpg",
		"http://lh3.ggpht.com/_QFsB_q7HFlo/TCItc19Jw3I/AAAAAAAAFs4/nPfiz5VGENk/s144-c/4551649039_852be0a952_o.jpg",
		"http://lh6.ggpht.com/_TQY-Nm7P7Jc/TBpjA0ks2MI/AAAAAAAABcI/J6ViH98_poM/s144-c/IMG_6517.jpg",
		"http://lh3.ggpht.com/_rfAz5DWHZYs/S9cLAeKuueI/AAAAAAAAeYU/E19G8DOlJRo/s144-c/DSC_4397_8_9_tonemapped2.jpg",
		"http://lh4.ggpht.com/_TQY-Nm7P7Jc/TBpi6rKfFII/AAAAAAAABbg/79FOc0Dbq0c/s144-c/david_lee_sakura.jpg",
		"http://lh3.ggpht.com/_TQY-Nm7P7Jc/TBpi8EJ4eDI/AAAAAAAABb0/AZ8Cw1GCaIs/s144-c/Hokkaido%20Swans.jpg",
		"http://lh3.ggpht.com/_1aZMSFkxSJI/TCIjB6od89I/AAAAAAAACHM/CLWrkH0ziII/s144-c/079.jpg",
		"http://lh5.ggpht.com/_loGyjar4MMI/S-InWuHkR9I/AAAAAAAADJE/wD-XdmF7yUQ/s144-c/Colorado%20River%20Sunset.jpg",
		"http://lh3.ggpht.com/_0YSlK3HfZDQ/TCExCG1Zc3I/AAAAAAAAX1w/9oCH47V6uIQ/s144-c/3138923889_a7fa89cf94_o.jpg",
		"http://lh6.ggpht.com/_K29ox9DWiaM/TAXe4Fi0xTI/AAAAAAAAVIY/zZA2Qqt2HG0/s144-c/IMG_7100.JPG",
		"http://lh6.ggpht.com/_0YSlK3HfZDQ/TCEx16nJqpI/AAAAAAAAX1c/R5Vkzb8l7yo/s144-c/4235400281_34d87a1e0a_o.jpg",
		"http://lh4.ggpht.com/_8zSk3OGcpP4/TBsOVXXnkTI/AAAAAAAAAEo/0AwEmuqvboo/s144-c/yosemite_forrest.jpg",
		"http://lh4.ggpht.com/_6_dLVKawGJA/SLZToqXXVrI/AAAAAAAAG5k/7fPSz_ldN9w/s144-c/coastal-1.jpg",
		"http://lh4.ggpht.com/_WW8gsdKXVXI/TBpVr9i6BxI/AAAAAAABhNg/KC8aAJ0wVyk/s144-c/IMG_6233_1_2-2.jpg",
		"http://lh3.ggpht.com/_loGyjar4MMI/S-InS0tJJSI/AAAAAAAADHU/E8GQJ_qII58/s144-c/Windmills.jpg",
		"http://lh4.ggpht.com/_loGyjar4MMI/S-InbXaME3I/AAAAAAAADHo/4gNYkbxemFM/s144-c/Frantic.jpg",
		"http://lh5.ggpht.com/_loGyjar4MMI/S-InKAviXzI/AAAAAAAADHA/NkyP5Gge8eQ/s144-c/Rice%20Fields.jpg",
		"http://lh3.ggpht.com/_loGyjar4MMI/S-InZA8YsZI/AAAAAAAADH8/csssVxalPcc/s144-c/Seahorse.jpg",
		"http://lh3.ggpht.com/_syQa1hJRWGY/TBwkCHcq6aI/AAAAAAABBEg/R5KU1WWq59E/s144-c/Antelope.JPG",
		"http://lh5.ggpht.com/_MoEPoevCLZc/S9fHzNgdKDI/AAAAAAAADwE/UAno6j5StAs/s144-c/c84_7083.jpg",
		"http://lh4.ggpht.com/_DJGvVWd7IEc/TBpRsGjdAyI/AAAAAAAAFNw/rdvyRDgUD8A/s144-c/Free.jpg",
		"http://lh6.ggpht.com/_iO97DXC99NY/TBwq3_kmp9I/AAAAAAABcz0/apq1ffo_MZo/s144-c/IMG_0682_cp.jpg",
		"http://lh4.ggpht.com/_7V85eCJY_fg/TBpXudG4_PI/AAAAAAAAPEE/8cHJ7G84TkM/s144-c/20100530_120257_0273-Edit-2.jpg" };
	
	//=============================================================================================================//
	
	class HomeAdapter extends BaseAdapter {
		
		private boolean mBusy = false;

		private ImageLoader mImageLoader;
		public List<UserTask> mlist;
		private Context mContext;
		private String[] urlArrays;

		public HomeAdapter(Context context, List<UserTask> list, String[] url) {
			super();
			this.mContext = context;
			this.mlist = list;
			urlArrays = url;
			mImageLoader = new ImageLoader(context);
		}

		@Override
		public int getCount() {
			return mlist.size();
		}

		@Override
		public Object getItem(int position) {
			return mlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			HomeViewHolder viewHolder = new HomeViewHolder();
			UserTask data = mlist.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.home_list_item, null);
				viewHolder.home_headicon = (ImageView) convertView.findViewById(R.id.home_headicon);
				viewHolder.home_nick = (TextView) convertView.findViewById(R.id.home_nick);
				viewHolder.home_vip = (ImageView) convertView.findViewById(R.id.home_vip);
				viewHolder.home_hasimage = (ImageView) convertView.findViewById(R.id.home_hasimage);
				viewHolder.home_timestamp = (TextView) convertView.findViewById(R.id.home_timestamp);
				viewHolder.home_origtext = (TextView) convertView.findViewById(R.id.home_origtext);
				listView = (GridView) findViewById(R.id.gridview);
				viewHolder.home_gridview = (GridView) listView;
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (HomeViewHolder) convertView.getTag();
			}

			if (data != null) {
				try {
					// viewHolder.home_headicon.setImageDrawable(ImageUtil.getDrawableFromUrl(data.getString("head")+"/100"));//同步加载图片
					viewHolder.home_nick.setText(data.getCreateusername());
					if (data.getStatus() != 1) {
						viewHolder.home_vip.setVisibility(View.GONE);// 非vip隐藏vip标志
					}

					viewHolder.home_timestamp.setText(TimeUtil.converTime(Long.parseLong(data.getCreatetime())));
					
					//异步加载图片
					String url = "";
					url = urlArrays[position % urlArrays.length];
					String headUrl = ImageUtil.concatUrl(data.getUserIcon());
					viewHolder.home_headicon.setImageResource(R.drawable.ic_launcher);
					if (!mBusy) {
						mImageLoader.DisplayImage(headUrl, viewHolder.home_headicon, false);
					} else {
						mImageLoader.DisplayImage(headUrl, viewHolder.home_headicon, false);
					}

					if (null != data.getImgurl()) {
						mImageLoader.DisplayImage(url, viewHolder.home_hasimage, false);
					}

					// 微博内容开始
					String origtext = data.getContent();
					SpannableString spannable = new SpannableString(origtext);
					viewHolder.home_origtext.setText(spannable);
					// 微博内容设置结束
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return convertView;
		}

		class HomeViewHolder {
			private ImageView home_headicon;
			private TextView home_nick;
			private ImageView home_vip;
			private TextView home_timestamp;
			private TextView home_origtext;
			private ImageView home_hasimage;
			private GridView home_gridview;
		}
	}

}