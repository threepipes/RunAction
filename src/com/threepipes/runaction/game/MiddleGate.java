package com.threepipes.runaction.game;

import com.threepipes.runaction.MusicManager;
import com.threepipes.runaction.R;

public class MiddleGate extends Sprite{
	private double psx, psy;
	private boolean used;
	public MiddleGate(int x, int y, Map map) {
		super(x, y, map, null, R.drawable.gate);
		used = false;
		psy = y;
	}
	
	public boolean hitPlayer(Player player, Map map) {
		if(death) return false;
		death = true;
		psx = player.getX();
		MusicManager.getInstance().playSE(R.raw.gate);
		return false;
	}
	
	public void setPlayerState(Player player){
		if(!death) return;
		player.setPoint(psx, psy);
		used = true;
	}
	
	public boolean isUsed(){
		return used;
	}
	
	@Override
	public void reset() {
	}
	
	@Override
	public void update() {
		
	}
}
