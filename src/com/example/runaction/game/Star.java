package com.example.runaction.game;

public class Star extends Sprite{
	public Star(int x, int y, Map map) {
		super(x, y, map, null, 0);
	}
	
	@Override
	public boolean hitPlayer(Player player, Map map) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
	
	public void setExistOff(){
		death = true;
	}
	
	public void reset() {
		
	}
	
	@Override
	public void update() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
