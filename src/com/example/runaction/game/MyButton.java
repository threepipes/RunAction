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
		this.action = action;
	}
	
	public void draw(Canvas c, Paint p){
		if(!isVisualize) return;
		if(!isPushed) p.setColor(0xFFAABBCC);
		else p.setColor(0xFF778899);
		c.drawRect(rect, p);
		p.setColor(0xFF110F00);
		c.drawText(str, rect.left, rect.bottom, p);
	}
	
	public boolean touchEvent(int x, int y, int event){
		if(!isVisualize || !rect.contains(x, y)){
			isPushed = false;
			return false;
		}

		if((event | GameMode.KEY_PRESSED) > 0){
			isPushed = true;
//			return true;
		}
		if(isPushed && (event & GameMode.KEY_RELEASED) > 0){
			isPushed = false;
			action.onClickAction();
			return true;
		}
		return false;
	}
	
	public boolean collision(int x, int y){
		return rect.contains(x, y);
	}
	
	public void setVisualize(boolean vis){
		isVisualize = vis;
	}
}

interface ButtonAction {
	public void onClickAction();
}
