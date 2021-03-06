package com.threepipes.runaction.game;

import android.content.Context;

public class StageHistoryManager {
	private static StageHistoryManager instance;
	private StageHistory[] stage;
	private final static int STAGE_MAX = 3;
	private Context context;
	private boolean needSave;
	private StageHistoryManager(){
		stage = new StageHistory[STAGE_MAX];
	}
	
	public void init(Context context){
		for(int i=0; i<STAGE_MAX; i++){
			stage[i] = new StageHistory(i+1, context);
		}
		needSave = false;
		this.context = context;
	}
	
	public static StageHistoryManager getInstance(){
		if(instance == null){
			instance = new StageHistoryManager();
		}
		return instance;
	}
	
	public void playGame(int stageID){
		stage[stageID].incPlay();
		needSave = true;
	}
	
	public void clearGame(int stageID, boolean useGate, int treadEnemy, int star){
		stage[stageID].incClear(useGate);
		stage[stageID].setMaxBeat(treadEnemy);
		stage[stageID].setGottenStars(star);
		needSave = true;
	}
	
	public boolean gameOver(int stageID, int reach){
		needSave = true;
		return stage[stageID].setMaxReach(reach);
	}
	
	public void save(){
		if(!needSave) return;
		needSave = false;
		for(int i=0; i<STAGE_MAX; i++){
			stage[i].saveHistory(context);
		}
	}
	
	public int getMaxReach(int stageID){
		return stage[stageID].getMaxReach();
	}
	
	public int getMaxBeat(int stageID){
		return stage[stageID].getMaxBeat();
	}
	
	public int getStarState(int stageID){
		return stage[stageID].getStarState();
	}
	
	public boolean isCleared(int stageID){
		return stage[stageID].getClearNum()>0;
	}
	
	public boolean isGateLessCleared(int stageID){
		return stage[stageID].gateLessCleared();
	}
}
