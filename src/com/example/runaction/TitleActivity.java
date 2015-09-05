package com.example.runaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TitleActivity extends Activity {

	private Setting setting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageManager.getInstance().setResources(getResources());
		setting = Setting.getInstance();
		setContentView(R.layout.activity_title);
		Button btn = (Button) findViewById(R.id.button);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TitleActivity.this, MainActivity.class);
				intent.putExtra("settings", setting.getAllValue());
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			}
		});
		
		btn = (Button) findViewById(R.id.button_volume);
		if(setting.getSettingValue(Setting.SET_VOLUME)) btn.setText(R.string.button_voleme_off);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Button b = (Button) v;
				setting.setSettingValue(Setting.SET_VOLUME
						, !setting.getSettingValue(Setting.SET_VOLUME));
				if(!setting.getSettingValue(Setting.SET_VOLUME)){
					b.setText(R.string.button_volume);
				}else{
					b.setText(R.string.button_voleme_off);
				}
			}
		});

		Log.d("TITLE", "called onCreate");
	}

}
