package com.example.runaction.game;

import com.example.runaction.R;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/*
 * Created on 2005/06/06
 *
 */

/**
 * @author mori
 *  
 */
public class Player extends GameObject{
	// 幅
	public static final int WIDTH = 32;
	// 高さ
	public static final int HEIGHT = 32;
	// スピード
	private static final int SPEED = 6;
	// ジャンプ力
	private static final int JUMP_SPEED = 20;

	// 位置
	private double x;
	private double y;

	// 速度
	private double vx;
	private double vy;

	// 着地しているか
	private boolean onGround;

	// マップへの参照
	private Map map;

	// BGMを鳴らすのに必要
	private GameMode manager;

	// Goalかどうかのチェックに必要
	// Goalしたらupdateでこれをtrueにすることで、ゲームクリアとなる
	private boolean goal;
	// GoalするためのX座標   これを超えたらゴールとする
	// マップ自体に設定するのが望ましい
	// ゴール周辺は平らな地形にするように(激突してもゴールみたいなことを防ぐ)
	private int goalX = (30 - 2) * Map.TILE_SIZE - 10;

	public Player(double x, double y, Map map, GameMode manager) {
		this.x = x;
		this.y = y;
		initState();
		this.map = map;
		this.manager = manager;
	}

	// player状態のリセット
	private void initState(){
		vx = SPEED;
		vy = 0;
		onGround = false;
		goal = false;
	}

	public void setPoint(double x, double y){
		this.x = x;
		this.y = y;
		initState();
	}

	/**
	 * 停止する

    public void stop() {
        vx = 0;
    }
	 */
	/**
	 * 左に加速する

    public void accelerateLeft() {
        vx = -SPEED;
    }*/

	/**
	 * 右に加速する

    public void accelerateRight() {
        vx = SPEED;
    }*/

	/**
	 * ジャンプする
	 */
	public void jump() {
		if (onGround) {
			// 上向きに速度を加える
			vy = -JUMP_SPEED;
			onGround = false;
			// Jump効果音
			manager.playSE(R.raw.jump);
		}
	}

	/**
	 * プレイヤーの状態を更新する
	 */
	public void update() {
		// 重力で下向きに加速度がかかる
		vy += Map.GRAVITY;

		// x方向の当たり判定
		// 移動先座標を求める
		double newX = x + vx;
		// 移動先座標で衝突するタイルの位置を取得
		// x方向だけ考えるのでy座標は変化しないと仮定
		Point tile = map.getTileCollision(this, newX, y);
		if (tile == null) {
			// 衝突するタイルがなければ移動
			x = newX;
		} else {
			// 衝突するタイルがある場合
			map.exitRequest();
			/*if (vx > 0) { // 右へ移動中なので右のブロックと衝突
                // ブロックにめりこむ or 隙間がないように位置調整
                //x = Map.tilesToPixels(tile.x) - WIDTH;
            } else if (vx < 0) { // 左へ移動中なので左のブロックと衝突
                // 位置調整
                x = Map.tilesToPixels(tile.x + 1);
            }*/
			//vx = 0;
		}

		// y方向の当たり判定
		// 移動先座標を求める
		double newY = y + vy;
		//穴に落ちたらゲームオーバー
		// 移動先座標で衝突するタイルの位置を取得
		// y方向だけ考えるのでx座標は変化しないと仮定
		tile = map.getTileCollision(this, x, newY);
		if (tile == null) {
			// 衝突するタイルがなければ移動
			y = newY;
			// 衝突してないということは空中
			onGround = false;
		} else {
			// 衝突するタイルがある場合
			if (vy > 0) { // 下へ移動中なので下のブロックと衝突（着地）
				// 位置調整
				y = Map.tilesToPixels(tile.y) - HEIGHT;
				// 着地したのでy方向速度を0に
				vy = 0;
				// 着地
				onGround = true;
				// 着地のSE
				//                manager.playSE(R.raw.landing);
			} else if (vy < 0) { // 上へ移動中なので上のブロックと衝突（天井ごん！）
				// 位置調整
				y = Map.tilesToPixels(tile.y + 1);
				// 天井にぶつかったのでy方向速度を0に
				vy = 0;
			}
		}

		// Goal判定
		if(x >= goalX) goal = true;
	}

	public boolean checkGoal(){
		return goal;
	}

	/**
	 * プレイヤーを描画
	 * 
	 * @param g 描画オブジェクト
	 * @param offsetX X方向オフセット
	 * @param offsetY Y方向オフセット
	 */
	public void draw(Canvas c, Paint p, int offsetX, int offsetY) {
		p.setColor(0xFFFF0000);
		final int dx = (int) x + offsetX;
		final int dy = (int) y + offsetY;
		c.drawRect(new Rect(dx, dy, dx+WIDTH, dy+HEIGHT), p);
	}

	/**
	 * @return Returns the x.
	 */
	public double getX() {
		return x;
	}
	/**
	 * @return Returns the y.
	 */
	public double getY() {
		return y;
	}
}
