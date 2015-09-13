package com.example.runaction;

import com.example.runaction.TitleView.TitleThread;
import com.example.runaction.game.Animation;
import com.example.runaction.game.AutoAnimation;
import com.example.runaction.game.Player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TitleView  extends SurfaceView
implements SurfaceHolder.Callback{
	TitleThread titleThread;
	TitleActivity titleActivity;

	public TitleView(TitleActivity context){
		super(context);
		init(context);
	}
	
	private void init(TitleActivity context){
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// 画像の設定(ここで呼び出すべきかどうか TODO )
		setTitleImage();
		titleThread = new TitleThread(holder, context, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		});
		this.titleActivity = context;
	}

	public void setTitleImage(){
		ImageManager manager = ImageManager.getInstance();
		manager.loadBitmap(R.drawable.title);
		manager.loadBitmap(R.drawable.player);
		manager.loadBitmap(R.drawable.music);
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
			titleActivity.intentToGame();
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
		bgmState = !Setting.getInstance().getSettingValue(Setting.SET_VOLUME);
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
				{30, AutoAnimation.JUMP, 5, 2},
				{AutoAnimation.FLAG_GROUND, AutoAnimation.NO_ACTION, 0, 3},
				{20, AutoAnimation.NO_ACTION, 0, 4},
				{40, AutoAnimation.WALK, 5, 5},
				{AutoAnimation.FLAG_OUTOFWINDOW, AutoAnimation.NO_ACTION, 0, 6},
		};
		playerAnimation = new AutoAnimation(animMacro, new Animation(playerAnim), R.drawable.player);
		playerAnimation.setFloor(GameThread.WINDOW_HEIGHT - Player.HEIGHT*3);
		playerAnimation.startAnimation(PLAYER_X, PLAYER_Y);
	}
	
	public void setVolumeAnimation(boolean flag){
		bgmState = flag;
	}
	
	public void awake(){
		playerAnimation.contact();
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
	
	public void draw(Canvas c, Paint p){
		ImageManager im = ImageManager.getInstance();
		im.drawBitmap(c, p, R.drawable.title, windowRect, windowRect);
		if(bgmState) im.drawBitmap(c, p, R.drawable.music, bgmAnimation.getRect(), new Rect(140, 850, 140 + 32, 850 + 32));
		playerAnimation.draw(c, p);
	}
}
