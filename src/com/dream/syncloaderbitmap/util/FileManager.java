package com.dream.syncloaderbitmap.util;


public class FileManager {

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "com/dream/files/";
		} else {
			return CommonUtil.getRootFilePath() + "com.dream/files/";
		}
	}
}
