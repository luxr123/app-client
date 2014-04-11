package com.dream.db;

import com.dream.client.entity.User;
import com.dream.client.entity.UserTask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteHelper extends SQLiteOpenHelper {
	public static final String TB_USER = "user";
	public static final String TB_USERTASK = "usertask";

	public SqliteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	// 创建表
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_USER + "(" + 
				User.ID + " integer primary key," + 
				User.USERID + " integer,"+ 
				User.NAME + " varchar,"+ 
				User.ICONURL + " varchar" + ")");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_USERTASK + "(" + 
				UserTask.ID + " integer primary key," + 
				UserTask.TASKID + " integer,"+ 
				UserTask.CONTENT + " varchar,"+ 
				UserTask.IMGURL + " varchar," + 
				UserTask.CONTACTINFO + " varchar," + 
				UserTask.CREATETIME  + " varchar," + 
				UserTask.ENDTIME + " varchar," + 
				UserTask.CREATEUSERID + " integer" + ")");
		
		Log.e("Database", "onCreate");
	}

	// 更新表
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TB_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TB_USERTASK);
		onCreate(db);
		Log.e("Database", "onUpgrade");
	}

	// 更新列
	public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn) {
		try {
			db.execSQL("ALTER TABLE " + TB_USERTASK + " CHANGE " + oldColumn + " " + newColumn + " " + typeColumn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}