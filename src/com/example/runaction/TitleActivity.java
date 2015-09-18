package com.example.runaction;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class TitleActivity extends Activity {

	private Setting setting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadMusic();
		ImageManager.getInstance().setResources(getResources());
		
		setContentView(R.layout.activity_title);
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		StartFragment fragment = new StartFragment(this);
		fragmentTransaction.replace(R.id.container, fragment);
		fragmentTransaction.commit();
		
		setting = Setting.getInstance();
//		setButtonEvent();
		Log.d("TITLE", "called onCreate");
	}
	
	public void moveToSelect(){
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		SelectStageFragment fragment = new SelectStageFragment(this);

		fragmentTransaction.setCustomAnimations(
				R.anim.fragment_slide_right_enter
				, R.anim.fragment_slide_left_exit);
		fragmentTransaction.replace(R.id.container, fragment);
		fragmentTransaction.commit();
	}
	
	public void moveToTitle(){
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		StartFragment fragment = new StartFragment(this);
		fragmentTransaction.setCustomAnimations(
				R.anim.fragment_slide_left_enter
				, R.anim.fragment_slide_right_exit);
		fragmentTransaction.replace(R.id.container, fragment);
		fragmentTransaction.commit();
	}
	
	public void setStartAnimation(int stage){
		TitleView view = (TitleView)findViewById(R.id.titleView);
		view.setEvent(TitleView.EVENT_GAMESTART);
		view.setTouchState(true);
		view.setStage(stage);
	}

	public void setEvent(int event){
		((TitleView)findViewById(R.id.titleView)).setEvent(event);
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
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		StartFragment fragment = new StartFragment(this);
		fragmentTransaction.replace(R.id.container, fragment);
		fragmentTransaction.commit();
		
		MusicManager.getInstance().setBGM(R.raw.title, true, false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MusicManager.getInstance().setMusicState(false);
	}
}

class StartFragment extends Fragment{
	private TitleActivity activity;
	private Setting setting;
	public StartFragment(TitleActivity title) {
		activity = title;
		setting = Setting.getInstance();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_title, container, false);
		setButtonAction(view);
        return view;
	}
	
	private void setButtonAction(View view){
		// start
		Button btn = (Button) view.findViewById(R.id.button);
		btn.setVisibility(Button.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.moveToSelect();
			}
		});
		// bgm
		btn = (Button) view.findViewById(R.id.button_volume);
		if(setting.getSettingValue(Setting.SET_VOLUME_OFF)) btn.setText(R.string.button_voleme_off);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Button b = (Button) v;
				setting.setSettingValue(Setting.SET_VOLUME_OFF
						, !setting.getSettingValue(Setting.SET_VOLUME_OFF));
				if(!setting.getSettingValue(Setting.SET_VOLUME_OFF)){
					activity.setEvent(TitleView.EVENT_BGM_ON);
					b.setText(R.string.button_volume);
				}else{
					activity.setEvent(TitleView.EVENT_BGM_OFF);
					b.setText(R.string.button_voleme_off);
				}
			}
		});
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
				activity.setStartAnimation(0);
				setInvisibleSelectButton();
			}
		});
		btn = (Button) view.findViewById(R.id.button_back);
		btn.setVisibility(Button.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// titleに戻る
				activity.moveToTitle();
			}
		});
	}
	
	private void setInvisibleSelectButton(){
		// buttonの不可視化(フラグメントごと消せるならそれで)
		((Button) activity.findViewById(R.id.button_stage01)).setVisibility(Button.INVISIBLE);
		((Button) activity.findViewById(R.id.button_stage02)).setVisibility(Button.INVISIBLE);
		((Button) activity.findViewById(R.id.button_back)).setVisibility(Button.INVISIBLE);
	}
}

