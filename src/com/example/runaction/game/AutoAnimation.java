package com.example.runaction.game;

import com.example.runaction.GameThread;
import com.example.runaction.ImageManager;
import com.example.runaction.R;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

// オープニングやクリア画面、ゲームオーバー画面など、自動で動く(かつ移動を伴う)アニメーションのマクロ的なもの
public class AutoAnimation {
	
	private Animation animation;
	private int[][] macro;
	// macro の第二添え字の役割
	// アクション実行開始フレーム
	private final static int KEY_FLAME = 0;
	// アクションの種類
	private final static int KEY_ACTION = 1;
	// (速度、ジャンプ力などの)値
	private final static int KEY_VALUE = 2;
	// アニメーションの値(animation に対応)
	private final static int KEY_ANIMATION = 3;
	
	private int actionCount;
	private int flameCount;
	
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
	
	// 画像ID
	private int imageID;
	// アニメーション実行中フラグ
	private boolean onAnimation;
	
    // ---------- animationの仮データ ----------
    // 以下の通りセル座標で指定する
    private final static int[][][] animationData = {
    		{{0, 1, Animation.FRAME_LOOP}},// ジャンプ
    		{{1, 1, Animation.FRAME_LOOP}},// 右を向く
    		{{0, 0, 5},{1, 0, 5},{2, 0, 5},{3, 0, 5},{Animation.FLAG_LOOP, 0}},// 走る
    };
    private final static int ANIM_JUMP = 0;
    private final static int ANIM_TO_RIGHT = 1;
    private final static int ANIM_RUN = 2;
    // ---------- 仮データここまで ----------
	
	public AutoAnimation() {
		// 必要引数: animation, 画像ID, 疑似キー入力(というか動きのマクロ), (x, y)座標
		int[][] macro = {
				{0, JUMP, 3, ANIM_JUMP},
				{30, NO_ACTION, 0, ANIM_TO_RIGHT},
				{50, WALK, 3, ANIM_RUN},
		};
		imageID = R.drawable.player;
		floor = GameThread.height - SIZE_Y * 1; // 一時処置(Mapが決定し次第変更)
		x = GameThread.width/2 - SIZE_X;
		y = GameThread.height - SIZE_Y * 2;
		// ----------- ここまで引数(予定)のデータ ----------
		
		grav = (int) Map.GRAVITY;
		
		this.macro = macro;
		init();
	}
	
	private void init(){
		actionCount = 0;
		flameCount = 0;
		onAnimation = false;
	}
	
	public boolean inAnimation(){
		return onAnimation;
	}
	
	public void startAnimation(){
		onAnimation = true;
	}
	
	public void update(){
		if(!onAnimation) return;
		actionUpdate();
		flameCount++;
		animation.update();
		moveUpdate();
	}
	
	private final static int MAX_SPEED = 10;
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
		if(actionCount >= macro.length){
			onAnimation = false;
			return;
		}
		while(flameCount == macro[actionCount][KEY_FLAME]){
			final int[] act = macro[actionCount];
			switch(act[KEY_ACTION]){
			case WALK:
				walk(act[KEY_VALUE]);
				break;
			case JUMP:
				jump(act[KEY_VALUE]);
				break;
			case SET_X:
				setX(act[KEY_VALUE]);
				break;
			case SET_Y:
				setY(act[KEY_FLAME]);
				break;
			}
			animation.setAnim(act[KEY_ANIMATION]);
			actionCount++;
			if(actionCount >= macro.length){
				onAnimation = false;
				return;
			}
		}
	}
	
	public void draw(Canvas c, Paint p){
		// offsetは考慮すべき？
		ImageManager.getInstance().drawBitmap(c, p, imageID
				, animation.getRect(), new Rect(x, y, x+SIZE_X, y+SIZE_Y));
	}
	
	private final static int NO_ACTION = 0;
	private final static int WALK = 1;
	private final static int JUMP = 2;
	private final static int SET_X = 3;
	private final static int SET_Y = 4;
	
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
