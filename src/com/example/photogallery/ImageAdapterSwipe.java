/*hw #3
Shruti Satish
Kruti Satish*/

package com.example.photogallery;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageAdapterSwipe extends PagerAdapter {
	ProgressDialog progressDialog;
	Context context;
	ArrayList<String> list;

	DiskLruCach diskLruCache;
	Map<String, String> valueMap = new HashMap<>();

	public ImageAdapterSwipe(PhotoActivity activity, ArrayList<String> result,
			DiskLruCach diskLruCache) {
		this.context = activity;
		this.list = result;
		this.diskLruCache = diskLruCache;
		int count = 0;
		for (String string : result) {
			valueMap.put(string, String.valueOf(count));
			count++;
		}
	}

	public int getCount() {
		return list.size();
	}

	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(context);

		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Loading Image...");
		progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
		// progressDialog.show();
		new AsyncTaskGetPhotos(imageView, progressDialog).execute(list
				.get(position));

		((ViewPager) container).addView(imageView, 0);
		return imageView;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}

	private class AsyncTaskGetPhotos extends AsyncTask<String, Void, Bitmap> {
		ImageView imageView;

		ProgressDialog progressDialog;

		public AsyncTaskGetPhotos(ImageView imageView,
				ProgressDialog progressDialog2) {
			super();
			this.imageView = imageView;
			this.progressDialog = progressDialog2;

		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			Bitmap image = null;
			try {

				if (diskLruCache.containsKey(valueMap.get(arg0[0]))) {
					// progressDialog.dismiss();
					return diskLruCache.getBitmap(valueMap.get(arg0[0]));
				}

				URL url = new URL(arg0[0]);
				image = BitmapFactory.decodeStream(url.openStream());
				diskLruCache.put(valueMap.get(arg0[0]), image);
				// progressDialog.dismiss();
			} catch (IOException e) {

				e.printStackTrace();
			}

			return image;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		//	progressDialog.show();
		}

		@Override
		protected void onPostExecute(final Bitmap result) {
			//progressDialog.dismiss();
			imageView.setImageBitmap(result);

		}
	}

}
