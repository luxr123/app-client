
package com.dream.client.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dream.client.R;

/**
 * User: xiaorui.lu
 * Date: 2014年1月19日 下午8:03:26
 */
public class TestView extends Activity{

	private ImageView iv_img;
	private int windowHeight;
	private int windowWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		this.iv_img = (ImageView) this.findViewById(R.id.iv_img);
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		// 第一种获取手机屏幕宽高的方法
		this.windowHeight = manager.getDefaultDisplay().getHeight();
		this.windowWidth = manager.getDefaultDisplay().getWidth();
		System.out.println("手机宽 ：" + this.windowWidth);
		System.out.println("手机高 ：" + this.windowHeight);

		// 第二种获取手机屏幕宽高的方法，但是getSize()是从 API Level 13才有的方法
		// Point outSize = new Point();
		// manager.getDefaultDisplay().getSize(outSize );
		// this.windowWidth = outSize.x;
		// this.windowHeight = outSize.y;
	}

	public void load1(View view) {
		String src = "mnt/sdcard/DSC.jpg";
		Bitmap bitmap = BitmapFactory.decodeFile(src);
		this.iv_img.setImageBitmap(bitmap);
	}

	public void load2(View view) {
		String src = "mnt/sdcard/DSC.jpg";

		// 图片解析的配置
		Options options = new Options();
		// 不去真正解析图片，只是获取图片的宽高
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(src, options);
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		System.out.println("图片宽 :" + imageWidth);
		System.out.println("图片高 :" + imageHeight);

		int scaleX = imageWidth / this.windowWidth;
		int scaleY = imageHeight / this.windowHeight;
		int scale = 1;
		if (scaleX >= scaleY && scaleX >= 1) {
			// 水平方向的缩放比例比竖直方向的缩放比例大，同时图片的宽要比手机屏幕要大,就按水平方向比例缩放
			System.out.println("按宽比例缩放");
			scale = scaleX;
		} else if (scaleY >= scaleX && scaleY >= 1) {
			// 竖直方向的缩放比例比水平方向的缩放比例大，同时图片的高要比手机屏幕要大，就按竖直方向比例缩放
			System.out.println("按高比例缩放");
			scale = scaleY;
		}
		System.out.println("缩放比例：" + scale);
		// 真正解析图片
		options.inJustDecodeBounds = false;
		// 设置采样率
		options.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeFile(src,options);
		this.iv_img.setImageBitmap(bitmap);

	}
}

