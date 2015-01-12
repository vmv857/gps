package kz.alfa.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.AsyncTask;

@SuppressLint("DefaultLocale")
public class LocUpTask extends AsyncTask<String, Void, String> { 
	//private static String LOG_TAG = "LocUpTask";
	public static int taskStarted = 0;
	private static final String url = "http://adimurka.16mb.com/lins.php?";

	@Override
	protected String doInBackground(String... params) {
		taskStarted = taskStarted + 1;
		try {
			//Log.d(LOG_TAG, "LocUpTask doInBackground start " + url);
			upload_all();
		} catch (Throwable e) {
			e.printStackTrace();
			//Log.e(LOG_TAG, e.toString());
		} finally {
			taskStarted = taskStarted - 1;
		}
		return "OK";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

	public static void upload_all() {
		List<Location> list = (new LocDbHlp(Cnt.get())).upload_list("dt_up_http");
		for (int i = 0; i < list.size(); i++) {
			Location loc = list.get(i);
			ins(loc);
		}
	}

	public static String enc(String par) throws UnsupportedEncodingException {
		return URLEncoder.encode(par, "UTF-8");
	}

	public static String get(String l_url) throws Exception {
		HttpURLConnection con = null;
		InputStream is = null;
		con = (HttpURLConnection) (new URL(l_url)).openConnection();
		con.setRequestMethod("GET");
		con.setDoInput(true);
		con.setDoOutput(false);
		con.setConnectTimeout(60000);
		con.connect();
		int stt = con.getResponseCode();
		//Log.v(LOG_TAG, "get html getResponseCode " + stt);
		if (stt != 200)
			return "0";
		StringBuffer buffer = new StringBuffer();
		is = con.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null)
			buffer.append(line + "\r\n");
		is.close();
		con.disconnect();
		//Log.e(LOG_TAG, "get html data=" + buffer.toString());
		return buffer.toString();
	}

	public static void ins(Location loc) {
		try {
			String url_data = url + "&idwho=" + Uid.get() + "&raw="
					+ loc.toString() + "&Latitude=" + loc.getLatitude()
					+ "&Longitude=" + loc.getLongitude() + "&Altitude="
					+ loc.getAltitude() + "&Speed=" + loc.getSpeed()
					+ "&Bearing=" + loc.getBearing() + "&Accuracy="
					+ loc.getAccuracy() + "&Provider=" + loc.getProvider()
					+ "&DTime=" + loc.getTime() + "&table=loc_log";
			String res = get(url_data);
			//Log.v(LOG_TAG, "res="+res);
			if ((res != null) && (Integer.valueOf(res.trim())==1)) {
				(new LocDbHlp(Cnt.get())).upd_upload(loc.getTime(), "dt_up_http");
				//Log.v(LOG_TAG, "upd_upload resLocDbHlp = " + ir);//+" url_data="+url_data);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			//Log.e(LOG_TAG, e.toString());
		}
	}
	/*
	 * LocUpTask taskLup = new LocUpTask();
	 * taskLup.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[] {
	 * "" });
	 * 
	 * create table loc_log ( _id integer primary key AUTO_INCREMENT, idwho
	 * text, raw text, Latitude double, Longitude double, Altitude double, Speed
	 * double, Bearing double, Accuracy double, Provider text, DTime bigint,
	 * date_time TIMESTAMP default CURRENT_TIMESTAMP );
	 * 
	 * http://adimurka.16mb.com/ltrk.php?id=ffffffff-c572-1a3f-ffff-ffffe8747ef5
	 * http://adimurka.16mb.com/lins.php?idwho=test&table=loc_log
	 * http://adimurka.16mb.com/lins.php?idwho=tevgst&Dtime=1&table=loc_log
	 * http:
	 * //adimurka.16mb.com/lins.php?&idwho=ffffffff-c572-1a3f-ffff-ffffe8747ef5
	 * &raw
	 * =Location[mProvider=gps,mTime=1419314634000,mLatitude=12.23781997,mLongitude
	 * =
	 * 109.19176674,mHasAltitude=true,mAltitude=44.70000076293945,mHasSpeed=true
	 * ,
	 * mSpeed=0.0,mHasBearing=true,mBearing=0.0,mHasAccuracy=true,mAccuracy=24.0
	 * ,mExtras=null]&Latitude=12.23781997&Longitude=109.19176674&Altitude=
	 * 44.70000076293945
	 * &Speed=0.0&Bearing=0.0&Accuracy=24.0&Provider=gps&DTime=1419314634000
	 * &table=loc_log
	 */
}
