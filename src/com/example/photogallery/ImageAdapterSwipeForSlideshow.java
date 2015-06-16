/*hw #3
Shruti Satish
Kruti Satish*/

package com.example.photogallery;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageAdapterSwipeForSlideshow extends PagerAdapter {

	Context context;
	ArrayList<String> list;
	DiskLruCach diskLruCache;
	Map<String, String> valueMap = new HashMap<>();

	public ImageAdapterSwipeForSlideshow(PhotoActivity activity,
			ArrayList<String> result, DiskLruCach diskLruCache) {
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

		new AsyncTaskGetPhotos(imageView).execute(list.get(position));

		((ViewPager) container).addView(imageView, 0);
		return imageView;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}

	private class AsyncTaskGetPhotos extends AsyncTask<String, Void, Bitmap> {
		ImageView imageView;

		public AsyncTaskGetPhotos(ImageView imageView) {
			super();
			this.imageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			Bitmap image = null;
			try {
				if (diskLruCache.containsKey(valueMap.get(arg0[0]))) {

					return diskLruCache.getBitmap(valueMap.get(arg0[0]));
				}

				URL url = new URL(arg0[0]);
				image = BitmapFactory.decodeStream(url.openStream());
				diskLruCache.put(valueMap.get(arg0[0]), image);
			} catch (IOException e) {

				e.printStackTrace();
			}

			return image;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
		}
	}

}
