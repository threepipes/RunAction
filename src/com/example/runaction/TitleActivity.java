package com.example.runaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TitleActivity extends Activity {

	private int setting_value = 0;
	public static final int SET_VOLUME = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_title);
		Button btn = (Button) findViewById(R.id.button);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TitleActivity.this, MainActivity.class);
				intent.putExtra("settings", setting_value);
				startActivity(intent);
			}
		});
		btn = (Button) findViewById(R.id.button_volume);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setting_value = (~setting_value)&SET_VOLUME;
				Button b = (Button) v;
				if((setting_value & SET_VOLUME) == 0){
					b.setText("BGM:ON");
				}else{
					b.setText("BGM:OFF");
				}
			}
		});
	}
}
