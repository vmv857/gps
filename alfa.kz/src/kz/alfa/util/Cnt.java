package kz.alfa.util;

import android.content.Context;

// easy get to Context but first set it: Cnt.set(getApplicationContext());
public class Cnt {
	private static Context context = null;
	
	public static void set(Context inCnt){
		if (inCnt != null)
			context = inCnt;
	}

	public static Context get(){
		if (context == null)
			android.util.Log.e("Cnt", "Error ! first we need set(Context)!!! ");
		return context;
	}
}
