package com.dream.db;

import java.util.ArrayList;
import java.util.List;

import com.dream.client.Config;
import com.dream.client.entity.User;
import com.dream.client.entity.UserTask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataHelper {
	// 数据库名称
	private static String DB_NAME = "nexus.db";
	// 数据库版本
	private static int DB_VERSION = 1;
	private SQLiteDatabase db;
	private SqliteHelper dbHelper;

	public DataHelper(Context context) {
		//定义一个SQLite数据库
		dbHelper = new SqliteHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public void Close() {
		db.close();
		dbHelper.close();
	}

	/**
	 * 保存新用户注册信息
	 * @param userTask
	 */
	public Long saveUser() {
		ContentValues values = new ContentValues();
		values.put(User.USERID, Config.myId);
		values.put(User.NAME, Config.myName);
		values.put(User.ICONURL, Config.myIconUrl);
		Long uid = db.insert(SqliteHelper.TB_USER, User.USERID, values);
		Log.e("SaveUser", uid + "");
		return uid;
	}
	
	/**
	 * 得到用户头像url
	 * @param userTask
	 */
	public String getUserIconUrl(long userid) {
		Cursor cursor = db.query(SqliteHelper.TB_USER, new String[] { User.ICONURL }, User.USERID + "=?",
				new String[] { String.valueOf(userid) }, null, null, null);
		if (cursor.moveToNext())
			return cursor.getString(0);
		return null;
	}
	
	/**
	 * 保存任务信息
	 * @param userTask
	 */
	public Long saveUserTask(UserTask userTask) {
		ContentValues values = new ContentValues();
		values.put(UserTask.TASKID, userTask.getId());
		values.put(UserTask.CONTENT, userTask.getContent());
		values.put(UserTask.CONTACTINFO, userTask.getContactinfo());
		values.put(UserTask.CREATETIME, userTask.getCreatetime());
		values.put(UserTask.CREATEUSERID, userTask.getCreateuserid());
		values.put(UserTask.ENDTIME, userTask.getEndtime());
		Long tid = db.insert(SqliteHelper.TB_USERTASK, UserTask.TASKID, values);
		Log.e("SaveUserTask", tid + "");
		return tid;
	}
	
	public List<UserTask> getAllTask(){
		List<UserTask> taskList = new ArrayList<UserTask>();
		Cursor cursor = db.query(SqliteHelper.TB_USERTASK, new String[] {UserTask.ID,UserTask.CONTENT,UserTask.CREATETIME}
		, null, null, null,
				null, UserTask.ID + " DESC", "0,10");
		UserTask task;
		while (cursor.moveToNext()) {
			task = new UserTask();
			task.setId(cursor.getLong(0));
			task.setContent(cursor.getString(1));
			task.setCreatetime(cursor.getString(2));
			taskList.add(task);
		}
		return taskList;
	}
}