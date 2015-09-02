package com.example.runaction.game;

import com.example.runaction.GameThread;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class GameManager extends Mode{
    // パネルサイズ
//    public static final int Width = 540;
//    public static final int Height = 960;

    // マップ
    private Map map;

    // プレイヤー
    private Player player;
    
    private int offsetX;
    private int offsetY;

    // キーの状態（押されているか、押されてないか）
//    private boolean leftPressed;
//    private boolean rightPressed;
//    private boolean upPressed;
    
    private GameThread parent;
    
    private boolean alive;// 一時変数
    
	public GameManager(Context context, GameThread thread){
		this.parent = thread;
		init(context);
	}
	
	public void init(Context context){

        // マップを作成
        map = new Map(this);

        // プレイヤーを作成
        player = new Player(60, 800, map, this);
        
        alive = true;
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
		if(!alive) return;
		
		culcOffset();
        
        player.update();
	}
	
	public void draw(Canvas c){
		Paint paint = new Paint();
        // 背景を黒で塗りつぶす
		c.drawColor(0xFF000000);
        paint.setColor(0xFF111111);
        c.drawRect(new Rect(0, 0, Width, Height), paint);

        // マップを描画
        map.draw(c, paint, offsetX, offsetY);

        // プレイヤーを描画
        player.draw(c, paint, offsetX, offsetY);
	}
	
	public static final int KEY_PRESSED = 1;
	public static final int KEY_RELEASED = 2;
	public void touchEvent(int keytype){
		if((keytype|KEY_PRESSED) > 0){
			player.jump();
		}
	}
	
	public static final int EXIT_DEATH = 1;
	public void exitRequest(int code){
		alive = false;
		Log.d("GAME", "exit="+code);
	}

}