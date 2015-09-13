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
//		TitleView titleView = new TitleView(this);
		setContentView(R.layout.activity_title);
//		setContentView(titleView);
		Button btn = (Button) findViewById(R.id.button);
		btn.setVisibility(Button.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((TitleView)findViewById(R.id.titleView)).setEvent(TitleView.EVENT_GAMESTART);
//				intentToGame();
				((Button) findViewById(R.id.button)).setVisibility(Button.INVISIBLE);
				((Button) findViewById(R.id.button_volume)).setVisibility(Button.INVISIBLE);
			}
		});
		
		btn = (Button) findViewById(R.id.button_volume);
		btn.setVisibility(Button.VISIBLE);
		if(setting.getSettingValue(Setting.SET_VOLUME)) btn.setText(R.string.button_voleme_off);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Button b = (Button) v;
				setting.setSettingValue(Setting.SET_VOLUME
						, !setting.getSettingValue(Setting.SET_VOLUME));
				if(!setting.getSettingValue(Setting.SET_VOLUME)){
					((TitleView)findViewById(R.id.titleView)).setEvent(TitleView.EVENT_BGM_ON);
					b.setText(R.string.button_volume);
				}else{
					((TitleView)findViewById(R.id.titleView)).setEvent(TitleView.EVENT_BGM_OFF);
					b.setText(R.string.button_voleme_off);
				}
			}
		});

		Log.d("TITLE", "called onCreate");
	}
	
	public void intentToGame(){
		Intent intent = new Intent(TitleActivity.this, MainActivity.class);
		intent.putExtra("settings", setting.getAllValue());
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}

}
