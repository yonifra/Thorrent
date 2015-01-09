package com.cryptocodes.thorrent;

import android.util.Log;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;

import java.util.List;

/**
 * Created by jonathanf on 13/11/2014.
 */
public class MovieManager {
    private static MovieManager instance;
    private static TheMovieDbApi api;

    protected MovieManager() {
    }

    public static MovieManager getInstance() {
        if (instance == null) {
            instance = new MovieManager();
            try {
                api = new TheMovieDbApi("6f8f5ded34fa534314a23fa7d705681b");
            } catch (MovieDbException e) {
                e.printStackTrace();
                Log.e("MovieManager", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MovieManager", e.getMessage());
            }
        }

        return instance;
    }

    public MovieDb getMovie(String movieName, int year) {
        List<MovieDb> results = null;

        if (movieName == null || movieName == "") return null;

        if (api == null) {
            try {
                api = new TheMovieDbApi("6f8f5ded34fa534314a23fa7d705681b");
            } catch (MovieDbException e) {
                e.printStackTrace();
                Log.e("MovieManager", e.getMessage());
            }
        }

        try {
            results = api.searchMovie(movieName, year, "en", true, 0).getResults();
        } catch (MovieDbException e) {
            e.printStackTrace();
        }

        if (results != null && !results.isEmpty()) {
            return results.get(0);
        }

        return null;
    }
}
