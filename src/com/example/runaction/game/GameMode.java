package com.example.runaction.game;

import com.example.runaction.GameThread;
import com.example.runaction.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class GameMode extends Mode{
	// マップ
	private Map map;
	// プレイヤー
	private Player player;
	// マップのオフセット
	private int offsetX;
	private int offsetY;

	private GameThread parent;

	private boolean releaseSubMode;
	// 現在有効なSubMode
	private SubMode activeSubMode;
	// スタート直前の状態
	private SubMode standby;
	// ゲームオーバー状態(将来的にはSubModeでなくActivityにしたい)
	private SubMode gameover;
	// ポーズ状態
	private SubMode pause;
	private MyButton pauseButton; // ポーズ状態へ遷移するためのボタン
	
	// 現在有効な自動アニメーション
	private AutoAnimation activeAnimation;
	// ゲームオーバー用アニメーション
	private AutoAnimation gameoverAnimation;
	// ゲームクリア用アニメーション
	private AutoAnimation clearAnimation;
	
	// 画面を暗くする
	private boolean blackout;

	// 自動アニメーション終了後に行う動作の予約
	private int reservedEvent;
	private final static int RESERVE_GAMEOVER = 1;
	private final static int RESERVE_GAMECLEAR = 2;

	// updateを有効にするか
	// これがfalseのとき、ゲームは停止状態になる
	private boolean validateUpdate;

	public GameMode(Context context, GameThread thread){
		this.parent = thread;
		// pause画面などの一時画面を生成
		createSubMode();
		// AutoAnimationの設定
		createAutoAnimation();

		init(context);
	}

	private static final int Player_init_x = 60;
	private static final int Player_init_y = 850;
	// 将来的には、ここでステージ名をセット
	public void init(Context context){
		// マップを作成
		map = new Map(this);
		// プレイヤーを作成
		player = new Player(Player_init_x, Player_init_y, map, this);
		validateUpdate = true;
		blackout = false;
	}

	public void restart(){
		map.resetStage();
		player.setPoint(Player_init_x, Player_init_y);
		validateUpdate = true;
		blackout = false;
	}
	
	// 自動アニメーションの生成(マクロやアニメーションのデータは別ファイルから持ってくるようにしたい)
	private void createAutoAnimation(){
		// gameover
		int[][] macroG = {
				{0, AutoAnimation.JUMP, 5, 0},
				{AutoAnimation.FLAG_OUTOFWINDOW, AutoAnimation.NO_ACTION, 0, 0},
		};
		int[][][] animG = {
				{{3, 1, Animation.FRAME_LOOP}}
		};
		gameoverAnimation = new AutoAnimation(macroG, new Animation(animG), R.drawable.player);
		gameoverAnimation.setFloor(GameThread.WINDOW_HEIGHT + Player.HEIGHT);
		
		// clear
		int[][] macroC = {
				{0, AutoAnimation.NO_ACTION, 0, 0},
				{0, AutoAnimation.BLACKOUT, 0xFF/10, 0},
				{AutoAnimation.FLAG_GROUND, AutoAnimation.WALK, 5, 1},
				{AutoAnimation.FLAG_OUTOFWINDOW, AutoAnimation.NO_ACTION, 0, 1},
		};
		int[][][] animC = {
				{{0, 1, Animation.FRAME_LOOP}},
	    		{{0, 0, 5},{1, 0, 5},{2, 0, 5},{3, 0, 5},{Animation.FLAG_LOOP, 0}},// 走る
		};
		clearAnimation = new AutoAnimation(macroC, new Animation(animC), R.drawable.player);
		clearAnimation.setFloor(GameThread.WINDOW_HEIGHT - Player.HEIGHT);
		
		activeAnimation = null;
	}

	private void createSubMode(){
		// standby状態(スタート直前状態)の生成
		ButtonManager bm = new ButtonManager();
		bm.put(0, new MyButton(new Rect(170, 600, 370, 650), "START", new ButtonAction() {
			@Override
			public void onClickAction() {
				releaseSubMode = true;
			}
		}));
		standby = new SubMode(bm);

		// gameover状態
		bm = new ButtonManager();
		bm.put(0, new MyButton(new Rect(170, 500, 370, 550), "RESTART", new ButtonAction() {
			@Override
			public void onClickAction() {
				releaseSubMode = true;
				restart();
			}
		}));
		bm.put(1, new MyButton(new Rect(170, 600, 370, 650), "TITLE", new ButtonAction() {
			@Override
			public void onClickAction() {
				returnTitle();
			}
		}));
		gameover = new GameOverSubMode(bm);

		// pause状態
		bm = new ButtonManager();
		bm.put(0, new MyButton(new Rect(170, 500, 370, 550), "CONTINUE", new ButtonAction() {
			@Override
			public void onClickAction() {
				releaseSubMode = true;
			}
		}));
		bm.put(1, new MyButton(new Rect(170, 600, 370, 650), "RESTART", new ButtonAction() {
			@Override
			public void onClickAction() {
				releaseSubMode = true;
				restart();
			}
		}));
		bm.put(2, new MyButton(new Rect(170, 700, 370, 750), "TITLE", new ButtonAction() {
			@Override
			public void onClickAction() {
				returnTitle();
			}
		}));
		pause = new SubMode(bm);
		pauseButton = new MyButton(new Rect(10, 10, 110, 60), "PAUSE", new ButtonAction() {
			@Override
			public void onClickAction() {
				changeActiveSubMode(pause);
			}
		});

		// standby状態でゲーム開始
		changeActiveSubMode(standby);
	}
	
	private void executeReservedEvent(){
		if(reservedEvent == 0) return;
		if(reservedEvent == RESERVE_GAMEOVER){
			changeActiveSubMode(gameover);
		}else if(reservedEvent == RESERVE_GAMECLEAR){
			gameClear();
		}
		reservedEvent = 0;
	}
	
	private void changeActiveSubMode(SubMode mode){
		activeSubMode = mode;
		activeSubMode.init();
	}
	
	private void startAutoAnimation(AutoAnimation anim, int x, int y){
		player.setExist(false);
		activeAnimation = anim;
		activeAnimation.startAnimation(x, y);
	}

	public void playSE(int id){
		parent.playSE(id);
	}

	private void culcOffset(){
		// X方向のオフセットを計算
		offsetX = Width / 2 - (int)player.getX();
		// マップの端ではスクロールしないようにする
		offsetX = Math.min(offsetX, 0);
		offsetX = Math.max(offsetX, Width - Map.WIDTH);

		// Y方向のオフセットを計算
		offsetY = Height / 2 - (int)player.getY();
		// マップの端ではスクロールしないようにする
		offsetY = Math.min(offsetY, 0);
		offsetY = Math.max(offsetY, Height - Map.HEIGHT);	
	}
	
	public int getOffsetX(){
		return offsetX;
	}

	public void update(){
		if(activeAnimation != null){
			if(!activeAnimation.inAnimation() || !activeAnimation.inWindow()){
				activeAnimation = null;
				executeReservedEvent();
			}else activeAnimation.update();
			return;
		}

		if(releaseSubMode){
			activeSubMode = null;
			releaseSubMode = false;
		}
		if(activeSubMode != null){
			activeSubMode.update();
			return;
		}
		if(!validateUpdate) return;

		culcOffset();

		player.update();
		if(player.checkGoal()){
			validateUpdate = false;
			startAutoAnimation(clearAnimation, (int)(player.getX() + offsetX), (int)player.getY());
			reservedEvent = RESERVE_GAMECLEAR;
		}
	}

	public void draw(Canvas c, Paint p){
		// 背景を黒で塗りつぶす
		c.drawColor(0xFF000000);
		p.setColor(0xFF111111);
		c.drawRect(new Rect(0, 0, Width, Height), p);

		// マップを描画
		map.draw(c, p, offsetX, offsetY);

		if(activeAnimation != null){
			activeAnimation.draw(c, p);
			return;
		}
		// プレイヤーを描画
		player.draw(c, p, offsetX, offsetY);

		// ポーズ用ボタン描画
		pauseButton.draw(c, p);

		if(activeSubMode != null){
			activeSubMode.draw(c, p);
		}
		if(blackout){
			p.setColor(0xFF000000);
			c.drawRect(new Rect(0, 0, Width, Height), p);
		}
	}

	public static final int KEY_PRESSED = 1;
	public static final int KEY_RELEASED = 2;
	private boolean keyPressed;
	public void touchEvent(int x, int y, int keytype){
		Log.d("GAME", "Touched ("+x+","+y+"):code="+keytype);
		if(activeAnimation != null) return;

		// SubModeが有効なら、SubModeに入力の焦点を合わせる
		if(activeSubMode != null){
			activeSubMode.touchEvent(x, y, keytype);
			return;
		}
		// ポーズボタンが押されてたらJump入力は受け付けない
		if(pauseButton.collision(x, y)){
			pauseButton.touchEvent(x, y, keytype);
			return;
		}
		if((keytype|KEY_PRESSED) > 0){
			if(!keyPressed) player.jump();
			keyPressed = true;
		}
		if((keytype|KEY_RELEASED) > 0){
			keyPressed = false;
		}
	}

	public static final int EXIT_DEATH = 1;
	public void exitRequest(int code){
		startAutoAnimation(gameoverAnimation, (int)player.getX()+offsetX, (int)player.getY());
		reservedEvent = RESERVE_GAMEOVER;
		validateUpdate = false;
		Log.d("GAME", "exit="+code);
	}

	public void gameClear(){
		blackout = true;
		parent.intentToGoal();
	}

	public void returnTitle(){
		parent.intentToTitle();
	}
}
