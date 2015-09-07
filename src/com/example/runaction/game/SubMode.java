package com.example.runaction.game;

import com.example.runaction.GameThread;

import android.graphics.Canvas;
import android.graphics.Paint;

public class SubMode extends Mode{
	protected ButtonManager buttonManager;
	public SubMode(ButtonManager bm) {
		buttonManager = bm;
	}
	
	public void init(){
		// SubModeの呼び出し時に最初に呼ばれる
	}
	
	@Override
	public void draw(Canvas c, Paint p) {
		p.setColor(0x90000000);
		c.drawRect(GameThread.WindowRect, p);
		buttonManager.draw(c, p);
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void touchEvent(int x, int y, int event) {
		final int buttonKey = buttonManager.touchEvent(x, y, event);
	}
}
