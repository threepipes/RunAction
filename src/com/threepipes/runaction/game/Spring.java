package com.threepipes.runaction.game;

import com.threepipes.runaction.R;

public class Spring extends Sprite {

	private final static int[][][] anim = {
			{{0, 0, 1},{1, 0, 3},{2, 0, 4},{3, 0, Animation.FRAME_LOOP}}
	};
    public Spring(double x, double y, Map map) {
    	super(x,y,map,new Animation(anim),R.drawable.jump);
    	animation.stopAnimation();
	}
    
    public void setAnimation(){
    	animation.restartAnimation();
    }
    
    public void reset(){
    	animation.init();
    	animation.stopAnimation();
    }
    
    @Override
    public boolean hitPlayer(Player player, Map map) {
        setAnimation();
        player.jump2();
        return true;
    }

	public void update() {
		animation.update();
    }
}
