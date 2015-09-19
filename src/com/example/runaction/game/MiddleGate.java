package com.example.runaction.game;

import com.example.runaction.MusicManager;
import com.example.runaction.R;

public class MiddleGate extends Sprite{
	private double psx, psy;
	public MiddleGate(int x, int y, Map map) {
		super(x, y, map, null, R.drawable.gate);
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
	}
	
	@Override
	public void reset() {
	}
	
	@Override
	public void update() {
		
	}
}
