package com.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.models.Movie;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView moviePosterIv = findViewById(R.id.movie_poster_iv);

        Intent intent = getIntent();

        if (intent != null) {
            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);

            populateUI(movie);

            Picasso.with(this)
                    .load(MainActivity.BASE_IMAGE_URL + MainActivity.IMAGE_SIZE + movie.poster_path)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.drawable.ic_launcher_background)
                    .into(moviePosterIv);

            setTitle(movie.title);
        }
        else
            closeOnError();
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_LONG).show();
    }

    private void populateUI(Movie movie) {
        TextView titleTv = findViewById(R.id.title_tv);
        TextView releaseDateTv = findViewById(R.id.release_date_tv);
        TextView voteAverageTv = findViewById(R.id.vote_average_tv);
        TextView plotSynopsisTv = findViewById(R.id.plot_synopsis_tv);

        titleTv.setText(movie.title);
        releaseDateTv.setText(movie.release_date);
        voteAverageTv.setText(movie.vote_average);
        plotSynopsisTv.setText(movie.overview);
    }
}
