/**
 * <p>Title: FileUtil.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: Alibaba YunOS</p>
 * @author jinlong.rhj
 * @date 2013-4-7
 * @version 1.0
 */
package com.android.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.java.util.file.FileUtil;

import android.app.Activity;
import android.os.Environment;

/** 
 * @author 锦龙-冉海均 E-mail:jinlong.rhj@alibaba-inc.com 
 * @version 创建时间：2013-4-7 上午11:03:38
 */

/**
 * @author jinlong.rhj
 * 
 */
public class AndroidFileUtil extends FileUtil {

	/**
	 * 将Assets中初始化的文件复制到其他路径
	 * 
	 * @param mActivity
	 * @param fileName
	 * @param dstPath
	 * @author jinlong.rhj
	 * @date 2013-4-7
	 * @version 1.0
	 */
	public static void copyAssetsFileTo(Activity mActivity, String fileName,
			String dstPath) {

		File f = new File(dstPath);
		// 如 database 目录不存在，新建该目录
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdir();
		}

		try {
			// 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
			InputStream is = mActivity.getBaseContext().getAssets()
					.open(fileName);
			// 输出流
			OutputStream os = new FileOutputStream(dstPath);

			// 文件写入
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}

			// 关闭文件流
			os.flush();
			os.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 将应用的本地包文件复制到SD卡根目录
	 * 
	 * @param pkgName
	 * @return
	 * @throws IOException
	 * @author jinlong.rhj
	 * @date 2013-4-8
	 * @version 1.0
	 */
	public static boolean copyPkgFolderToSD(String pkgName) throws IOException {

		String path = "/data/data/" + pkgName;
		// printLog("包路径：" + path);
		String sdpath = AndroidFileUtil.getSDRootPath() + File.separator
				+ pkgName;
		if (!new File(path).exists()) {
			// printLog(path + "路径不存在");
			return false;
		}
		if (!new File(sdpath).exists()) {
			new File(sdpath).mkdir();
		}
		// printLog("sdpath：" + sdpath);
		FileUtil.copyDir(path, sdpath);

		if (new File(sdpath).listFiles().length > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 获取SD卡根路径
	 * 
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-4-8
	 * @version 1.0
	 */
	public static String getSDRootPath() {
		if (StorageUtils.isSdAvailable()) {
			return Environment.getExternalStorageDirectory().getPath();
		} else {
			return null;
		}
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public static File creatSDFile(String filePath) throws IOException {
		File file = new File(filePath);
		file.createNewFile();
		return file;
	}

	/**
	 * 删除SD卡上的文件
	 * 
	 * @param fileName
	 */
	public static boolean delSDFile(String filePath) {
		File file = new File(filePath);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		file.delete();
		return true;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public static File creatSDDir(String dirPath) {
		File dir = new File(dirPath);
		dir.mkdir();
		return dir;
	}

	/**
	 * 删除SD卡上的目录
	 * 
	 * @param dirName
	 */
	public static boolean delSDDir(String dirPath) {
		File dir = new File(dirPath);
		return delDir(dir);
	}

	/**
	 * 修改SD卡上的文件或目录名
	 * 
	 * @param fileName
	 */
	public static boolean renameSDFile(String oldfilePath, String newfilePath) {
		File oleFile = new File(oldfilePath);
		File newFile = new File(newfilePath);
		return oleFile.renameTo(newFile);
	}

	/**
	 * 拷贝SD卡上的单个文件
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static boolean copySDFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(srcFileName);
		File destFile = new File(destFileName);
		return copyFileTo(srcFile, destFile);
	}

	/**
	 * 拷贝SD卡上指定目录的所有文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public static boolean copySDFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(srcDirName);
		File destDir = new File(destDirName);
		return copyFilesTo(srcDir, destDir);
	}

	/**
	 * 移动SD卡上的单个文件
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public static boolean moveSDFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(srcFileName);
		File destFile = new File(destFileName);
		return moveFileTo(srcFile, destFile);
	}

	/**
	 * 移动SD卡上的指定目录的所有文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public static boolean moveSDFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(srcDirName);
		File destDir = new File(destDirName);
		return moveFilesTo(srcDir, destDir);
	}

	/**
	 * 建立私有文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File creatDataFile(String fileName) throws IOException {
		File file = new File(fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 建立私有目录
	 * 
	 * @param dirName
	 * @return
	 */
	public static File creatDataDir(String dirName) {
		File dir = new File(dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 删除私有文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean delDataFile(String fileName) {
		File file = new File(fileName);
		return delFile(file);
	}

	/**
	 * 删除私有目录
	 * 
	 * @param dirName
	 * @return
	 */
	public static boolean delDataDir(String dirName) {
		File file = new File(dirName);
		return delDir(file);
	}

	/**
	 * 更改私有文件名
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public static boolean renameDataFile(String oldName, String newName) {
		File oldFile = new File(oldName);
		File newFile = new File(newName);
		return oldFile.renameTo(newFile);
	}

	/**
	 * 在私有目录下进行文件复制
	 * 
	 * @param srcFileName
	 *            ： 包含路径及文件名
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public static boolean copyDataFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(srcFileName);
		File destFile = new File(destFileName);
		return copyFileTo(srcFile, destFile);
	}

	/**
	 * 复制私有目录里指定目录的所有文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public static boolean copyDataFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(srcDirName);
		File destDir = new File(destDirName);
		return copyFilesTo(srcDir, destDir);
	}

	/**
	 * 移动私有目录下的单个文件
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public static boolean moveDataFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(srcFileName);
		File destFile = new File(destFileName);
		return moveFileTo(srcFile, destFile);
	}

	/**
	 * 移动私有目录下的指定目录下的所有文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public static boolean moveDataFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(srcDirName);
		File destDir = new File(destDirName);
		return moveFilesTo(srcDir, destDir);
	}

	/**
	 * 删除一个文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean delFile(File file) {
		if (file.isDirectory())
			return false;
		return file.delete();
	}

	/**
	 * 删除一个目录（可以是非空目录）
	 * 
	 * @param dir
	 */
	public static boolean delDir(File dir) {
		if (dir == null || !dir.exists() || dir.isFile()) {
			return false;
		}
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				delDir(file);// 递归
			}
		}
		dir.delete();
		return true;
	}

	/**
	 * 拷贝一个文件,srcFile源文件，destFile目标文件
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static boolean copyFileTo(File srcFile, File destFile)
			throws IOException {
		if (srcFile.isDirectory() || destFile.isDirectory())
			return false;// 判断是否是文件
		FileInputStream fis = new FileInputStream(srcFile);
		FileOutputStream fos = new FileOutputStream(destFile);
		int readLen = 0;
		byte[] buf = new byte[1024];
		while ((readLen = fis.read(buf)) != -1) {
			fos.write(buf, 0, readLen);
		}
		fos.flush();
		fos.close();
		fis.close();
		return true;
	}

	/**
	 * 拷贝目录下的所有文件到指定目录
	 * 
	 * @param srcDir
	 * @param destDir
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFilesTo(File srcDir, File destDir)
			throws IOException {
		if (!srcDir.isDirectory() || !destDir.isDirectory())
			return false;// 判断是否是目录
		if (!destDir.exists())
			return false;// 判断目标目录是否存在
		File[] srcFiles = srcDir.listFiles();
		for (int i = 0; i < srcFiles.length; i++) {
			if (srcFiles[i].isFile()) {
				// 获得目标文件
				File destFile = new File(destDir.getPath() + File.separator
						+ srcFiles[i].getName());
				copyFileTo(srcFiles[i], destFile);
			} else if (srcFiles[i].isDirectory()) {
				File theDestDir = new File(destDir.getPath() + File.separator
						+ srcFiles[i].getName());
				copyFilesTo(srcFiles[i], theDestDir);
			}
		}
		return true;
	}

	/**
	 * 移动一个文件
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 * @throws IOException
	 */
	public static boolean moveFileTo(File srcFile, File destFile)
			throws IOException {
		boolean iscopy = copyFileTo(srcFile, destFile);
		if (!iscopy)
			return false;
		delFile(srcFile);
		return true;
	}

	/**
	 * 移动目录下的所有文件到指定目录
	 * 
	 * @param srcDir
	 * @param destDir
	 * @return
	 * @throws IOException
	 */
	public static boolean moveFilesTo(File srcDir, File destDir)
			throws IOException {
		if (!srcDir.isDirectory() || !destDir.isDirectory()) {
			return false;
		}
		File[] srcDirFiles = srcDir.listFiles();
		for (int i = 0; i < srcDirFiles.length; i++) {
			if (srcDirFiles[i].isFile()) {
				File oneDestFile = new File(destDir.getPath() + File.separator
						+ srcDirFiles[i].getName());
				moveFileTo(srcDirFiles[i], oneDestFile);
				delFile(srcDirFiles[i]);
			} else if (srcDirFiles[i].isDirectory()) {
				File oneDestFile = new File(destDir.getPath() + File.separator
						+ srcDirFiles[i].getName());
				moveFilesTo(srcDirFiles[i], oneDestFile);
				delDir(srcDirFiles[i]);
			}

		}
		return true;
	}

}
