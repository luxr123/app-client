package com.dream.client.view;

/**
 * User: xiaorui.lu
 * Date: 2014年1月14日 下午10:53:46
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dream.client.R;

import encode.BASE64Decoder;
import encode.BASE64Encoder;
 
/**
 * Activity 上传的界面
 *
 */
public class SelectFileActivity extends Activity implements OnClickListener {
    private static final String TAG = "uploadImage";
    private static String requestURL = "http://10.100.50.38:8080/app-web/user/uploadImg";
    private Button selectImage, uploadImage;
    private ImageView imageView;
    private ImageView imageView2;
    private static Uri uri;
 
    private String picPath = null;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
 
        selectImage = (Button) this.findViewById(R.id.selectImage);
        uploadImage = (Button) this.findViewById(R.id.uploadImage);
        selectImage.setOnClickListener(this);
        uploadImage.setOnClickListener(this);
 
        imageView = (ImageView) this.findViewById(R.id.imageView);
    }
 
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.selectImage:
            /***
             * 这个是调用android内置的intent，来过滤图片文件 ，同时也可以过滤其他的
             */
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent, 1);
            break;
        case R.id.uploadImage:
        	if (uri == null) {
				Toast.makeText(SelectFileActivity.this, "您尚未选择等待上传的图片", Toast.LENGTH_LONG).show();
			}
        	String path = GetImageStr(uri);
        	System.out.println("===================================");
        	System.out.println("123:" + path.length());
        	System.out.println("===================================");
        	byte[] b = null;
			// 对获得的数据进行64解码处理
			BASE64Decoder decoder = new BASE64Decoder();
			// b = bkb.getBytes();
			try {
				b = decoder.decodeBuffer(path);
				System.out.println("===================================");
				System.out.println("bbb:" + b.length);
				System.out.println("===================================");
				
				ByteArrayInputStream bais = null;
				bais = new ByteArrayInputStream(b);
				imageView2.setImageDrawable(Drawable.createFromStream(bais, "b"));// 把图片设置到ImageView对象中
				bais.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
            /*File file = new File(picPath);
            if (file != null) {
                String request = UploadUtil.uploadFile(file, requestURL);
                uploadImage.setText(request);
            }*/
            break;
        default:
            break;
        }
    }
 
	// 图片选择回调事件
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			/**
			 * 当选择的图片不为空的话，在获取到图片的途径
			 */
			uri = data.getData();
			Log.e(TAG, "uri = " + uri);
			// 通过路径加载图片
			// 图片缩放的实现，请看：http://blog.csdn.net/reality_jie_blog/article/details/16891095
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				((BitmapDrawable) imageView.getDrawable()).getBitmap().compress(CompressFormat.PNG, 50, baos);// 压缩为PNG格式,100表示跟原图大小一样
				baos.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			this.imageView.setImageURI(uri);
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
 
	private void alert() {
		Dialog dialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("您选择的不是有效的图片")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						picPath = null;
					}
				}).create();
		dialog.show();
	}
 
}