/*hw #3
Shruti Satish
Kruti Satish*/

package com.example.photogallery;

import java.util.ArrayList;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	ArrayList<String> list;

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new AsyncGetUrl(MainActivity.this).execute();

		Button btn1 = (Button) findViewById(R.id.button1);
		btn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						PhotoActivity.class);
			    intent.putExtra("urllist",list);
			    intent.putExtra("mode", "display");
			    
			    Log.d("demo1", list.toString());
				startActivity(intent);

			}
		});

		Button btn2 = (Button) findViewById(R.id.button2);
		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						PhotoActivity.class);
				intent.putExtra("urllist",list);
				 intent.putExtra("mode", "slideshow");
				startActivity(intent);

			}
		});

	}
}
