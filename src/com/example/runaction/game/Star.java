package com.example.runaction.game;

public class Star extends Sprite{
	private int id;
	public Star(int x, int y, Map map, int id) {
		super(x, y, map, null, 0);
	}
	
	@Override
	public boolean hitPlayer(Player player, Map map) {
		map.getStar(id);
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
