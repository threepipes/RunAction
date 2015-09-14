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
		loadMusic();
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
				TitleView view = (TitleView)findViewById(R.id.titleView);
				view.setEvent(TitleView.EVENT_GAMESTART);
				view.setTouchState(true);
//				intentToGame();
				((Button) findViewById(R.id.button)).setVisibility(Button.INVISIBLE);
				((Button) findViewById(R.id.button_volume)).setVisibility(Button.INVISIBLE);
			}
		});
		
		btn = (Button) findViewById(R.id.button_volume);
		btn.setVisibility(Button.VISIBLE);
		if(setting.getSettingValue(Setting.SET_VOLUME_OFF)) btn.setText(R.string.button_voleme_off);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Button b = (Button) v;
				setting.setSettingValue(Setting.SET_VOLUME_OFF
						, !setting.getSettingValue(Setting.SET_VOLUME_OFF));
				if(!setting.getSettingValue(Setting.SET_VOLUME_OFF)){
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
	
	private void loadMusic(){
		MusicManager manager = MusicManager.getInstance();
		// set SE
		manager.setContext(this);
		int[] seList = {
				R.raw.jump,
		};
		manager.loadSE(seList);
		
		// set BGM
//		manager.setBGM(R.raw.title, true);
	}
	
	public void intentToGame(){
		Intent intent = new Intent(TitleActivity.this, MainActivity.class);
		intent.putExtra("settings", setting.getAllValue());
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		TitleView view = (TitleView)findViewById(R.id.titleView);
		view.setTouchState(false);
		((Button) findViewById(R.id.button)).setVisibility(Button.VISIBLE);
		((Button) findViewById(R.id.button_volume)).setVisibility(Button.VISIBLE);
		
		MusicManager.getInstance().setBGM(R.raw.title, true, false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MusicManager.getInstance().setMusicState(false);
	}
}
