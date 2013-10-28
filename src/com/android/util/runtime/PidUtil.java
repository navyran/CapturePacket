/**
 * <p>Title: ProcessUtils.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: Alibaba YunOS</p>
 * @author jinlong.rhj
 * @date 2013-6-1
 * @version 1.0
 */
package com.android.util.runtime;

import java.io.IOException;
import java.util.List;

import com.java.util.cmd.Cmd;
import com.java.util.text.StringUtil;

/** 
 * @author 锦龙-冉海均 E-mail:jinlong.rhj@alibaba-inc.com 
 * @version 创建时间：2013-6-1 上午11:51:06
 */

/**
 * @author jinlong.rhj
 * 
 */
public class PidUtil {

	/**
	 * 获取在Android 中运行PS命令的返回字符串
	 * 
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-17
	 * @version 1.0
	 */
	public static String getPsString() {
		String output = null;
		try {
			Cmd cmd = new Cmd("ps");
			output = cmd.getOutputString(2000);
			// System.out.println(output);
			cmd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * 从Android PS命令返回的字符串，获取指定进程的PID，只获取第一个
	 * 
	 * @param psString
	 * @param processName
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-17
	 * @version 1.0
	 */
	public static int getPidFromPs(String psString, String processName) {
		String[] subString = null;
		List<String> list = StringUtil.getLinesListFromString(psString);
		for (String str : list) {
			if (str.indexOf(processName) >= 0) {
				subString = str.split(" ");
				// System.out.println(str);
			}
		}

		if (!StringUtil.isEmpty(subString)) {
			int i = 0;
			for (String sub : subString) {
				i++;
				if (i < 2) {
					continue;
				}
				if (!StringUtil.isEmpty(sub)) {
					return Integer.parseInt(sub);
				}
			}
		}
		return 0;

	}

	/**
	 * 根据进程PID杀死android进程
	 * 
	 * @param pid
	 * @author jinlong.rhj
	 * @date 2013-5-17
	 * @version 1.0
	 */
	public static void killProcessByPid(int pid) {
		try {
			Cmd cmd = new Cmd();
			cmd.exec("su");
			cmd.execWriteBytes("kill " + pid);
			System.out.println(cmd.getOutputString());
			cmd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void killProcessByName(String processName) {
		int pid = getPidFromPs(getPsString(), processName);
		killProcessByPid(pid);

	}
}
