package me.noip.vmv857.loc;

import kz.alfa.util.Log;
import kz.alfa.util.Pref;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocAllDbHlp extends SQLiteOpenHelper {
	public static final String LOG_TAG = "LocDbHlp";
	public static final String DB_NAME = "LocAllDB";
	public static final String TAB_NAME = "locAll";

	public LocAllDbHlp(Context context) {
		// конструктор суперкласса
		super(context, DB_NAME, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(LOG_TAG, "--- onCreate database ---");
	
		db.execSQL("create table idwho ("
				+ "_id integer primary key, " //  с сервера
				+ "idwho varchar(100) unique not null, " // источник
				+ "cnt_gps INTEGER, " // 
				+ "cnt_net INTEGER, " // 
				+ "date_time TIMESTAMP default CURRENT_TIMESTAMP );");

		db.execSQL("create table "+TAB_NAME+" ("
				+ "_id integer primary key ," //  с сервера
				+ "idwho text," // ,"источник"
				+ "Latitude real," // "Широта (°)"
				+ "Longitude real," // ,"Долгота (°)"
				+ "Altitude real," // ,"Высота (м)"
				+ "Speed real," // ,"Скорость (м/с)"
				+ "Bearing real," // ,,"Азимут (°)"
				+ "Accuracy real," // ,"Точность (м)"
				+ "Provider text," // ,"провайдер этих данных"
				+ "DTime INTEGER," // ,"Время в милесикундах"
				+ "d_t text," // ,"время с сервера"
				+ "date_time TIMESTAMP default CURRENT_TIMESTAMP );");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public long ins(LocationEx loc) {
		//Log.e(LOG_TAG, "ins_loc loc.getTime() = "+loc.getTime()+" loc.getAltitude()= "+loc.getAltitude());
		ContentValues cv = new ContentValues();
		SQLiteDatabase db = this.getWritableDatabase();
		cv.put("Altitude", loc.getAltitude()); // + "Altitude number," // ,"Высота (м)"
		cv.put("Latitude", loc.getLatitude()); // + "Latitude number," // "Широта (°)"	
		cv.put("Longitude", loc.getLongitude()); // + "Longitude number," // ,"Долгота (°)"
		cv.put("Speed", loc.getSpeed()); // + "Speed number," // ,"Скорость (м/с)"
		cv.put("Bearing", loc.getBearing()); // + "Bearing number," // ,,"Азимут (°)"
		cv.put("Accuracy", loc.getAccuracy()); // + "Accuracy number," // ,"Точность (м)"
		cv.put("Provider", loc.getProvider()); // ,"провайдер этих данных"
		cv.put("DTime", loc.getTime()); // + "DTime TIMESTAMP," // ,"Время из loc"
		
		cv.put("_id", loc._id);
		cv.put("idwho", loc.idwho);
		cv.put("d_t", loc.d_t);
		long res = db.insert(TAB_NAME, null, cv);
		//Log.e(LOG_TAG, "after row inserted  result = " + res);
		if (res > 0){ // чтобы не качать повторно
			long l = Pref.getLong("last_loc_id", 0);
			if (loc._id > l)
				Pref.edit("last_loc_id", loc._id);
		}
		this.close();
		return res;
	}

	public Cursor get_loc(String where) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(TAB_NAME, null, where, null, null, null, " _id desc ");
		return c;
	}

	public static LocationEx getLoc(Cursor c){
		LocationEx l = new LocationEx("gps");
		l.setProvider(c.getString(c.getColumnIndex("Provider")));
		l.setLatitude(Double.valueOf(c.getDouble(c.getColumnIndex("Latitude"))));
		l.setLongitude(Double.valueOf(c.getDouble(c.getColumnIndex("Longitude"))));
		l.setAltitude(Double.valueOf(c.getDouble(c.getColumnIndex("Altitude"))));
		l.setSpeed(Float.valueOf(c.getFloat(c.getColumnIndex("Speed"))));
		l.setBearing(Float.valueOf(c.getFloat(c.getColumnIndex("Bearing"))));
		l.setAccuracy(Float.valueOf(c.getFloat(c.getColumnIndex("Accuracy"))));
		l.setTime(c.getLong(c.getColumnIndex("DTime")));		
		l._id = c.getLong(c.getColumnIndex("_id"));
		l.d_t = c.getString(c.getColumnIndex("d_t"));
		l.idwho = c.getString(c.getColumnIndex("idwho"));
		
		return l;
	}

	public static void s_print_loc_idwho(Context cnt){
		LocAllDbHlp dh = new LocAllDbHlp(cnt);
		dh.print_loc_idwho();
	}
	
	public void print_loc_idwho() {
		SQLiteDatabase db = this.getReadableDatabase();
		Log.v(LOG_TAG, "--- Rows in loc: ---");
		Cursor c = db.query(true, TAB_NAME, new String[]{"idwho"}, null, null, null, null, null, null);
		String prn = "\n <table border=1 >";
		if (c.moveToFirst()) {
			int n = 0;
			do {
				n++;
				prn += "\n <tr>";
				for(int i=0; i<c.getColumnCount(); ++i)
					prn += "\n <td>"+i+" "+c.getColumnName(i)+" = "+c.getString(i)+"</td>";
				prn += "\n </tr>";
			} while ((n<100)&&(c.moveToNext()));
			prn = prn + "\n </table>";
			Log.e(LOG_TAG, prn);
			Log.e(LOG_TAG, n+" rows");
		} else{
			Log.e(LOG_TAG, "0 rows");
		}
		c.close();
		this.close();
	}

	public String get_idwho(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query("idwho", null, "_id="+id, null, null, null, null);
		if (c.moveToFirst()) {
			return c.getString(c.getColumnIndex("idwho"));
		}
		c.close();
		this.close();
		return "%";
	}

	public Cursor get_idwho() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query("idwho", null, null, null, null, null, " _id desc ");
		return c;
	}

}
