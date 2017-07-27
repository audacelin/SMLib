/*
 * StringUtils.java
 *
 * Created on 2007年5月15日, 下午8:46
 *

 */
package com.smlib.utils;

/**
 * 字符串处理的辅助方法
 * 
 * @author gzit
 */
public class StringUtils {

	// 安全地截断空白符号
	public static String safeTrim(String src) {
		if (src == null)
			return src;
		return src.trim();

	}

	// 如果字符串为null的话则转化为空
	public static String emptyIfNull(String src) {
		return src == null ? "" : src;
	}

	// 当字符串为Null时，设定默认值
	public static String setValueIfNull(String src, String def) {
		return src == null ? def : ("null".equals(src) ? null : src);
	}

	// 判断字符串是否为空
	public static boolean isBlank(StringBuffer value) {

		return isBlank(value == null ? null : value.toString());

	}

	// 判断字符串是否为空
	public static boolean isBlank(String value) {
		if (value == null || value.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isAllNotBlank(String... values) {
		for (String value : values) {
			if (isBlank(value)) {
				return false;
			}

		}

		return true;

	}

	public static boolean isAllNotStrickBlank(String... values) {

		for (String value : values) {
			if (isStickBlank(value)) {
				return false;
			}

		}

		return true;

	}

	// 查找某个字符串在字符串数组中的位置
	public static int findIndex(String[] items, String item) {
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(item)) {
				return i;
			}
		}

		return -1;
	}

	public static boolean isStickBlank(String value) {
		if (isBlank(value) || "null".equals(value)) {
			return true;
		}

		return false;

	}

	public static String emptyIfStrickBlank(String value) {
		if (isStickBlank(value)) {
			return "";
		}

		return value;
	}

	public static String emptyIfBlank(String value) {
		if (isBlank(value))
			return "";

		return value;

	}

}
