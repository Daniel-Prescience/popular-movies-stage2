package com.udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.popularmovies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_NAME_MOVIE_ID + " LONG NOT NULL," +
                MovieEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.COLUMN_NAME_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT NOT NULL," +
                MovieEntry.COLUMN_NAME_VOTE_AVERAGE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_NAME_POPULARITY + " TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
