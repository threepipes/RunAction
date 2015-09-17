package com.example.runaction.game;

import com.example.runaction.R;

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
    	// TODO 自動生成されたメソッド・スタブ
        setAnimation();
        player.jump2();
        return true;
    }

	public void update() {
		animation.update();
    }
}
