package dc.utils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import android.os.Environment;

public class PyLog {

	public static String TAG = "py";

	public static void log(String msg) {
		__log(msg);
	}

	public static String getCallback() {
		return Log.getStackTraceString(new Throwable());

	}

	public static void log(Throwable e) {
		__log(Log.getStackTraceString(e));
	}

	public static void log(String reason, Exception e) {
		__log(reason + "\r\n" + Log.getStackTraceString(e));
	}

	public static void log(Exception e) {
		__log(Log.getStackTraceString(e));
	}

	public static void log_callback() {
		__log(Log.getStackTraceString(new Throwable()));
	}

	public static void log_tag(String tag, String msg) {
		Log.i(tag, msg);
	}

	private static void __log(String msg) {
		Log.i(TAG, msg);
	}
}
