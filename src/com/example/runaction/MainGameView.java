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
	
	public MainGameView(MainActivity context, int setting){
		super(context);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// プレイヤー画像の設定(ここで呼び出すべきかどうか TODO )
		setGameImage();
		gameThread = new GameThread(holder, context, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		});
	}
	
	public void setGameImage(){
		ImageManager manager = ImageManager.getInstance();
		manager.loadBitmap(R.drawable.player);
		manager.loadBitmap(R.drawable.button);
		manager.loadBitmap(R.drawable.button_pressed);
		manager.loadBitmap(R.drawable.button_pause);
		manager.loadBitmap(R.drawable.button_pause_pressed);
		manager.loadBitmap(R.drawable.gameover);
		manager.loadBitmap(R.drawable.kuri);
		manager.loadBitmap(R.drawable.toge);
		manager.loadBitmap(R.drawable.jump);
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
