package com.example.runaction.game;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class SubMode extends Mode{
	protected ButtonManager buttonManager;
	public SubMode(ButtonManager bm) {
		buttonManager = bm;
	}
	
	@Override
	public void draw(Canvas c, Paint p) {
		buttonManager.draw(c, p);
	}
	
	@Override
	public void touchEvent(int x, int y, int event) {
		final int buttonKey = buttonManager.touchEvent(x, y, event);
	}
}
