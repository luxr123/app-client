package com.dream.client.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * User: xiaorui.lu 
 * Date: 2014年1月2日 下午8:14:44
 */
public class ErrorCode {
	// 存储k-v
	static Map<String, String> map = new HashMap<String, String>();

	// 定义所有的错误码以及对应的信息
	public static String CODE_SUCCESS = "0000";
	static {
		map.put(CODE_SUCCESS, "执行成功");
	}

	public static String CODE_FAILED = "0001";
	static {
		map.put(CODE_FAILED, "执行失败");
	}

	public static String CODE_FILE_NOT_EXIST = "0002";
	static {
		map.put(CODE_FILE_NOT_EXIST, "文件不存在");
	}

	public static String CODE_MISSING_PARAM = "0003";
	static {
		map.put(CODE_MISSING_PARAM, "缺少参数");
	}

	public static String CODE_STATUS_ERROR = "0004";
	static {
		map.put(CODE_STATUS_ERROR, "状态错误");
	}
	
	public static String CODE_CHECK_ERROR = "0004";
	static {
		map.put(CODE_CHECK_ERROR, "验证码错误");
	}

	// 特定的几个错误对象
	public static ErrorCode SUCCESS = new ErrorCode(CODE_SUCCESS);

	public static ErrorCode FAILED = new ErrorCode(CODE_FAILED);
	
	// 两个属性
	private String err;
	private String msg;
	private boolean succeed;

	public ErrorCode() {
	}

	public ErrorCode(String err) {
		this.err = err;
		this.msg = map.get(this.err);
		this.succeed = isSucceed();
	}

	public ErrorCode(String err, String msg) {
		this.err = err;
		this.msg = map.get(this.err) + "--" + msg;
		this.succeed = isSucceed();
	}

	public void setSucceed(boolean succeed) {
		this.succeed = succeed;
	}

	public boolean isSucceed() {
		return this.err.equals(CODE_SUCCESS);
	}

	public String getErr() {
		return err;
	}

	public void setErr(String err) {
		this.err = err;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "ErrorCode [err=" + err + ", msg=" + msg + ", succeed=" + succeed + "]";
	}

	public static void main(String[] args) {
		ErrorCode errorCode = new ErrorCode("0000");
		System.out.println(errorCode);
		System.out.println(ErrorCode.SUCCESS);
	}
}
