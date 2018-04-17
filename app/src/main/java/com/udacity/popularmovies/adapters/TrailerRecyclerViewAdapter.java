package com.udacity.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.fragments.TrailerListFragment.OnListFragmentInteractionListener;
import com.udacity.popularmovies.models.Trailer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.ViewHolder> {
    private final Trailer[] mTrailers;
    private final OnListFragmentInteractionListener mListener;

    public TrailerRecyclerViewAdapter(Trailer[] trailers, OnListFragmentInteractionListener listener) {
        mTrailers = trailers;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mTrailers[position];

        // Set trailer title as list item text.
        holder.mTrailerName.setText(holder.mItem.name);

        // Set on click listener.
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mTrailers == null)
            return 0;

        return mTrailers.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailer_name_tv)
        TextView mTrailerName;

        @BindView(R.id.trailer_layout)
        ConstraintLayout mLayout;

        Trailer mItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
