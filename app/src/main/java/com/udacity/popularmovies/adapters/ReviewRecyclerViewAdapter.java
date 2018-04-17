package com.udacity.popularmovies.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.fragments.ReviewListFragment.OnListFragmentInteractionListener;
import com.udacity.popularmovies.models.Review;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {
    private final Review[] mReviews;
    private final OnListFragmentInteractionListener mListener;

    public ReviewRecyclerViewAdapter(Review[] reviews, OnListFragmentInteractionListener listener) {
        mReviews = reviews;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mReviews[position];

        // Set author name.
        holder.mAuthor.setText(holder.mItem.author);

        // Set review text.
        holder.mReview.setText(holder.mItem.content);

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
        if (mReviews == null)
            return 0;

        return mReviews.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_tv)
        TextView mReview;
        @BindView(R.id.author_tv)
        TextView mAuthor;

        @BindView(R.id.review_layout)
        ConstraintLayout mLayout;

        public Review mItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
