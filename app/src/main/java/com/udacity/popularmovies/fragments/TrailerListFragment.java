package com.udacity.popularmovies.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.activities.DetailActivity;
import com.udacity.popularmovies.adapters.TrailerRecyclerViewAdapter;
import com.udacity.popularmovies.models.Trailer;

public class TrailerListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;

    public TrailerListFragment() { }

    @SuppressWarnings("unused")
    public static TrailerListFragment newInstance() {
        return new TrailerListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trailer_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new TrailerRecyclerViewAdapter(DetailActivity.TrailerList, mListener));
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
            recyclerView.setAdapter(new TrailerRecyclerViewAdapter(DetailActivity.TrailerList, mListener));
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Trailer trailer);
    }
}
