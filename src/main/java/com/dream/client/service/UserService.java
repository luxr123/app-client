package com.dream.client.service;

import android.content.Context;

public class UserService {

	private DBOpenHelper dbOpenHelper;

	public UserService(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
	}

}
