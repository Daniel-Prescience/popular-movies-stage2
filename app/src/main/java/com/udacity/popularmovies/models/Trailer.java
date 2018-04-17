package com.udacity.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {
    private final String id;
    public final String key;
    public final String name;
    private final String site;

    public Trailer(String id, String key, String name, String site) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
    }

    private Trailer(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
    }
}
