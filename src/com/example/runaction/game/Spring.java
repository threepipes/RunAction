package com.example.runaction.game;

import com.example.runaction.R;

public class Spring extends Sprite {

	private final static int[][][] anim = {
			{{0, 0, 3},{1, 0, 3},{2, 0, 3},{3, 0, Animation.FRAME_LOOP}}
	};
    public Spring(double x, double y, Map map) {
    	super(x,y,map,new Animation(anim),R.drawable.jump);
    	animation.stopAnimation();
	}
    
    public void setAnimation(){
    	animation.restartAnimation();
    }

	public void update() {
		animation.update();
    }
}
