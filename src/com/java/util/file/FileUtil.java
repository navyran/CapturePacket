package com.java.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.java.util.text.StringUtil;

//import org.apache.log4j.Logger;

public class FileUtil {

	// protected static Logger log = Logger.getLogger(FileUtils.class);

	/**
	 * The buffer.
	 */
	protected static byte buf[] = new byte[1024];
	private static List<String> filesList = new ArrayList<String>();

	/**
	 * Read content from local file. FIXME How to judge UTF-8 and GBK, the
	 * correct code should be: FileReader fr = new FileReader(new
	 * InputStreamReader(fileName, "ENCODING")); Might let the user select the
	 * encoding would be a better idea. While reading UTF-8 files, the content
	 * is bad when saved out.
	 * 
	 * @param fileName
	 *            - local file name to read
	 * @return
	 * @throws Exception
	 */
	public static String readFileAsString(String fileName) throws Exception {
		String content = new String(readFileBinary(fileName));
		return content;
	}

	/**
	 * 读取文件并返回为给定字符集的字符串.
	 * 
	 * @param fileName
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String readFileAsString(String fileName, String encoding)
			throws Exception {
		String content = new String(readFileBinary(fileName), encoding);
		return content;
	}

	/**
	 * 读取文件并返回为给定字符集的字符串.
	 * 
	 * @param fileName
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String readFileAsString(InputStream in) throws Exception {
		String content = new String(readFileBinary(in));
		return content;
	}

	public static InputStream readFileAsInputStream(String fileName)
			throws FileNotFoundException {
		InputStream is = new FileInputStream(fileName);
		return is;

	}

	/**
	 * Read content from local file to binary byte array.
	 * 
	 * @param fileName
	 *            - local file name to read
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFileBinary(String fileName) throws Exception {
		FileInputStream fin = new FileInputStream(fileName);
		return readFileBinary(fin);
	}

	/**
	 * 从输入流读取数据为二进制字节数组.
	 * 
	 * @param streamIn
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileBinary(InputStream streamIn)
			throws IOException {
		BufferedInputStream in = new BufferedInputStream(streamIn);
		ByteArrayOutputStream out = new ByteArrayOutputStream(10240);
		int len;
		while ((len = in.read(buf)) >= 0)
			out.write(buf, 0, len);
		in.close();
		return out.toByteArray();
	}

	/**
	 * Write string content to local file.
	 * 
	 * @param fileName
	 *            - local file name will write to
	 * @param content
	 *            String text
	 * @return true if success
	 * @throws IOException
	 */
	public static boolean writeFileString(String fileName, String content)
			throws IOException {
		FileWriter fout = new FileWriter(fileName);
		fout.write(content);
		fout.close();
		return true;
	}

	/**
	 * Write string content to local file using given character encoding.
	 * 
	 * @param fileName
	 *            - local file name will write to
	 * @param content
	 *            String text
	 * @param encoding
	 *            the encoding
	 * @return true if success
	 * @throws IOException
	 */
	public static boolean writeFileString(String fileName, String content,
			String encoding) throws IOException {
		OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(
				fileName), encoding);

