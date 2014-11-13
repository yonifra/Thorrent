package com.cryptocodes.thorrent;

import android.util.Log;

import com.omertron.fanarttvapi.FanartTvApi;
import com.omertron.fanarttvapi.FanartTvException;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.model.Series;
import com.omertron.tvrageapi.TVRageApi;
import com.omertron.tvrageapi.model.ShowInfo;

import java.util.List;

/**
 * Created by jonathanf on 13/11/2014.
 */
public class MovieManager {
    private static MovieManager instance;
    private static TheMovieDbApi api;
    //private static TVRageApi tvRageApi;
    private static FanartTvApi fanartTvApi;
    public static MovieManager getInstance(){
        if (instance == null)
        {
            instance = new MovieManager();
            try {
                api = new TheMovieDbApi("6f8f5ded34fa534314a23fa7d705681b");
                //tvRageApi = new TVRageApi("itvZsAlUGQtel7AxEVsI");
                fanartTvApi = new FanartTvApi("61c3c64203aefe3fe16c7797f9a72bd8");
            } catch (MovieDbException e) {
                e.printStackTrace();
                Log.e("MovieManager", e.getMessage());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("MovieManager", e.getMessage());
            }
        }

        return instance;
    }

    public MovieDb getMovie(String movieName, int year) {
        List<MovieDb> results = null;

        try {
            results = api.searchMovie(movieName, year, "en", true, 0).getResults();
        } catch (MovieDbException e) {
            e.printStackTrace();
        }

        if (results != null && !results.isEmpty())
        {
            return results.get(0);
        }

        return null;
    }

    protected MovieManager()
    {
    }

//    public ShowInfo getSeries(String showTitle) {
//        if (fanartTvApi == null || showTitle == null)
//            return null;
//
//        List<ShowInfo> results = null;
//
//        results = fanartTvApi.getShow(showTitle);
//
//        if (results != null && results.isEmpty()) {
//            return results.get(0);
//        }
//
//        return null;
//    }

    public com.omertron.fanarttvapi.model.FTSeries getTvArtwork(String showName)
    {
        try {
            //fanartTvApi.
            return fanartTvApi.getTvArtwork(showName);
        } catch (FanartTvException e) {
            e.printStackTrace();
        }
        return null;
    }
}
