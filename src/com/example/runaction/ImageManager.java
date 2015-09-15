package com.example.runaction;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.SparseArray;

// 画像管理クラス（全体の画像を一元管理する）
public class ImageManager {
	// singleton
	private static ImageManager instance;
	private ImageManager(){}
	public static ImageManager getInstance(){
		if(instance == null){
			instance = new ImageManager();
		}
		return instance;
	}
	
	// 画像を読み込むのに必要
	// 画像をロードする前に必ずセットしておく必要がある
	private Resources res;
	// Bitmap保持
	private SparseArray<Bitmap> bitmaps = new SparseArray<Bitmap>();
	
	public void setResources(Resources r){
		res = r;
	}
	
	// 画像をBitmapとして読み込み
	public void loadBitmap(int id){
		if(bitmaps.get(id, null) != null) return;
		bitmaps.append(id, BitmapFactory.decodeResource(res, id));
	}
	
	public void drawBitmap(Canvas c, Paint p, int id, Rect drawableRange, Rect drawRange){
		final Bitmap bitmap = bitmaps.get(id, null);
		if(bitmap == null){
			// Bitmapが見つからなかった場合
			c.drawRect(drawRange, p);
			return;
		}
		c.drawBitmap(bitmap, drawableRange, drawRange, null);
	}
}

class BackGroundImage{
	ImageManager manager;
	// 背景画像(奥のものから順に格納)
	private int[] imageID;
	// 各画像の描画位置
	private Rect[] rect;
	// offsetに係数をかけて画像の移動を制限
	private double[] offsetCoeff;
	
	public BackGroundImage(int[] id, Rect[] rect, double[] coeff){
		imageID = id;
		this.rect = rect;
		offsetCoeff = coeff;
		manager = ImageManager.getInstance();
	}
	
	public void draw(Canvas c, Paint p, int offsetX, int offsetY){
		for(int i=0; i<imageID.length; i++){
			final int offX = (int)(offsetX * offsetCoeff[i]);
			final int wid = rect[i].right - rect[i].left;
			final int drawX = (offX/wid)*wid - offX;
			final int drawNum = (- drawX + GameThread.WINDOW_WIDTH*2 - 1) / wid;
			for(int j=0; j<drawNum; j++){
				final int dx = drawX + j*wid;
				manager.drawBitmap(c, p, imageID[i]
						, new Rect(0, 0, wid, rect[i].bottom-rect[i].top)
						, new Rect(dx, rect[i].top, dx + wid, rect[i].bottom));
			}
		}
	}
}
