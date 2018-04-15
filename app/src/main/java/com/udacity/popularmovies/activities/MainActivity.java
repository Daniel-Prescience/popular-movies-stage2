package com.udacity.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.fragments.MovieGridFragment;
import com.udacity.popularmovies.loaders.GetMoviesAsyncTaskLoader;
import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.utilities.NetworkUtils;

import static com.udacity.popularmovies.loaders.GetMoviesAsyncTaskLoader.EXTRA_SORT_BY;

public class MainActivity extends AppCompatActivity implements
        MovieGridFragment.OnListFragmentInteractionListener,
        AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Movie[]> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BASE_MOVIES_URL = "http://api.themoviedb.org/3/movie/";
    public static final String MOVIE_TRAILERS_SEGMENT = "/videos";
    public static final String MOVIE_REVIEWS_SEGMENT = "/reviews";
    public static final String API_SEGMENT = "?api_key=";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w185";
    private static final String SAVED_STATE_MOVIES = "SAVED_STATE_MOVIES";

    public static final int LOADER_ID_MOVIES = 22;

    public static Movie[] MovieList;

    private static FragmentManager supportFragmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        supportFragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(LOADER_ID_MOVIES, null, null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(SAVED_STATE_MOVIES, MovieList);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object item = parent.getItemAtPosition(position);

        if (NetworkUtils.IsOnline(this)) {
            Bundle taskLoaderBundle = new Bundle();
            taskLoaderBundle.putString(EXTRA_SORT_BY, item.toString());

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<Boolean> movieLoader = loaderManager.getLoader(LOADER_ID_MOVIES);

            if (movieLoader == null)
                loaderManager.initLoader(LOADER_ID_MOVIES, taskLoaderBundle, this);
            else
                loaderManager.restartLoader(LOADER_ID_MOVIES, taskLoaderBundle, this);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @NonNull
    @Override
    public Loader<Movie[]> onCreateLoader(int id, @Nullable Bundle args) {
        return new GetMoviesAsyncTaskLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Movie[] moviesLoaded) {
        if (moviesLoaded != null) {
            MovieList = moviesLoaded;
            //region Inspired by: https://stackoverflow.com/a/31832721/5999847
            if (supportFragmentManager != null) {
                MovieGridFragment fragment = (MovieGridFragment) supportFragmentManager.findFragmentById(R.id.fragment_movie_grid);

                // Notify fragment that the data set has changed and it should update is adapter.
                if (fragment != null)
                    fragment.NotifyChange();
            }
            //endregion
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) { }

    private void launchDetailActivity(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(Movie item) {
        launchDetailActivity(item);
    }

}
