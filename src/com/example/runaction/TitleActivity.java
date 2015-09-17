package com.example.runaction;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class TitleActivity extends Activity {

	private Setting setting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadMusic();
		ImageManager.getInstance().setResources(getResources());
		setting = Setting.getInstance();
		setContentView(R.layout.activity_title);
		setButtonEvent();
		Log.d("TITLE", "called onCreate");
	}
	
	public void setStartAnimation(int stage){
		TitleView view = (TitleView)findViewById(R.id.titleView);
		view.setEvent(TitleView.EVENT_GAMESTART);
		view.setTouchState(true);
		view.setStage(stage);
//		((Button) findViewById(R.id.button)).setVisibility(Button.INVISIBLE);
//		((Button) findViewById(R.id.button_volume)).setVisibility(Button.INVISIBLE);
	}
	
	private void setButtonEvent(){
		Button btn = (Button) findViewById(R.id.button);
//		btn.setVisibility(Button.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setStartAnimation(0);
//				RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_title);
//				layout.removeAllViews();
//				getLayoutInflater().inflate(R.layout.layout_select, layout);
//				setStageButtonAction();
			}
		});
		
		btn = (Button) findViewById(R.id.button_volume);
//		btn.setVisibility(Button.VISIBLE);
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
	
	public void intentToGame(int stage){
		Intent intent = new Intent(TitleActivity.this, MainActivity.class);
		intent.putExtra("stage", stage);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		TitleView view = (TitleView)findViewById(R.id.titleView);
		view.setTouchState(false);
//		((Button) findViewById(R.id.button)).setVisibility(Button.VISIBLE);
//		((Button) findViewById(R.id.button_volume)).setVisibility(Button.VISIBLE);
		
		MusicManager.getInstance().setBGM(R.raw.title, true, false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MusicManager.getInstance().setMusicState(false);
	}
}


class SelectStageFragment extends Fragment{
	private TitleActivity activity;
	public SelectStageFragment(TitleActivity title) {
		activity = title;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.layout_select, container, false);
		setStageButtonAction(rootView);
        return rootView;
	}
	
	private void setStageButtonAction(View view){
		Button btn = (Button) view.findViewById(R.id.button_stage01);
		btn.setVisibility(Button.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.setStartAnimation(0);
				setInvisibleSelectButton();
			}
		});
		btn = (Button) view.findViewById(R.id.button_stage02);
		btn.setVisibility(Button.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.setStartAnimation(1);
				setInvisibleSelectButton();
			}
		});
		btn = (Button) view.findViewById(R.id.button_back);
		btn.setVisibility(Button.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// titleに戻る
			}
		});
	}
	
	private void setInvisibleSelectButton(){
		// buttonの不可視化(フラグメントごと消せるならそれで)
	}
}
