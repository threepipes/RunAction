package com.example.runaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		int setting = intent.getIntExtra("settings", 0);
		
		MainGameView gview = new MainGameView(this, setting);
		setContentView(gview);
	}
}
