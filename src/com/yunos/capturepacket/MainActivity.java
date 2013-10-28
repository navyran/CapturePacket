package com.yunos.capturepacket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.domob.android.ads.DomobAdEventListener;
import cn.domob.android.ads.DomobAdManager.ErrorCode;
import cn.domob.android.ads.DomobAdView;

import com.android.util.file.AndroidFileUtil;
import com.android.util.file.StorageUtils;
import com.android.util.runtime.PidUtil;
import com.java.util.cmd.Cmd;
import com.java.util.file.FileUtil;
import com.java.util.text.StringUtil;

public class MainActivity extends Activity {

	Button btn_startCapture;
	Button btn_endCapture;
	TextView txtv_description;
	TextView txtv_capState;
	TextView txtv_aboutAuthor;

	String captureToolPath = "/system/xbin/tcpdump";
	String outputDir;
	String outputCapFile;

	Cmd cmd = null;

	public static ArrayList<Activity> activityList = new ArrayList<Activity>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
		init();
		initTool();
		// setupAds();
	}

	public void init() {
		String descriptionString = "说明：\n1.请确认你的手机已ROOT 或者 能够在电脑上连接ADB。\n"
				+ "2.如果你的手机已经有ROOT权限，可以直接使用此应用进行抓包。\n"
				+ "3.如果没有获取ROOT权限，请使用 ADB命令 将应用生成的[/sdcard/tcpdump]复制到[/ststem/xbin/]目录下,并修改权限为6755后再次运行此应用抓包。\n"
				+ "4.抓包文件保存在/sdcard/目录下,文件格式为.pcap；可多次抓包且不会覆盖以前的抓包。\n"
				+ "5.所抓取的数据包可在电脑上使用Wireshark打开分析\n\n";
		descriptionString += "ADB命令示例：\n" + "adb root\n" + "adb remount\n"
				+ "adb pull /sdcard/tcpdump c:\\\n"
				+ "adb push c:\\tcpdump /system/xbin/\n"
				+ "adb shell chmod 6755  /system/xbin/tcpdump\n";
		txtv_description.setText(descriptionString);

		txtv_aboutAuthor
				.setText(Html
						.fromHtml("联系作者：<a href=\"mailto:ranhaijun1129@163.com\">ranhaijun1129@163.com</a><br/><br/>"));
		txtv_aboutAuthor.setMovementMethod(LinkMovementMethod.getInstance());

		btn_startCapture.setOnClickListener(new MyOnClickListener());
		btn_endCapture.setOnClickListener(new MyOnClickListener());

		if (isCapturing()) {
			btn_startCapture.setEnabled(false);
			btn_endCapture.setEnabled(true);
		} else {
			btn_startCapture.setEnabled(true);
			btn_endCapture.setEnabled(false);
		}

		activityList.add(this);
	}

	public void initView() {
		btn_startCapture = (Button) findViewById(R.id.btn_startCapture);
		btn_endCapture = (Button) findViewById(R.id.btn_endCapture);
		txtv_description = (TextView) findViewById(R.id.txtv_description);
		txtv_capState = (TextView) findViewById(R.id.txtv_capState);
		txtv_aboutAuthor = (TextView) findViewById(R.id.txtv_aboutAuthor);
	}

	public void initTool() {
		if (StorageUtils.isSdAvailable()) {
			outputDir = getSaveDir();
		} else {
			outputDir = this.getFilesDir().getPath();
		}

		if (!new File(captureToolPath).exists()) {
			try {
				copyCaptureTool();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!new File(captureToolPath).exists()) {
				txtv_capState.setText("抓包工具未复制成功，请手动连接ADB复制。");
				// printLog("抓包工具未复制成功，请手动连接ADB复制。");
			}
		}

	}

	public String getSaveDir() {
		String sd = AndroidFileUtil.getSDRootPath();
		sd = StringUtil.isEmpty(sd) ? "/sdcard" : sd;
		return sd + File.separator + "capture_packet";
	}

	protected class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_startCapture:

				startCapture();
				if (isCapturing()) {
					txtv_capState.setText("正在抓包...");
					btn_startCapture.setEnabled(false);
					btn_endCapture.setEnabled(true);
				} else {
					txtv_capState.setText("抓包工具不存在或者没有可执行权限。");
				}

				break;

			case R.id.btn_endCapture:
				// printLog(outputDir + File.separator + outputCapFile);
				stopCapture();

				if (FileUtil.getFileInstance(
						outputDir + File.separator + outputCapFile).exists()) {
					txtv_capState.setText("已停止抓包\n\n抓包已保存：\n" + outputDir
							+ outputCapFile);
					btn_startCapture.setEnabled(true);
					btn_endCapture.setEnabled(false);
				}

				break;

			default:
				break;
			}
		}
	}

	public void startCapture() {
		if (!FileUtil.getFileInstance(captureToolPath).exists()) {
			return;
		}

		try {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					outputCapFile = getOutputCapFileName();
					String execString = captureToolPath + " -p -s 0 -w "
							+ outputDir + File.separator + outputCapFile;
					System.out.println(execString);
					Cmd cmd = new Cmd();
					try {
						cmd.exec("su");
						System.out.println(cmd.getOutputString());
						cmd.execWriteBytes(execString);
						System.out.println(cmd.getOutputString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}).start();

			// Process process = Runtime.getRuntime().exec("su");
			// DataOutputStream os = new DataOutputStream(
			// process.getOutputStream());
			// os.writeBytes(execString + "\n");
			// os.writeBytes("exit\n");
			// os.flush();

			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isCapturing() {
		int pid = PidUtil.getPidFromPs(PidUtil.getPsString(), "tcpdump");
		if (pid > 0) {
			return true;
		}
		return false;
	}

	public boolean stopCapture() {

		if (isCapturing()) {
			PidUtil.killProcessByName("tcpdump");

			if (isCapturing()) {
				return false;
			} else {
				return true;
			}

		}
		return true;
	}

	/**
	 * (non-Javadoc) Title: copyCaptureTool Description: 合并 、 复制文件
	 * 
	 * @throws IOException
	 */
	public void copyCaptureTool() throws IOException {
		ArrayList<String> partFileList = new ArrayList<String>();
		try {
			Runtime.getRuntime().exec("su");
			// printLog("开始复制文件");

			partFileList.add("tcpdump1");
			partFileList.add("tcpdump2");
			mergeAssetsFile(this, partFileList, captureToolPath);

		} catch (IOException e) {
			// TODO: handle exception
			printLog(e.getMessage());
			if (!(new File(captureToolPath).exists())) {
				printLog(outputDir + File.separator + "tcpdump");
				mergeAssetsFile(this, partFileList, outputDir + File.separator
						+ "tcpdump");
			}
			printLog("你的系统没有ROOT权限。");
			// printLog("请使用ADB命令将应用生成的[/sdcard/tcpdump]放到[/ststem/xbin/]目录下,"
			// + "并修改权限为6755后再次运行。");
		}
	}

	/**
	 * 合并文件
	 * 
	 * @param c
	 * @param partFileList
	 *            小文件名集合
	 * @param dst
	 *            目标文件路径
	 * @throws IOException
	 * 
	 * @author zuolongsnail
	 */
	public void mergeAssetsFile(Context c, ArrayList<String> partFileList,
			String dst) throws IOException {
		if (!new File(dst).exists()) {
			OutputStream out = new FileOutputStream(dst);
			byte[] buffer = new byte[1024];
			InputStream in;
			int readLen = 0;
			for (int i = 0; i < partFileList.size(); i++) {
				// 获得输入流
				in = c.getAssets().open(partFileList.get(i));
				while ((readLen = in.read(buffer)) != -1) {
					out.write(buffer, 0, readLen);
				}
				out.flush();
				in.close();
			}
			// 把所有小文件都进行写操作后才关闭输出流，这样就会合并为一个文件了
			out.close();
		}
	}

	// 复制文件
	public static void copyFile(InputStream inputStreamSrc, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(inputStreamSrc);

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public String getOutputCapFileName() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(currentTime);
		return "capture_" + dateString + ".pcap";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * 返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			exitApp();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 完完全全退出应用程序
	 */
	public void exitApp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("您确定要退出程序吗?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if (activityList.size() > 0) {
					for (Activity activity : activityList) {
						activity.finish();
					}
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}

		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 日志打印
	 * 
	 * @param msg
	 */
	public void printLog(String msg) {
		Log.v("my_log", msg);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 添加广告
	 */
	private void setupAds() {
		LinearLayout mAdContainer;
		DomobAdView mAdView;
		String PUBLISHER_ID = "56OJzsbYuNeaSrodvy";

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		mAdContainer = (LinearLayout) findViewById(R.id.adcontainer);
		mAdView = new DomobAdView(this, PUBLISHER_ID,
				DomobAdView.INLINE_SIZE_320X50);

		mAdContainer.addView(mAdView);
		// layout.addView(mAdView);

		mAdView.setAdEventListener(new DomobAdEventListener() {

			@Override
			public void onDomobAdReturned(DomobAdView adView) {
				// Log.i("DomobSDKDemo", "onDomobAdReturned");
			}

			@Override
			public void onDomobAdOverlayPresented(DomobAdView adView) {
				// Log.i("DomobSDKDemo", "overlayPresented");
			}

			@Override
			public void onDomobAdOverlayDismissed(DomobAdView adView) {
				// Log.i("DomobSDKDemo", "Overrided be dismissed");
			}

			@Override
			public void onDomobAdClicked(DomobAdView arg0) {
				// Log.i("DomobSDKDemo", "onDomobAdClicked");
			}

			@Override
			public void onDomobAdFailed(DomobAdView arg0, ErrorCode arg1) {
				// Log.i("DomobSDKDemo", "onDomobAdFailed");
			}

			@Override
			public void onDomobLeaveApplication(DomobAdView arg0) {
				// Log.i("DomobSDKDemo", "onDomobLeaveApplication");
			}
		});

	}

}
