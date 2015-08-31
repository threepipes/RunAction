package com.example.runaction;

import com.example.runaction.MainGameView.GameThread;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

public class ModeAction extends Mode{
	
	Player player;
	static int width;
	static int height;
	private GameThread parent;
	
	static final int pSize = 32;
	public ModeAction(Context context, GameThread parent) {
		init(context);
		this.parent = parent;
	}
	
	public void init(Context context){
		player = new Player(context, this, pSize, pSize);
		player.setXY(100, 100);
	}
	
	@Override
	public void update() {
		player.update();
	}
	
	@Override
	public void draw(Canvas c) {
		c.drawColor(0xFF99AAFF);
//		c.drawARGB(255, 0, 100, 100);
		player.draw(c);
		
	}
	
	public void playSE(int id){
		parent.playSE(id);
	}
	
	private final static int DOWN = 2;
	private final static int UP = 1;
	
	@Override
	public void touchEvent(MotionEvent event) {
		final int act = event.getAction();
		Log.d("Mes", act+"");
		if((act & DOWN) > 0){
			player.setTouch(true);
		}
		if((act & UP) > 0){
			player.setTouch(false);
		}
	}
}
