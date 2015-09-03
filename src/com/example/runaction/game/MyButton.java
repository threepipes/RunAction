package com.example.runaction.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class MyButton {
	private Rect rect;
	private boolean isPushed;
	private boolean isVisualize;
	private ButtonAction action;
	
	String str;
	
	public MyButton(Rect rect, String str, ButtonAction action) {
		this.rect = rect;
		this.str = str;
		isPushed = false;
		isVisualize = true;
	}
	
	public void draw(Canvas c, Paint p){
		if(!isVisualize) return;
		p.setColor(0xFFAABBCC);
		c.drawRect(rect, p);
		p.setColor(0xFF110F00);
		c.drawText(str, rect.left, rect.top, p);
	}
	
	public boolean touchEvent(int x, int y, int event){
		if(!isVisualize || !rect.contains(x, y)) return false;
		if(isPushed && (event | GameMode.KEY_RELEASED) > 0){
			isPushed = false;
			action.onClickAction();
			return true;
		}
		if((event | GameMode.KEY_PRESSED) > 0) isPushed = true;
		return false;
	}
	
	public void setVisualize(boolean vis){
		isVisualize = vis;
	}
}

interface ButtonAction {
	public void onClickAction();
}
