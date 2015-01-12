package kz.alfa;

import me.noip.vmv857.loc.LocAllDbHlp;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LocCntProv extends ContentProvider {
	final String LOG_TAG = "LocCntProv";

	static final String DB_NAME = "LocAllDB";
	static final int DB_VERSION = 2;
	static final String LOC_TABLE = "locAll";

	// Поля
	static final String LOC_ID = "_id";
	static final String LOC_WHO = "idwho";
	static final String LOC_EMAIL = "Latitude";

	// // Uri
	// authority
	static final String AUTHORITY = "me.noip.allloc.prv";

	// path
	static final String LOC_PATH = "loc";

	// Общий Uri
	public static final Uri LOC_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + LOC_PATH);

	// Типы данных
	// набор строк
	static final String LOC_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
			+ AUTHORITY + "." + LOC_PATH;

	// одна строка
	static final String LOC_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
			+ AUTHORITY + "." + LOC_PATH;

	// // UriMatcher
	// общий Uri
	static final int URI_CONTACTS = 1;

	// Uri с указанным ID
	static final int URI_CONTACTS_ID = 2;

	// описание и создание UriMatcher
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, LOC_PATH, URI_CONTACTS);
		uriMatcher.addURI(AUTHORITY, LOC_PATH + "/#", URI_CONTACTS_ID);
	}

	LocAllDbHlp dbHelper; // my one for db
	SQLiteDatabase db;

	public boolean onCreate() {
		Log.d(LOG_TAG, "onCreate");
		dbHelper = new LocAllDbHlp(getContext());
		return true;
	}

	// чтение
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.d(LOG_TAG, "query, " + uri.toString());
		// проверяем Uri
		switch (uriMatcher.match(uri)) {
		case URI_CONTACTS: // общий Uri
			Log.d(LOG_TAG, "URI_CONTACTS");
			// если сортировка не указана, ставим свою - по имени
			if (TextUtils.isEmpty(sortOrder)) {
				sortOrder = LOC_WHO + " ASC";
			}
			break;
		case URI_CONTACTS_ID: // Uri с ID
			String id = uri.getLastPathSegment();
			Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
			// добавляем ID к условию выборки
			if (TextUtils.isEmpty(selection)) {
				selection = LOC_ID + " = " + id;
			} else {
				selection = selection + " AND " + LOC_ID + " = " + id;
			}
			break;
		default:
			throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(LOC_TABLE, projection, selection,
				selectionArgs, null, null, sortOrder);
		// просим ContentResolver уведомлять этот курсор
		// об изменениях данных в LOC_CONTENT_URI
		cursor.setNotificationUri(getContext().getContentResolver(),
				LOC_CONTENT_URI);
		return cursor;
	}

	public Uri insert(Uri uri, ContentValues values) {
		Log.d(LOG_TAG, "insert, " + uri.toString());
		if (uriMatcher.match(uri) != URI_CONTACTS)
			throw new IllegalArgumentException("Wrong URI: " + uri);

		db = dbHelper.getWritableDatabase();
		long rowID = db.insert(LOC_TABLE, null, values);
		Uri resultUri = ContentUris.withAppendedId(LOC_CONTENT_URI, rowID);
		// уведомляем ContentResolver, что данные по адресу resultUri изменились
		getContext().getContentResolver().notifyChange(resultUri, null);
		return resultUri;
	}

	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(LOG_TAG, "delete, " + uri.toString());
		switch (uriMatcher.match(uri)) {
		case URI_CONTACTS:
			Log.d(LOG_TAG, "URI_CONTACTS");
			break;
		case URI_CONTACTS_ID:
			String id = uri.getLastPathSegment();
			Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
			if (TextUtils.isEmpty(selection)) {
				selection = LOC_ID + " = " + id;
			} else {
				selection = selection + " AND " + LOC_ID + " = " + id;
			}
			break;
		default:
			throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		int cnt = db.delete(LOC_TABLE, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return cnt;
	}

	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.d(LOG_TAG, "update, " + uri.toString());
		switch (uriMatcher.match(uri)) {
		case URI_CONTACTS:
			Log.d(LOG_TAG, "URI_CONTACTS");

			break;
		case URI_CONTACTS_ID:
			String id = uri.getLastPathSegment();
			Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
			if (TextUtils.isEmpty(selection)) {
				selection = LOC_ID + " = " + id;
			} else {
				selection = selection + " AND " + LOC_ID + " = " + id;
			}
			break;
		default:
			throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		int cnt = db.update(LOC_TABLE, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return cnt;
	}

	public String getType(Uri uri) {
		Log.d(LOG_TAG, "getType, " + uri.toString());
		switch (uriMatcher.match(uri)) {
		case URI_CONTACTS:
			return LOC_CONTENT_TYPE;
		case URI_CONTACTS_ID:
			return LOC_CONTENT_ITEM_TYPE;
		}
		return null;
	}
}