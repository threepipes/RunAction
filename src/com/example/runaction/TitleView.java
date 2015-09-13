package com.example.runaction;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TitleView  extends SurfaceView
implements SurfaceHolder.Callback{
	TitleThread titleThread;

	public TitleView(MainActivity context){
		super(context);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// プレイヤー画像の設定(ここで呼び出すべきかどうか TODO )
		setGameImage();
		titleThread = new TitleThread(holder, context, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		});
	}

	public void setGameImage(){
		ImageManager manager = ImageManager.getInstance();
		manager.loadBitmap(R.drawable.player);
	}

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public final static int EVENT_START = 1;
	public void setEvent(int event){
		titleThread.setEvent(event);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		titleThread.setWindowSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		titleThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		titleThread.destroy();
		titleThread = null;
	}

	class TitleThread extends Thread{
		SurfaceHolder surfaceHolder;
		
		public static final int width = 540;
		public static final int height = 960;
		public final Rect WindowRect = new Rect(0, 0, width, height);
		public float scale;
		public float translateX;
		public float translateY;
		
		public TitleThread(SurfaceHolder surfaceHolder, MainActivity context, Handler handler){
			this.surfaceHolder = surfaceHolder;
			
		}
		
		public void setEvent(int event){
			
		}
		
		public void setWindowSize(int w, int h){
			float scaleX = (float)w / width;
			float scaleY = (float)h /  height;
			scale = scaleX > scaleY ? scaleY : scaleX;
			translateX = (w - width*scale)/2;
			translateY = (h - height*scale)/2;
		}
		
		public void destroy(){
			
		}
		
		@Override
		public void run() {
			while(true){
				
			}
		}
	}
}
