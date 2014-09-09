package com.scouthere;

import com.scouthere.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NotConnectedActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notconnected);
	}
	
	public void retryConnection(View view) {

		Intent intent = new Intent(this, InitialActivity.class);
		startActivity(intent);

	}

}
