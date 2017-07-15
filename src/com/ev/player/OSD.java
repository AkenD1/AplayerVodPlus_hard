package com.ev.player;

public abstract class OSD{
	
	public static final int PROPERITY_LEVEL_1 = 1;
	public static final int PROPERITY_LEVEL_2 = 2;
	public static final int PROPERITY_LEVEL_3 = 3;
	public static final int PROPERITY_LEVEL_4 = 4;
	public static final int PROPERITY_LEVEL_5 = 5;
	public static final int PROPERITY_LEVEL_6 = 6;
	
	/*
	 * OSD��ʾʱ��Ϊ5S������5S OSDδ��������OSD����
	 */
	public static final int OSD_SHOW_TIME = 15;   
	 
	/*
	 * OSD��Ĭ�ϵȼ�Ϊ2
	 */
	private int mProperity = PROPERITY_LEVEL_2;
	/*
	 * �������������ͬȨ�޵ȼ���OSD�Ƿ���Լ��ݣ�����ȼ���ͬ�Ļ�
	 * �򲻼��ݣ����������
	 */
	private int mCompatibility = 0;
	
	public static int COMPATIBILITY_01 = 1;
	public static int COMPATIBILITY_02 = 2;
	public static int COMPATIBILITY_03 = 3;
	
	public void setProperity(int properity){
		mProperity = properity;
	};

	public int getProperity(){
		return mProperity;
	}
	
	public abstract void setVisibility(int visibility);
	
	public void setCompatibility(int compatibility){
		mCompatibility = compatibility;
	}
	
	public int getCompatibility(){
		return mCompatibility;
	}
	
}
