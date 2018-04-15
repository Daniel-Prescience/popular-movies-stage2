package com.udacity.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY = "com.udacity.popularmovies";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    private MovieContract() {}

    public static class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        public static final String COLUMN_NAME_POSTER_PATH = "poster_path";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_NAME_POPULARITY = "popularity";

        public static final int COLUMN_INDEX_ROW_ID = 0;
        public static final int COLUMN_INDEX_MOVIE_ID = 1;
        public static final int COLUMN_INDEX_TITLE = 2;
        public static final int COLUMN_INDEX_OVERVIEW = 3;
        public static final int COLUMN_INDEX_RELEASE_DATE = 4;
        public static final int COLUMN_INDEX_POSTER_PATH = 5;
        public static final int COLUMN_INDEX_VOTE_AVERAGE = 6;
        public static final int COLUMN_INDEX_POPULARITY = 7;
    }
}
