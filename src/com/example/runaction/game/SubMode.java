package com.example.runaction.game;

import com.example.runaction.GameThread;

import android.graphics.Canvas;
import android.graphics.Paint;

public class SubMode extends Mode{
	protected ButtonManager buttonManager;
	protected int backgroundAlpha;
	public SubMode(ButtonManager bm) {
		buttonManager = bm;
		backgroundAlpha = 0x90;
	}
	
	public SubMode(ButtonManager bm, int alpha) {
		buttonManager = bm;
		backgroundAlpha = alpha;
	}
	
	public void init(){
		// SubModeの呼び出し時に最初に呼ばれる
	}
	
	@Override
	public void draw(Canvas c, Paint p) {
		p.setColor(backgroundAlpha << 6*4);
		c.drawRect(GameThread.WindowRect, p);
		// 文字(仮)の色設定
		p.setColor(0xFF000000);
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
