package kz.alfa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.AsyncTask;

@SuppressLint("DefaultLocale")
public class LocJdbcTask extends AsyncTask<String, Void, String> {
	private static String LOG_TAG = "LocJdbcTask";
	public static int taskStarted = 0;
	private static final String url = "jdbc:mysql://adimur.noip.me:3306/adimur";
	
	private static final String user = "adimur";
	private static final String pass = "1234567890";

	@SuppressLint("DefaultLocale")
	@Override
	protected String doInBackground(String... params) {
		taskStarted = taskStarted + 1;
		try {
			//Log.v(LOG_TAG, "com.mysql.jdbc.Driver");
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, pass);
			Log.v(LOG_TAG, "Database connection success "+url);
			
			upload_all(con);
			//testDB(con);
			con.close();
		} catch (Throwable e) {
			e.printStackTrace();
			Log.e(LOG_TAG, e.toString());
		} finally {
			taskStarted = taskStarted - 1;
		}
		return "OK";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

	public static String testDB(Connection con) {
		try {
			int n = 0;

			String result = "Database connection success\n";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from loc_log order by _id desc ");
			ResultSetMetaData rsmd = rs.getMetaData();

			result += "\n  <table border=1 > \n";
			while ((rs.next())&&(n<10)) {
				result += " <tr> \n";
				for (int j=1;j<rsmd.getColumnCount();j++){
					result += "<dt> "+rsmd.getColumnName(j) + " : " + rs.getString(j)+" </dt> \n";
				}
				result += " </tr> \n";
				++n;
			}
			result += "\n  </table> \n";
			//Log.v(LOG_TAG, "count = " + n + " \n " + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_TAG, e.toString());
			return e.toString();
		}
	}
	
	public static void upload_all(Connection con) {
		List<Location> list = (new LocDbHlp(Cnt.get())).upload_list("dt_up_jdbc");
		for (int i = 0; i < list.size(); i++) {
			Location loc = list.get(i);
			ins(con, loc);
		}		
	}
	
	// idwho ?// 
	public static void ins(Connection con, Location loc) {
		try {
			Statement st = con.createStatement();
			st = con.createStatement();
			String sql = "INSERT INTO loc_log (idwho,raw,Latitude"
						+",Longitude,Altitude,Speed"
						+",Bearing,Accuracy,Provider,DTime)"
					+" VALUES ('"+Uid.get()+"', '"+loc.toString()+"', '"+loc.getLatitude()+"','"
						+loc.getLongitude()+ "','"+loc.getAltitude()+"','"+loc.getSpeed()+"','"
						+loc.getBearing()+"','"+loc.getAccuracy()+"','"+loc.getProvider()+"','"+loc.getTime()+"' )";
			int res = st.executeUpdate(sql);
			if (res == 1)
				(new LocDbHlp(Cnt.get())).upd_upload(loc.getTime(), "dt_up_jdbc");
			//Log.v(LOG_TAG, "\n res = " + res);// + " sql =  " + sql);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_TAG, e.toString());
		}
	}
	/*	LocJdbcTask taskJ = new LocJdbcTask();
		taskJ.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,	new String[] { "" });
				
	  create table loc_log ( _id integer primary key AUTO_INCREMENT, idwho
	  text, raw text, Latitude double, Longitude double, Altitude double, Speed
	  double, Bearing double, Accuracy double, Provider text, DTime bigint, date_time
	  TIMESTAMP default CURRENT_TIMESTAMP );
	 */
}
