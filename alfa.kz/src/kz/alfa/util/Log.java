package kz.alfa.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

// This class write to log file (if can!) and to LogCat
public class Log {
	static public boolean emptyTag = false;
	static public String initDir = "";
	static private String LevelD = "W";
	static private String logFile = "";
	static private FileOutputStream os = null;
	static private OutputStreamWriter oos = null;

	public static String fstr(int inum) {
		String cRes = inum + "";
		if (inum <= 9) {
			cRes = "0" + inum;
		}
		return cRes;
	}

	public static String getDay() {
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		String mMonth = fstr(c.get(Calendar.MONTH) + 1);
		String mDay = fstr(c.get(Calendar.DAY_OF_MONTH));
		return mYear + "_" + mMonth + "_" + mDay;
	}

	public static String getMSec() {
		final Calendar c = Calendar.getInstance();
		String mHour = fstr(c.get(Calendar.HOUR_OF_DAY));
		String mMinute = fstr(c.get(Calendar.MINUTE));
		String mSec = fstr(c.get(Calendar.SECOND));
		String mMis = fstr(c.get(Calendar.MILLISECOND));
		return mHour + ":" + mMinute + ":" + mSec + "." + mMis + " ";
	}

	public static String getDateDir() {
		// получаем текущее время
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		String mMonth = fstr(c.get(Calendar.MONTH) + 1);
		String mDay = fstr(c.get(Calendar.DAY_OF_MONTH));
		// String mHour = fstr(c.get(Calendar.HOUR_OF_DAY));
		// String mMinute = fstr(c.get(Calendar.MINUTE));
		// String mSec = fstr(c.get(Calendar.SECOND));
		String str_file_name = mYear + "/" + mMonth + "/" + mDay + "/";
		return str_file_name;
	}

	public static String getDir() {
		File sdDir;
		sdDir = new File(initDir);
		if (!sdDir.isDirectory()) {
			String sdState = android.os.Environment.getExternalStorageState();
			if (sdState.equals(android.os.Environment.MEDIA_MOUNTED))
				sdDir = android.os.Environment.getExternalStorageDirectory();
			else if (Cnt.get() != null)
				sdDir = Cnt.get().getFilesDir();
		}
		return sdDir.getPath();
	}

	static public void wrLogFile(String str, String fileName) {
		try {
			if (Cnt.get() != null) {
				if (os == null) {
					os = new FileOutputStream(fileName, true);
					oos = new OutputStreamWriter(os);
				}
				oos.append(str);
				oos.flush();
				// oos.close();
				// os.close();
			} else
				android.util.Log.e("LLG", "Error no Context in Cnt.get() !!! ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			android.util.Log.e("LLG", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			android.util.Log.e("LLG", e.getMessage());
		}
	}

	static public void wrLogFile(String str) {
		// android.util.Log.e("Log LLG", str);
		try {
			if (logFile.length()==0) {
				File dir = new File(getDir() + "/" + getDateDir());
				dir.mkdirs();
				if (dir.exists()) {
					logFile = dir.getAbsolutePath() + "/log_" + getDay()
							+ ".txt";
				} else {
					logFile = getDir() + "/log_" + getDay() + ".txt";
				}
				android.util.Log.e("LLG", logFile);
			}
			wrLogFile(getMSec() + str + "\n", logFile);
		} catch (Exception e) {
			android.util.Log.e("wrLogFile", str + e.toString());
		}
	}

	public static int v(String tag, String msg) {
		if (emptyTag || tag.length() > 0) {
			if (LevelD.equalsIgnoreCase("V"))
				wrLogFile("V " + tag + " " + msg);
			return android.util.Log.v(tag, msg);
		}
		return -1;
	}

	public static int d(String tag, String msg) {
		if (emptyTag || tag.length() > 0) {
			if (LevelD.equalsIgnoreCase("D") || LevelD.equalsIgnoreCase("V"))
				wrLogFile("D " + tag + " " + msg);
			return android.util.Log.d(tag, msg);
		}
		return -1;
	}

	public static int i(String tag, String msg) {
		if (emptyTag || tag.length() > 0) {
			if (LevelD.equalsIgnoreCase("I") || LevelD.equalsIgnoreCase("D")
					|| (LevelD.equalsIgnoreCase("V")))
				wrLogFile("I " + tag + " " + msg);
			return android.util.Log.i(tag, msg);
		}
		return -1;
	}

	public static int w(String tag, String msg) {
		if (emptyTag || tag.length() > 0) {
			if (LevelD.equalsIgnoreCase("V") || LevelD.equalsIgnoreCase("D")
					|| LevelD.equalsIgnoreCase("I")
					|| LevelD.equalsIgnoreCase("W"))
				wrLogFile("W " + tag + " " + msg);
			return android.util.Log.w(tag, msg);
		}
		return -1;
	}

	public static int e(String tag, String msg) {
		if (LevelD.equalsIgnoreCase("V") || LevelD.equalsIgnoreCase("D")
				|| LevelD.equalsIgnoreCase("I") || LevelD.equalsIgnoreCase("W")
				|| (LevelD.equalsIgnoreCase("E")))
			wrLogFile("E " + tag + " " + msg);
		return android.util.Log.e(tag, msg);
	}

}
