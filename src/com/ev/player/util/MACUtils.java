package com.ev.player.util;

import android.annotation.SuppressLint;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MACUtils {
	public static String getMac2() {
		FileInputStream localFileInputStream;
		String mac = "";
		try {
			localFileInputStream = new FileInputStream("/sys/class/net/eth0/address");
			byte[] arrayOfByte = new byte[17];
			localFileInputStream.read(arrayOfByte, 0, 17);
			mac = new String(arrayOfByte);
			localFileInputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return mac.toLowerCase();
	}

	public static String getMac() {
		FileInputStream localFileInputStream;
		String str = "";
		try {
			localFileInputStream = new FileInputStream("/sys/class/net/eth0/address");
			byte[] arrayOfByte = new byte[17];
			localFileInputStream.read(arrayOfByte, 0, 17);
			str = new String(arrayOfByte);
			localFileInputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
			str = getWifiMac();
		}
		if (str.equals("") || str == "") {
			str = getWifiMac();
		}
		if (str.equals("") || str == "") {
			str = getMacAddrWifi7();
		}

		if (str.contains(":"))
			str = str.replace(":", "").trim();
		if (str.contains("-"))
			str = str.replace("-", "").trim();
		return str.toLowerCase();
	}

	public static String getWifiMac() {
		String str = "";
		String macSerial = "";
		try {
			java.lang.Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
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
				return loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
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

	/**
	 * 4.0一直到6.0，7.0系统都可以获取得到wifiMac地址
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getMacAddrWifi7() {
		try {
			List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface nif : all) {
				if (!nif.getName().equalsIgnoreCase("wlan0"))
					continue;

				byte[] macBytes = nif.getHardwareAddress();
				if (macBytes == null) {
					return "";
				}

				StringBuilder res1 = new StringBuilder();
				for (byte b : macBytes) {
					res1.append(String.format("%02X:", b));
				}

				if (res1.length() > 0) {
					res1.deleteCharAt(res1.length() - 1);
				}
				return res1.toString();
			}
		} catch (Exception ex) {
			return "";
		}
		return "";
	}
}
