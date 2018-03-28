package com.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.udacity.popularmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements MovieGridFragment.OnListFragmentInteractionListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BASE_MOVIES_URL = "http://api.themoviedb.org/3/movie/";
    public static final String API_SEGMENT = "?api_key=";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w185";
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

    private static FragmentManager supportFragmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        supportFragmentManager = getSupportFragmentManager();
    }

    // Credit: https://stackoverflow.com/a/37250623/5999847
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        Spinner spinner = (Spinner) menu.findItem(R.id.spinner).getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        return true;
    }

    private static String getSortBy(String sort) {
        if (sort.equals("Popularity"))
            return "popular";
        else if (sort.equals("Rating"))
            return "top_rated";
        return "popular";
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object item = parent.getItemAtPosition(position);

        if (isOnline()) {
            GetMoviesTask getMovies = new GetMoviesTask();
            getMovies.execute(item.toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private static class GetMoviesTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... sort) {
            return getMovies(sort[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {

                //region Inspired by: https://stackoverflow.com/a/31832721/5999847
                if (supportFragmentManager != null) {
                    MovieGridFragment fragment = (MovieGridFragment) supportFragmentManager.findFragmentById(R.id.fragment);

                    // Notify fragment that the data set has changed and it should update is adapter.
                    if (fragment != null)
                        fragment.NotifyChange();
                }
                //endregion
            }

            super.onPostExecute(result);
        }
    }

    private static boolean getMovies(String sort) {
        HttpURLConnection urlConnection = null;

        try {
            String requestUrlString = BASE_MOVIES_URL + getSortBy(sort) + API_SEGMENT + BuildConfig.API_KEY;
            Uri builtUri = Uri.parse(requestUrlString);
            URL url = new URL(builtUri.toString());
            String responseBody = null;

            //region Inspired by NetworkUtils
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext())
                responseBody = scanner.next();
            //endregion

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

            return true;
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
            return false;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error", e);
            return false;
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
            return false;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    private void launchDetailActivity(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    // Inspired by: https://stackoverflow.com/a/4009133/5999847
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;

        if (cm != null)
            netInfo = cm.getActiveNetworkInfo();

        boolean online = netInfo != null && netInfo.isConnectedOrConnecting();

        if (!online)
            ShowToastMessage("No network connection. Please try again later.");

        return online;
    }

    @Override
    public void onListFragmentInteraction(Movie item) {
/*        ShowToastMessage(item.title + " clicked");*/

        launchDetailActivity(item);
    }

    private void ShowToastMessage(String message) {
        if (toastMessage!= null) {
            toastMessage.cancel();
        }
        toastMessage = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toastMessage.show();
    }
}
