package com.udacity.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.util.LongSparseArray;

import com.udacity.popularmovies.BuildConfig;
import com.udacity.popularmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.udacity.popularmovies.activities.MainActivity.API_SEGMENT;
import static com.udacity.popularmovies.activities.MainActivity.BASE_MOVIES_URL;
import static com.udacity.popularmovies.activities.MainActivity.MOVIE_TRAILERS_SEGMENT;
import static com.udacity.popularmovies.utilities.NetworkUtils.GetResponseFromUrl;

public class GetMovieTrailersAsyncTaskLoader extends AsyncTaskLoader<Trailer[]> {

    private Long mMovieId = null;
    private static LongSparseArray<Trailer[]> m_movieTrailers = new LongSparseArray<>();

    private static final String TAG = GetMovieTrailersAsyncTaskLoader.class.getSimpleName();

    public static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";

    private static final String JSON_RESULTS_KEY = "results";
    private static final String JSON_ID_KEY = "id";
    private static final String JSON_KEY_KEY = "key";
    private static final String JSON_NAME_KEY = "name";
    private static final String JSON_SITE_KEY = "site";

    public GetMovieTrailersAsyncTaskLoader(Context context, Bundle args) {
        super(context);

        if (args != null)
            mMovieId = args.getLong(EXTRA_MOVIE_ID);
    }

    @Override
    protected void onStartLoading() {
        if (m_movieTrailers != null) {
            Trailer[] trailers = m_movieTrailers.get(mMovieId);

            if (trailers != null) {
                // Delivers any previously loaded data immediately
                deliverResult(trailers);
            } else {
                // Force a new load
                forceLoad();
            }
        }
    }

    @Override
    public Trailer[] loadInBackground() {
        if (mMovieId == null)
            return null;

        return getTrailers(mMovieId);
    }

    @Override
    public void deliverResult(Trailer[] data) {
        m_movieTrailers.put(mMovieId, data);
        super.deliverResult(data);
    }

    private Trailer[] getTrailers(long movieId) {
        try {
            String requestUrlString = BASE_MOVIES_URL + movieId + MOVIE_TRAILERS_SEGMENT + API_SEGMENT + BuildConfig.API_KEY;

            String responseBody = GetResponseFromUrl(requestUrlString, TAG);

            JSONObject jsonObj = new JSONObject(responseBody);
            JSONArray trailersJson = jsonObj.getJSONArray(JSON_RESULTS_KEY);

            // looping through trailers
            Trailer[] trailers = new Trailer[trailersJson.length()];
            for (int i = 0; i < trailersJson.length(); ++i) {
                JSONObject trailerInfo = trailersJson.getJSONObject(i);

                Trailer trailer = new Trailer(
                        trailerInfo.getString(JSON_ID_KEY),
                        trailerInfo.getString(JSON_KEY_KEY),
                        trailerInfo.getString(JSON_NAME_KEY),
                        trailerInfo.getString(JSON_SITE_KEY)
                );

                trailers[i] = trailer;
            }

            return trailers;
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
            return null;
        }
    }
}