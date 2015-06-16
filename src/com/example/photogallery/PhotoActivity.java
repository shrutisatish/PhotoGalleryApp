/*hw #3
Shruti Satish
Kruti Satish*/


package com.example.photogallery;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

public class PhotoActivity extends Activity {
	int position = 0;
	Runnable runnable = null;
	String mode;
	DiskLruCach diskLruCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		mode = getIntent().getExtras().getString("mode");
		diskLruCache = new DiskLruCach(getApplicationContext());
	//	diskLruCache.clearCache();
		new AsyncTaskGetPhotos(this).execute();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		diskLruCache.clearCache();

	}

	private class AsyncTaskGetPhotos extends
			AsyncTask<String, Void, ArrayList<String>> {

		private PhotoActivity activity;

		public AsyncTaskGetPhotos(PhotoActivity photoActivity) {
			this.activity = photoActivity;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected ArrayList<String> doInBackground(String... params) {

			return (ArrayList<String>) getIntent().getExtras().getSerializable(
					"urllist");

		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			super.onPostExecute(result);

			if (mode.equals("display")) {
				final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

				ImageAdapterSwipe adapter = new ImageAdapterSwipe(activity,
						result, diskLruCache);

				viewPager.setAdapter(adapter);
				viewPager.setCurrentItem(0);
			} else {
				final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

				ImageAdapterSwipeForSlideshow adapter = new ImageAdapterSwipeForSlideshow(
						activity, result, diskLruCache);

				viewPager.setAdapter(adapter);
				viewPager.setCurrentItem(0);
				final Handler handler = new Handler();

				runnable = new Runnable() {
					public void run() {

						viewPager.setCurrentItem(position, true);
						position = position + 1;
						handler.postDelayed(runnable, 5000);
					}
				};
				runnable.run();
			}
		}

	}
}
