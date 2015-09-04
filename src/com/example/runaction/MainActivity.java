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
	
	public void intentToTitle(){
		Intent intent = new Intent(MainActivity.this, TitleActivity.class);
//		intent.putExtra("settings", setting_value);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void intentToGoal(){
		Intent intent = new Intent(MainActivity.this, GoalActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}
}
