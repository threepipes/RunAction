package com.threepipes.runaction.game;

import com.threepipes.runaction.ImageManager;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;

public class MyButton {
	private Rect rect;
	private boolean isPushed;
	private boolean isVisualize;
	private ButtonAction action;
	private int imageID;
	private int imageID_pushed;
	
	private String str;
	
	public MyButton(Rect rect, int imageID, int pushedID, String str, ButtonAction action) {
		this.rect = rect;
		this.str = str;
		isPushed = false;
		isVisualize = true;
		this.action = action;
		this.imageID = imageID;
		this.imageID_pushed = pushedID;
		centerX = (rect.left + rect.right) / 2;
		centerY = (rect.top + rect.bottom) / 2;
		if(imageID < 0) return;
		final int width = rect.right - rect.left;
		final int height = rect.bottom - rect.top;
		range = new Rect(0, 0, width, height);
	}
	
	private Rect range;
	private int centerX;
	private int centerY;
	public void draw(Canvas c, Paint p){
		if(!isVisualize) return;
		if(imageID < 0){
			if(!isPushed) p.setColor(0xFFAABBCC);
			else p.setColor(0xFF778899);
			c.drawRect(rect, p);
			p.setColor(0xFF110F00);
		}else{
			ImageManager.getInstance().drawBitmap(c, p
					, isPushed ? imageID_pushed : imageID, range, rect);
		}
		if(str != null) drawText(c, p, str, centerX, centerY);
	}
	
	private void drawText(Canvas c, Paint p, String text, int centerX, int centerY){
		final int dx = (int)(centerX - p.measureText(text)/2);
		final FontMetrics met = p.getFontMetrics();
		final int dy = (int)(centerY - (met.bottom - met.top)/2 - met.top);
		c.drawText(text, dx, dy, p);
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
