package com.udacity.popularmovies.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.activities.MainActivity;
import com.udacity.popularmovies.adapters.MovieRecyclerViewAdapter;
import com.udacity.popularmovies.models.Movie;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MovieGridFragment extends Fragment {

    private static final String KEY_LAYOUT_MANAGER_STATE = "KEY_LAYOUT_MANAGER_STATE";
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private Parcelable mSavedRecyclerLayoutState;


    public MovieGridFragment() {
    }

    @SuppressWarnings("unused")
    public static MovieGridFragment newInstance(int columnCount) {
        return new MovieGridFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;

            int columnCount = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE ? 3 : 2;

            mGridLayoutManager = new GridLayoutManager(context, columnCount);
            mRecyclerView.setLayoutManager(mGridLayoutManager);

            mRecyclerView.setAdapter(new MovieRecyclerViewAdapter(MainActivity.MovieList, mListener, context));
        }
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {
            mSavedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mSavedRecyclerLayoutState = mGridLayoutManager.onSaveInstanceState();
        outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, mSavedRecyclerLayoutState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void NotifyChange() {
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(new MovieRecyclerViewAdapter(MainActivity.MovieList, mListener, getActivity()));

            if (mSavedRecyclerLayoutState != null)
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Movie item);
    }
}
