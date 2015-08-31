package com.example.runaction;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class Mode {
	static int width, height;
	public abstract void update();
	public abstract void draw(Canvas c);
	public abstract void touchEvent(MotionEvent event);
	public void setWindowSize(int w, int h){
		width = w;
		height = h;
	}
}
