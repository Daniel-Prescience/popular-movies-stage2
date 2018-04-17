package com.udacity.popularmovies.activities;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.data.MovieContract.MovieEntry;
import com.udacity.popularmovies.fragments.ReviewListFragment;
import com.udacity.popularmovies.fragments.TrailerListFragment;
import com.udacity.popularmovies.loaders.GetMovieReviewsAsyncTaskLoader;
import com.udacity.popularmovies.loaders.GetMovieTrailersAsyncTaskLoader;
import com.udacity.popularmovies.models.Movie;
import com.udacity.popularmovies.models.Review;
import com.udacity.popularmovies.models.Trailer;
import com.udacity.popularmovies.utilities.NetworkUtils;
import com.udacity.popularmovies.utilities.UserInterfaceUtils;

public class DetailActivity extends AppCompatActivity implements
        TrailerListFragment.OnListFragmentInteractionListener,
        ReviewListFragment.OnListFragmentInteractionListener,
        LoaderManager.LoaderCallbacks {

    private static final int LOADER_ID_MOVIE_TRAILERS = 23;
    private static final int LOADER_ID_MOVIE_REVIEWS = 24;
    public static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    private static final String KEY_LAYOUT_MANAGER_STATE = "KEY_LAYOUT_MANAGER_STATE";

    public static Trailer[] TrailerList;
    public static Review[] ReviewList;

    private TrailerListFragment fragmentTrailers;
    private ReviewListFragment fragmentReviews;

    private NestedScrollView mDetailsScrollView;
    private int mScrollPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //region Inspired by: https://stackoverflow.com/a/31832721/5999847
        FragmentManager supportFragmentManager = getSupportFragmentManager();

        if (supportFragmentManager != null) {
            fragmentTrailers = (TrailerListFragment) supportFragmentManager.findFragmentById(R.id.fragment_trailer_list);
            fragmentReviews = (ReviewListFragment) supportFragmentManager.findFragmentById(R.id.fragment_review_list);
        }
        //endregion

        mDetailsScrollView = findViewById(R.id.details_scrollview);

        Intent intent = getIntent();

        if (intent != null) {
            final Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);

            ImageView moviePosterIv = findViewById(R.id.movie_poster_iv);

            populateUI(movie);

            Picasso.with(this)
                    .load(MainActivity.BASE_IMAGE_URL + MainActivity.IMAGE_SIZE + movie.poster_path)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.drawable.ic_launcher_background)
                    .into(moviePosterIv);

            setTitle(movie.title);

            if (movie.id != null && NetworkUtils.IsOnline(this)) {
                Bundle taskLoaderBundle = new Bundle();
                taskLoaderBundle.putLong(GetMovieTrailersAsyncTaskLoader.EXTRA_MOVIE_ID, movie.id);

                LoaderManager loaderManager = getSupportLoaderManager();

                loaderManager.restartLoader(LOADER_ID_MOVIE_TRAILERS, taskLoaderBundle, this);
                loaderManager.restartLoader(LOADER_ID_MOVIE_REVIEWS, taskLoaderBundle, this);
            }
        } else
            closeOnError();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mScrollPosition = mDetailsScrollView.getScrollY();
        outState.putInt(KEY_LAYOUT_MANAGER_STATE, mScrollPosition);

        ToggleButton favoriteToggle = findViewById(R.id.toggleButton);
        favoriteToggle.setOnCheckedChangeListener(null);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mScrollPosition = savedInstanceState.getInt(KEY_LAYOUT_MANAGER_STATE, 0);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        if (id == LOADER_ID_MOVIE_TRAILERS)
            return new GetMovieTrailersAsyncTaskLoader(this, args);
        else
            return new GetMovieReviewsAsyncTaskLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        if (data != null) {
            if (loader.getId() == LOADER_ID_MOVIE_TRAILERS) {
                TrailerList = (Trailer[])data;

                // Notify fragment that the data set has changed and it should update is adapter.
                if (fragmentTrailers != null)
                    fragmentTrailers.NotifyChange();
            }
            else if (loader.getId() == LOADER_ID_MOVIE_REVIEWS) {
                ReviewList = (Review[])data;

                // Notify fragment that the data set has changed and it should update is adapter.
                if (fragmentReviews != null)
                    fragmentReviews.NotifyChange();
            }
        }

        mDetailsScrollView.setScrollY(mScrollPosition);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) { }

    private void closeOnError() {
        finish();
        UserInterfaceUtils.ShowToastMessage(getString(R.string.detail_error_message), this);
    }

    private void populateUI(final Movie movie) {
        TextView titleTv = findViewById(R.id.title_tv);
        TextView releaseDateTv = findViewById(R.id.release_date_tv);
        TextView voteAverageTv = findViewById(R.id.vote_average_tv);
        TextView plotSynopsisTv = findViewById(R.id.plot_synopsis_tv);
        ToggleButton favoriteToggle = findViewById(R.id.toggleButton);

        titleTv.setText(movie.title);
        releaseDateTv.setText(movie.release_date);
        voteAverageTv.setText(movie.vote_average);
        plotSynopsisTv.setText(movie.overview);
        favoriteToggle.setChecked(movie.favorite);

        favoriteToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieEntry.COLUMN_NAME_MOVIE_ID, movie.id);
                    contentValues.put(MovieEntry.COLUMN_NAME_TITLE, movie.title);
                    contentValues.put(MovieEntry.COLUMN_NAME_OVERVIEW, movie.overview);
                    contentValues.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.release_date);
                    contentValues.put(MovieEntry.COLUMN_NAME_POSTER_PATH, movie.poster_path);
                    contentValues.put(MovieEntry.COLUMN_NAME_VOTE_AVERAGE, movie.vote_average);
                    contentValues.put(MovieEntry.COLUMN_NAME_POPULARITY, movie.popularity);

                    Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI, contentValues);

                    if (uri != null)
                        UserInterfaceUtils.ShowToastMessage(movie.title + " added to favorites.", DetailActivity.this);
                }
                else if (movie.id != null) {
                    // The toggle is disabled
                    int deleted = getContentResolver().delete(MovieEntry.CONTENT_URI, MovieEntry.COLUMN_NAME_MOVIE_ID + " = ? ", new String[]{Long.toString(movie.id)});

                    if (deleted > 0) {
                        UserInterfaceUtils.ShowToastMessage(movie.title + " removed from favorites.", DetailActivity.this);
                    }
                }
            }
        });
    }

    @Override
    public void onListFragmentInteraction(Trailer trailer) {
        if (NetworkUtils.IsOnline(this)) {
            //region Credit to: https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.key));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.key));
            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(webIntent);
            }
            //endregion
        }
    }

    @Override
    public void onListFragmentInteraction(Review review) { }
}
