package com.wenbchen.android.imdb.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieSQLiteHelper extends SQLiteOpenHelper {
	  public static final String TAG = "MySQLiteHelper";

	  public static final String TABLE_NAME = "watched_movies";
	  public static final String COLUMN_NAME_ID = "_id";
	  public static final String COLUMN_IS_WATCHED = "is_watched";

	  private static final String DATABASE_NAME = "watched.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
	      + TABLE_NAME + "(" +
			COLUMN_NAME_ID + " TEXT PRIMARY KEY, " +
	        COLUMN_IS_WATCHED + " BOOLEAN" +
	      ")";

	  public MovieSQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MovieSQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	    onCreate(db);
	  }
	} 
