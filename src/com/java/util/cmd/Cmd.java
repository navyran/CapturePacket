package com.java.util.cmd;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;

/**
 * Cmd执行类
 * 
 * @author E-mail:jinlong.rhj@alibaba-inc.com
 * 
 */
public class Cmd {
	private Process process;
	private static BufferedReader br;

	public Cmd() {
		// TODO Auto-generated constructor stub
	}

	public Cmd(String commandLineString) throws IOException {
		process = exeCmd(commandLineString);
	}

	public Cmd(String[] commandLineArray) throws IOException {
		String commandLineString = "";
		for (String string : commandLineArray) {
			commandLineString += string;
		}
		process = exeCmd(commandLineString);
	}

	public Process getProcess() {
		return process;
	}

	/**
	 * 外部调用执行命令
	 * 
	 * @param commandLineString
	 * @throws IOException
	 * @author jinlong.rhj
	 * @date 2013-4-9
	 * @version 1.0
	 */
	public void exec(String commandLineString) throws IOException {
		process = exeCmd(commandLineString);
	}

	/**
	 * 字节流方式传输执行
	 * 
	 * @param commandLine
	 * @throws IOException
	 * @author jinlong.rhj
	 * @date 2013年8月15日
	 * @version 1.0
	 */
	public void execWriteBytes(String commandLine) throws IOException {
		DataOutputStream dos = new DataOutputStream(process.getOutputStream());
		dos.writeBytes(commandLine + "\n");
		dos.flush();
	}

	/**
	 * 设定时间内，cmd执行未关闭线程则显示超时
	 * 
	 * @param commandLineString
	 * @param timeout
	 * @return
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @author jinlong.rhj
	 * @date 2013-6-3
	 * @version 1.0
	 */
	public int exec(String commandLineString, long timeout) throws IOException,
			TimeoutException, InterruptedException {
		process = exeCmd(commandLineString);
		WaitForThread wait = new WaitForThread(process);
		wait.start();

		try {
			wait.join(timeout);
			if (wait.exit != null) {
				return wait.exit;
			} else {
				System.out.println("超时异常。");
				wait.interrupt();
				process.destroy();
				// throw new TimeoutException();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("中断 超时异常。");
			wait.interrupt();
			process.destroy();
			// Thread.currentThread().interrupt();
			// throw e;
		} finally {
			process.destroy();
		}
		return 0;

	}

	/**
	 * 内部调用执行命令
	 * 
	 * @param commandLineString
	 * @return
	 * @throws IOException
	 * @author jinlong.rhj
	 * @date 2013-4-9
	 * @version 1.0
	 */
	private Process exeCmd(String commandLineString) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		return runtime.exec(commandLineString);
	}

	/**
	 * 获取执行结果代码 返回0表示正常完成
	 * 
	 * @return
	 * @throws InterruptedException
	 * @author jinlong.rhj
	 * @date 2013-4-9
	 * @version 1.0
	 */
	public int getExitCode() throws InterruptedException {
		return process.waitFor();
	}

	/**
	 * 等待线程执行完毕后关闭
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @author jinlong.rhj
	 * @date 2013-5-9
	 * @version 1.0
	 */
	public void close() throws IOException, InterruptedException {
		// System.out.println(process.toString());
		process.getOutputStream().close();
		process.getInputStream().close();
		process.getErrorStream().close();
		if (getExitCode() != 0) {
			process.destroy();
			process = null;
		}
	}

	/**
	 * 强制关闭线程
	 * 
	 * @author jinlong.rhj
	 * @date 2013-5-9
	 * @version 1.0
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void forceClose(long waitTime) throws IOException,
			InterruptedException {
		// System.out.println(process.toString());
		Thread.sleep(waitTime);
		process.getOutputStream().close();
		process.getInputStream().close();
		process.getErrorStream().close();
		process.destroy();
		process = null;
	}

	/**
	 * 返回输出字符串
	 * 
	 * @return
	 * @author jinlong.rhj
	 * @date 2013-4-22
	 * @version 1.0
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public String getOutputString() throws InterruptedException, IOException {
		if (getExitCode() == 0) {
			return inputStream2String(process.getInputStream());
		} else {
			return inputStream2String(process.getErrorStream());
		}
	}

	/**
	 * 获取CMD线程的子线程输出
	 * 
	 * @param waitTime
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @author jinlong.rhj
	 * @date 2013-5-9
	 * @version 1.0
	 */
	public String getOutputString(long waitTime) throws InterruptedException,
			IOException {
		Thread.sleep(waitTime);
		String output = inputStream2String(process.getInputStream());
		process.getInputStream().close();
		process.getErrorStream().close();
		process.destroy();
		return output;
	}

