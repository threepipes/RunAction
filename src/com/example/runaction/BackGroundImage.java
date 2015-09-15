package com.example.runaction;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BackGroundImage{
	ImageManager manager;
	// 背景画像(奥のものから順に格納)
	private int[] imageID;
	// 各画像の描画位置
	private Rect[] rect;
	// offsetに係数をかけて画像の移動を制限
	private double[] offsetCoeff;
	// このBackGround自身を識別するためのID
	private int id;
	
	public BackGroundImage(int[] id, Rect[] rect, double[] coeff, int backgroundID){
		imageID = id;
		this.rect = rect;
		offsetCoeff = coeff;
		manager = ImageManager.getInstance();
		this.id = backgroundID;
	}
	
	public void draw(Canvas c, Paint p, int offsetX, int offsetY){
		for(int i=0; i<imageID.length; i++){
			final int offX = -(int)(offsetX * offsetCoeff[i]);
			final int wid = rect[i].right - rect[i].left;
			final int drawX = (offX/wid)*wid - offX;
			int drawNum = (- drawX + GameThread.WINDOW_WIDTH*2 - 1) / wid + (wid > GameThread.WINDOW_WIDTH ? 1 : 0);
			for(int j=0; j<drawNum; j++){
				final int dx = drawX + j*wid;
				manager.drawBitmap(c, p, imageID[i]
						, new Rect(0, 0, wid, rect[i].bottom-rect[i].top)
						, new Rect(dx, rect[i].top, dx + wid, rect[i].bottom));
			}
		}
	}
	
	// 背景Bitmapは大きい(場合が多い)ので、Load場所を細かく設定する必要がある
	public void load(){
		for(int i=0; i<imageID.length; i++){
			manager.loadBitmap(imageID[i]);
		}
	}
	
	public void destroy(){
		for(int i=0; i<imageID.length; i++){
			manager.release(imageID[i]);
		}
	}
	
	public int getID(){
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof BackGroundImage)){
			return false;
		}
		return ((BackGroundImage)o).getID() == id;
	}
}

