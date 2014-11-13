package com.cryptocodes.thorrent;

import android.util.Log;
import com.omertron.themoviedbapi.model.MovieDb;

/**
 * Created by jonathanf on 12/11/2014.
 */
public class MovieItem extends ThorrentItem {
    public Resolution resolution;
    public String posterUrl;
    public int year;

    protected String[] splittedStrings;
    protected int yearIndex;
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

    protected void getImdbData() {
        posterUrl = "";

        MovieDb movie = MovieManager.getInstance().getMovie(rawMovieName, year);
        if (movie != null)
        {
            posterUrl = "http://image.tmdb.org/t/p/w185" + movie.getPosterPath();
        }
    }

    protected String getTitle() {
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

    protected void getResolution()
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

    protected int getYear()
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

    protected boolean isContained(String value)
    {
        for (String s : splittedStrings)
        {
            if (s.toLowerCase().contains(value.toLowerCase()))
                return true;
        }

        return false;
    }
}
