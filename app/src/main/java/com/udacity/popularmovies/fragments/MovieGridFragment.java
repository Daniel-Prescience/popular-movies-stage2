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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MovieGridFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String KEY_LAYOUT_MANAGER_STATE = "KEY_LAYOUT_MANAGER_STATE";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private Parcelable mSavedRecyclerLayoutState;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieGridFragment() {
    }

    @SuppressWarnings("unused")
    public static MovieGridFragment newInstance(int columnCount) {
        MovieGridFragment fragment = new MovieGridFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;

            mGridLayoutManager = new GridLayoutManager(context, mColumnCount);
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
    public void onSaveInstanceState(Bundle outState) {
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
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Movie item);
    }
}
