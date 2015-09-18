package com.example.runaction.game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import com.example.runaction.BackGroundImage;
import com.example.runaction.GameThread;
import com.example.runaction.ImageManager;
//import com.example.runaction.Kuribo;
//import com.example.runaction.Needle;
import com.example.runaction.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;


class MapData{
	int mapDataID, eventID, mapChipID;
	BackGroundImage backgroundData;
	public MapData(int mID, int eID, int mapchip, BackGroundImage bgData){
		mapDataID = mID;
		eventID = eID;
		mapChipID = mapchip;
		backgroundData = bgData;
	}
}

public class Map {
	// タイルサイズ
	public static final int TILE_SIZE = 32;
	// 行数
	public static int ROW = 30;
	// 列数
	public static int COL = 1000;
	// 幅
	public static int WIDTH = TILE_SIZE * COL;
	// 高さ
	public static int HEIGHT = TILE_SIZE * ROW;
	// 重力
	public static final double GRAVITY = 2.0;
	
	   // スプライトリスト
    private LinkedList<Sprite> sprites;
    private LinkedList<Sprite> tmpsprites;

	// マップ
	private byte[][] map;

	private GameMode manager;
	private final static int mapImageID = R.drawable.map;

	public Map(GameMode manager, Context context, int mapNumber) {
		sprites = new LinkedList<Sprite>();
		tmpsprites = new LinkedList<Sprite>();
		setMapData(createMapData(mapNumber), context);
		Collections.sort(sprites);
//		map = load(R.raw.map, R.raw.event, context);
//		
//		// 背景の読み込み(将来的には移動させたい)
//		loadBackground();
		ImageManager.getInstance().loadBitmap(mapImageID);
		
		
		this.manager = manager;
	}
	
	private void setMapData(MapData data, Context context){
		map = load(data.mapDataID, data.eventID, context);
		ImageManager.getInstance().setBackGround(data.backgroundData);
	}
	
	private MapData createMapData(int mapNumber){
		if(mapNumber == 0){
			return new MapData(R.raw.map, R.raw.event, R.drawable.map, getBackgroundData());
		}else if(mapNumber == 1){
			return new MapData(R.raw.map_easy01, R.raw.event_easy01, R.drawable.map, getBackgroundData());
		}
		return null;
	}
	
	private BackGroundImage getBackgroundData(){
		int[] imageID = {
				R.drawable.sky,
				R.drawable.yama
		};
		final int yamaHeight = 180;
		Rect[] rects = {
				new Rect(0, 0, GameThread.WINDOW_WIDTH, GameThread.WINDOW_HEIGHT),
				new Rect(0, GameThread.WINDOW_HEIGHT-yamaHeight, 1440, GameThread.WINDOW_HEIGHT)
		};
		double[] offsetCoeff = {
				0.5,
				0.8
		};
		return new BackGroundImage(imageID, rects, offsetCoeff, 0);
	}

	public void resetStage(){
		// 敵などの動くオブジェクトがある場合初期状態に戻す（マップのリセット）
		Iterator<Sprite> it = tmpsprites.iterator();
        while (it.hasNext()) {
        	Sprite sp = it.next();
        	sprites.add(sp);
        }
        Iterator<Sprite> iterator = sprites.iterator();
        while(iterator.hasNext()){
        	Sprite sprite = iterator.next();
        	sprite.reset();
        }
        tmpsprites.clear();
        Collections.sort(sprites);
	}

	public void mapupdate(Player player){
		// マップにいるスプライトを取得
        LinkedList<Sprite> sprites = getSprites();            
        Iterator<Sprite> iterator = sprites.iterator();
        while (iterator.hasNext()) {
            Sprite sprite = iterator.next();
            
            // スプライトの状態を更新する
            sprite.update();

            // プレイヤーと接触してたら
            if (player.isCollision(sprite) && sprite.hitPlayer(player, this))
            	break;
            
            // プレイヤーより一画面以上先にいるスプライトと衝突判定しても仕方ないのでbreak
            if(sprite.getX() > player.getX() + GameThread.WINDOW_WIDTH)
            	break;
            
            // プレイヤーより後ろ且つ画面外に出たスプライトはリセットまで扱わないのでいったん除外
            if(sprite.getX() < player.getX() - GameThread.WINDOW_WIDTH/2 || sprite.isdeath()){
            	tmpsprites.add(sprite);
            	iterator.remove();
            }
        }
	}
	
	/**
	 * マップを描画する
	 * 
	 * @param g 描画オブジェクト
	 * @param offsetX X方向オフセット
	 * @param offsetY Y方向オフセット
	 */
	public void draw(Canvas c, Paint p, int offsetX, int offsetY, int playerX) {
		ImageManager.getInstance().drawBackground(c, p, offsetX, offsetY);
		drawMap(c, p, offsetX, offsetY);
		drawObject(c, p, offsetX, offsetY, playerX);
	}
	
	private void drawObject(Canvas c, Paint p, int offsetX, int offsetY, int playerX){
        // スプライトを描画
        // マップにいるスプライトを取得
        LinkedList<Sprite> sprites = getSprites();            
        Iterator<Sprite> iterator = sprites.iterator();
        while (iterator.hasNext()) {
        	Sprite sp = iterator.next();
        	sp.draw(c, p, offsetX, offsetY);
        	// プレイヤーより一画面以上先にいるスプライトを描いても仕方ないのでbreak
            if(sp.getX() > playerX + GameThread.WINDOW_WIDTH)
            	break;
        }
	}
	
