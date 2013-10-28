package com.java.util.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * @author 锦龙-冉海均 E-mail:jinlong.rhj@alibaba-inc.com 
 * @version 创建时间：2013-4-25 下午8:12:42
 */

/**
 * @author jinlong.rhj
 * 
 */
public class StringUtil {

	public static boolean isEmpty(String string) {
		if (string == null || string.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEmpty(String[] strArray) {
		String[] strArray2 = strArray;
		if (strArray == null || strArray2.length <= 0) {
			return true;
		}
		return false;
	}

	public static String getSubStringFromPos(String string, int pos) {
		if (string == null || string.length() <= pos) {
			return null;
		} else {
			return string.substring(pos);
		}
	}

	/**
	 * 检查是否包含中文
	 * 
	 * @param string
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-3-25
	 * @version 1.0
	 */
	public static boolean isChiness(String string) {
		String pattern = "[\u4e00-\u9fa5]+";
		Pattern p = Pattern.compile(pattern);
		Matcher result = p.matcher(string);
		return result.find();
	}

	/**
	 * 按行返回整个字符串的list内容
	 * 
	 * @param content
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-9
	 * @version 1.0
	 */
	public static List<String> getLinesListFromString(String content) {
		String[] lines = content.split("\n");
		List<String> linesList = new ArrayList<String>();
		for (String line : lines) {
			if (line == null || line.trim().length() == 0) {
				continue;
			}
			linesList.add(line);
		}
		return linesList;
	}

	/**
	 * @param args
	 * @author jinlong.rhj
	 * @date 2013-4-25
	 * @version 1.0
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
