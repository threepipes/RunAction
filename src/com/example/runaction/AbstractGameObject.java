package com.example.runaction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public abstract class AbstractGameObject {
	protected Drawable drawableImg;
	protected int width, height;
	protected int x, y;
	public AbstractGameObject(Context context, int resourceID, int width, int height) {
		if(resourceID > 0) drawableImg = context.getResources().getDrawable(resourceID);
		this.width = width;
		this.height = height;
	}
	
	public void draw(Canvas c){
		if(drawableImg != null){
			drawableImg.setBounds(x, y, x+width, y+height);
			drawableImg.draw(c);
		}else{
			Paint paint = new Paint();
			paint.setColor(0xFFFFFFFF);
			c.drawRect(new Rect(x, y, x+width, y+height), paint);
		}
	}
	
	public void setXY(int x, int y){
		this.x = x;
		this.y = y;
	}
	
}
