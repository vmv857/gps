package kz.alfa.util;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class LocDbHlp extends SQLiteOpenHelper {
	public static final String LOG_TAG = "LocDbHlp";

	public LocDbHlp(Context context) {
		// конструктор суперкласса
		super(context, "LocDB", null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Log.e(LOG_TAG, "--- onCreate database ---");
		
		db.execSQL("create table loc ("
				+ "_id integer primary key autoincrement," 
				+ "raw text," // object
				+ "Latitude real," // "Широта (°)"
				+ "Longitude real," // ,"Долгота (°)"
				+ "Altitude real," // ,"Высота (м)"
				+ "Speed real," // ,"Скорость (м/с)"
				+ "Bearing real," // ,,"Азимут (°)"
				+ "Accuracy real," // ,"Точность (м)"
				+ "Provider text," // ,"провайдер этих данных"
				+ "DTime INTEGER," // ,"Время в милесикундах"
				+ "dt_up_jdbc INTEGER," // ,"выгружено ли в милесикундах jdbc"
				+ "dt_up_http INTEGER," // ,"выгружено ли в милесикундах http"
				+ "date_time TIMESTAMP default CURRENT_TIMESTAMP );");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion == 2){
			//Log.v(LOG_TAG, oldVersion+"--- onUpgrade database ---"+newVersion);
			db.execSQL("drop table loc ; ");
			onCreate(db);
		}
	}

	public long ins_loc(Location loc) {
		//Log.e(LOG_TAG, "ins_loc loc.getTime() = "+loc.getTime()+" loc.getAltitude()= "+loc.getAltitude());
		ContentValues cv = new ContentValues();
		SQLiteDatabase db = this.getWritableDatabase();
		cv.put("Raw", loc.toString());
		cv.put("Altitude", loc.getAltitude()); // + "Altitude number," // ,"Высота (м)"
		cv.put("Latitude", loc.getLatitude()); // + "Latitude number," // "Широта (°)"	
		cv.put("Longitude", loc.getLongitude()); // + "Longitude number," // ,"Долгота (°)"
		cv.put("Speed", loc.getSpeed()); // + "Speed number," // ,"Скорость (м/с)"
		cv.put("Bearing", loc.getBearing()); // + "Bearing number," // ,,"Азимут (°)"
		cv.put("Accuracy", loc.getAccuracy()); // + "Accuracy number," // ,"Точность (м)"
		cv.put("Provider", loc.getProvider()); // ,"провайдер этих данных"
		cv.put("DTime", loc.getTime()); // + "DTime TIMESTAMP," // ,"Время из loc"
		long res = db.insert("loc", null, cv);
		//Log.d(LOG_TAG, "after row inserted  result = " + res);
		this.close();
		return res;
	}

	public int upd_upload(long dtime, String col_upd) {
		//Log.e(LOG_TAG, col_upd+" upd_upload DTime = "+dtime);
		ContentValues cv = new ContentValues();
		SQLiteDatabase db = this.getWritableDatabase();
		cv.put(col_upd, System.currentTimeMillis());
		String[] w = {dtime+""};
		int res = db.update("loc", cv, "DTime = ?", w );
		//Log.e(LOG_TAG, "after row updated  result = " + res);
		this.close();
		return res;
	}

	public void print_loc() {
		SQLiteDatabase db = this.getWritableDatabase();
		//Log.v(LOG_TAG, "--- Rows in loc: ---");
		Cursor c = db.query("loc", null, null, null, null, null, " _id desc ");
		String prn = "\n <table border=1 >";
		if (c.moveToFirst()) {
			int n = 0;
			do {
				n++;
				prn += "\n <tr>";
				for(int i=0; i<c.getColumnCount(); ++i)
					prn += "\n <td>"+i+" "+c.getColumnName(i)+" = "+c.getString(i)+"</td>";
				prn += "\n </tr>";
			} while ((n<10)&&(c.moveToNext()));
			prn = prn + "\n </table>";
			//Log.v(LOG_TAG, prn);
			//Log.v(LOG_TAG, n+" rows");
		} else{
			//Log.v(LOG_TAG, "0 rows");
		}
		c.close();
		this.close();
	}

	public List<Location> upload_list(String col_upd) {
		List<Location> list = new ArrayList<Location>();
		SQLiteDatabase db = this.getWritableDatabase();
		//Log.v(LOG_TAG, "--- upload: ---col_upd="+col_upd);
		Cursor c = db.query("loc", null, col_upd+" is null", null, null, null, null);
		if (c.moveToFirst()) {
			int n = 0;
			do {
				n++;
				list.add(getLoc(c));
			} while ((c.moveToNext()));
			Log.v(LOG_TAG, n+" rows to upload for "+col_upd);
		} else {
			//Log.e(LOG_TAG, "0 rows to upload for "+ col_upd);
		}
		c.close();
		this.close();
		return list;
	}
	
	public static Location getLoc(Cursor c){
		Location l = new Location("gps");
		l.setProvider(c.getString(c.getColumnIndex("Provider")));
		l.setLatitude(Double.valueOf(c.getDouble(c.getColumnIndex("Latitude"))));
		l.setLongitude(Double.valueOf(c.getDouble(c.getColumnIndex("Longitude"))));
		l.setAltitude(Double.valueOf(c.getDouble(c.getColumnIndex("Altitude"))));
		l.setSpeed(Float.valueOf(c.getFloat(c.getColumnIndex("Speed"))));
		l.setBearing(Float.valueOf(c.getFloat(c.getColumnIndex("Bearing"))));
		l.setAccuracy(Float.valueOf(c.getFloat(c.getColumnIndex("Accuracy"))));
		long dt = c.getLong(c.getColumnIndex("DTime"));
		l.setTime(dt);			
		return l;
	}


}
