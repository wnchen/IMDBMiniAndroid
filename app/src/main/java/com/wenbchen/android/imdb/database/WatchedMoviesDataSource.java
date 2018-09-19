package com.wenbchen.android.imdb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.wenbchen.android.imdb.model.WatchedMovie;

import java.util.ArrayList;
import java.util.List;

public class WatchedMoviesDataSource {
	public static final String TAG = "WatchedMoviesDataSource";
	
	// Database fields
	  private SQLiteDatabase database;
	  private MovieSQLiteHelper dbHelper;
	  private String[] allColumns = { MovieSQLiteHelper.COLUMN_NAME_ID,
	      MovieSQLiteHelper.COLUMN_IS_WATCHED };

	  public WatchedMoviesDataSource(Context context) {
	    dbHelper = new MovieSQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public void insertMovie(String uuid, int isWatched) {
	  	boolean isExisted = isExist(uuid);
	  	if (!isExisted) {
	  		insert(uuid, isWatched);
		}
	  }

	private void insert(String uuid, int isWatched) {
	  	ContentValues values = new ContentValues();
	  	values.put(MovieSQLiteHelper.COLUMN_NAME_ID, uuid);
	  	values.put(MovieSQLiteHelper.COLUMN_IS_WATCHED, isWatched);
	  	database.insert(MovieSQLiteHelper.TABLE_NAME, null, values);
	}

	private boolean isExist(String uuid) {
	  	String[] columns = new String[]{MovieSQLiteHelper.COLUMN_NAME_ID};
	  	String selection = MovieSQLiteHelper.COLUMN_NAME_ID + " =?";
	  	String[] selectionArgs = new String[] {uuid};
	  	String limit = "1";
	  	Cursor cursor = database.query(true, MovieSQLiteHelper.TABLE_NAME, columns, selection, selectionArgs, null,
				null, null, limit);
	  	boolean existed = cursor.getCount() > 0;
	  	cursor.close();
	  	return existed;

	}

	public void deleteComment(WatchedMovie watchedMovie) {
	    long id = watchedMovie.getId();
	    System.out.println("watchedMovie deleted with uuid: " + id);
	    database.delete(MovieSQLiteHelper.TABLE_NAME, MovieSQLiteHelper.COLUMN_NAME_ID
	        + " = " + id, null);
	  }

	  public List<WatchedMovie> getAllComments() {
	    List<WatchedMovie> comments = new ArrayList<WatchedMovie>();

	    Cursor cursor = database.query(MovieSQLiteHelper.TABLE_NAME,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	WatchedMovie comment = cursorToComment(cursor);
	    	comments.add(comment);
	    	cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return comments;
	  }

	  private WatchedMovie cursorToComment(Cursor cursor) {
		WatchedMovie watchedMovie = new WatchedMovie();
		watchedMovie.setId(cursor.getLong(0));
		watchedMovie.setWatched(cursor.getInt(1));
	    return watchedMovie;
	  }
	  
	  public boolean exists(String _id) {
		  if (database == null) return false;
		  Cursor cursor = database.rawQuery("select 1 from watched_movies where _id=?", 
			        new String[] { _id });
		   boolean exists = (cursor.getCount() > 0);
		   cursor.close();
		   return exists;
	  }
	} 
