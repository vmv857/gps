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

@SuppressLint("DefaultLocale")
public class GetTask extends AsyncTask<String, Integer, String> {
	private static String LOG_TAG = "GetTask";
	public static int taskStarted = 0;
	private static final String url = "http://vmchat.besaba.com/";

	@Override
	protected String doInBackground(String... params) {
		String res = "OK";
		taskStarted = taskStarted + 1;
		try {
			// Log.d(LOG_TAG, "LocUpTask doInBackground start " + url);
			if (!download_all())
				res = "NO";
		} catch (Throwable e) {
			e.printStackTrace();
			Log.e(LOG_TAG, e.toString());
			res = "ER";
		} finally {
			taskStarted = taskStarted - 1;
		}
		return res;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		ChatActivity.ch.RefreshW();
	}

	@Override
	protected void onPostExecute(String res) {
		//Log.v(LOG_TAG, "onPostExecute " + res);
		super.onPostExecute(res);
		ChatActivity.ch.RefreshW();
	}

	public boolean download_all() {
		List<ChMsg> list = new ArrayList<ChMsg>();
		String url_data = url + "?startfrom=" + Pref.getLong("last_id", 0);
		String data = get(url_data);
		publishProgress(0);
		//Log.v(LOG_TAG, "data.length()=" + data.length());
		if (data != null) { // parse
			scan_html(data, list);
		}
		ins(list);
		if (list.size() >= 1)
			return true;
		return false;
	}

	public static String enc(String par) throws UnsupportedEncodingException {
		return URLEncoder.encode(par, "UTF-8");
	}

	public static String get(String l_url) {
		HttpURLConnection con = null;
		InputStream is = null;
		try {
			con = (HttpURLConnection) (new URL(l_url)).openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(false);
			con.setConnectTimeout(60000);
			con.connect();
			int stt = con.getResponseCode();
			//Log.v(LOG_TAG, "get html getResponseCode " + stt);
			if (stt == 200) {
				StringBuffer buffer = new StringBuffer();
				is = con.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null)
					buffer.append(line + "\r\n");
				//Log.v(LOG_TAG, "get html data length =" + buffer.length());
				return buffer.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_TAG, e.toString());
		} finally {
			try {
				is.close();
			} catch (Throwable t) {
			}
			try {
				con.disconnect();
			} catch (Throwable t) {
			}
		}
		return null;
	}

	public void ins(List<ChMsg> list) {
		//Log.v(LOG_TAG, "ins list.size()=" + list.size());
		for (int i = 0; i < list.size(); i++) {
			ChMsg msg = list.get(i);
			publishProgress(i+1);
			try {
				Db_Helper dbHelp = new Db_Helper(Cnt.get());
				//Log.e(LOG_TAG, msg.toString());
				dbHelp.ins_msg(msg);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, e.toString());
			}
		}
	}

	protected void scan_html(String data, List<ChMsg> list) {
		//Log.v(LOG_TAG, "scan_html list.size()=" + list.size());
		int start = 2;
		int end;
		while (start > 1) {
			ChMsg msg = new ChMsg();
			end = data.indexOf("</tr>", start + 1);
			if (end > 0) {
				try {
					int st_td = start + 1;
					for (int i = 0; i <= 5; i++) {
						st_td = data.indexOf("<td>", st_td);
						if (st_td > 1) {
							int en_td = data.indexOf("</td>", st_td);
							if (en_td > 1) {
								String url = data.substring(st_td + 4, en_td);
								// Log.v(LOG_TAG, i + " " + url + " scan_html "
								// + st_td + " start scan " + en_td);
								switch (i) {
								case 0:
									msg.id_serv = url;
									break;
								case 1:
									msg.who = url;
									break;
								case 2:
									msg.what = url;
									break;
								case 3:
									msg.dt_serv = url;
									break;
								case 4:
									msg.toh = url;
									break;
								case 5:
									msg.udt = url;
									break;
								}
								st_td = en_td;
							}
						}
					}
					//Log.e(LOG_TAG, start + " msg=" + msg.toString());
				} catch (Exception e) {
					Log.e(LOG_TAG, "scan_html " + e.toString());
					msg = null;
				}
				if (msg != null)
					list.add(msg);
			}
			start = end + 1;
			start = data.indexOf("<tr>", start);
		}
	}

}
