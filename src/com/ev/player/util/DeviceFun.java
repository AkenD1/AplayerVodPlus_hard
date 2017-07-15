package com.ev.player.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.ev.player.Configs;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceFun {
	private static final String TELEPHONY_SERVICE = null;

	public static String GetFileCpu() {
		String cpuid="";
		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			String text = "";
			while ((text = br.readLine()) != null) {
				Log.d("CPU INFO", text);
				if (text.contains("Serial")) {
					int index = text.indexOf(":");
					Log.d("CPU ID", text.substring(index + 2));
					cpuid = text.substring(index + 2);
				}
			}
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			cpuid=GetCpuByfileNull();
		} catch (IOException e) {
//			e.printStackTrace();
			cpuid=GetCpuByfileNull();
		}
		if(cpuid.equals("") || cpuid.equals("")){
			cpuid=GetCpuByfileNull();
		}
		if(Configs.debug){
			return "12345678910";
		}else{
			return cpuid;
		}
	}
	public static String GetCpuByfileNull(){
		String cpuid="";
		String m_szDevIDShort = "";
		try {
			m_szDevIDShort = "35"
					+ // we make this look like a valid IMEI
  
					Build.BOARD.length() % 10
					+ Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
					+ Build.ID.length() % 10 
					+ Build.USER.length() % 10; // 13 digits
		} catch (Exception e) {
			// TODO: handle exception
			
			
		}
		String m_szLongID=m_szDevIDShort+MACUtils.getMac();
		 MessageDigest m = null;   
		try {
		 m = MessageDigest.getInstance("MD5");
		 } catch (NoSuchAlgorithmException e) {
		 e.printStackTrace();   
		}    
		m.update(m_szLongID.getBytes(),0,m_szLongID.length());   
		// get md5 bytes   
		byte p_md5Data[] = m.digest();   
		// create a hex string   
		String m_szUniqueID = new String();   
		for (int i=0;i<p_md5Data.length;i++) {   
		     int b =  (0xFF & p_md5Data[i]);    
		// if it is a single digit, make sure it have 0 in front (proper padding)    
		    if (b <= 0xF) 
		        m_szUniqueID+="0";    
		// add number to string    
		    m_szUniqueID+=Integer.toHexString(b); 
		   }   // hex string to uppercase   
		m_szUniqueID = m_szUniqueID.toUpperCase();
		cpuid=m_szUniqueID;
		return cpuid;
	}
	@SuppressLint("NewApi")
	public static String GetCpuId(Context context) {
		String m_szImei = "";
		try {
			TelephonyManager TelephonyMgr = (TelephonyManager) context
					.getSystemService(TELEPHONY_SERVICE);
			m_szImei = TelephonyMgr.getDeviceId();
		} catch (Exception e) {
			// TODO: handle exception
		}
		String m_szDevIDShort = "";
		try {
			m_szDevIDShort = "35"
					+ // we make this look like a valid IMEI
  
					Build.BOARD.length() % 10
					+ Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
					+ Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
					+ Build.PRODUCT.length() % 10
					+ Build.TYPE.length() % 10
					+ Build.USER.length() % 10; // 13 digits
		} catch (Exception e) {
			// TODO: handle exception
		}
	
		
		
		String m_szAndroidID="";
		try {
			m_szAndroidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		} catch (Exception e) {
			// TODO: handle exception
		}
		String m_szWLANMAC="";
		try {
		//	WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
			 m_szWLANMAC = getWifiMac();
		} catch (Exception e) {
			// TODO: handle exception
		}

		String m_szBTMAC="";
		try {
			BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
			m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();      
			 m_szBTMAC = m_BluetoothAdapter.getAddress();
		} catch (Exception e) {
			// TODO: handle exception
		}
//		String m_szLongID = m_szImei + m_szDevIDShort 
//			    + m_szAndroidID+ m_szWLANMAC + m_szBTMAC;      
			// compute md5   
		
		String m_szLongID=m_szDevIDShort+MACUtils.getMac();
			 MessageDigest m = null;   
			try {
			 m = MessageDigest.getInstance("MD5");
			 } catch (NoSuchAlgorithmException e) {
			 e.printStackTrace();   
			}    
			m.update(m_szLongID.getBytes(),0,m_szLongID.length());   
			// get md5 bytes   
			byte p_md5Data[] = m.digest();   
			// create a hex string   
			String m_szUniqueID = new String();   
			for (int i=0;i<p_md5Data.length;i++) {   
			     int b =  (0xFF & p_md5Data[i]);    
			// if it is a single digit, make sure it have 0 in front (proper padding)    
			    if (b <= 0xF) 
			        m_szUniqueID+="0";    
			// add number to string    
			    m_szUniqueID+=Integer.toHexString(b); 
			   }   // hex string to uppercase   
			m_szUniqueID = m_szUniqueID.toUpperCase();
			if(Configs.debug){
				return "12345678910";
			}else{
				return m_szUniqueID;
			}
	
		//Log.d(tag, "----:" + m_szUniqueID+"");
	}
	@SuppressLint("NewApi")
	public static String GetCpuIdbak(Context context) {
		String m_szImei = "";
		try {
			TelephonyManager TelephonyMgr = (TelephonyManager) context
					.getSystemService(TELEPHONY_SERVICE);
			m_szImei = TelephonyMgr.getDeviceId();
		} catch (Exception e) {
			// TODO: handle exception
		}
		String m_szDevIDShort = "";
		try {
			m_szDevIDShort = "35"
					+ // we make this look like a valid IMEI

					Build.BOARD.length() % 10 + Build.BRAND.length() % 10
					+ Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
					+ Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
					+ Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
					+ Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
					+ Build.TAGS.length() % 10 + Build.TYPE.length() % 10
					+ Build.USER.length() % 10; // 13 digits
		} catch (Exception e) {
			// TODO: handle exception
		}
		String m_szAndroidID="";
		try {
			m_szAndroidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		} catch (Exception e) {
			// TODO: handle exception
		}
		String m_szWLANMAC="";
		try {
		//	WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
			 m_szWLANMAC = getWifiMac();
		} catch (Exception e) {
			// TODO: handle exception
		}

		String m_szBTMAC="";
		try {
			BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
			m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();      
			 m_szBTMAC = m_BluetoothAdapter.getAddress();
		} catch (Exception e) {
			// TODO: handle exception
		}
		String m_szLongID = m_szImei + m_szDevIDShort 
			    + m_szAndroidID+ m_szWLANMAC + m_szBTMAC;      
			// compute md5     
			 MessageDigest m = null;   
			try {
			 m = MessageDigest.getInstance("MD5");
			 } catch (NoSuchAlgorithmException e) {
			 e.printStackTrace();   
			}    
			m.update(m_szLongID.getBytes(),0,m_szLongID.length());   
			// get md5 bytes   
			byte p_md5Data[] = m.digest();   
			// create a hex string   
			String m_szUniqueID = new String();   
			for (int i=0;i<p_md5Data.length;i++) {   
			     int b =  (0xFF & p_md5Data[i]);    
			// if it is a single digit, make sure it have 0 in front (proper padding)    
			    if (b <= 0xF) 
			        m_szUniqueID+="0";    
			// add number to string    
			    m_szUniqueID+=Integer.toHexString(b); 
			   }   // hex string to uppercase   
			m_szUniqueID = m_szUniqueID.toUpperCase();
		    return m_szUniqueID;
	
		//Log.d(tag, "----:" + m_szUniqueID+"");
	}
	public static String getWifiMac(){
        String str="";
        String macSerial="";
        try {
            java.lang.Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return macSerial;
    }
    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }
    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

}
