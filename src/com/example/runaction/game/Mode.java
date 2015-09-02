package com.example.runaction.game;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class Mode {
	static int Width, Height;
	public abstract void update();
	public abstract void draw(Canvas c);
	public abstract void touchEvent(int event);
	public void setWindowSize(int w, int h){
		Width = w;
		Height = h;
	}
}
