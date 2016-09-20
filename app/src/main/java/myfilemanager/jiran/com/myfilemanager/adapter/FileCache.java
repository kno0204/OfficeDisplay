package myfilemanager.jiran.com.myfilemanager.adapter;

import java.io.File;


import android.content.Context;
import android.util.Log;

public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {
		// Find the dir to save cached images
		cacheDir = new File(android.os.Environment.getExternalStorageDirectory() + "/Android/data/com.jiran.directbox/.img/");

		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url) {
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.

		String filename = String.valueOf(url.hashCode());

		// Another possible solution (thanks to grantland)
		// String filename = URLEncoder.encode(url);
		// Log.v("jiran getfile", filename);
		File f = new File(cacheDir, filename);

		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

	public void log(String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	private final String tag = this.getClass().getName();
	private final boolean isDebug = true;

}