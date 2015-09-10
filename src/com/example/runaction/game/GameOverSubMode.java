package com.example.runaction.game;

import com.example.runaction.GameThread;

import android.graphics.Canvas;
import android.graphics.Paint;

public class GameOverSubMode extends SubMode{
	// 背景の暗さを調整するため
	private int alpha;
	// ブラックアウトにかける時間(Frame)
	private static final int BLACKOUT_TIME = 8;
	private static final int ADD_ALPHA = 0xFF / BLACKOUT_TIME;
	// 完全にブラックアウトした後画像表示するためのフラグ
	private boolean lightUp;
	
	public GameOverSubMode(ButtonManager bm) {
		super(bm);
	}
	
	public void init(){
		alpha = 0;
		lightUp = false;
	}
	
	@Override
	public void update() {
		// アニメーション値の調整
		// 完全にブラックアウトしたら、電気をつけるみたいに画像とボタンを表示
		if(alpha < 0xFF){
			alpha += ADD_ALPHA;
			if(alpha > 0xFF){
				alpha = 0xFF;
				lightUp = true;
			}
		}
	}
	
	@Override
	public void draw(Canvas c, Paint p) {
		p.setColor(alpha << 6*4);
		c.drawRect(GameThread.WindowRect, p);
		if(!lightUp) return;
		// 背景画像表示
		
		// ボタン表示
		buttonManager.draw(c, p);
	}
	
	@Override
	public void touchEvent(int x, int y, int event) {
		// ブラックアウト中はタッチ無効
		if(lightUp) super.touchEvent(x, y, event);
	}
}
