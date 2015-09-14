package com.example.runaction.game;

import com.example.runaction.GameThread;
import com.example.runaction.ImageManager;
import com.example.runaction.MusicManager;
import com.example.runaction.R;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameOverSubMode extends SubMode{
	// 背景の暗さを調整するため
	private int alpha;
	// ブラックアウトにかける時間(Frame)
	private static final int BLACKOUT_TIME = 30;
	private static final int ADD_ALPHA = 0xFF / BLACKOUT_TIME;
	// 完全にブラックアウトした後画像表示するためのフラグ
	private boolean lightUp;
	
	private MusicManager mManager;
	
	public GameOverSubMode(ButtonManager bm) {
		super(bm);
		mManager = MusicManager.getInstance();
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
				mManager.setBGM(R.raw.gameover, true, true);
			}
		}
	}
	private final static int IMAGE_WIDTH = 300;
	private final static int IMAGE_HEIGHT = 300;
	private final static int DRAW_X = GameThread.WINDOW_WIDTH/2 - IMAGE_WIDTH/2;
	private final static int DRAW_Y = 200;
	private Rect bgRect = new Rect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
	private Rect drawRect = new Rect(DRAW_X, DRAW_Y, DRAW_X+IMAGE_WIDTH, DRAW_Y+IMAGE_HEIGHT);
	@Override
	public void draw(Canvas c, Paint p) {
		p.setColor(alpha << 6*4);
		c.drawRect(GameThread.WindowRect, p);
		if(!lightUp) return;
		// 背景画像表示
		ImageManager.getInstance().drawBitmap(c, p, R.drawable.gameover, bgRect, drawRect);
		// ボタン表示
		buttonManager.draw(c, p);
	}
	
	@Override
	public void touchEvent(int x, int y, int event) {
		// ブラックアウト中はタッチ無効
		if(lightUp) super.touchEvent(x, y, event);
	}
}
