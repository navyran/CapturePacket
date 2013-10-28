/**
 * <p>Title: StreamUtils.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: Alibaba YunOS</p>
 * @author jinlong.rhj
 * @date 2013-5-14
 * @version 1.0
 */
package com.java.util.stream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** 
 * @author 锦龙-冉海均 E-mail:jinlong.rhj@alibaba-inc.com 
 * @version 创建时间：2013-5-14 下午3:39:45
 */

/**
 * @author jinlong.rhj
 * 
 */
public class StreamUtils {

	public static InputStream String2InputStream(String str) {
		ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
		return stream;
	}

	public static String inputStream2String(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line + "\r\n");
		}
		return buffer.toString();
	}

	/**
	 * @param args
	 * @author jinlong.rhj
	 * @date 2013-5-14
	 * @version 1.0
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
