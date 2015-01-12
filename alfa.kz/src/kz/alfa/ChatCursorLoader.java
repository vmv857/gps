package kz.alfa;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import kz.alfa.util.Db_Helper;

public class ChatCursorLoader extends CursorLoader {

	Db_Helper db;
    
    public ChatCursorLoader(Context context, Db_Helper db) {
      super(context);
      this.db = db;
    }
    
    @Override
    public Cursor loadInBackground() {
      Cursor cursor = db.getAllData();
      return cursor;
    }
    
  }