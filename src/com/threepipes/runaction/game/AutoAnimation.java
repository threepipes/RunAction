package com.threepipes.runaction.game;

import com.threepipes.runaction.GameThread;
import com.threepipes.runaction.ImageManager;
import com.threepipes.runaction.MusicManager;
import com.threepipes.runaction.R;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

// オープニングやクリア画面、ゲームオーバー画面など、自動で動く(かつ移動を伴う)アニメーションのマクロ的なもの
public class AutoAnimation {
	
	private Animation animation;
	private int[][] macro;
	// macro の第二添え字の役割
	// アクション実行開始フレーム
	private final static int KEY_FRAME = 0;
	// アクションの種類
	private final static int KEY_ACTION = 1;
	// (速度、ジャンプ力などの)値
	private final static int KEY_VALUE = 2;
	// アニメーションの値(animation に対応)
	private final static int KEY_ANIMATION = 3;
	
	private int actionCount;
	private int frameCount;
	
	private int actionFlag;
	public final static int FLAG_GROUND = -1;
	public final static int FLAG_OUTOFWINDOW = -2;
	// 外部からの直接進行命令
	public final static int FLAG_EXE_COMMAND = -4;
	public final static int FLAG_ENDOFBLACKOUT = -8;
	
	private final static int SIZE_X = 32;
	private final static int SIZE_Y = 32;
	// アニメーションキャラクタに働く物理法則
	// 床の位置
	private int floor;
	// 重力加速度
	private double grav;
	// その他位置関係値
	private double vx, vy;
	private double x, y;
	// 画面内にいるかどうか
	private boolean onWindow;
	
	// その他のアニメーション
	// ブラックアウト
	private int backgroundColor;
	private int backgroundAlpha;
	private int blackoutPace;
	
	// (重力などの影響を受けず)静止状態かどうか
	private boolean stop;
	
	// 画像ID
	private int imageID;
	// アニメーション実行中フラグ
	private boolean onAnimation;
	
	public AutoAnimation(int[][] macro, Animation anim, int imageID) {
		this.imageID = imageID;
		this.macro = macro;
		animation = anim;
		floor = GameThread.WINDOW_HEIGHT - SIZE_Y * 1; // 一時処置(Mapが決定し次第変更)
		grav = Map.GRAVITY;
		
		init();
	}
	
	private void init(){
		actionCount = 0;
		frameCount = 0;
		actionFlag = 0;
		onWindow = true;
		onAnimation = false;
	}
	
	// アニメーションが継続しているかどうか
	public boolean inAnimation(){
		return onAnimation;
	}
	
	public boolean inWindow(){
		return onWindow;
	}
	
	// アニメーションを開始するときに外部から呼び出す
	// 呼び出しておかないとUpdateされない
	public void startAnimation(int x, int y){
		init();
		onAnimation = true;
		this.x = x;
		this.y = y;
	}
	
	public void update(){
		if(!onAnimation) return;
		actionUpdate();
		frameCount++;
		animation.update();
		moveUpdate();
	}
	
	private final static int MAX_SPEED = 20;
	private void moveUpdate(){
		if(stop) return;
		vy += grav;
		if(vy > MAX_SPEED){
			vy = MAX_SPEED;
		}
		final boolean onGround = y >= floor; 
		x += vx;
		y += vy;
		
		if(y > floor){
			y = floor;
			vy = 0;
			if(!onGround) actionFlag |= -FLAG_GROUND;
		}
		if(checkOutOfWindow()){
			onWindow = false;
			actionFlag |= -FLAG_OUTOFWINDOW;
		}
	}
	
	public void contact(){
		actionFlag |= -FLAG_EXE_COMMAND;
	}
	
	private boolean checkOutOfWindow(){
		return y < -SIZE_Y || y > GameThread.WINDOW_HEIGHT || x < -SIZE_X || x > GameThread.WINDOW_WIDTH;
	}
	
	private void actionUpdate(){
		if(actionCount >= macro.length){
			onAnimation = false;
			return;
		}
		while(frameCount == macro[actionCount][KEY_FRAME]
				|| checkActionFlag(macro[actionCount][KEY_FRAME])){
			// フラグによる進行の場合、この時点でframeが不定なのでリセットされる
			if(actionFlag != 0) frameCount = 0;
			stop = false;
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
				setY(act[KEY_VALUE]);
				break;
			case BLACKOUT:
				blackoutPace = act[KEY_VALUE];
				break;
			case STOP:
				stop = true;
				break;
			case PLAY_SE:
				playSE(act[KEY_VALUE]);
				break;
			}
			if(act[KEY_ACTION] != PLAY_SE) animation.setAnim(act[KEY_ANIMATION]);
			actionCount++;
			if(actionCount >= macro.length){
				onAnimation = false;
				return;
			}
		}
		actionFlag = 0;
	}
	
	private boolean checkActionFlag(int nextFlag){
		return nextFlag < 0 && (actionFlag & -nextFlag) > 0;
	}
	
	public void draw(Canvas c, Paint p){
		backgroundAlpha += blackoutPace;
		if(backgroundAlpha > 0){
			if(backgroundAlpha > 0xFF){
				backgroundAlpha = 0xFF;
				actionFlag |= -FLAG_ENDOFBLACKOUT;
			}
			p.setColor(backgroundAlpha << 6*4 | backgroundColor);
			c.drawRect(GameThread.WindowRect, p);
		}
		// offsetは考慮すべき？
		ImageManager.getInstance().drawBitmap(c, p, imageID
				, animation.getRect(), new Rect((int)x, (int)y, (int)(x+SIZE_X), (int)(y+SIZE_Y)));
	}
	
	public final static int NO_ACTION = 0;
	public final static int WALK = 1;
	public final static int JUMP = 2;
	public final static int SET_X = 3;
	public final static int SET_Y = 4;
	public final static int BLACKOUT = 5;
	public final static int STOP = 6;
	public final static int PLAY_SE = 7;
	
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
	
	private void playSE(int id){
		MusicManager.getInstance().playSE(id);
	}
	
	public void setFloor(int f){
		floor = f;
	}
	
	public void setGravity(double grav){
		this.grav = grav;
	}
	
	public void setVX(int svx){
		vx = svx;
	}
	
	public void setVY(int svy){
		vy = svy;
	}
}
