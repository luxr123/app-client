package com.dream.mulimage.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.dream.client.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;

public class DateTimeActivity extends Activity {

	private DatePicker datePicker;
	private TimePicker timePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datetime);

		init();
		initDate();
	}

	private void init() {
		datePicker = (DatePicker) findViewById(R.id.dpPicker);
		timePicker = (TimePicker) findViewById(R.id.tpPicker);
	}

	private void initDate() {
		datePicker.init(2014, 2, 10, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				// 获取一个日历对象，并初始化为当前选中的时间
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, monthOfYear, dayOfMonth);
				SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
				Toast.makeText(DateTimeActivity.this, format.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
			}

		});

		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				//Toast.makeText(DateTimeActivity.this, hourOfDay + "小时" + minute + "分钟", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void setDatetime(View v) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		bundle.putString("datetime", format.format(calendar.getTime()));
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}

}
