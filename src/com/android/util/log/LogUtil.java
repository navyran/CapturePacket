package com.android.util.log;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class LogUtil {

	public static boolean logSwitch = true;
	public static String TAG = "Tag";

	public LogUtil(boolean myLogSwitch) {
		// TODO Auto-generated constructor stub

	}

	public static void v(String tag, String msg) {
		if (logSwitch) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (logSwitch) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (logSwitch) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (logSwitch) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (logSwitch) {
			Log.e(tag, msg);
		}
	}

	public static void v(String msg) {
		if (logSwitch) {
			Log.v(TAG, msg);
		}
	}

	public static void d(String msg) {
		if (logSwitch) {
			Log.v(TAG, msg);
		}
	}

	public static void w(String msg) {
		if (logSwitch) {
			Log.v(TAG, msg);
		}
	}

	public static void e(String msg) {
		if (logSwitch) {
			Log.v(TAG, msg);
		}
	}

	public static void i(String msg) {
		if (logSwitch) {
			Log.v(TAG, msg);
		}
	}

	public static void print(Activity mActivity, String msg) {
		Log.d(TAG, msg);
		Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
	}

	
}
