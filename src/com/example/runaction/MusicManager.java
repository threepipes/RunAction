package com.example.runaction;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.SparseIntArray;

public class MusicManager {
	private static MusicManager instance;
	private MusicManager(){
		seMap = new SparseIntArray();
		sePlayer = buildSoundPool(1);
	}
	
	private Context context;
	public void setContext(Context context){
		this.context = context.getApplicationContext();
	}
	
	public static MusicManager getInstance(){
		if(instance == null){
			instance = new MusicManager();
		}
		return instance;
	}
	
	private MediaPlayer bgmPlayer;
	private SoundPool sePlayer;
	private SparseIntArray seMap;
	
	// idを指定してBGMを開始
	// loopがtrueのときループ再生
	public void setBGM(int id, boolean loop){
		if(bgmPlayer != null && bgmPlayer.isPlaying()) bgmPlayer.stop();
		bgmPlayer = MediaPlayer.create(context, id);
		try {
			if(loop) bgmPlayer.setLooping(true);
//			bgmPlayer.setVolume(0.1f, 0.1f);
			bgmPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	public void setMusicState(boolean on){
		if(bgmPlayer == null) return;
		if(!on && bgmPlayer.isPlaying()){
			bgmPlayer.pause();
		}else if(on && !bgmPlayer.isPlaying()){
			bgmPlayer.start();
		}
	}
	
	// 呼び出すたびに音楽状態のオンオフ切り替え(不要？)
	public void switchMusicState(){
		if(bgmPlayer == null) return;
		if(bgmPlayer.isPlaying()){
			bgmPlayer.pause();
		}else{
			bgmPlayer.start();
		}
	}
	
	// ロードするSEのリソースIDを配列で指定
	public void loadSE(int[] seList){
		for(int i=0; i<seList.length; i++){
			int id = sePlayer.load(context, seList[i], 1);
			seMap.put(seList[i], id);
		}
	}
	
	// リソースIDで指定したSEを再生
	public void playSE(int id){
		if(sePlayer == null) return;
		sePlayer.play(seMap.get(id), 1.0f, 1.0f, 1, 0, 1.0f);
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
	
	// アプリ終了時に呼び出す
	public void destroy(){
		if(sePlayer != null){
			sePlayer.release();
		}
		if(bgmPlayer != null){
			bgmPlayer.stop();
			bgmPlayer.release();
		}
	}
	
}
