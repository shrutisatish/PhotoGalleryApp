/*hw #3
Shruti Satish
Kruti Satish*/

package com.example.photogallery;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.disklrucache.DiskLruCache;

public class DiskLruCach {

	private DiskLruCache diskLruCache;

	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	private static final String DISK_CACHE_SUBDIR = "uncc_photos";

	private File cacheDir;

	public DiskLruCach(Context context) {

		cacheDir = getDiskCacheDir(context);
		try {
			diskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
		} catch (IOException e) {
			Toast.makeText(context, "Failed to create Disk LRU Cache",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
			throws IOException, FileNotFoundException {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(editor.newOutputStream(0),
					Utils.IO_BUFFER_SIZE);
			return bitmap.compress(CompressFormat.JPEG, 100, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private File getDiskCacheDir(Context context) {

		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Utils.isExternalStorageRemovable() ? Utils
				.getExternalCacheDir(context).getPath() : context.getCacheDir()
				.getPath();

		return new File(cachePath + File.separator + DISK_CACHE_SUBDIR);
	}

	public void put(String key, Bitmap data) {

		DiskLruCache.Editor editor = null;
		try {
			editor = diskLruCache.edit(key);
			if (editor == null) {
				return;
			}

			if (writeBitmapToFile(data, editor)) {
				diskLruCache.flush();
				editor.commit();
				if (BuildConfig.DEBUG) {
					Log.d("Dumbo", "image put on disk cache index: " + key);
				}
			} else {
				editor.abort();
				if (BuildConfig.DEBUG) {
					Log.d("Dumbo", "ERROR on: image put on disk cache index: "
							+ key);
				}
			}
		} catch (IOException e) {
			if (BuildConfig.DEBUG) {
				Log.d("Dumbo", "ERROR on: image put on disk cache index:" + key);
			}
			try {
				if (editor != null) {
					editor.abort();
				}
			} catch (IOException ignored) {
			}
		}

	}

	public Bitmap getBitmap(String key) {

		Bitmap bitmap = null;
		DiskLruCache.Snapshot snapshot = null;
		try {

			snapshot = diskLruCache.get(key);
			if (snapshot == null) {
				return null;
			}
			final InputStream in = snapshot.getInputStream(0);
			if (in != null) {
				final BufferedInputStream buffIn = new BufferedInputStream(in,
						Utils.IO_BUFFER_SIZE);
				bitmap = BitmapFactory.decodeStream(buffIn);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) {
				snapshot.close();
			}
		}

		if (BuildConfig.DEBUG) {
			Log.d("Dumbo", bitmap == null ? ""
					: "image read from disk cache index: " + key);
		}

		return bitmap;
	}

	public boolean containsKey(String key) {

		boolean contained = false;
		DiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = diskLruCache.get(key);
			contained = snapshot != null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) {
				snapshot.close();
			}
		}

		return contained;

	}

	public void clearCache() {
		if (BuildConfig.DEBUG) {
			Log.d("Dumbo", "disk cache CLEARED");
		}
		try {
			diskLruCache.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getCacheFolder() {
		return diskLruCache.getDirectory();
	}
}
