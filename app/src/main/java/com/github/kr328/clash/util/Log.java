package com.github.kr328.clash.util;


public class Log {
	static final boolean LOG = true;


	public static void v(String tag, String string) { // log level set to 0 for
		// log from here
		if (LOG) {
			android.util.Log.v(tag, string);
			//appendLog(tag , string);
		}
	}

	public static void d(String tag, String string) { // log level set to 1 for
														// log from here
		if (LOG) {
			android.util.Log.d(tag, string);
			//appendLog(tag , string);
	}
	}

	public static void i(String tag, String string) { // log level set to 2 for
														// log from here
		if (LOG ) {
			android.util.Log.i(tag, string);
			//appendLog(tag , string);
	}
	}

	public static void w(String tag, String string) { // log level set to 3 for
														// log from here
		if (LOG) {
			android.util.Log.w(tag, string);
			//appendLog(tag , string);
	}
	}

	public static void e(String tag, String string) { // log level set to 4 for
														// log from here
		if (LOG ) {
			android.util.Log.e(tag, string);
			//appendLog(tag , string);
	}
	}

	/*private static void appendLog(String tag, String msg) {
		android.util.Log.i(tag, msg);
	}*/

	private static void appendLog(String tag, String msg) {

	    java.io.File logFile = new java.io.File("sdcard/Hydraulixlog.file");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (Exception e) {

				e.printStackTrace();
			}
		}else{
			if(logFile.length()>1048576){
				logFile.delete();
				try {
					logFile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			java.io.BufferedWriter buf = new java.io.BufferedWriter(new java.io.FileWriter(logFile,
					true));
			buf.append(tag+":"+msg);
			buf.newLine();
			buf.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	    }

	/*if(tag.contains("UncaughtException")){
	    System.exit(1);
    }*/



}
