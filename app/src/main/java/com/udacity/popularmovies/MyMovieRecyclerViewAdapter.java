package com.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.Model.Movie;
import com.udacity.popularmovies.MovieGridFragment.OnListFragmentInteractionListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Movie} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMovieRecyclerViewAdapter extends RecyclerView.Adapter<MyMovieRecyclerViewAdapter.ViewHolder> {
    private static final String LOG_TAG = MyMovieRecyclerViewAdapter.class.getSimpleName();

    private final Movie[] mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;

    public MyMovieRecyclerViewAdapter(Movie[] items, OnListFragmentInteractionListener listener, Context activityContext) {
        mValues = items;
        mListener = listener;
        mContext = activityContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues[position];
        Picasso.with(mContext)
                .load(MainActivity.BASE_IMAGE_URL + MainActivity.IMAGE_SIZE + holder.mItem.poster_path)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.drawable.ic_launcher_background)
                .into(holder.mImageView);

        // Set movie title as content description on image.
        holder.mImageView.setContentDescription(holder.mItem.title);

        // Set on click listener.
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
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
        if (mValues == null)
            return 0;

        return mValues.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView mImageView;
        public Movie mItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
