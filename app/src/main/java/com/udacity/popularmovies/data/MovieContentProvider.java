package com.udacity.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.udacity.popularmovies.data.MovieContract.MovieEntry;

public class MovieContentProvider extends ContentProvider {

    private static final int MOVIES = 100;
    private static final int MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    private MovieDbHelper mMovieDbHelper;

    public MovieContentProvider() {
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);

        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(MovieEntry.TABLE_NAME, null, values);

                if (id > 0){
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();

        if (context != null)
            context.getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match) {
            case MOVIES:
                returnCursor = db.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                        );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();

        if (context != null)
            returnCursor.setNotificationUri(context.getContentResolver(), uri);

        return returnCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int numRowsDeleted;

        switch (match) {
            case MOVIES:
                numRowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();

        if (numRowsDeleted != 0 && context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                // directory
                return "vnd.android.cursor.dir" + "/" + MovieContract.AUTHORITY + "/" + MovieContract.PATH_MOVIES;
            case MOVIE_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + MovieContract.AUTHORITY + "/" + MovieContract.PATH_MOVIES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
