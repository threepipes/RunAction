package com.threepipes.runaction;

import com.threepipes.runaction.R;
import com.threepipes.runaction.TitleView.TitleThread;
import com.threepipes.runaction.game.Animation;
import com.threepipes.runaction.game.AutoAnimation;
import com.threepipes.runaction.game.GameMode;
import com.threepipes.runaction.game.Player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TitleView  extends SurfaceView
implements SurfaceHolder.Callback{
	private TitleThread titleThread;
	private TitleActivity titleActivity;
	
	private boolean touchState;
	private boolean touched;
	
	private int stage;

	public TitleView(TitleActivity context){
		super(context);
		init(context);
	}
	
	private void init(TitleActivity context){
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		setTitleImage();
		titleThread = new TitleThread(holder, context, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		});
		this.titleActivity = context;
		touchState = false;
		touched = false;
	}

	public void setTitleImage(){
		ImageManager manager = ImageManager.getInstance();
		manager.loadBitmap(R.drawable.title);
		manager.loadBitmap(R.drawable.player);
		manager.loadBitmap(R.drawable.music);
//		manager.loadBitmap(R.drawable.title_logo);
	}

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init((TitleActivity)context);
	}

	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init((TitleActivity)context);
	}

	public final static int EVENT_BGM_ON = 1;
	public final static int EVENT_BGM_OFF = 2;
	public final static int EVENT_GAMESTART = 3;
	public final static int EVENT_SKIP_ANIMATION = 4;
	public void setEvent(int event){
		titleThread.setEvent(event);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		titleThread.setWindowSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(titleThread == null) init(titleActivity);
		titleThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		titleThread.destroy();
		titleThread = null;
	}
	
	public void setStage(int st){
		stage = st;
	}
	
	public void setTouchState(boolean state){
		touchState = state;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if(!touchState) return false;
		final int act = e.getAction();
		if((act & 2) > 0 || act == 0){
			touched = true;
		}
		if((act & 1) > 0 && touched){
			titleThread.setEvent(EVENT_SKIP_ANIMATION);
		}
		return true;
	}

	class TitleThread extends Thread{
		SurfaceHolder surfaceHolder;
		
		public final Rect WindowRect = new Rect(0, 0, GameThread.WINDOW_WIDTH, GameThread.WINDOW_HEIGHT);
		public float scale;
		public float translateX;
		public float translateY;
		
		private TitleManager manager;
		
		public TitleThread(SurfaceHolder surfaceHolder, TitleActivity context, Handler handler){
			this.surfaceHolder = surfaceHolder;
			manager = new TitleManager(this);
		}
		
		public void setEvent(int event){
			if(event == TitleView.EVENT_GAMESTART){
				manager.awake();
			}
			switch(event){
			case TitleView.EVENT_GAMESTART:
				manager.awake();
				break;
			case TitleView.EVENT_BGM_ON:
				manager.setVolumeAnimation(true);
				break;
			case TitleView.EVENT_BGM_OFF:
				manager.setVolumeAnimation(false);
				break;
			case TitleView.EVENT_SKIP_ANIMATION:
				gotoGame();
				break;
			}
		}
		
		private Rect[] black;
		public void setWindowSize(int w, int h){
			float scaleX = (float)w / GameThread.WINDOW_WIDTH;
			float scaleY = (float)h /  GameThread.WINDOW_HEIGHT;
			scale = scaleX > scaleY ? scaleY : scaleX;
			translateX = (w - GameThread.WINDOW_WIDTH*scale)/2;
			translateY = (h - GameThread.WINDOW_HEIGHT*scale)/2;
			black = new Rect[2];
			if(translateX > 0){
				black[0] = new Rect(0, 0, (int)translateX, h);
				black[1] = new Rect((int)(translateX + GameThread.WINDOW_WIDTH*scale), 0
						, (int)(translateX*2 + GameThread.WINDOW_WIDTH*scale), h);
			}else{
				black[0] = new Rect(0, 0, w, (int)translateY);
				black[1] = new Rect(0, (int)(translateY + GameThread.WINDOW_HEIGHT*scale)
						, w, (int)(translateY*2 + GameThread.WINDOW_HEIGHT*scale));
			}
		}
		
		public void gotoGame(){
			manager.setSkipUpdate();
			titleActivity.intentToGame(stage);
		}
		
		public void destroy(){
			
		}
		
		@Override
		public void run() {
			Paint paint = new Paint();
			while(true){
				manager.update();

				Canvas c = surfaceHolder.lockCanvas();
				if(c == null) break;
				c.translate(translateX, translateY);
				c.scale(scale, scale);
				
				manager.draw(c, paint);
				if(black != null){
					c.scale(1.0f/scale, 1.0f/scale);
					c.translate(-translateX, -translateY);
					paint.setColor(0xFF000000);
					c.drawRect(black[0], paint);
					c.drawRect(black[1], paint);
				}
				surfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}
}

class TitleManager{
	public final Rect windowRect = new Rect(0, 0, GameThread.WINDOW_WIDTH, GameThread.WINDOW_HEIGHT);
	private TitleThread thread;
	
	// BGMがONのときアニメーションする
	private Animation bgmAnimation;
	private boolean bgmState;
	
	private AutoAnimation playerAnimation;
	
	private boolean skipUpdate;
	
	public TitleManager(TitleThread thread) {
		this.thread = thread;
		bgmState = !Setting.getInstance().getSettingValue(Setting.SET_VOLUME_OFF);
		setAnimation();
		skipUpdate = false;
	}
	
	private final static int PLAYER_X = 98;
	private final static int PLAYER_Y = 828;
	private void setAnimation(){
		int[][][] bgmAnim = {
				{{0, 0, 30}, {1, 0, 30}, {Animation.FLAG_LOOP, 0, 0}},	
		};
		bgmAnimation = new Animation(bgmAnim);
		
		int[][][] playerAnim = {
				{{3, 2, Animation.FRAME_LOOP}}, // 寝てる
				{{1, 3, Animation.FRAME_LOOP}}, // 起きる
				{{0, 3, Animation.FRAME_LOOP}}, // 垂直ジャンプ
				{{1, 1, Animation.FRAME_LOOP}}, // 着地
				{{2, 1, Animation.FRAME_LOOP}}, // 右向く
	    		{{0, 0, 5},{1, 0, 5},{2, 0, 5},{3, 0, 5},{Animation.FLAG_LOOP, 0}},// 走る
		};
		int[][] animMacro = {
				{0, AutoAnimation.STOP, 0, 0},
				{AutoAnimation.FLAG_EXE_COMMAND, AutoAnimation.STOP, 0, 1},
				{20, AutoAnimation.JUMP, 10, 2},
				{20, AutoAnimation.PLAY_SE, R.raw.jump, 0},
				{AutoAnimation.FLAG_GROUND, AutoAnimation.NO_ACTION, 0, 3},
				{10, AutoAnimation.NO_ACTION, 0, 4},
				{20, AutoAnimation.WALK, 5, 5},
				{AutoAnimation.FLAG_OUTOFWINDOW, AutoAnimation.NO_ACTION, 0, 6},
		};
		playerAnimation = new AutoAnimation(animMacro, new Animation(playerAnim), R.drawable.player);
		playerAnimation.setFloor(GameThread.WINDOW_HEIGHT - Player.HEIGHT*3);
		playerAnimation.setGravity(1);
		playerAnimation.startAnimation(PLAYER_X, PLAYER_Y);
	}
	
	public void setVolumeAnimation(boolean flag){
		bgmState = flag;
	}
	
	public void awake(){
		playerAnimation.contact();
	}
	
	public void setSkipUpdate(){
		skipUpdate = true;
	}
	
	public void update(){
		if(skipUpdate) return;
		if(bgmState) bgmAnimation.update();
		playerAnimation.update();
		if(!playerAnimation.inWindow()){
			thread.gotoGame();
			skipUpdate = true;
		}
	}
	
	private final static int BGM_ANIMATION_X = 150;
	private final static int BGM_ANIMATION_Y = 840;
	
	private final static int LOGO_WID = 500;
	private final static int LOGO_HEI = 200;
	private final static int LOGO_X = (GameThread.WINDOW_WIDTH-LOGO_WID)/2;
	private final static int LOGO_Y = 70;
	public void draw(Canvas c, Paint p){
		ImageManager im = ImageManager.getInstance();
		im.drawBitmap(c, p, R.drawable.title, windowRect, windowRect);
		if(bgmState) im.drawBitmap(c, p, R.drawable.music, bgmAnimation.getRect()
				, new Rect(BGM_ANIMATION_X, BGM_ANIMATION_Y
						, BGM_ANIMATION_X + Animation.SIZE_X, BGM_ANIMATION_Y + Animation.SIZE_Y));
//		im.drawBitmap(c, p, R.drawable.title_logo, new Rect(0, 0, LOGO_WID, LOGO_HEI)
//				, new Rect(LOGO_X, LOGO_Y, LOGO_X+LOGO_WID, LOGO_Y+LOGO_HEI));
		playerAnimation.draw(c, p);
	}
}