	public void inputCmd(String cmd) {
		if (process == null) {
			return;
		}
		InputStream is = String2InputStream(cmd);
		// PrintWriter writer = new PrintWriter(process.getOutputStream(),
		// true);
		// Scanner scanner = new Scanner(is);
		// writer.println(scanner.nextLine());
		// writer.flush();

		DataOutputStream os = new DataOutputStream(process.getOutputStream());
		try {
			os.writeBytes(cmd + "\n");
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 打印正常CMD输出信息
	 * 
	 * @author jinlong.rhj
	 * @date 2013-4-9
	 * @version 1.0
	 */
	public void printInputStream() {
		new Thread(new StreamDrainer(process.getInputStream())).start();
	}

	/**
	 * 将输入流输出到文件
	 * 
	 * @param filePath
	 * @author jinlong.rhj
	 * @date 2013-5-7
	 * @version 1.0
	 */
	public void writeInputStream(String filePath) {
		new Thread(new StreamWriter(process.getInputStream(), filePath))
				.start();
	}

	/**
	 * 打印cmd执行出错信息
	 * 
	 * @author jinlong.rhj
	 * @date 2013-4-9
	 * @version 1.0
	 */
	public void printErrorStream() {
		new Thread(new StreamDrainer(process.getErrorStream())).start();
	}

	/**
	 * 异步获取输出流，可以直接toString
	 * 
	 * @return
	 * @author jinlong.rhj
	 * @date 2013年8月1日
	 * @version 1.0
	 */
	public ByteArrayOutputStream getOutputStreamAsync() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PrintWriter pw = new PrintWriter(baos);
		new Thread(new StreamDrainer(process.getInputStream(), pw)).start();
		return baos;
	}

	/**
	 * 异步获取输出流并返回String
	 * 
	 * @param waitTime
	 * @return
	 * @throws InterruptedException
	 * @author jinlong.rhj
	 * @date 2013年8月1日
	 * @version 1.0
	 * @throws IOException
	 */
	public String getOutputStringAsync(long waitTime)
			throws InterruptedException, IOException {
		ByteArrayOutputStream baos = getOutputStreamAsync();
		Thread.sleep(waitTime);
		String str = baos.toString();
		baos.close();
		return str;
	}

	/**
	 * 打印输出线程
	 * 
	 * @author jinlong.rhj
	 * 
	 */
	public static class StreamDrainer implements Runnable {
		private InputStream ins;
		private PrintWriter pw;

		public StreamDrainer(InputStream ins) {
			this.ins = ins;
		}

		public StreamDrainer(InputStream ins, PrintWriter pw) {
			this.ins = ins;
			this.pw = pw;
		}

		public void run() {
			try {
				br = new BufferedReader(new InputStreamReader(ins));
				String line = null;
				while ((line = br.readLine()) != null) {
					outputLine(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void outputLine(String line) {
			if (pw == null) {
				System.out.println(line);
			} else {
				pw.println(line);
				pw.flush();
			}

		}
	}

	/**
	 * 将输出写入文本
	 * 
	 * @author jinlong.rhj
	 * 
	 */
	public static class StreamWriter implements Runnable {
		private InputStream ins;
		private String filePath;

		public StreamWriter(InputStream ins, String filePath) {
			this.ins = ins;
			this.filePath = filePath;
		}

		public void run() {
			try {
				writeInputStream(filePath, ins, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 超时等待线程
	 * 
	 * @author jinlong.rhj
	 * 
	 */
	public static class WaitForThread extends Thread {
		private final Process process;
		private Integer exit;

		private WaitForThread(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}

	/**
	 * 基本用法
	 * 
	 * @param args
	 * @author jinlong.rhj
	 * @date 2013-4-9
	 * @version 1.0
	 */
	public static void main(String[] args) {
		Cmd cmd = null;
		String commandLineString = "adb shell";
		try {
			cmd = new Cmd(commandLineString);
			cmd.inputCmd("ls");
			cmd.printInputStream();
			cmd.printErrorStream();

			int exitValue = cmd.getExitCode();

			if (exitValue == 0) {

			} else {
			}
			cmd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static InputStream String2InputStream(String str) {
		ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
		return stream;
	}

	private static String inputStream2String(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line + "\r\n");
		}
		return buffer.toString();
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
		fos = new FileOutputStream(fileName, append);
		while ((ch = is.read()) != -1) {
			fos.write(ch);
		}
		fos.close();
		is.close();

		return true;
	}
}
