package com.dream.client;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

public class TaskPublishedActivity extends Activity {
	
	private EditText editTask = null;
	private Button ok = null;
	private Button cancle = null;
	private ImageButton imgTaskBtn = null;
	private GridView  gridView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_published);
		
		editTask = (EditText) this.findViewById(R.id.editTask);
		ok = (Button) this.findViewById(R.id.task_ok);
		cancle = (Button) this.findViewById(R.id.task_cancle);
		imgTaskBtn = (ImageButton) this.findViewById(R.id.imageTask);
		
		gridView = (GridView)findViewById(R.id.gridview_task);
		
		
		
		ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_published, menu);
		return true;
	}

}
