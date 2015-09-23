package com.threepipes.runaction;

public class Setting {
	private static Setting singleton = new Setting();
	
	private int value;

	public static final int SET_VOLUME_OFF = 1;
	
	private Setting(){
		value = 0;
	}
	
	public static Setting getInstance(){
		if(singleton == null){
			singleton = new Setting();
		}
		return singleton;
	}
	
	public void setSettingValue(int key, boolean value){
		if(key == SET_VOLUME_OFF){
			MusicManager.getInstance().setMusicSetting(value);
		}
		
		if(value) this.value |= key;
		else this.value &= ~key;
	}
	
	public boolean getSettingValue(int key){
		return (value & key) > 0;
	}
	
	public int getAllValue(){
		return value;
	}
}
