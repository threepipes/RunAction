package com.example.runaction.game;

import android.graphics.Canvas;
import android.graphics.Paint;

// オープニングやクリア画面、ゲームオーバー画面など、自動で動く(かつ移動を伴う)アニメーションのマクロ的なもの
public class AutoAnimation {
	
	private Animation animation;
	private int[][] macro = {
			{},
	};
	private final static int KEY_FLAME = 0;
	private final static int KEY_ACTION = 1;
	private final static int KEY_VALUE = 2;
	private int actionCount;
	
	private final static int SIZE_X = 32;
	private final static int SIZE_Y = 32;
	// アニメーションキャラクタに働く物理法則
	// 床の位置
	private int floor;
	// 重力加速度
	private int grav;
	// その他位置関係値
	private int vx, vy;
	private int x, y;
	
	// アニメーション実行中フラグ
	private boolean onAnimation;
	
	public AutoAnimation() {
		// 必要引数: animation, 画像ID, 疑似キー入力(というか動きのマクロ), (x, y)座標
		init();
	}
	
	private void init(){
		actionCount = 0;
		onAnimation = false;
	}
	
	private final static int MAX_SPEED = 10;
	public void update(){
		if(!onAnimation) return;
		actionUpdate();
		moveUpdate();
	}
	
	private void moveUpdate(){
		vy += grav;
		if(vy > MAX_SPEED){
			vy = MAX_SPEED;
		}
		
		x += vx;
		y += vy;
		
		if(y > floor){
			y = floor;
			vy = 0;
		}
	}
	
	private void actionUpdate(){
		
	}
	
	public void draw(Canvas c, Paint p){
		
		
	}
	
	private final static int WALK = 1;
	private final static int JUMP = 2;
	private final static int SET_X = 4;
	private final static int SET_Y = 8;
	
	private void setX(int sx){
		x = sx;
	}
	
	private void setY(int sy){
		y = sy;
	}
	
	private void jump(int jumpPower){
		this.vy = -jumpPower;
	}
	
	private void walk(int vx){
		this.vx = vx;
	}
	
	private void setFloor(int f){
		floor = f;
	}
}
