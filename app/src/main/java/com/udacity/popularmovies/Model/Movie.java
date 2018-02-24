package com.udacity.popularmovies.Model;

public class Movie {
    public final Long id;
    public final String title;
    public final String overview;
    public final String release_date;
    public final String poster_path;
    public final String vote_average;
    public final String popularity;

    public Movie(Long id, String title, String overview, String release_date, String poster_path, String vote_average, String popularity) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.popularity = popularity;
    }
}
