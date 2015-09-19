package com.example.runaction.game;

public class MiddleGate extends Sprite{
	private double psx, psy;
	public MiddleGate(int x, int y, Map map) {
		super(x, y, map, null, 0);
		psy = y*Map.TILE_SIZE;
	}
	
	public boolean hitPlayer(Player player, Map map) {
		death = true;
		psx = player.getX();
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
