package com.ev.player;

import java.util.HashMap;
import java.util.Map;

import android.view.View;


public class OSDManager {
	
	/* 
	 *  properity  compatibility				
	 *  osdcache :    3              0 
	 *  osdcontrolbar 3              1
	 *  osdloading :  6              
	 *  osdpause :    3  
	 *  osdvolume :   3              1
	 *  
	 */
	
	public static final String OSD_CONTROLBAR = "com.moon.osd.controlbar";
	public static final String OSD_PAUSE = "com.moon.osd.pause";
	public static final String OSD_VOLUME = "com.moon.osd.volume";
	public static final String OSD_LOADING = "com.moon.osd.loading";
	public static final String OSD_CACHE = "com.moon.osd.caching";
	public static final String OSD_AD = "com.moon.osd.ad";
	private static Map<String,OSD> OSDList = new HashMap<String,OSD>();
	
	public OSD getOSD(String osdTag){
		return OSDList.get(osdTag);
	}
	
	public void addOSD(String osdTags,OSD osd){
		OSDList.put(osdTags,osd);
	}
	
	public void deleteOSD(String osdTags){
		OSDList.remove(osdTags);
	}
	
	public void closeOSD(OSD osd){
		osd.setVisibility(View.GONE);
	}
	
	 public void closeAllOSD(){
		for(String key : OSDList.keySet())
		{
			OSD osd = OSDList.get(key);
			osd.setVisibility(View.GONE);
		}
	}
	
	public void showOSD(OSD osd){
		for(String key : OSDList.keySet()){
			OSD osdTemp = OSDList.get(key);
			if(osdTemp == osd) continue;
			int showOsdProperity = osd.getProperity();
			int tempOsdProperity = osdTemp.getProperity();
			OSD osdClose = showOsdProperity >= tempOsdProperity ? osdTemp : osd;
			//�ж��Ƿ����
			if((osd.getCompatibility() == osdTemp.getCompatibility() && showOsdProperity == tempOsdProperity)
				 || showOsdProperity > tempOsdProperity)
				osdClose.setVisibility(View.GONE);
			
		}
		osd.setVisibility(View.VISIBLE);
	}
}
