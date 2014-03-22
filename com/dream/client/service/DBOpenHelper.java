package com.dream.client.service;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context) {
		super(context, "dream.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE user (id long primary key autoincrement,name varchar(20) ,password varchar(20), role varchar(20), checkcode varchar(20), guid varchar(20), salt varchar(20), createtime varchar(20), updatetime varchar(20))";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
