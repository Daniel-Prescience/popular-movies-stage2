package com.udacity.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.udacity.popularmovies.activities.MainActivity.API_SEGMENT;
import static com.udacity.popularmovies.activities.MainActivity.BASE_MOVIES_URL;
import static com.udacity.popularmovies.utilities.NetworkUtils.GetResponseFromUrl;

public class GetMoviesAsyncTaskLoader extends AsyncTaskLoader<Movie[]> {

    private String m_currentSorting = null;
    private static Map<String, Movie[]> m_moviesBySorting = new HashMap<>();

    private static final String TAG = GetMoviesAsyncTaskLoader.class.getSimpleName();

    public static final String EXTRA_SORT_BY = "EXTRA_SORT_BY";

    private static final String JSON_RESULTS_KEY = "results";
    private static final String JSON_ID_KEY = "id";
    private static final String JSON_TITLE_KEY = "original_title";
    private static final String JSON_OVERVIEW_KEY = "overview";
    private static final String JSON_RELEASE_DATE_KEY = "release_date";
    private static final String JSON_POSTER_KEY = "poster_path";
    private static final String JSON_RATING_KEY = "vote_average";
    private static final String JSON_POPULARITY_KEY = "popularity";

    public GetMoviesAsyncTaskLoader(Context context, Bundle args) {
        super(context);

        if (args != null)
            m_currentSorting = args.getString(EXTRA_SORT_BY);
    }

    private static String getSortBy(String sort) {
        if (sort.equals("Popularity"))
            return "popular";
        else if (sort.equals("Rating"))
            return "top_rated";
        return "popular";
    }
    @Override
    protected void onStartLoading() {
        if (m_moviesBySorting != null && m_moviesBySorting.containsKey(m_currentSorting)) {
            // Delivers any previously loaded data immediately
            deliverResult(m_moviesBySorting.get(m_currentSorting));
        } else {
            // Force a new load
            forceLoad();
        }
    }

    @Override
    public Movie[] loadInBackground() {
        if (m_currentSorting == null || TextUtils.isEmpty(m_currentSorting))
            return null;

        return getMovies(m_currentSorting);
    }

    @Override
    public void deliverResult(Movie[] data) {
        m_moviesBySorting.put(m_currentSorting, data);
        super.deliverResult(data);
    }

    private Movie[] getMovies(String sort) {
        try {
            String requestUrlString = BASE_MOVIES_URL + getSortBy(sort) + API_SEGMENT + BuildConfig.API_KEY;

            String responseBody = GetResponseFromUrl(requestUrlString, TAG);

            JSONObject jsonObj = new JSONObject(responseBody);
            JSONArray moviesJson = jsonObj.getJSONArray(JSON_RESULTS_KEY);

            // looping through movies
            Movie[] movies = new Movie[moviesJson.length()];
            for (int i = 0; i < moviesJson.length(); ++i) {
                JSONObject movie = moviesJson.getJSONObject(i);
                movies[i] = new Movie(
                        movie.getLong(JSON_ID_KEY),
                        movie.getString(JSON_TITLE_KEY),
                        movie.getString(JSON_OVERVIEW_KEY),
                        movie.getString(JSON_RELEASE_DATE_KEY),
                        movie.getString(JSON_POSTER_KEY),
                        movie.getString(JSON_RATING_KEY),
                        movie.getString(JSON_POPULARITY_KEY),
                        false);
            }

            return movies;
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
            return null;
        }
    }
}