		fout.write(content);
		fout.close();
		return true;
	}

	/**
	 * Write binary byte array to local file.
	 * 
	 * @param fileName
	 *            - local file name will write to
	 * @param content
	 *            binary byte array
	 * @return true if success
	 * @throws IOException
	 */
	public static boolean writeFileBinary(String fileName, byte[] content)
			throws IOException {
		FileOutputStream fout = new FileOutputStream(fileName);
		fout.write(content);
		fout.close();
		return true;
	}

	/**
	 * 追加写入流
	 * 
	 * @param fileName
	 * @param is
	 * @return
	 * @throws IOException
	 * @author jinlong.rhj
	 * @date 2013-5-7
	 * @version 1.0
	 */
	public static boolean appendFileInputStream(String fileName, InputStream is)
			throws IOException {
		return writeInputStream(fileName, is, true);
	}

	/**
	 * 写入inputStream流
	 * 
	 * @param fileName
	 * @param is
	 * @return
	 * @throws IOException
	 * @author jinlong.rhj
	 * @date 2013-5-7
	 * @version 1.0
	 */
	public static boolean writeFileInputStream(String fileName, InputStream is)
			throws IOException {
		return writeInputStream(fileName, is, false);
	}

	/**
	 * @param fileName
	 * @param is
	 * @param append
	 * @return
	 * @throws IOException
	 * @author jinlong.rhj
	 * @date 2013-5-7
	 * @version 1.0
	 */
	private static boolean writeInputStream(String fileName, InputStream is,
			boolean append) throws IOException {
		FileOutputStream fos = null;
		// 将输入流is写入文件输出流fos中
		int ch = 0;
		try {
			fos = new FileOutputStream(fileName, append);
			while ((ch = is.read()) != -1) {
				fos.write(ch);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			// 关闭输入流等（略）
			fos.close();
			is.close();

		}
		return true;
	}

	/**
	 * 检查文件名是否合法.文件名字不能包含字符\/:*?"<>|
	 * 
	 * @param fileName文件名
	 *            ,不包含路径
	 * @return boolean is valid file name
	 */
	public static boolean isValidFileName(String fileName) {
		boolean isValid = true;
		String errChar = "\\/:*?\"<>|"; //
		if (fileName == null || fileName.length() == 0) {
			isValid = false;
		} else {
			for (int i = 0; i < errChar.length(); i++) {
				if (fileName.indexOf(errChar.charAt(i)) != -1) {
					isValid = false;
					break;
				}
			}
		}
		return isValid;
	}

	/**
	 * 把非法文件名转换为合法文件名.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String replaceInvalidFileChars(String fileName) {
		StringBuffer out = new StringBuffer();

		for (int i = 0; i < fileName.length(); i++) {
			char ch = fileName.charAt(i);
			// Replace invlid chars: \\/:*?\"<>|
			switch (ch) {
			case '\\':
			case '/':
			case ':':
			case '*':
			case '?':
			case '\"':
			case '<':
			case '>':
			case '|':
				out.append('_');
				break;
			default:
				out.append(ch);
			}
		}

		return out.toString();
	}

	/**
	 * Convert a given file name to a URL(URI) string.
	 * 
	 * @param fileName
	 *            - the file to parse
	 * @return - URL string
	 */
	public static String filePathToURL(String fileName) {
		String fileUrl = new File(fileName).toURI().toString();
		return fileUrl;
	}

	/**
	 * Write string content to local file. - 追加写入文件 ，根据编码
	 * 
	 * @param fileName
	 *            - local file name will write to
	 * @param content
	 *            String text
	 * @return true if success
	 * @throws IOException
	 */
	public static boolean appendFileString(String fileName, String content,
			String encode) throws IOException {
		OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(
				fileName, true), encode);
		fout.write(content);
		fout.close();
		return true;
	}

	/**
	 * 末尾追加文件
	 * 
	 * @param fileName
	 * @param content
	 * @throws IOException
	 * @author jinlong.rhj
	 * @date 2013-5-3
	 * @version 1.0
	 */
	public static void appendRandomAccessFileString(String fileName,
			String content) throws IOException {

		// 打开一个随机访问文件流，按读写方式
		RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
		// 文件长度，字节数
		long fileLength = randomFile.length();
		// 将写文件指针移到文件尾。
		randomFile.seek(fileLength);
		randomFile.writeBytes(content);
		randomFile.close();
	}

	/**
	 * 追加形式写文件
	 * 
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	public static void appendFileString(String fileName, String content)
			throws IOException {
		// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
		FileWriter fr = new FileWriter(fileName, true);
		fr.write(content);
		fr.close();
	}

	/**
	 * 
	 * 压缩文件
	 * 
	 * @param inputFileName
	 *            要压缩的文件或文件夹路径，例如：c:\\a.txt,c:\\a\
	 * @param outputFileName
	 *            输出zip文件的路径，例如：c:\\a.zip
	 */

	public static void zip(String inputFileName, String outputFileName)
			throws Exception {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				outputFileName));
		zip(out, new File(inputFileName), "");
		// log.debug("压缩完成！");
		out.closeEntry();
		out.close();
	}

	/**
	 * 
	 * 压缩文件
	 * 
	 * @param out
	 *            org.apache.tools.zip.ZipOutputStream
	 * 
	 * @param file
	 *            待压缩的文件
	 * 
	 * @param base
	 *            压缩的根目录
	 */

	private static void zip(ZipOutputStream out, File file, String base)
			throws Exception {
		if (file.isDirectory()) {
			File[] fl = file.listFiles();
			base = base.length() == 0 ? "" : base + File.separator;
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
		} else {
			out.putNextEntry(new ZipEntry(base));
			// log.debug("添加压缩文件：" + base);
			FileInputStream in = new FileInputStream(file);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
		}
	}

	/**
	 * 
	 * 解压zip文件
	 * 
	 * @param zipFileName
	 *            待解压的zip文件路径，例如：c:\\a.zip
	 * @param outputDirectory
	 *            解压目标文件夹,例如：c:\\a\
	 */

	public static void unZip(String zipFileName, String outputDirectory)
			throws Exception {
		ZipFile zipFile = new ZipFile(zipFileName);
		try {
			Enumeration<?> e = zipFile.entries();
			ZipEntry zipEntry = null;
			createDirectory(outputDirectory, "");
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				// log.debug("解压：" + zipEntry.getName());
				if (zipEntry.isDirectory()) {
					String name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					f.mkdir();
					// log.debug("创建目录：" + outputDirectory + File.separator +
					// name);
				} else {
					String fileName = zipEntry.getName();
					fileName = fileName.replace('\\', '/');
					if (fileName.indexOf("/") != -1) {
						createDirectory(outputDirectory, fileName.substring(0,
								fileName.lastIndexOf("/")));
						fileName = fileName.substring(
								fileName.lastIndexOf("/") + 1,
								fileName.length());
					}
					File f = new File(outputDirectory + File.separator
							+ zipEntry.getName());
					f.createNewFile();
					InputStream in = zipFile.getInputStream(zipEntry);
					FileOutputStream out = new FileOutputStream(f);
					byte[] by = new byte[1024];
					int c;
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					in.close();
					out.close();
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			zipFile.close();
			// log.debug("解压完成！");
		}
	}

	/**
	 * 创建目录，带子目录
	 * 
	 * @param directory
	 * @param subDirectory
	 */
	private static void createDirectory(String directory, String subDirectory) {
		String dir[];
		File fl = new File(directory);
		try {
			if (subDirectory == "" && fl.exists() != true) {
				fl.mkdir();
			} else if (subDirectory != "") {
				dir = subDirectory.replace('\\', '/').split("/");
				for (int i = 0; i < dir.length; i++) {
					File subFile = new File(directory + File.separator + dir[i]);
					if (subFile.exists() == false)
						subFile.mkdir();
					directory += File.separator + dir[i];
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * 
	 * 递归拷贝文件夹中的所有文件到另外一个文件夹
	 * 
	 * @param srcDirector
	 *            源文件夹
	 * 
	 * @param desDirector
	 *            目标文件夹
	 */

	public static void copyDir(String srcDirector, String desDirector)
			throws IOException {
		(new File(desDirector)).mkdirs();
		File[] file = (new File(srcDirector)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// log.debug("拷贝：" + file[i].getAbsolutePath() + "-->"
				// + desDirector + "/" + file[i].getName());
				FileInputStream input = new FileInputStream(file[i]);
				FileOutputStream output = new FileOutputStream(desDirector
						+ File.separator + file[i].getName());
				byte[] b = new byte[1024 * 5];
				int len;
				while ((len = input.read(b)) != -1) {
					output.write(b, 0, len);
				}
				output.flush();
				output.close();
				input.close();
			}

			if (file[i].isDirectory()) {
				// log.debug("拷贝：" + file[i].getAbsolutePath() + "-->"
				// + desDirector + "/" + file[i].getName());
				copyDir(srcDirector + File.separator + file[i].getName(),
						desDirector + File.separator + file[i].getName());
			}
		}
	}

	/**
	 * 只删除指定文件
	 * 
	 * @param filePath
	 * @author jinlong.rhj
	 * @date 2013-4-2
	 * @version 1.0
	 */
	public static boolean delFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return false;
		}
		if (!file.isFile()) {
			return false;
		}

		file.delete();

		if (!file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 删除文件夹
	 * 
	 * @param folderPath
	 *            folderPath 文件夹完整绝对路径
	 */

	public static void delDir(String folderPath) throws Exception {
		// 删除完里面所有内容
		delAllFile(folderPath);
		String filePath = folderPath;
		filePath = filePath.toString();
		File myFilePath = new File(filePath);
		// 删除空文件夹
		myFilePath.delete();
	}

	/**
	 * 
	 * 删除指定文件夹下所有文件
	 * 
	 * @param path
	 *            文件夹完整绝对路径
	 */

	public static boolean delAllFile(String path) throws Exception {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				// 先删除文件夹里面的文件
				delAllFile(path + "/" + tempList[i]);
				// 再删除空文件夹
				delDir(path + "/" + tempList[i]);
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 递归创建目录
	 * 
	 * @param file
	 *            文件夹的file
	 */
	public static void mkDir(File file) {
		// LogUtil.v(AppConf.TAG, "目录递归创建成功");
		if (file.getParentFile().exists()) {
			file.mkdir();
		} else {
			mkDir(file.getParentFile());
			file.mkdir();
		}
	}

	/**
	 * 获取文件所在目录
	 * 
	 * @param filePathString
	 * @return
	 */
	public static String getFilePath(String filePathString) {
		File file = new File(filePathString);
		String filePath = file.getPath();
		return filePath.substring(0, filePath.length()
				- file.getName().length());
	}

	/**
	 * 读取日志文件最后一行，并返回内容
	 * 
	 * @param filename
	 * @param charset
	 * @return
	 */
	public static String readFileLastLine(String filename, String charset) {

		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(filename, "r");
			long len = rf.length();
			long start = rf.getFilePointer();
			long nextend = start + len;
			String line;
			// System.out.println(start+"________"+len+"________"+nextend);
			rf.seek(nextend);
			int c = -1;
			while (nextend > start) {

				c = rf.read();
				if (c == '\n' || c == '\r') {
					line = rf.readLine();
					if (line != null) {
						return (new String(line.getBytes("ISO-8859-1"), charset));
					} else {
						continue;// 输出为null，可以注释掉
					}
				}
				nextend--;
				rf.seek(nextend);
				if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行
					System.out.println(new String(rf.readLine().getBytes(
							"ISO-8859-1"), charset));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rf != null)
					rf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 按行返回整个文件的list内容
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 * @author jinlong.rhj
	 * @date 2013-5-7
	 * @version 1.0
	 */
	public static List<String> getLinesListFromFile(String filePath)
			throws Exception {
		String content = FileUtil.readFileAsString(filePath);
		return StringUtil.getLinesListFromString(content);
	}

	/**
	 * 返回文件流的总行数
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static int getTotalLines(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		int lines = 0;
		String tmp;
		while ((tmp = reader.readLine()) != null) {
			lines++;
		}
		reader.close();
		return lines;
	}

	/**
	 * 返回整个文件内容，但耗费资源太大，不建议使用
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static String getStringFromStream(InputStream inputStream)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String tmp = "";
		String outString = "";
		while ((tmp = reader.readLine()) != null) {
			outString += tmp + "\r\n";
		}
		reader.close();
		return outString;
	}

	/**
	 * 获取目录大小
	 * 
	 * @param dir
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-29
	 * @version 1.0
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 如果遇到目录则通过递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 递归求取目录下文件总数
	 * 
	 * @param dir
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-29
	 * @version 1.0
	 */
	public long getDirFilesCount(File dir) {
		long size = 0;
		File flist[] = dir.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getDirFilesCount(flist[i]);
				size--;
			}
		}
		return size;
	}

	/**
	 * 获取指定行的文本内容，输入参数为输入流
	 * 
	 * @param inputStream
	 * @param startLine
	 * @param endLine
	 * @return
	 * @throws IOException
	 */
	public static String getStringFromStream(InputStream inputStream,
			int startLine, int endLine) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String tmp = "";
		String outString = "\t\t";

		if (startLine <= 0 || startLine > endLine) {
			System.out.println("不在文件的行数范围(1至总行数)之内。");
			startLine = 1;
		}

		int i = 0;
		while (tmp != null) {
			i++;
			tmp = reader.readLine();

			if (startLine > endLine) {
				break;
			}
			if (i == startLine) {
				startLine++;
				if (tmp != null) {
					outString += tmp + "\n\r\t\t";
				}
			}
		}
		reader.close();
		return outString;
	}

	/**
	 * 遍历文件夹下的文件路径到一个list,不递归
	 * 
	 * @param dirPath
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-3-23
	 * @version 1.0
	 */
	public static ArrayList<String> getFilesListByDir(String dirPath) {
		ArrayList<String> fileList = new ArrayList<String>();
		File dir = new File(dirPath);
		File[] files = dir.listFiles();

		if (files == null)
			return fileList;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				// System.out.println("------------" +
				// files[i].getAbsolutePath());
				continue;
			} else {
				String strFileName = files[i].getAbsolutePath();
				// System.out.println("---" + strFileName);
				fileList.add(strFileName);
			}
		}
		return fileList;
	}

	/**
	 * 递归遍历文件夹下的文件路径到一个list
	 * 
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-2
	 * @version 1.0
	 */
	public static List<String> getFilesListByDirRecurse(String dirPath) {

		File dir = new File(dirPath);
		File[] files = dir.listFiles();

		if (files == null)
			return filesList;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				// System.out.println("------------" +
				// files[i].getAbsolutePath());
				getFilesListByDirRecurse(files[i].getAbsolutePath());
			} else {
				String strFileName = files[i].getAbsolutePath();
				// System.out.println("---" + strFileName);
				filesList.add(strFileName);
			}
		}
		return filesList;
	}

	/**
	 * 根据文件名过滤文件夹下的文件，生成文件名list
	 * 
	 * @param dir
	 * @param prefix
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-2
	 * @version 1.0
	 */
	public static List<String> getFilesListByDir(String dir, String prefix) {
		List<String> filesList = getFilesListByDir(dir);
		List<String> logFilesList = new ArrayList<String>();
		for (String string : filesList) {
			if (string.indexOf(prefix) >= 0) {
				// System.out.println(string);
				logFilesList.add(string);
			} else {
				// System.out.println("-----");
			}
		}
		return logFilesList;
	}

	/**
	 * 获取当前目录，不带斜杠
	 * 
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-5-7
	 * @version 1.0
	 */
	public static String getCurrentDir() {
		return System.getProperty("user.dir");
	}

	/**
	 * Java文件操作 获取文件扩展名
	 * 
	 * Created on: 2011-8-2
	 * 
	 * Author: blueeagle
	 */
	public static String getExtensionName(String filePath) {
		String filename = new File(filePath).getName();
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			} else {
				return "";
			}
		}
		return filename;
	}

	/**
	 * Java文件操作 获取不带扩展名的文件名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getFileNameNoExtension(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	/**
	 * 通过管道复制文件
	 * 
	 * @param fromFile
	 * @param toFile
	 * @return
	 * @throws Exception
	 * @author jinlong.rhj
	 * @date 2013-3-23
	 * @version 1.0
	 */
	public static void copyFileByChannel(File fromFile, File toFile)
			throws Exception {

		int length = 2097152;
		FileInputStream in = new FileInputStream(fromFile);
		FileOutputStream out = new FileOutputStream(toFile);
		FileChannel inC = in.getChannel();
		FileChannel outC = out.getChannel();
		ByteBuffer b = null;
		while (true) {
			if (inC.position() == inC.size()) {
				inC.close();
				outC.close();
			}
			if ((inC.size() - inC.position()) < length) {
				length = (int) (inC.size() - inC.position());
			} else
				length = 2097152;
			b = ByteBuffer.allocateDirect(length);
			inC.read(b);
			b.flip();
			outC.write(b);
			outC.force(false);
		}
	}

	/**
	 * 不考虑多线程优化，单线程文件复制最快的方法是(文件越大该方法越有优势，一般比常用方法快30+%):
	 * 
	 * @param source
	 * @param target
	 * @throws IOException
	 * @author jinlong.rhj
	 * @date 2013-3-23
	 * @version 1.0
	 */
	public static void copyFileByNioTransfer(File source, File target)
			throws IOException {
		FileChannel in = null;
		FileChannel out = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			inStream = new FileInputStream(source);
			outStream = new FileOutputStream(target);
			in = inStream.getChannel();
			out = outStream.getChannel();
			in.transferTo(0, in.size(), out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			inStream.close();
			in.close();
			outStream.close();
			out.close();
		}
	}

	/**
	 * 常用的Java复制文件方法
	 * 
	 * @param f1
	 * @param f2
	 * @throws Exception
	 * @author jinlong.rhj
	 * @date 2013-3-23
	 * @version 1.0
	 */
	public static void copyFileByJava(File fromFile, File toFile)
			throws Exception {
		int length = 2097152;
		FileInputStream in = new FileInputStream(fromFile);
		FileOutputStream out = new FileOutputStream(toFile);
		byte[] buffer = new byte[length];
		while (true) {
			int ins = in.read(buffer);
			if (ins == -1) {
				in.close();
				out.flush();
				out.close();
			} else
				out.write(buffer, 0, ins);
		}
	}

	/**
	 * 移动文件到指定路径
	 * 
	 * @param srcPath
	 *            String 如：c:/fqf.txt
	 * @param dstPath
	 *            String 如：d:/fqf.txt
	 * @throws Exception
	 */
	public static void moveFile(String srcPath, String dstPath)
			throws Exception {
		if (srcPath.equalsIgnoreCase(dstPath)) {
			return;
		}
		copyFileByNioTransfer(new File(srcPath), new File(dstPath));
		delFile(srcPath);
	}

	/**
	 * 移动目录到指定路径
	 * 
	 * @param oldPath
	 *            String 如：c:/fqf.txt
	 * @param newPath
	 *            String 如：d:/fqf.txt
	 * @return
	 * @throws Exception
	 */
	public static boolean moveDir(String srcPath, String dstPath)
			throws Exception {
		if (srcPath.equalsIgnoreCase(dstPath)) {
			return false;
		}
		copyDir(srcPath, dstPath);
		delDir(srcPath);
		if (!getFileInstance(srcPath).exists()
				&& getFileInstance(dstPath).exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static File getFileInstance(String path) {
		return new File(path);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
