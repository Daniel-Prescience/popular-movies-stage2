package com.udacity.popularmovies.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.data.MovieContract.MovieEntry;
import com.udacity.popularmovies.fragments.MovieGridFragment;
import com.udacity.popularmovies.loaders.GetMoviesAsyncTaskLoader;
import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.utilities.NetworkUtils;

import static com.udacity.popularmovies.loaders.GetMoviesAsyncTaskLoader.EXTRA_SORT_BY;

public class MainActivity extends AppCompatActivity implements
        MovieGridFragment.OnListFragmentInteractionListener,
        AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BASE_MOVIES_URL = "http://api.themoviedb.org/3/movie/";
    public static final String MOVIE_TRAILERS_SEGMENT = "/videos";
    public static final String MOVIE_REVIEWS_SEGMENT = "/reviews";
    public static final String API_SEGMENT = "?api_key=";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w185";
    private static final String SAVED_STATE_MOVIES = "SAVED_STATE_MOVIES";
    private static final String SAVED_STATE_FAVORITES = "SAVED_STATE_FAVORITES";

    public static final int LOADER_ID_MOVIES = 22;
    public static final int LOADER_ID_FAVORITE_MOVIES = 29;

    public static Movie[] MovieList;
    public static Movie[] FavoriteList = new Movie[0];
    public static String SortSelection;

    private static FragmentManager supportFragmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        supportFragmentManager = getSupportFragmentManager();

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Boolean> movieLoader = loaderManager.getLoader(LOADER_ID_FAVORITE_MOVIES);

        if (movieLoader == null)
            loaderManager.initLoader(LOADER_ID_FAVORITE_MOVIES, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SortSelection != null && SortSelection.equals(getString(R.string.favorites))) {
            MovieList = FavoriteList;
        }
        else
            getSupportLoaderManager().restartLoader(LOADER_ID_FAVORITE_MOVIES, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        SortSelection = item.toString();
        LoaderManager loaderManager = getSupportLoaderManager();

        if (SortSelection.equals(getString(R.string.favorites))){
            MovieList = FavoriteList;
            RefreshFragmentData();
        }
        else if (NetworkUtils.IsOnline(this)) {
            Bundle taskLoaderBundle = new Bundle();
            taskLoaderBundle.putString(EXTRA_SORT_BY, SortSelection);

            loaderManager.restartLoader(LOADER_ID_MOVIES, taskLoaderBundle, this);
        }
    }

    private void RefreshFragmentData() { //boolean restoreState) {
        //region Inspired by: https://stackoverflow.com/a/31832721/5999847
        if (supportFragmentManager != null) {
            MovieGridFragment fragment = (MovieGridFragment) supportFragmentManager.findFragmentById(R.id.fragment_movie_grid);

            // Notify fragment that the data set has changed and it should update is adapter.
            if (fragment != null) {
                fragment.NotifyChange();

/*                if (restoreState) {
                    Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE);
                    fragment.mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
                }*/
            }
        }
        //endregion
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        if (id == LOADER_ID_FAVORITE_MOVIES){
            return new CursorLoader(this,
                    MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }
        else {
            return new GetMoviesAsyncTaskLoader(this, args);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object loaderData) {
        if (loaderData != null) {
            if (loader.getId() == LOADER_ID_FAVORITE_MOVIES) {
                Cursor cursor = (Cursor) loaderData;
                Movie[] favorites = new Movie[cursor.getCount()];

                int i = 0;
                if (cursor.moveToFirst()) {
                    do {
                        favorites[i] = new Movie(
                                cursor.getLong(MovieEntry.COLUMN_INDEX_MOVIE_ID),
                                cursor.getString(MovieEntry.COLUMN_INDEX_TITLE),
                                cursor.getString(MovieEntry.COLUMN_INDEX_OVERVIEW),
                                cursor.getString(MovieEntry.COLUMN_INDEX_RELEASE_DATE),
                                cursor.getString(MovieEntry.COLUMN_INDEX_POSTER_PATH),
                                cursor.getString(MovieEntry.COLUMN_INDEX_VOTE_AVERAGE),
                                cursor.getString(MovieEntry.COLUMN_INDEX_POPULARITY),
                                true
                        );

                        i++;
                    }
                    while (cursor.moveToNext());
                }

                FavoriteList = favorites;

                if (SortSelection != null && SortSelection.equals(getString(R.string.favorites)))
                    MovieList = FavoriteList;
            }
            else if (SortSelection == null || !SortSelection.equals(getString(R.string.favorites))){
                MovieList = (Movie[]) loaderData;
            }

            RefreshFragmentData();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) { }

    private void launchDetailActivity(Movie movie) {
        for (Movie favoriteMovie : FavoriteList) {
            if (favoriteMovie.id != null && favoriteMovie.id.equals(movie.id))
                movie.favorite = true;
        }

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(Movie item) {
        launchDetailActivity(item);
    }

}
