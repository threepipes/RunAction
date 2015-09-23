package com.threepipes.runaction.game;

public class StarManager {
	Star[] stars;
	
	public StarManager(int get, Star[] stars){
		this.stars = stars;
		for(int i=0; i<stars.length; i++){
			if((get&1<<i) == 0) stars[i].setExistOff();
		}
	}
	
	public int getState(){
		int state = 0;
		for(int i=0; i<stars.length; i++){
			if(!stars[i].isdeath()) state |= 1<<i;
		}
		return state;
	}
}
