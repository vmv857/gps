package kz.alfa.util;

import java.util.UUID;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Patterns;

public class Uid {
	public static final String LOG_TAG = "Uid";
	private static String uid = null;
	
	public static String get() {
		if (uid == null) try {
			
			Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
			Account[] accounts = AccountManager.get(Cnt.get()).getAccounts();
			for (Account account : accounts) {
			    if (emailPattern.matcher(account.name).matches()) {
			    	if ((uid == null) || (uid.length() < 3))
			    		uid = account.name;
			        Log.e(LOG_TAG, "possibleEmail="+account.name);
			    }
			}

			final TelephonyManager tm = (TelephonyManager) Cnt.get().getSystemService(Context.TELEPHONY_SERVICE);

			String tmDevice, tmSerial, androidId;
			tmDevice = "" + tm.getDeviceId();
			if (uid.length() < 3)
				uid =  tmDevice;
			Log.e(LOG_TAG, "tmDevice = "+tmDevice);
				
			String myPhoneNumber =  tm.getLine1Number();
			Log.e(LOG_TAG, "myPhoneNumber = "+myPhoneNumber);
			
			tmSerial = "" + tm.getSimSerialNumber();
			Log.e(LOG_TAG, "tmSerial = "+tmSerial);
			if (uid.length() < 3)
				uid += "."+tmSerial;
				
			androidId = ""	+ android.provider.Settings.Secure.getString(
							Cnt.get().getContentResolver(),
							android.provider.Settings.Secure.ANDROID_ID);
			Log.e(LOG_TAG, "androidId = "+androidId);
			if (uid.length() < 5)
					uid += "."+androidId;

			UUID deviceUuid = new UUID(androidId.hashCode(),
					((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());			
			Log.e(LOG_TAG, "deviceUuid = "+deviceUuid);
			if (uid.length() < 7)
				uid = deviceUuid.toString();
			
			Log.e(LOG_TAG, "uid = "+uid);
		} catch(Exception e) {
			Log.e(LOG_TAG, e.toString());
			return "Error";
		}
		//Log.d(LOG_TAG, "uid = "+uid);
		return uid;
	}
}
