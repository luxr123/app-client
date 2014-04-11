package com.dream.syncloaderbitmap.cache;

import com.dream.syncloaderbitmap.util.FileManager;

import android.content.Context;

public class FileCache extends AbstractFileCache {

	public FileCache(Context context) {
		super(context);

	}

	@Override
	public String getSavePath(String url) {
		String filename = String.valueOf(url.hashCode());
		return getCacheDir() + filename;
	}

	@Override
	public String getCacheDir() {

		return FileManager.getSaveFilePath();
	}

}
