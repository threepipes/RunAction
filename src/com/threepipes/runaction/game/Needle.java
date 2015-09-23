package com.threepipes.runaction.game;

import com.threepipes.runaction.R;

/*
 * Created on 2005/06/24
 *
 */

/**
 * @author mori
 *
 */
public class Needle extends Sprite {

    public Needle(double x, double y, Map map) {
    	super(x,y,map,null, R.drawable.toge);
	}
    
    @Override
    public boolean hitPlayer(Player player, Map map) {
    	map.exitRequest();
    	return true;
    }
    
    public void reset(){
    	
    }
    
	public void update() {
    }
}