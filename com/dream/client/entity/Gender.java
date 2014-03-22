package com.dream.client.entity;

/**
 * User: xiaorui.lu 
 * Date: 2014年1月20日 下午10:00:19
 */
public enum Gender {

	male("male"), female("female");

	private final String gender;

	private Gender(String gender) {
		this.gender = gender;
	}

	public String getGender() {
		return gender;
	}

}
