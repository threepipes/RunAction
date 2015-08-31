package com.example.runaction;

import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class MainGameView extends SurfaceView
	implements SurfaceHolder.Callback{
	GameThread gameThread;
	
	public MainGameView(Context context){
		super(context);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		gameThread = new GameThread(holder, context, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		});
		
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
	
	class GameThread extends Thread{
		SurfaceHolder surfaceHolder;
		Mode mode;
		MotionEvent event;
		SoundPool sePlayer;
		MediaPlayer bgmPlayer;
		
		public int width;
		public int height;
		
		boolean shouldContinue = true;
		public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler){
			this.surfaceHolder = surfaceHolder;
			mode = new ModeAction(context, this);
			event = null;
			
			setBGM(context);
			loadMusic(context);
		}
		
		void setBGM(Context context){
			bgmPlayer = MediaPlayer.create(context, R.raw.chiptune);
			try {
				bgmPlayer.setLooping(true);
//				bgmPlayer.setVolume(0.1f, 0.1f);
				bgmPlayer.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		
		SparseIntArray seMap;
		private void loadMusic(Context context){
			seMap = new SparseIntArray();
			sePlayer = buildSoundPool(1);
			
			int tap = sePlayer.load(context, R.raw.landing, 1);
			seMap.put(R.raw.landing, tap);
		}
		
		public void playSE(int id){
			sePlayer.play(seMap.get(id), 1.0f, 1.0f, 1, 0, 1.0f);
//			mp.start();
			Log.d("Sound", "PlaySE");
		}
		
		@SuppressWarnings("deprecation")
		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		private SoundPool buildSoundPool(int poolMax)
		{
		    SoundPool pool = null;

		    if (true || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
		        pool = new SoundPool(poolMax, AudioManager.STREAM_MUSIC, 0);
		    }
		    else {
		        AudioAttributes attr = new AudioAttributes.Builder()
		            .setUsage(AudioAttributes.USAGE_MEDIA)
		            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
		            .build();

		        pool = new SoundPool.Builder()
		            .setAudioAttributes(attr)
		            .setMaxStreams(poolMax)
		            .build();
		        
		    }
		    
		    return pool;
		}

		
		public void setEvent(MotionEvent e){
			this.event = e;
		}
		
		public void destroy(){
			shouldContinue = false;
			sePlayer.release();
			bgmPlayer.stop();
			bgmPlayer.release();
		}
		
		public void setWindowSize(int width, int height){
			this.width = width;
			this.height = height;
			mode.setWindowSize(width, height);
		}
		
		@Override
		public void run() {
			while(shouldContinue){
				Canvas c = surfaceHolder.lockCanvas();
				if(event != null){
					mode.touchEvent(event);
					event = null;
				}
				mode.update();
				if(c == null) break;
				mode.draw(c);
				surfaceHolder.unlockCanvasAndPost(c);
			}
		}
		
		
	}
}
