package com.example.runaction.game;

public class StageHistory {
	// 値の最大値
	private final static int MAX = 1000;
	// クリア回数
	private int clearNum;
	// 1プレイ中に倒した敵の数の最大値
	private int beatEnemy;
	// 取得したスター(未実装)のフラグ(ビット制御)
	private int stars;
	
	private int stageID;
	public StageHistory(int id){
		stageID = id;
	}
	
	public void incClear(){
		if(clearNum >= MAX) return;
		clearNum++;
	}
	
	public int getClearNum(){
		return clearNum;
	}
	
	// とったスターの番号を設定する
	private void setStar(int num){
		stars |= 1<<num;
	}
	
}
