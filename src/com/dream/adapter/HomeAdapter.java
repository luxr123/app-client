//package com.dream.adapter;
//
//import java.util.List;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.dream.client.Config;
//import com.dream.client.R;
//import com.dream.client.entity.UserTask;
//import com.dream.client.util.ImageUtil;
//import com.dream.client.util.TimeUtil;
//import com.dream.syncloaderbitmap.cache.ImageLoader;
//
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//import android.text.SpannableString;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
///**
// * Simple to Introduction
// * 
// * @ProjectName: [app-client]
// * @Package: [com.dream.adapter]
// * @ClassName: [HomeAdapter]
// * @Description: [一句话描述该类的功能]
// * @Author: [xiaorui.lu]
// * @CreateDate: [2014年3月31日 下午9:23:21]
// * @UpdateUser: [xiaorui.lu]
// * @UpdateDate: [2014年3月31日 下午9:23:21]
// * @UpdateRemark: [说明本次修改内容]
// * @Version: [v1.0]
// * 
// */
//public class HomeAdapter extends BaseAdapter {
//	
//	private boolean mBusy = false;
//
//	private ImageLoader mImageLoader;
//	public List<UserTask> mlist;
//	private Context mContext;
//	private String[] urlArrays;
//
//	public HomeAdapter(Context context, List<UserTask> list, String[] url) {
//		super();
//		this.mContext = context;
//		this.mlist = list;
//		urlArrays = url;
//		mImageLoader = new ImageLoader(context);
//	}
//
//	@Override
//	public int getCount() {
//		return mlist.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return mlist.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(final int position, View convertView, ViewGroup parent) {
//		HomeViewHolder viewHolder = new HomeViewHolder();
//		UserTask data = mlist.get(position);
//		if (convertView == null) {
//			convertView = LayoutInflater.from(mContext).inflate(R.layout.home_list_item, null);
//			viewHolder.home_headicon = (ImageView) convertView.findViewById(R.id.home_headicon);
//			viewHolder.home_nick = (TextView) convertView.findViewById(R.id.home_nick);
//			viewHolder.home_vip = (ImageView) convertView.findViewById(R.id.home_vip);
//			viewHolder.home_hasimage = (ImageView) convertView.findViewById(R.id.home_hasimage);
//			viewHolder.home_timestamp = (TextView) convertView.findViewById(R.id.home_timestamp);
//			viewHolder.home_origtext = (TextView) convertView.findViewById(R.id.home_origtext);
//			convertView.setTag(viewHolder);
//		}else {
//			viewHolder = (HomeViewHolder) convertView.getTag();
//		}
//
//		if (data != null) {
//			try {
//				//convertView.setTag(data.get("id"));
//				// viewHolder.home_headicon.setImageDrawable(ImageUtil.getDrawableFromUrl(data.getString("head")+"/100"));//同步加载图片
//				viewHolder.home_nick.setText(data.getCreateusername());
//				if (data.getStatus() != 1) {
//					viewHolder.home_vip.setVisibility(View.GONE);// 非vip隐藏vip标志
//				}
//
//				viewHolder.home_timestamp.setText(TimeUtil.converTime(Long.parseLong(data.getCreatetime())));
//				
//				//异步加载图片
//				String url = "";
//				url = urlArrays[position % urlArrays.length];
//				String headUrl = ImageUtil.concatUrl(data.getUserIcon());
//				viewHolder.home_headicon.setImageResource(R.drawable.ic_launcher);
//				if (!mBusy) {
//					mImageLoader.DisplayImage(headUrl, viewHolder.home_headicon, false);
//				} else {
//					mImageLoader.DisplayImage(headUrl, viewHolder.home_headicon, false);
//				}
//
//				if (null != data.getImgurl()) {
//					mImageLoader.DisplayImage(url, viewHolder.home_hasimage, false);
//				}
//
//				// 微博内容开始
//				String origtext = data.getContent();
//				SpannableString spannable = new SpannableString(origtext);
//				viewHolder.home_origtext.setText(spannable);
//				// 微博内容设置结束
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return convertView;
//	}
//
//	static class HomeViewHolder {
//		private ImageView home_headicon;
//		private TextView home_nick;
//		private ImageView home_vip;
//		private TextView home_timestamp;
//		private TextView home_origtext;
//		private ImageView home_hasimage;
//	}
//}
