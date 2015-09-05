package com.example.runaction.game;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;

// キャラクタをアニメーションさせるためのクラス
public class Animation {
	
	// サイズ
	public static final int SIZE_X = 32;
	public static final int SIZE_Y = 32;
    // 現在のアニメーションにおいて何フレーム目か
    private int animFrame;
    // 画像上で、表示する位置
    private int animX;
    private int animY;
    
    // アニメーション管理マップ
    private SparseArray<AnimData[]> animMap;
    // 現在のアクティブアニメーション
    private AnimData[] animation;
    // デフォルトアニメーション(与えられたアニメーションIDが無効だった場合に設定される)
    private AnimData[] defaultAnimation;
    
    // TODO 画像IDまたは画像オブジェクトの設定
    public Animation(SparseArray<AnimData[]> map, AnimData[] initAnim){
    	animMap = map;
    	// 初期設定アニメーション
    	animation = initAnim;
    	defaultAnimation = initAnim;
    }
    
    // アニメーションIDのセット
    // 呼ばれる場合、ループ内においてupdateよりも先に呼び出すこと
    public void setAnim(int key){
    	animation = animMap.get(key, null);
    	if(animation == null) animation = defaultAnimation;
    	animFrame = 0;
    }
    
    public static final int FRAME_LOOP = 0;
    public static final int FLAG_NONE = 0;
    // 有効なフラグは必ず負の値にすること
    public static final int FLAG_LOOP = -1;
    public void update(){
    	// 終端はかならず(フラグ, 位置)で指定すること (無限ループの場合を除く)
    	checkAnimationException();
    	final AnimData data = animation[animFrame];
    	if(data.flag != FLAG_NONE){
    		if(data.flag == FLAG_LOOP){
    			animFrame = data.frame;
    		}
    		animX = data.x*SIZE_X;
    		animY = data.y*SIZE_X;
    		return;
    	}
    	if(data.frame == FRAME_LOOP){
    		// 無限ループ
    		return;
    	}
    	animX = data.x*SIZE_X;
    	animY = data.y*SIZE_Y;
    	animFrame++;
    }
    
    private void checkAnimationException(){
    	if(animation == null){
    		Log.e("ANIM", "Animation: animation data is null!");
    		animation = defaultAnimation;
    		animFrame = 0;
    	}else if(animFrame >= animation.length){
    		Log.e("ANIM", "Animation: frame index is out of range!");
    		animFrame = 0;
    	}
    }
    
    private Rect rect = new Rect();
    public Rect getRect(){
    	// 画像内の(x, y, x+size, y+size)で囲まれた領域を返す
    	rect.left = animX;
    	rect.top = animY;
    	rect.right = animX + SIZE_X;
    	rect.left = animY + SIZE_Y;
    	return rect;
    }
    
 
}

class AnimData{
	int x, y;
	int frame;
	int flag;
	public AnimData(int x, int y, int frame, int flag){
		this.x = x;
		this.y = y;
		this.frame = frame;
		this.flag = flag;
	}
}
