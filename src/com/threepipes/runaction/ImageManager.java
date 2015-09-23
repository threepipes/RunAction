package com.threepipes.runaction;

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
	// 背景画像はサイズが大きいので別で管理
	private BackGroundImage bgImage;
	
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
	
	// ---------- 背景を扱う関数群 ----------
	public void setBackGround(BackGroundImage bg){
		if(bgImage != null && bg.equals(bgImage)) return;
		// 新しいのが読み込まれるたびに古いものは解放する
		if(bgImage != null) bgImage.destroy();
		bgImage = bg;
		bgImage.load();
	}
	
	public void drawBackground(Canvas c, Paint p, int offsetX, int offsetY){
		if(bgImage == null) return;
		bgImage.draw(c, p, offsetX, offsetY);
	}
	
	public void release(int id){
		final Bitmap bitmap = bitmaps.get(id, null);
		if(bitmap == null){
			// Bitmapが見つからなかった場合
			return;
		}
		bitmap.recycle();
		bitmaps.delete(id);
	}
	// ---------- ここまで背景関係 ----------
}
