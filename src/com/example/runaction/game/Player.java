package com.example.runaction.game;

import com.example.runaction.ImageManager;
import com.example.runaction.R;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;

/*
 * Created on 2005/06/06
 *
 */

/**
 * @author mori
 *  
 */
public class Player extends Sprite{
	// スピード
	private static final int SPEED = 8;
	// ジャンプ力
	private static final int JUMP_SPEED = 20;
    // 速度
    private double vx;
    private double vy;
    
    // 着地しているか
    private boolean onGround;
    
    // 再ジャンプできるか
    private boolean forceJump;
    
    // 描画されるか
    private boolean isExist;
    
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
    private int goalX = (422 - 2) * Map.TILE_SIZE - 10;
    // キャラクタのアニメーションを実現するためのインスタンス
    private Animation animation;
    // playerがアクションを変えた場合に設定し、update内でアニメーションを切り替える
    private int actionChange;
    private final static int ACTION_NO_CHANGE = -1;
    private final static int ACTION_RUN = 0;
    private final static int ACTION_JUMP = 1;
    
    public Player(double x, double y, Map map, GameMode manager) {
    	super(x,y,map);
        this.x = x;
        this.y = y;
        this.map = map;
        this.manager = manager;
        animation = new Animation(animationData);
        initState();
    }
    
    // player状態のリセット
    private void initState(){
        vx = SPEED;
        vy = 0;
        onGround = false;
        forceJump = false;
        goal = false;
        animation.setAnim(ANIM_JUMP);
        actionChange = ACTION_NO_CHANGE;
        isExist = true;
    }
    
    public void setExist(boolean flag){
    	isExist = flag;
    }
    
    public void setPoint(double x, double y){
    	this.x = x;
        this.y = y;
        initState();
    }
    
    // ---------- animationの仮データ ----------
    // 以下の通りセル座標で指定する
    private final static int[][][] animationData = {
    		{{0, 0, 5},{1, 0, 5},{2, 0, 5},{3, 0, 5},{Animation.FLAG_LOOP, 0}},// 走る
    		{{0, 1, Animation.FRAME_LOOP}},// ジャンプ
    };
    private final static int ANIM_RUN = 0;
    private final static int ANIM_JUMP = 1;
    // ---------- 仮データここまで ----------
    
    private void initAnimation(){
    	// 将来的には別ファイルでアニメーションデータ(座標配列など)を管理し、ここでは読み込みを行う
    	SparseArray<AnimData[]> anim = new SparseArray<AnimData[]>();
    	AnimData[] defaultData = null;
    	for(int i=0; i<animationData.length; i++){
    		AnimData[] data = new AnimData[animationData[i].length];
    		for(int j=0; j<data.length; j++){
    			if(animationData[i][j][0] >= 0){
    				final int ax = animationData[i][j][0];
    				final int ay = animationData[i][j][1];
    				final int frame = animationData[i][j][2];
    				data[j] = new AnimData(ax, ay, frame, Animation.FLAG_NONE);
    			}else{
    				final int flag = animationData[i][j][0];
    				final int frame = animationData[i][j][1];
    				data[j] = new AnimData(0, 0, frame, flag);
    			}
    		}
    		if(i == 0) defaultData = data;
    		anim.append(i, data);
    	}
    	if(defaultData == null){
    		Log.e("ANIM_SET", "No animation data found");
    	}
    	animation = new Animation(anim, defaultData);
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
        if (onGround || forceJump) {
            // 上向きに速度を加える
            vy = -JUMP_SPEED;
            onGround = false;
            forceJump = false;
            // Jump効果音
            manager.playSE(R.raw.jump);
            actionChange = ACTION_JUMP;
        }
    }
    public void jump2(){
    	vy = -JUMP_SPEED * 2;
    	onGround = false;
    }
    
    public void setForceJump(boolean forceJump) {
        this.forceJump = forceJump;
    }
    
	/**
	 * プレイヤーの状態を更新する
	 */
	public void update() {
		if(!isExist) return;
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
                if(!onGround) actionChange = ACTION_RUN;
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
        
        // アニメーション設定
        if(actionChange != ACTION_NO_CHANGE){
        	if(actionChange == ACTION_RUN) animation.setAnim(ANIM_RUN);
        	else if(actionChange == ACTION_JUMP) animation.setAnim(ANIM_JUMP);
        	actionChange = ACTION_NO_CHANGE;
        }
        animation.update();
        
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
    	if(!isExist) return;
        p.setColor(0xFFFF0000);
        final int dx = (int) x + offsetX;
        final int dy = (int) y + offsetY;
        ImageManager.getInstance().drawBitmap(c, p, R.drawable.player
        		, animation.getRect(), new Rect(dx, dy, dx+WIDTH, dy+HEIGHT));
//        c.drawRect(new Rect(dx, dy, dx+WIDTH, dy+HEIGHT), p);
    }

    @Override
    public void death() {
    	manager.exitRequest(GameMode.EXIT_DEATH);
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
