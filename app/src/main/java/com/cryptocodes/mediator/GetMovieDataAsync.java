package com.cryptocodes.mediator;

import android.os.AsyncTask;

/**
 * Created by Jake on 5/1/2015.
 */
public class GetMovieDataAsync extends AsyncTask<String, Integer, MovieDetail> {
    @Override
    protected MovieDetail doInBackground(String... strings) {

//        OmdbApi omdb = new OmdbApi();
//        omdb.setTomatoes(false);
//        omdb.setShortPlot();

//        try {
//            OmdbVideoFull search = omdb.movieInfo(strings[0]);//,Integer.parseInt(strings[1]));
//
//            if (search != null) {
//                MovieDetail md = new MovieDetail();
//
//                md.posterUrl = search.getPoster();
//                md.rating = Float.parseFloat(search.getImdbRating());
//               // md.plot = search.getPlot();
//                md.imdbUrl = "http://www.imdb.com/title/" + search.getImdbID();
//            }
//        } catch (OMDBException e) {
//            e.printStackTrace();
//        }
//        MovieDb movie = MovieManager.getInstance().getMovie(strings[0], Integer.parseInt(strings[1]));
//        if (movie != null)
//        {
//
//            MovieDetail md = new MovieDetail();
//
//            md.posterUrl = "http://image.tmdb.org/t/p/w185" + movie.getPosterPath();
//            md.rating = movie.getVoteAverage();
//            md.imdbUrl = "http://www.imdb.com/title/" + movie.getImdbID();
//           // StringBuilder sb = new StringBuilder();
//            //posterUrl = "http://image.tmdb.org/t/p/w185" + movie.getPosterPath();
//            //rating = movie.getVoteAverage();
//            //  description += sb.append("[").append(ThorrentApp.getContext().getString(R.string.rating_text)).append(": ").append(rating).append("/10]");
//
//            return md;
//        }

        return null;
    }

    @Override
    protected void onPostExecute(MovieDetail movieDetail) {
        // do something with movie details
    }
}
