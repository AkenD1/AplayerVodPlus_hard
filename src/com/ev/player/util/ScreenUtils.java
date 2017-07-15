package com.ev.player.util;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ScreenUtils {

	public static Integer[] getScreenSize(Activity activity) {
		// ��ȡ��Ļ��ߣ�����1��
		int screenWidth = activity.getWindowManager().getDefaultDisplay()
				.getWidth(); // ��Ļ�����أ��磺480px��
		int screenHeight = activity.getWindowManager().getDefaultDisplay()
				.getHeight(); // ��Ļ�ߣ����أ��磺800p��
		// ��ȡ��Ļ�ܶȣ�����2��
		DisplayMetrics dm = new DisplayMetrics();
		dm = activity.getResources().getDisplayMetrics();
		float density = dm.density; // ��Ļ�ܶȣ����ر�����0.75/1.0/1.5/2.0��
		int densityDPI = dm.densityDpi; // ��Ļ�ܶȣ�ÿ�����أ�120/160/240/320��
		float xdpi = dm.xdpi;
		float ydpi = dm.ydpi;
		screenWidth = dm.widthPixels; // ��Ļ�����أ��磺480px��
		screenHeight = dm.heightPixels; // ��Ļ�ߣ����أ��磺800px��
		// ��ȡ��Ļ�ܶȣ�����3��
//		dm = new DisplayMetrics();
//		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//		density = dm.density; // ��Ļ�ܶȣ����ر�����0.75/1.0/1.5/2.0��
//		densityDPI = dm.densityDpi; // ��Ļ�ܶȣ�ÿ�����أ�120/160/240/320��
//		xdpi = dm.xdpi;
//		ydpi = dm.ydpi;
//		int screenWidthDip = dm.widthPixels; // ��Ļ��dip���磺320dip��
//		int screenHeightDip = dm.heightPixels; // ��Ļ��dip���磺533dip��
//		screenWidth = (int) (dm.widthPixels * density + 0.5f); // ��Ļ��px���磺480px��
//		screenHeight = (int) (dm.heightPixels * density + 0.5f); // ��Ļ�ߣ�px���磺800px��
		return new Integer[] {screenHeight+50, screenWidth};
	}
	
	
	public static Integer[] getScreenSizePX(Activity activity) {
		int screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
		return new Integer[] {screenWidth, screenHeight};
	}
}
