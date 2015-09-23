package com.threepipes.runaction.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.SparseArray;

public class ButtonManager {
	SparseArray<MyButton> buttonMap;
	public ButtonManager() {
		buttonMap = new SparseArray<MyButton>();
	}
	
	public void put(int key, MyButton btn){
		buttonMap.append(key, btn);
	}
	
	public MyButton get(int key){
		return buttonMap.get(key);
	}
	
	public void draw(Canvas c, Paint p){
		for(int i=0; i<buttonMap.size(); i++){
			buttonMap.valueAt(i).draw(c, p);
		}
	}
	
	public int touchEvent(int x, int y, int event){
		for(int i=0; i<buttonMap.size(); i++){
			if(buttonMap.valueAt(i).touchEvent(x, y, event))
				return buttonMap.keyAt(i);
		}
		return -1;
	}
}
