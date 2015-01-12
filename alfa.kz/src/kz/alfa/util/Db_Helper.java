package kz.alfa.util;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class Db_Helper extends SQLiteOpenHelper {

	final public static String LOG_TAG = "Db_Helper";
	final public static String DB_NAME = "dbChat";
	final public static int DB_VERSION = 2;
	private SQLiteDatabase mDB;

	public Db_Helper(Context context) {
		// конструктор суперкласса
		super(context, DB_NAME, null, DB_VERSION);
		mDB = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Log.e(LOG_TAG, "--- before onCreate database ---");
		// создаем таблицу url-ов
		db.execSQL("create table tblChat ("
				+ "_id integer primary key autoincrement," // обязательное поле
				+ "id_serv integer unique , " + "who text ," + "what text,"
				+ "toh text," + "dt_serv text," + "udt text,"
				+ "date_time TIMESTAMP default CURRENT_TIMESTAMP );");
		//Log.e(LOG_TAG, "--- after onCreate database ---");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void ins_msg(ChMsg msg) {
		ContentValues cv = new ContentValues();
		// подключаемся к БД
		SQLiteDatabase db = mDB;

		//Log.e(LOG_TAG, "--- before Insert in mytable: ---" + msg);
		// подготовим данные для вставки в виде пар: наименование
		// столбца=значение
		if (msg.id_serv != null) {
			cv.put("id_serv", msg.id_serv);
			//Log.e(LOG_TAG, "--- b put" + msg.id_serv);
		} else {
			cv.putNull("id_serv");
			//Log.e(LOG_TAG, "--- b putNull" + msg.id_serv);
		}
		cv.put("who", msg.who);
		cv.put("what", msg.what);
		cv.put("toh", msg.toh);
		cv.put("udt", msg.udt);
		cv.put("dt_serv", msg.dt_serv);
		// вставляем запись и получаем ее ID
		long rowID = db.insert("tblChat", null, cv);
		//Log.e(LOG_TAG, msg + " ins_msg rowID = " + rowID);
		if ((rowID >= 1) && (msg.id_serv != null)) {
			if (Pref.getLong("last_id", 0) < Long.valueOf(msg.id_serv))
				Pref.edit("last_id", Long.valueOf(msg.id_serv));
		}
		//Log.e(LOG_TAG, "row inserted, ID = " + rowID);
	}

	public int get_max_id() {
		int n = 0;
		// подключаемся к БД
		SQLiteDatabase db = mDB;
		// делаем запрос всех данных из таблицы mytable, получаем Cursor
		Cursor c = db.query("tblChat", null, null, null, null, null,
				" id_serv DESC ");
		if (c.moveToFirst())
			n = c.getInt(c.getColumnIndex("id_serv"));
		//Log.e(LOG_TAG, " get_max_id= " + n);
		c.close();
		return n;
	}

	public List<ChMsg> get_up_list() { // ограничить размер списка
		List<ChMsg> list = new ArrayList<ChMsg>();
		int n = 0;
		// подключаемся к БД
		SQLiteDatabase db = mDB;
		// делаем запрос всех данных из таблицы mytable, получаем Cursor
		Cursor c = db.query("tblChat", null, "id_serv is null", null, null,
				null, " _id ASC ");

		// ставим позицию курсора на первую строку выборки
		// если в выборке нет строк, вернется false
		if (c.moveToFirst()) {
			do {
				n = n + 1;
				ChMsg msg = new ChMsg();
				msg.who = c.getString(c.getColumnIndex("who"));
				msg.what = c.getString(c.getColumnIndex("what"));
				msg.toh = c.getString(c.getColumnIndex("toh"));
				msg.udt = c.getString(c.getColumnIndex("udt"));

				list.add(msg);
			} while ((c.moveToNext()));// && (n < cnt));
		}
		//Log.e(LOG_TAG, n + " rows selected ");
		c.close();
		return list;
	}

	// удалить не отправленные 
	public void del_all() {
		mDB.delete("tblChat", null, null);
	}

	// удалить не отправленные 
	public void del_unsend() {
		mDB.delete("tblChat", "id_serv is null", null);
	}
	
	// получить все данные из таблицы DB_TABLE
	public Cursor getAllData() {
		Cursor c = mDB.query("tblChat", null, "id_serv is null", null, null,
				null, " _id ASC ");
		if (c.moveToFirst()) {
			do {
				ChMsg msg = new ChMsg();
				msg.who = c.getString(c.getColumnIndex("who"));
				msg.udt = c.getString(c.getColumnIndex("udt"));
				// Log.e(LOG_TAG, msg.udt+" msg.who = " + msg.who );
				Cursor cR = mDB.query("tblChat", null,
						" ((id_serv IS NOT NULL) and " + "(who='" + msg.who
								+ "') and " + "( udt='" + msg.udt + "')) ",
						null, null, null, " _id ASC ");
				//Log.e(LOG_TAG,msg.udt + " msg.who = " + msg.who + " cr="+ cR.getCount());
				if (cR.moveToFirst()) {
					int del = mDB.delete("tblChat",
							" _id =" + c.getString(c.getColumnIndex("_id")),
							null);
					Log.v(LOG_TAG, msg.udt + " delRes = " + del);
				}
			} while ((c.moveToNext()));
		}

		return mDB.query("tblChat", null, " ((id_serv is null) or (id_serv > "+Pref.getLong("show_id", 0)+")) "
				, null, null, null, " ifnull(id_serv, 999999999999) ASC, _id ASC ");
	}
}
