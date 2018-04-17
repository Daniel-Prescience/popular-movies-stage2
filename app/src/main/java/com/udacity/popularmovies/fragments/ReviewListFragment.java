package com.udacity.popularmovies.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.activities.DetailActivity;
import com.udacity.popularmovies.adapters.ReviewRecyclerViewAdapter;
import com.udacity.popularmovies.models.Review;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReviewListFragment.OnListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReviewListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;

    public ReviewListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TrailerListFragment.
     */
    @SuppressWarnings("unused")
    public static ReviewListFragment newInstance() {
        return new ReviewListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new ReviewRecyclerViewAdapter(DetailActivity.ReviewList, mListener));
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void NotifyChange() {
        if (recyclerView != null)
            recyclerView.setAdapter(new ReviewRecyclerViewAdapter(DetailActivity.ReviewList, mListener));
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Review review);
    }
}
