package com.ev.player;

import com.ev.player.util.Logger;

import android.content.Context;
import android.media.AudioManager;


public class AudioManage {
	
	private Logger logger = Logger.getLogger();
	private AudioManage(){}
	
	private static class AudioHolder{
		private final static AudioManage INSTANCE = new AudioManage();
	}
	
	public static AudioManage getInstance(){
		return AudioHolder.INSTANCE;
	}
	
	public int getMaxAudio(Context context){
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	public int getCurrentAudio(Context context){
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC); //��ȡ��ǰ����
	}
	
	public static int volume = 1;
	public void lowerVolume(Context context){
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, 0); 
		logger.i("Lower volume:maxVolume=" + getMaxAudio(context) +" currentVolume="+ getCurrentAudio(context));
	}
	
	public void upVolume(Context context){
		AudioManager audioManager  = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, 0);
		logger.i("up volume:maxVolume=" + getMaxAudio(context) +" currentVolume="+ getCurrentAudio(context));
	}
	
	public void setSlience(Context context){
		AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	}
}
