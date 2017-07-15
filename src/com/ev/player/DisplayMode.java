package com.ev.player;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.aplayer.vod.plus.R;

public class DisplayMode {

	public static final String SCREEN_4_3 = "4 : 3";
	public static final String SCREEN_16_9 = "16 : 9"; 
	public static final int SCREEN_FULL = R.string.full_screen;
	
	public static final int SCREEN_4_3_MODE = 0;
	public static final int SCREEN_16_9_MODE = 1;
	public static final int SCREEN_FULL_MODE = 2;
	
	public static List<DisplayModeEntity> getDisplayMode(Context context){
		List<DisplayModeEntity> listDiplayMode = new ArrayList<DisplayModeEntity>();
		listDiplayMode.add(new DisplayModeEntity(SCREEN_4_3,SCREEN_4_3_MODE));
		listDiplayMode.add(new DisplayModeEntity(SCREEN_16_9,SCREEN_16_9_MODE));
		String fullScreen = context.getResources().getString(SCREEN_FULL);
		listDiplayMode.add(new DisplayModeEntity(fullScreen,SCREEN_FULL_MODE));
		return listDiplayMode;  
	}
	
	
	public static final String VOD_HISTORY_SHARE = "vod_play_mode";
	public static final String MODE = "mode";
	public static void setMode(Context context,int mode){
		SharedPreferences sharedPreferences = context.getSharedPreferences(VOD_HISTORY_SHARE, Context.MODE_PRIVATE);
		Editor delEditor = sharedPreferences.edit();
		delEditor.remove(MODE);
		delEditor.commit();
		Editor addEditor = sharedPreferences.edit();
		addEditor.putInt(MODE, mode);
		addEditor.commit();
	}
	
	public static Integer getMode(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(VOD_HISTORY_SHARE, Context.MODE_PRIVATE);
		int value = sharedPreferences.getInt(MODE, SCREEN_FULL_MODE);
		return value;
	}
	
	
	public static class DisplayModeEntity{
		
		String modeString;
		int mode;
		
		public DisplayModeEntity(String modeName,int mode){
			modeString = modeName;
			this.mode = mode ;
		}
		
		public String getModeString() {
			return modeString;
		}
		public void setModeString(String modeString) {
			this.modeString = modeString;
		}
		public int getMode() {
			return mode;
		}
		public void setMode(int mode) {
			this.mode = mode;
		}
		
	}
	
	
}
