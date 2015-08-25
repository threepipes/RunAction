package com.example.runaction;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MainGameView extends SurfaceView
	implements SurfaceHolder.Callback{
	GameThread gameThread;
	Player player;
	static final int pSize = 200;
	
	public MainGameView(Context context){
		super(context);
	}
	
	public MainGameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		gameThread = new GameThread(holder, context, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		});
		player = new Player(context, pSize, pSize);
		player.setXY(100, 100);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		gameThread.start();
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		gameThread = null;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(0xFF99AAFF);
		player.draw(canvas);
	}
	
	public void update(){
		
	}
	
	class GameThread extends Thread{
		SurfaceHolder surfaceHolder;
		boolean shouldContinue = true;
		public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler){
			this.surfaceHolder = surfaceHolder;
		}
		
		@Override
		public void run() {
			while(shouldContinue){
				Canvas c = surfaceHolder.lockCanvas();
				update();
				draw(c);
				surfaceHolder.unlockCanvasAndPost(c);
			}
		}
		
		public void draw(Canvas c){
			c.drawARGB(255, 0, 0, 0);
		}
		
		
	}
}
