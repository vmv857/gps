package kz.alfa.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import kz.alfa.ChatActivity;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class SendTask extends AsyncTask<String, Integer, String> {
	private static List<ChMsg> list = new ArrayList<ChMsg>();

	private static String LOG_TAG = "SendTask";
	public static int taskStarted = 0;
	private static final String url = "http://vmchat.besaba.com/send.php?";

	@Override
	protected String doInBackground(String... params) {
		taskStarted = taskStarted + 1;
		try {
			//Log.e(LOG_TAG, " doInBackground start " + url);
			ChMsg msg = new ChMsg();
			msg.what = params[0];
			msg.udt = System.currentTimeMillis()+"."+System.nanoTime();
			msg.who = Uid.get();
			try {
				Db_Helper dbHelp = new Db_Helper(Cnt.get());
				dbHelp.ins_msg(msg);
				dbHelp = new Db_Helper(Cnt.get());
				publishProgress(0);
				list = dbHelp.get_up_list();
				//Log.e(LOG_TAG, " doInBackground list.size()= " + list.size());
				for (int i = 0; i < list.size(); i++) {
					msg = list.get(i);
					if (msg.what.length() > 0)
						ins(msg);
					//Log.e(LOG_TAG, " del = "+msg);
				}
				// считать с сервера
				if (GetTask.taskStarted < 2) {
					GetTask taskG = new GetTask();
					taskG.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							new String[] {});
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, e.toString());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			Log.e(LOG_TAG, e.toString());
		} finally {
			taskStarted = taskStarted - 1;
		}
		return "OK";
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		ChatActivity.ch.RefreshW();
	}

	@Override
	protected void onPostExecute(String res) {
		super.onPostExecute(res);
		ChatActivity.ch.RefreshW();
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
		//Log.v(LOG_TAG, "get html data=" + buffer.toString());
		return buffer.toString();
	}

	public static boolean ins(ChMsg msg) {
		try {
			String url_data = url + "who=" + enc(msg.who) + "&what="
					+ enc(msg.what) + "&udt=" + enc(msg.udt) + "&toh=ALL";
			String res = get(url_data);
			//Log.e(LOG_TAG, " ins(ChMsg msg)  res=" + res);
			if ((res != null) && (Integer.valueOf(res.trim()) == 1))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_TAG, e.toString());
		}
		return false;
	}
}
