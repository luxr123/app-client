package com.dream.client.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.dream.client.R;

import encode.BASE64Decoder;

public class MainActivity extends Activity {

	private ImageView iconShow;

	private Button show = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		iconShow = (ImageView) this.findViewById(R.id.icon_show);
		iconShow.setAdjustViewBounds(true); // 调整图片的大小

		show = (Button) findViewById(R.id.show);

		// iconShow.set

		show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				new MyRegister().execute();
			}

		});

	}

	private class MyRegister extends AsyncTask<Void, Void, String> {

		String url = "http://10.100.50.38:8080/app-web/user/showIcon";

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(Void... params) {
			HttpHeaders requestHeaders = new HttpHeaders();
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);
			RestTemplate restTemplate = new RestTemplate(true);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
			return responseEntity.getBody();
		}

		@Override
		protected void onPostExecute(String result) {
			System.out.println(result);
			byte[] b = null;
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(result);
				System.out.println("===================================");
				System.out.println("bbb:" + b.length);
				System.out.println("===================================");

				ByteArrayInputStream bais = null;
				bais = new ByteArrayInputStream(b);
				iconShow.setImageDrawable(Drawable.createFromStream(bais, "b"));// 把图片设置到ImageView对象中
				bais.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}