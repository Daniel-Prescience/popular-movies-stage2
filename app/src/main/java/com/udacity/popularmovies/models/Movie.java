package com.udacity.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    public final Long id;
    public final String title;
    public final String overview;
    public final String release_date;
    public final String poster_path;
    public final String vote_average;
    public final String popularity;
    public boolean favorite;

    public Movie(Long id, String title, String overview, String release_date, String poster_path, String vote_average, String popularity, boolean favorite) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.popularity = popularity;
        this.favorite = favorite;
    }

    private Movie(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        vote_average = in.readString();
        popularity = in.readString();
        favorite = in.readInt() == 1;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeString(vote_average);
        dest.writeString(popularity);
        dest.writeInt(favorite ? 1 : 0);
    }
}
