package com.example.runaction;

import com.example.runaction.game.Mode;
import com.example.runaction.game.ModeAction;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
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
	MotionEvent event;
	SoundPool sePlayer;
	MediaPlayer bgmPlayer;
	
	public static final int width = 540;
	public static final int height = 960;
	public float scale;
	public float translateX;
	public float translateY;
	
	boolean shouldContinue = true;
	public GameThread(SurfaceHolder surfaceHolder, int setting, Context context, Handler handler){
		this.surfaceHolder = surfaceHolder;
		mode = new ModeAction(context, this);
		event = null;
		
		if((setting & TitleActivity.SET_VOLUME) == 0){
			setBGM(context);
			loadMusic(context);
		}
	}
	
	void setBGM(Context context){
		bgmPlayer = MediaPlayer.create(context, R.raw.chiptune);
		try {
			bgmPlayer.setLooping(true);
//			bgmPlayer.setVolume(0.1f, 0.1f);
			bgmPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	void switchBGM(){
		if(bgmPlayer == null) return;
		if(bgmPlayer.isPlaying()){
			bgmPlayer.pause();
		}else{
			bgmPlayer.start();
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
		if(sePlayer == null) return;
		sePlayer.play(seMap.get(id), 1.0f, 1.0f, 1, 0, 1.0f);
//		mp.start();
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
		if(bgmPlayer != null){
			sePlayer.release();
			bgmPlayer.stop();
			bgmPlayer.release();
		}
	}
	
	public void setWindowSize(int w, int h){
		float scaleX = (float)w / width;
		float scaleY = (float)h /  height;
		scale = scaleX > scaleY ? scaleY : scaleX;
		translateX = (w - width*scale)/2;
		translateY = (h - height*scale)/2;
		Log.d("Window", "scale: "+scale+" , tX: "+translateX+" , tY: "+translateY);
		mode.setWindowSize(width, height);
	}
	
	@Override
	public void run() {
		while(shouldContinue){
			Canvas c = surfaceHolder.lockCanvas();
			if(c == null) break;
			c.translate(translateX, translateY); // 逕ｻ髱｢縺ｮ荳ｭ螟ｮ縺ｫ縺ｪ繧九ｈ縺�縺ｫ遘ｻ蜍輔＆縺帙ｋ
			c.scale(scale, scale); // 遶ｯ譛ｫ縺ｮ逕ｻ髱｢縺ｫ蜷医ｏ縺帙※諡｡螟ｧ繝ｻ邵ｮ蟆上☆繧�
			
			if(event != null){
				mode.touchEvent(event);
				event = null;
			}
			mode.update();
			mode.draw(c);
			surfaceHolder.unlockCanvasAndPost(c);
		}
	}
	
	
}
