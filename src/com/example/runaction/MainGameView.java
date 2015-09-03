package com.example.runaction;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGameView extends SurfaceView
	implements SurfaceHolder.Callback{
	GameThread gameThread;
//	MainActivity activity;
	
	public MainGameView(MainActivity context, int setting){
		super(context);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		gameThread = new GameThread(holder, context, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		});
//		activity = context;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gameThread.setEvent(event);
		return true;
	}
	
	public MainGameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MainGameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		gameThread.setWindowSize(width, height);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		gameThread.start();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		gameThread.destroy();
		gameThread = null;
	}

}
