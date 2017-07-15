package com.ev.player.util;

public class StringUtil {

	/**
	 * is null or is ""
	 * @param parameter
	 * @return
	 */
	public static boolean isBlank(String parameter){
		return (null == parameter) || ("".equals(parameter.trim()));
	}
}
