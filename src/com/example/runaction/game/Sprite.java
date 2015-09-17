package com.example.runaction.game;

import com.example.runaction.ImageManager;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/*
 * Created on 2005/06/24
 *
 */

/**
 * @author mori
 *
 */
public abstract class Sprite {
    // 位置
    protected double x;
    protected double y;
    
    protected double startx;
    protected double starty;
    
    // 幅
    public final static int WIDTH = 32;
    // 高さ
    public final static int HEIGHT = 32;

    // マップへの参照
    protected Map map;

    protected boolean death;
    
    protected Animation animation;
    protected int id;
    
    public Sprite(double x, double y, Map map, Animation anim, int imageID) {
        this.x = x;
        this.y = y;
        startx = x;
        starty = y;
        this.map = map;
        animation = anim;
        id = imageID;
    }

    /**
     * スプライトの状態を更新する
     */
    public abstract void update();
    
    public abstract boolean hitPlayer(Player player, Map map);

    /**
     * スプライトを描画
     * 
     * @param g 描画オブジェクト
     * @param offsetX X方向オフセット
     * @param offsetY Y方向オフセット
     */
    public void draw(Canvas c,Paint p, int offsetX, int offsetY) {
    	final int tx = (int)x + offsetX;
		final int ty = (int)y + offsetY;
		final Rect drawableRect = animation == null ? new Rect(0, 0, WIDTH, HEIGHT) : animation.getRect();
		ImageManager.getInstance().drawBitmap(c, p, id, drawableRect, new Rect(tx, ty, tx+WIDTH, ty+HEIGHT));
    }

    /**
     * 他のスプライトと接触しているか
     * @param sprite スプライト
     */
    public boolean isCollision(Sprite sprite) {
        Rect playerRect = getRect();
        Rect spriteRect = sprite.getRect();
        // 自分の矩形と相手の矩形が重なっているか調べる
        if (Rect.intersects(playerRect, spriteRect)) {
            return true;
        }
        
        return false;
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
    /**
     * @return Returns the width.
     */
    public int getWidth() {
        return WIDTH;
    }
    /**
     * @return Returns the height.
     */
    public int getHeight() {
        return HEIGHT;
    }
    public boolean isdeath(){
    	return death;
    }
    
    public void death(){
    	death = true;
    }
    public Rect getRect(){
    	return new Rect((int)x,(int)y,(int)x + WIDTH,(int)y + HEIGHT);
    }

    public abstract void reset();
}