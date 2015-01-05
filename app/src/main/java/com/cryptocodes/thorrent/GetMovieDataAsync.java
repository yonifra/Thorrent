package com.cryptocodes.thorrent;

import android.graphics.Movie;
import android.os.AsyncTask;

import com.omertron.themoviedbapi.model.MovieDb;

/**
 * Created by Jake on 5/1/2015.
 */
public class GetMovieDataAsync extends AsyncTask<String, Integer, MovieDetail> {
    private String posterUrl;
    private float rating;

    @Override
    protected MovieDetail doInBackground(String... strings) {
        posterUrl = "";

        MovieDb movie = MovieManager.getInstance().getMovie(strings[0], Integer.parseInt(strings[1]));
        if (movie != null)
        {
            MovieDetail md = new MovieDetail();

            md.posterUrl = "http://image.tmdb.org/t/p/w185" + movie.getPosterPath();
            md.rating = movie.getVoteAverage();
           // StringBuilder sb = new StringBuilder();
            //posterUrl = "http://image.tmdb.org/t/p/w185" + movie.getPosterPath();
            //rating = movie.getVoteAverage();
            //  description += sb.append("[").append(ThorrentApp.getContext().getString(R.string.rating_text)).append(": ").append(rating).append("/10]");

            return md;
        }

        return null;
    }

    @Override
    protected void onPostExecute(MovieDetail movieDetail) {
        // do something with movie details
    }
}
