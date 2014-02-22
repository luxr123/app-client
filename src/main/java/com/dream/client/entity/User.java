package com.dream.client.entity;

import java.util.Date;

/**
 * User: xiaorui.lu 
 * Date: 2013年12月13日 下午4:10:16
 */
public class User {

	private long id;

	private String name;
	
	private String password;

	private String gender;
	
	private boolean role;
	
	private String checkcode;

	private String guid;

	private String salt;

	private Date createtime;

	private Date updatetime;
	
	private String icon;
	
	private String iconPath;
	
	private String udid;
	
	private String tags;
	
	

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean isRole() {
		return role;
	}

	public void setRole(boolean role) {
		this.role = role;
	}

	public String getCheckcode() {
		return checkcode;
	}

	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + password + ", gender=" + gender + ", role=" + role
				+ ", checkcode=" + checkcode + ", guid=" + guid + ", salt=" + salt + ", createtime=" + createtime + ", updatetime="
				+ updatetime + ", icon=" + icon + ", iconPath=" + iconPath + "]";
	}



}
