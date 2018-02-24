package com.udacity.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.udacity.popularmovies.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements MovieGridFragment.OnListFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BASE_MOVIES_URL = "http://api.themoviedb.org/3/movie/";
    public static final String API_SEGMENT = "?api_key=";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w185";
    private String mSortBy = "Popularity";
    private Toast toastMessage;

    public static final String JSON_RESULTS_KEY = "results";
    public static final String JSON_ID_KEY = "id";
    public static final String JSON_TITLE_KEY = "original_title";
    public static final String JSON_OVERVIEW_KEY = "overview";
    public static final String JSON_RELEASE_DATE_KEY = "release_date";
    public static final String JSON_POSTER_KEY = "poster_path";
    public static final String JSON_RATING_KEY = "vote_average";
    public static final String JSON_POPULARITY_KEY = "popularity";

    public static Movie[] MovieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isOnline())
            getMovies(mSortBy);
    }

    private String getSortBy(String sort) {
        if (sort.equals("Popularity"))
            return "popular";
        else if (sort.equals("Rating"))
            return "top_rated";
        return "popular";
    }

    private void getMovies(String sort) {
        AsyncHttpClient client = new AsyncHttpClient();
        String requestUrl = BASE_MOVIES_URL + getSortBy(sort) + API_SEGMENT + BuildConfig.API_KEY;
        client.get(requestUrl, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                try {
                    JSONObject jsonObj = new JSONObject(responseBody);
                    JSONArray movies = jsonObj.getJSONArray(JSON_RESULTS_KEY);

                    // looping through movies
                    MovieList = new Movie[movies.length()];
                    for (int i = 0; i < movies.length(); ++i) {
                        JSONObject movie = movies.getJSONObject(i);
                        MovieList[i] = new Movie(
                                movie.getLong(JSON_ID_KEY),
                                movie.getString(JSON_TITLE_KEY),
                                movie.getString(JSON_OVERVIEW_KEY),
                                movie.getString(JSON_RELEASE_DATE_KEY),
                                movie.getString(JSON_POSTER_KEY),
                                movie.getString(JSON_RATING_KEY),
                                movie.getString(JSON_POPULARITY_KEY));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error", e);
                }

                //region Inspired by: https://stackoverflow.com/a/31832721/5999847
                MovieGridFragment fragment = (MovieGridFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

                // Notify fragment that the dataset has changed and it should update is adapter.
                if (fragment != null)
                    fragment.NotifyChange();
                //endregion
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Failed: ", "" + statusCode);
                Log.d("Error : ", "" + throwable);
            }

        });
    }

    // TODO: Credit below method to source: https://stackoverflow.com/a/4009133/5999847
    // Ensure there is an internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onListFragmentInteraction(Movie item) {
        if (toastMessage!= null) {
            toastMessage.cancel();
        }
        toastMessage = Toast.makeText(this, item.title + " clicked", Toast.LENGTH_SHORT);
        toastMessage.show();
    }
}
