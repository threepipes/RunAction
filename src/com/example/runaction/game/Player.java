package com.example.runaction.game;

import com.example.runaction.R;
import com.example.runaction.R.raw;

import android.content.Context;

public class Player extends AbstractGameObject{
	private float vx, vy;
	
	private boolean onTouch;
//	private static final int ax = 1;
	private static final float ay = 0.5f;
	private static final int MAX_VY = 15;
	public Player(Context context, ModeAction parent, int width, int height) {
		super(context, parent, -1, width, height);
		onTouch = false;
		vx = 0;
		vy = 0;
	}
	
	public void setTouch(boolean touch){
		onTouch = touch;
	}
	
	@Override
	public void update() {
		vy += ay*(onTouch ? -1 : 1);
		if(vy > MAX_VY) vy = MAX_VY;
		else if(vy < -MAX_VY) vy = -MAX_VY;
		
		y += vy;
		x += vx;
		if(y > Mode.height - height){
			y = Mode.height - height;
			if(vy > MAX_VY/2) parent.playSE(R.raw.landing);
			vy = 0;
		}else if(y < 0){
			y = 0;
			if(vy < -MAX_VY/2) parent.playSE(R.raw.landing);
			vy = 0;
		}
	}
}
