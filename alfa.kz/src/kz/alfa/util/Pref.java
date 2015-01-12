package kz.alfa.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Pref {
	public static SharedPreferences prefs;

	public static void init() {
		if (prefs == null)
			if (Cnt.get() != null)
				prefs = PreferenceManager
						.getDefaultSharedPreferences(Cnt.get());
	}

	public static void edit(String key, String value) {
		init();
		Editor ed;
		ed = prefs.edit();
		ed.putString(key, value);
		ed.commit();
	}

	public static void edit(String key, long value) {
		init();
		Editor ed;
		ed = prefs.edit();
		ed.putLong(key, value);
		ed.commit();
	}

	public static void edit(String key, boolean value) {
		init();
		Editor ed;
		ed = prefs.edit();
		ed.putBoolean(key, value);
		ed.commit();
	}
	
	public static String getString(String key, String defValue) {
		init();
		return prefs.getString(key, defValue);
	}

	public static long getLong(String key, long defValue) {
		init();
		return prefs.getLong(key, defValue);
	}

	public static boolean getBoolean(String key, boolean defValue) {
		init();
		return prefs.getBoolean(key, defValue);
	}
}
