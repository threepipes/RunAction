package com.example.runaction.game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.content.Context;
import android.util.Log;

public class StageHistory {
	// 値の最大値
	private final static int MAX = 1000;
	// 今までプレイした回数
	private int playNum;
	// 最大到達度
	private int maxReach;
	// 初クリアまでにかかった回数
	private int firstClear;
	// クリア回数
	private int clearNum;
	// 通しでクリアしたか
	private boolean notUseGate;
	// 1プレイ中に倒した敵の数の最大値
	private int beatEnemy;
	// 取得したスター(未実装)のフラグ(ビット制御)
	private int stars;
	
	
	private final static int KEY_PLAY = 0;
	private final static int KEY_REACH = 1;
	private final static int KEY_FIRST = 2;
	private final static int KEY_CLEAR = 3;
	private final static int KEY_GATE = 4;
	private final static int KEY_ENEMY = 5;
	private final static int KEY_STARS = 6;
	
	private int stageID;
	public StageHistory(int id, Context context){
		stageID = id;
		loadHistory(context);
	}
	
	private void loadHistory(Context context){
		try{
			Log.d("LOAD", "started");
			InputStream in = context.openFileInput("stage"+stageID+".sav");
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String[] s = reader.readLine().split(",");
			playNum = Integer.parseInt(s[KEY_PLAY]);
			maxReach = Integer.parseInt(s[KEY_REACH]);
			firstClear = Integer.parseInt(s[KEY_FIRST]);
			clearNum = Integer.parseInt(s[KEY_CLEAR]);
			notUseGate = Integer.parseInt(s[KEY_GATE])==1;
			beatEnemy = Integer.parseInt(s[KEY_ENEMY]);
			stars = Integer.parseInt(s[KEY_STARS]);
			reader.close();
			Log.d("LOAD", "completed");
		}catch(FileNotFoundException e){
			initData();
			Log.d("LOAD", "data not found");
		}catch(IOException e){
			e.printStackTrace();
			Log.d("LOAD", "data error");
		}
	}
	
	public void saveHistory(Context context){
		String s = playNum
				+","+maxReach
				+","+firstClear
				+","+clearNum
				+","+(notUseGate ? 1 : 0)
				+","+beatEnemy
				+","+stars
				+"\n";
		try{
			Log.d("SAVE", "started");
			OutputStream out = context.openFileOutput("stage"+stageID+".sav", Context.MODE_PRIVATE);
			PrintWriter writer =
				new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
			writer.write(s);
			writer.close();
			Log.d("SAVE", "completed");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void initData(){
		playNum = 0;
		maxReach = 0;
		firstClear = -1;
		clearNum = 0;
		notUseGate = false;
		beatEnemy = 0;
		stars = 0;
	}
	
	public void incPlay(){
		if(playNum >= MAX) return;
		playNum++;
	}
	
	public boolean setMaxReach(int val){
		if(val > maxReach){
			maxReach = val;
			return true;
		}
		return false;
	}
	
	public void incClear(){
		if(clearNum >= MAX) return;
		if(clearNum == 0) firstClear = playNum;
		clearNum++;
	}
	
	public int getClearNum(){
		return clearNum;
	}
	
	public int getMaxReach(){
		return maxReach;
	}
	
	// とったスターの番号を設定する
	public void setStar(int num){
		stars |= 1<<num;
	}
	
}
