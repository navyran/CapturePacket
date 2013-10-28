/**
 * <p>Title: StorageUtils.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: Alibaba YunOS</p>
 * @author jinlong.rhj
 * @date 2013-5-27
 * @version 1.0
 */
package com.android.util.file;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.android.util.log.LogUtil;
import com.java.util.cmd.Cmd;
import com.java.util.text.StringUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

/** 
 * @author 锦龙-冉海均 E-mail:jinlong.rhj@alibaba-inc.com 
 * @version 创建时间：2013-5-27 下午1:54:43
 */

/**
 * @author jinlong.rhj
 * 
 */
public class StorageUtils {

	/**
	 * 检查SD卡是否可用。
	 * 
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-27
	 * @version 1.0
	 */
	public static boolean isSdAvailable() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 获取SD卡剩余空间，返回剩余容量，以Byte为单位。
	 * 
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-27
	 * @version 1.0
	 */
	public static long getSDAvailableSpaceSize() {
		String sdcard = Environment.getExternalStorageDirectory().getPath();
		long availableSpare = getAvailableBlocks(sdcard) * getBlockSize(sdcard);
		return availableSpare;
	}

	public static long getSDTotalSpaceSize() {
		String path = Environment.getExternalStorageDirectory().getPath();
		return getBlockCount(path) * getBlockSize(path);
	}

	public static long getCountSpaceSize(String path) {
		return getBlockCount(path) * getBlockSize(path);
	}

	public static long getAvailableSpaceSize(String path) {
		return getAvailableBlocks(path) * getBlockSize(path);
	}

	public static float toMbSize(long size) {
		return (float) size / (1024f * 1024f);
	}

	/**
	 * 将字节转换为显示大小
	 * 
	 * @param paramLong
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-28
	 * @version 1.0
	 */
	public static String byteToDisplaySize(long paramLong) {
		String str;
		DecimalFormat df = new DecimalFormat("0.00");// 以Mb为单位保留两位小数
		if (paramLong / 1073741824L > 0L)
			str = df.format(paramLong / 1073741824D) + "GB";

		else if (paramLong / 1048576L > 0L)
			str = df.format(paramLong / 1048576D) + "MB";
		else if (paramLong / 1024L > 0L)
			str = df.format(paramLong / 1024D) + "KB";
		else
			str = String.valueOf(paramLong) + "bytes";

		return str;
	}

	/**
	 * 获取系统总剩余存储空间，以Byte为单位。
	 * 
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-27
	 * @version 1.0
	 */
	public static long getSystemAvailableSpaceSize() {
		String path = Environment.getRootDirectory().getPath();
		return getAvailableBlocks(path) * getBlockSize(path);
	}

	/**
	 * 获取手机内部剩余存储空间
	 * 
	 * @return
	 */
	public static long getInternalAvailableSpaceSize() {
		String path = Environment.getDataDirectory().getPath();
		return getAvailableBlocks(path) * getBlockSize(path);
	}

	/**
	 * 获取手机内部总的存储空间
	 * 
	 * @return
	 */
	public static long getInternalTotalSpaceSize() {
		String path = Environment.getDataDirectory().getPath();
		return getBlockCount(path) * getBlockSize(path);
	}

	private static StatFs getStatFs(String path) {
		if (new File(path).exists()) {
			return new StatFs(path);
		} else {
			return null;
		}
	}

	protected static long getBlockSize(String path) {
		StatFs stat = getStatFs(path);
		return stat.getBlockSize();
	}

	protected static long getBlockCount(String path) {
		StatFs stat = getStatFs(path);
		return stat.getBlockCount();
	}

	protected static long getAvailableBlocks(String path) {
		StatFs stat = getStatFs(path);
		return stat.getAvailableBlocks();
	}

	/**
	 * 检查外置SD卡是否作为默认存储位置
	 * 
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-27
	 * @version 1.0
	 */
	public static boolean isDefaultSaveToSD() {
		if (AndroidFileUtil.getSDRootPath().equalsIgnoreCase("/sdcard")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取挂载的磁盘路径数组，4.0以后的系统可用
	 * 
	 * @param mActivity
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-28
	 * @version 1.0
	 */
	public static String[] getStoragePathList(Activity mActivity) {
		Method mMethodGetPaths = null;
		String[] storagePathList = null;
		StorageManager mStorageManager = (StorageManager) mActivity
				.getSystemService(Activity.STORAGE_SERVICE);

		try {
			mMethodGetPaths = mStorageManager.getClass().getMethod(
					"getVolumePaths");
			if (mMethodGetPaths == null) {
				return null;
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		try {
			storagePathList = (String[]) mMethodGetPaths
					.invoke(mStorageManager);
			// for (String string : storagePathList) {
			// Log.i("11", string);
			// }

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// if (storagePathList != null) {
		// Log.d(TAG, "StorgaeList size: " + storagePathList.length);
		// if (storagePathList.length >= 2) {
		// mSDCardPath = storagePathList[0];
		// mSDCard2Path = storagePathList[1];
		// } else if (storagePathList.length == 1){
		// mSDCardPath = storagePathList[0];
		// }
		// }
		return storagePathList;
	}

	/**
	 * 获取已挂载的磁盘路径
	 * 
	 * @return
	 */
	public static List<String> getSdPathList() {
		Cmd cmd = new Cmd();
		String output = null;
		try {
			cmd.exec("su");
			cmd.exec("mount");

			output = cmd.getOutputString();
			cmd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// LogUtil.d(output);

		List<String> list = StringUtil.getLinesListFromString(output);

		HashSet<String> sdLineList = new HashSet<String>();
		List<String> sdPathList = new ArrayList<String>();

		for (String line : list) {
			if (line.toLowerCase().contains("secure"))
				continue;
			if (line.toLowerCase().contains("asec"))
				continue;
			if (line.toLowerCase().contains("legacy"))
				continue;
			if (line.toLowerCase().contains("obb"))
				continue;
			if (line.toLowerCase().contains("vold")
					|| line.toLowerCase().contains("fuse")) {
				if (line.toLowerCase().contains("sd")) {
					sdLineList.add(line);
				}
				if (line.toLowerCase().contains("storage")) {
					sdLineList.add(line);
				}

			}
		}

		for (String line : sdLineList) {

			String[] field = line.split(" ");
			File f = new File(field[1]);

			if (f.exists() && f.isDirectory()) {
				sdPathList.add(f.getPath());
			}

		}

		return sdPathList;
	}

	/**
	 * @param args
	 * @author jinlong.rhj
	 * @date 2013-5-27
	 * @version 1.0
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
