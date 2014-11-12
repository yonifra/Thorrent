package com.cryptocodes.thorrent;

import android.util.Log;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;

import java.util.List;

/**
 * Created by jonathanf on 12/11/2014.
 */
public class MovieItem extends ThorrentItem {
    public Resolution resolution;
    public String posterUrl;
    public int year;

    private String[] splittedStrings;
    private int yearIndex;
    private String rawMovieName;

    public MovieItem()
    {

    }

    public MovieItem(ThorrentItem baseItem)
    {
        this();
        title = baseItem.title;
        category = baseItem.category;
        creator = baseItem.creator;
        description = baseItem.description;
        time = baseItem.time;

        splittedStrings = title.split(" ");

        getResolution();

        title = getTitle();
        getImdbData();
    }

    private void getImdbData() {
        try {
            posterUrl = "";
            TheMovieDbApi api = new TheMovieDbApi("6f8f5ded34fa534314a23fa7d705681b");

            List<MovieDb> movieResults = api.searchMovie(rawMovieName, year, "en", true, 0).getResults();
            if (movieResults.size() > 0)
            {
                posterUrl = "http://image.tmdb.org/t/p/w185" + movieResults.get(0).getPosterPath();
            }
        } catch (MovieDbException e) {
            e.printStackTrace();
            Log.e("MovieItem", e.getMessage());
        }
    }

    private String getTitle() {
        StringBuilder sb = new StringBuilder();
        year = getYear();

        for (int i = 0; i < yearIndex; i++) {
            sb.append(splittedStrings[i] + " ");
        }

        rawMovieName = sb.toString().substring(0, sb.length() - 1);

        sb.append("(" + year + ")");

        if (sb.toString() != "")
            return sb.toString();

        return title;
    }

    private void getResolution()
    {
        if (isContained("720"))
        {
            resolution = Resolution.HDReady;
        }
        else if (isContained("1080"))
        {
            resolution = Resolution.FullHD;
        }
        else if (isContained("4K"))
        {
            resolution = Resolution.FourK;
        }
        else if (isContained("2K"))
        {
            resolution = Resolution.TwoK;
        }
        else if (isContained("DVD"))
        {
            resolution = Resolution.DVD;
        }
        else if (isContained("3D"))
        {
            resolution = Resolution.ThreeDimensions;
        }
        else if (isContained("CAM"))
        {
            resolution = Resolution.Cam;
        }
    }

    private int getYear()
    {
        for (int i = 0; i < splittedStrings.length; i++) {
            int len = splittedStrings[i].length();

            if (len != 4) continue;

            try {
                int year = Integer.parseInt(splittedStrings[i].substring(len - 4));
                yearIndex = i;
                return year;
            }
            catch (Exception ex) {
                Log.e("MovieItem", ex.getMessage());
            }
        }

        return -1;
    }

    private boolean isContained(String value)
    {
        for (String s : splittedStrings)
        {
            if (s.toLowerCase().contains(value.toLowerCase()))
                return true;
        }

        return false;
    }
}
