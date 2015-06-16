/*hw #3
Shruti Satish
Kruti Satish*/

package com.example.photogallery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;

public class AsyncGetUrl extends AsyncTask<String, Void, ArrayList<String>> {
	ProgressDialog progressDialog;
	 MainActivity activity;

	public AsyncGetUrl(MainActivity mainActivity) {
		this.activity = mainActivity;
	}


	@Override
	protected ArrayList<String> doInBackground(String... params) {
		Bundle bundle = new Bundle();
		ArrayList<String> result1 = new ArrayList<String>();

		try {

			URL url = new URL("http://liisp.uncc.edu/~mshehab/api/photos.txt");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			String result = "", line;
			int statusCode = con.getResponseCode();
			if (statusCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));

				while ((line = in.readLine()) != null) {
					result1.add(line);
				}
				in.close();
				con.disconnect();
			}
			Log.d("demo", result);	

		} catch (MalformedURLException e) {
			bundle.putString("ERROR", "Problem with URL");
		} catch (IOException e) {
			bundle.putString("ERROR", "Problem with connection");
		}
		return result1;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(activity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Retrieving Image URL's");
		progressDialog.show();
	}

	@Override
	protected void onPostExecute(ArrayList<String> result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		activity.setList(result);
		
	}

}