	private void drawMap(Canvas c, Paint p, int offsetX, int offsetY){
				// オフセットを元に描画範囲を求める
		int firstTileX = pixelsToTiles(-offsetX);
		int lastTileX = firstTileX + pixelsToTiles(GameMode.Width) + 2;
		// 描画範囲がマップの大きさより大きくならないように調整
		lastTileX = Math.min(lastTileX, COL);

		int firstTileY = pixelsToTiles(-offsetY);
		int lastTileY = firstTileY + pixelsToTiles(GameMode.Height) + 1;
		// 描画範囲がマップの大きさより大きくならないように調整
		lastTileY = Math.min(lastTileY, ROW);
		if(map == null){
			Log.e("ERR", "map is null");
		}

		ImageManager manager = ImageManager.getInstance();
		p.setColor(0xFFCC8820);
		for (int i = firstTileY; i < lastTileY; i++) {
			for (int j = firstTileX; j < lastTileX; j++) {
				if(map[i][j] == 0) continue;
				final int x = tilesToPixels(j) + offsetX;
				final int y = tilesToPixels(i) + offsetY;
				manager.drawBitmap(c, p, mapImageID, getDrawableRect(map[i][j]), new Rect(x, y, x+TILE_SIZE, y+TILE_SIZE));
			}
		}
	}
	
	private final static int MAPCHIP_COLUMN = 16;
	private Rect getDrawableRect(int mapChipID){
		final int tx = mapChipID%MAPCHIP_COLUMN;
		final int ty = mapChipID/MAPCHIP_COLUMN;
		return new Rect(tx*TILE_SIZE, ty*TILE_SIZE, (tx+1)*TILE_SIZE, (ty+1)*TILE_SIZE);
	}

	/**
	 * (newX, newY)で衝突するブロックの座標を返す
	 * @param player プレイヤーへの参照
	 * @param newX X座標
	 * @param newY Y座標
	 * @return 衝突するブロックの座標
	 */
	public Point getTileCollision(Sprite sprite, double newX, double newY) {
		// 小数点以下切り上げ
		// 浮動小数点の関係で切り上げしないと衝突してないと判定される場合がある
		newX = Math.ceil(newX);
		newY = Math.ceil(newY);

		double fromX = Math.min(sprite.getX(), newX);
		double fromY = Math.min(sprite.getY(), newY);
		double toX = Math.max(sprite.getX(), newX);
		double toY = Math.max(sprite.getY(), newY);

		int fromTileX = pixelsToTiles(fromX);
		int fromTileY = pixelsToTiles(fromY);
		int toTileX = pixelsToTiles(toX + Player.WIDTH - 1);
		int toTileY = pixelsToTiles(toY + Player.HEIGHT - 1);

		// 衝突しているか調べる
		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				// 画面外は衝突
				if (x < 0 || x >= COL) {
					return new Point(x, y);
				}
				if (y < 0 || y >= ROW) {
					//穴に落ちたらゲームオーバー
					//                	System.exit(0);
					//return new Point(x, y);
					sprite.death();
					return null;
				}
				// ブロックがあったら衝突
				if (map[y][x] != 0) {
					return new Point(x, y);
				}
			}
		}

		return null;
	}

    
    /**
     * ファイルからマップを読み込む
     * 
     * @param filename 読み込むマップデータのファイル名
     * @return 
     */
    private byte[][] load(int mapID, int eventID, Context context) {
    	byte[][] tmap = null;
    	
    	try {
            InputStream in = context.getResources().openRawResource(mapID);
            int row = in.read();
            int col = in.read()<<8 | in.read();
            ROW = row;
            COL = col;
            tmap = new byte[row][col];
            for (int i = 0; i < row; i++) {
            	for (int j = 0; j < col; j++) {
            		tmap[i][j] = (byte) in.read();   
            	}
            }
    		
//    		File file = new File("event.evt");
//    		FileReader filereader = new FileReader(file);
            InputStream is = context.getResources().openRawResource(eventID);
    		BufferedReader br = new BufferedReader(new InputStreamReader(is));
    		
    		String str;
    		String[] event;
    		int k,l;
    		while((str = br.readLine())!= null){
    			 event = str.split(",",-1);
    			 k = Integer.parseInt(event[1]);
    			 l = Integer.parseInt(event[2]);
    			 if(event[0].equals("ENEMY")){
    				 sprites.add(new Kuribo(tilesToPixels(k),tilesToPixels(l),this));
    			 }else if(event[0].equals("NEEDLE")){
    				 sprites.add(new Needle(tilesToPixels(k),tilesToPixels(l),this));
    			 }else if(event[0].equals("SPRING")){
    				 sprites.add(new Spring(tilesToPixels(k),tilesToPixels(l),this));
    			 }
    		}
    		
    		br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
        	System.out.println(e);
    	}
		return tmap;
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
    /**
     * @return Returns the sprites.
     */
    public LinkedList<Sprite> getSprites() {
        return sprites;
    }
	
	/**
	 * ピクセル単位をタイル単位に変更する
	 * @param pixels ピクセル単位
	 * @return タイル単位
	 */
	public static int pixelsToTiles(double pixels) {
		return (int)Math.floor(pixels / TILE_SIZE);
	}

	/**
	 * タイル単位をピクセル単位に変更する
	 * @param tiles タイル単位
	 * @return ピクセル単位
	 */
	public static int tilesToPixels(int tiles) {
		return tiles * TILE_SIZE;
	}

	public void exitRequest(){
		manager.exitRequest(GameMode.EXIT_DEATH);
	}
}
