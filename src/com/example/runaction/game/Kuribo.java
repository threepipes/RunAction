package com.example.runaction.game;


import com.example.runaction.R;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/*
 * Created on 2005/06/27
 *
 */

/**
 * @author mori
 *
 */
public class Kuribo extends Sprite {
    // スピード
    private static final double SPEED = 3;

    // 速度
    protected double vx;
    protected double vy;
    
    private final static int ANIM_LEFT = 0;
    private final static int ANIM_RIGHT = 1;
    private final static int[][][] anim = {
    		{{0, 0, 8},{1, 0, 8},{Animation.FLAG_LOOP, 0, 0}}, // 左向き	
    		{{0, 1, 8},{1, 1, 8},{Animation.FLAG_LOOP, 0, 0}}, // 右向き
    };

    public Kuribo(double x, double y, Map map) {
        super(x, y, map, new Animation(anim), R.drawable.kuri);
        // 左に移動を続ける
        vx = -SPEED;
        vy = 0;
    }

    public void update() {
    	if(death) return;
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
            if (vx > 0) { // 右へ移動中なので右のブロックと衝突
                // ブロックにめりこむ or 隙間がないように位置調整
                x = Map.tilesToPixels(tile.x) - WIDTH;
            } else if (vx < 0) { // 左へ移動中なので左のブロックと衝突
                // 位置調整
                x = Map.tilesToPixels(tile.x + 1);
            }
            // 移動方向を反転
            vx = -vx;
            animation.setAnim(vx < 0 ? ANIM_LEFT : ANIM_RIGHT);
        }

        // y方向の当たり判定
        // 移動先座標を求める
        double newY = y + vy;
        // 移動先座標で衝突するタイルの位置を取得
        // y方向だけ考えるのでx座標は変化しないと仮定
        tile = map.getTileCollision(this, x, newY);
        if (tile == null) {
            // 衝突するタイルがなければ移動
            y = newY;
        } else {
            // 衝突するタイルがある場合
            if (vy > 0) { // 下へ移動中なので下のブロックと衝突（着地）
                // 位置調整
                y = Map.tilesToPixels(tile.y) - HEIGHT;
                // 着地したのでy方向速度を0に
                vy = 0;
            } else if (vy < 0) { // 上へ移動中なので上のブロックと衝突（天井ごん！）
                // 位置調整
                y = Map.tilesToPixels(tile.y + 1);
                // 天井にぶつかったのでy方向速度を0に
                vy = 0;
            }
        }
        animation.update();
    }
    
    public void reset(){
    	x = startx;
    	y = starty;
    	death = false;
    }
    
    @Override
    public boolean hitPlayer(Player player, Map map) {
        if ((int)player.getY() < (int)getY()) {
            // 栗ボーは消える
            death();
            // 踏むとプレイヤーは再ジャンプ
            player.setForceJump(true);
            player.jump();
            return true;
        } else {
            // ゲームオーバー
        	map.exitRequest();
        }
    	return false;
    }
    
    @Override
	public void draw(Canvas c, Paint p,int offsetX, int offsetY) {
		if(death) return;
		super.draw(c,p, offsetX, offsetY);
	}
}

