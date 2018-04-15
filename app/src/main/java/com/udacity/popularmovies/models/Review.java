package com.udacity.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    public final String id;
    public final String author;
    public final String content;
    public final String url;

    public Review(String id, String key, String name, String site) {
        this.id = id;
        this.author = key;
        this.content = name;
        this.url = site;
    }

    private Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }
}
