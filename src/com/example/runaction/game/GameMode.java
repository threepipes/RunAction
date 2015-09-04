package com.example.runaction.game;

import com.example.runaction.GameThread;

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

	// updateを有効にするか
	// これがfalseのとき、ゲームは停止状態になる
	private boolean validateUpdate;

	public GameMode(Context context, GameThread thread){
		this.parent = thread;
		// pause画面などの一時画面を生成
		createSubMode();

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

	}

	public void restart(){
		map.resetStage();
		player.setPoint(Player_init_x, Player_init_y);
		validateUpdate = true;

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

		// gemeover状態
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
		gameover = new SubMode(bm);

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
				activeSubMode = pause;
			}
		});

		// standby状態でゲーム開始
		activeSubMode = standby;
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

	public void update(){

		if(releaseSubMode){
			activeSubMode = null;
			releaseSubMode = false;
		}
		if(activeSubMode != null){
			return;
		}
		if(!validateUpdate) return;

		culcOffset();

		player.update();
		if(player.checkGoal()){
			validateUpdate = false;
			gameClear();
		}
	}

	public void draw(Canvas c, Paint p){
		// 背景を黒で塗りつぶす
		c.drawColor(0xFF000000);
		p.setColor(0xFF111111);
		c.drawRect(new Rect(0, 0, Width, Height), p);

		// マップを描画
		map.draw(c, p, offsetX, offsetY);

		// プレイヤーを描画
		player.draw(c, p, offsetX, offsetY);

		// ポーズ用ボタン描画
		pauseButton.draw(c, p);

		if(activeSubMode != null){
			activeSubMode.draw(c, p);
		}
	}

	public static final int KEY_PRESSED = 1;
	public static final int KEY_RELEASED = 2;
	private boolean keyPressed;
	public void touchEvent(int x, int y, int keytype){
		Log.d("GAME", "Touched ("+x+","+y+"):code="+keytype);

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
		activeSubMode = gameover;
		validateUpdate = false;
		Log.d("GAME", "exit="+code);
	}

	public void gameClear(){
		parent.intentToGoal();
	}

	public void returnTitle(){
		parent.intentToTitle();
	}
}
