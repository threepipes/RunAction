package com.example.runaction.game;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class Mode {
	static int Width, Height;
	public abstract void update();
	public abstract void draw(Canvas c, Paint p);
	public abstract void touchEvent(int x, int y, int event);
	public void setWindowSize(int w, int h){
		Width = w;
		Height = h;
	}
}
