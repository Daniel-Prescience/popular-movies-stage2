package com.udacity.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.util.LongSparseArray;

import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.udacity.popularmovies.activities.MainActivity.API_SEGMENT;
import static com.udacity.popularmovies.activities.MainActivity.BASE_MOVIES_URL;
import static com.udacity.popularmovies.activities.MainActivity.MOVIE_REVIEWS_SEGMENT;
import static com.udacity.popularmovies.utilities.NetworkUtils.GetResponseFromUrl;

public class GetMovieReviewsAsyncTaskLoader extends AsyncTaskLoader<Review[]> {

    private Long mMovieId = null;
    private static LongSparseArray<Review[]> m_movieReviews = new LongSparseArray<>();

    private static final String TAG = GetMovieReviewsAsyncTaskLoader.class.getSimpleName();

    public static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";

    private static final String JSON_RESULTS_KEY = "results";
    private static final String JSON_ID_KEY = "id";
    private static final String JSON_AUTHOR_KEY = "author";
    private static final String JSON_CONTENT_KEY = "content";
    private static final String JSON_URL_KEY = "url";

    public GetMovieReviewsAsyncTaskLoader(Context context, Bundle args) {
        super(context);

        if (args != null)
            mMovieId = args.getLong(EXTRA_MOVIE_ID);
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
        if (m_movieReviews != null) {
            Review[] reviews = m_movieReviews.get(mMovieId);

            if (reviews != null) {
                // Delivers any previously loaded data immediately
                deliverResult(reviews);
            } else {
                // Force a new load
                forceLoad();
            }
        }
    }

    @Override
    public Review[] loadInBackground() {
        if (mMovieId == null)
            return null;

        return getReviews(mMovieId);
    }

    @Override
    public void deliverResult(Review[] data) {
        m_movieReviews.put(mMovieId, data);
        super.deliverResult(data);
    }

    private Review[] getReviews(long movieId) {
        try {
            String requestUrlString = BASE_MOVIES_URL + movieId + MOVIE_REVIEWS_SEGMENT + API_SEGMENT + BuildConfig.API_KEY;

            String responseBody = GetResponseFromUrl(requestUrlString, TAG);

            JSONObject jsonObj = new JSONObject(responseBody);
            JSONArray reviewsJson = jsonObj.getJSONArray(JSON_RESULTS_KEY);

            // looping through reviews
            Review[] reviews = new Review[reviewsJson.length()];
            for (int i = 0; i < reviewsJson.length(); ++i) {
                JSONObject reviewInfo = reviewsJson.getJSONObject(i);

                Review review = new Review(
                        reviewInfo.getString(JSON_ID_KEY),
                        reviewInfo.getString(JSON_AUTHOR_KEY),
                        reviewInfo.getString(JSON_CONTENT_KEY),
                        reviewInfo.getString(JSON_URL_KEY)
                );

                reviews[i] = review;
            }

            return reviews;
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
            return null;
        }
    }

}