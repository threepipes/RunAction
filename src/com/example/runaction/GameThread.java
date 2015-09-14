package com.example.runaction;

import com.example.runaction.game.GameMode;
import com.example.runaction.game.Mode;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public 	class GameThread extends Thread{
	SurfaceHolder surfaceHolder;
	Mode mode;
	int keyEvent;
	float keyX, keyY;
	MainActivity activity;
	
	public static final int WINDOW_WIDTH = 540;
	public static final int WINDOW_HEIGHT = 960;
	public static final Rect WindowRect = new Rect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
	public float scale;
	public float translateX;
	public float translateY;
	
	private Setting setting;
	
	boolean shouldContinue = true;
	public GameThread(SurfaceHolder surfaceHolder, MainActivity context, Handler handler){
		this.surfaceHolder = surfaceHolder;
		mode = new GameMode(context, this);
		setting = Setting.getInstance();
		
//		if(!setting.getSettingValue(Setting.SET_VOLUME_OFF)){
//			MusicManager mManager = MusicManager.getInstance();
//			mManager.setBGM(R.raw.chiptune, true);
//		}
		activity = context;
	}
	
	
	public void setEvent(MotionEvent e){
		final int act = e.getAction();
		Log.d("Mes", act+"");
		if((act & 2) > 0 || act == 0){
			keyEvent |= GameMode.KEY_PRESSED;
		}
		if((act & 1) > 0){
			keyEvent |= GameMode.KEY_RELEASED;
		}
		keyX = (e.getX()-translateX)/scale;
		keyY = (e.getY()-translateY)/scale;
	}
	
	public void destroy(){
		shouldContinue = false;
//		MusicManager.getInstance().setMusicState(false);
	}
	
	private Rect[] black;
	public void setWindowSize(int w, int h){
		float scaleX = (float)w / WINDOW_WIDTH;
		float scaleY = (float)h /  WINDOW_HEIGHT;
		scale = scaleX > scaleY ? scaleY : scaleX;
		translateX = (w - WINDOW_WIDTH*scale)/2;
		translateY = (h - WINDOW_HEIGHT*scale)/2;
		black = new Rect[2];
		if(translateX > 0){
			black[0] = new Rect(0, 0, (int)translateX, h);
			black[1] = new Rect((int)(translateX + WINDOW_WIDTH*scale), 0
					, (int)(translateX*2 + WINDOW_WIDTH*scale), h);
		}else{
			black[0] = new Rect(0, 0, w, (int)translateY);
			black[1] = new Rect(0, (int)(translateY + WINDOW_HEIGHT*scale)
					, w, (int)(translateY*2 + WINDOW_HEIGHT*scale));
		}
		Log.d("Window", "scale: "+scale+" , tX: "+translateX+" , tY: "+translateY);
		mode.setWindowSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	}
	
	@Override
	public void run() {
		Paint paint = new Paint();
		setPaint(paint);
		while(shouldContinue){
			Canvas c = surfaceHolder.lockCanvas();
			if(c == null) break;
			c.translate(translateX, translateY);
			c.scale(scale, scale);
			
			if(keyEvent != 0){
				mode.touchEvent((int)keyX, (int)keyY, keyEvent);
				keyEvent = 0;
			}
			mode.update();
			mode.draw(c, paint);
			if(black != null){
				c.scale(1.0f/scale, 1.0f/scale);
				c.translate(-translateX, -translateY);
				paint.setColor(0xFF000000);
				c.drawRect(black[0], paint);
				c.drawRect(black[1], paint);
			}
			surfaceHolder.unlockCanvasAndPost(c);
		}
	}
	
	private void setPaint(Paint p){
		p.setTextSize(40.0f);
	}
	
	public void intentToTitle(){
		activity.intentToTitle();
	}
	
	public void intentToGoal(){
		activity.intentToGoal();
	}
}
