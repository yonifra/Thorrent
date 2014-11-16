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
    public float rating;

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

        StringBuilder sb = new StringBuilder();

        // Add resolution string
        if (resolution != Resolution.NA) {
            sb.append("[Resolution: ").append(resolution).append("]");
        }

        // Don't add rating if it's zero
        if (rating > 0.001f) {
            if (resolution != Resolution.NA)
                sb.append(" ");

            sb.append("[Rating: ").append(rating).append("/10]");
        }

        description = sb.toString();
    }

    protected void getImdbData() {
        //new Thread(new Runnable() {
          //  public void run() {
                posterUrl = "";

                MovieDb movie = MovieManager.getInstance().getMovie(rawMovieName, year);
                if (movie != null)
                {
                    posterUrl = "http://image.tmdb.org/t/p/w185" + movie.getPosterPath();
                    rating = movie.getVoteAverage();
                }
         //   }
        //}).start();
    }

    protected String getTitle() {
        StringBuilder sb = new StringBuilder();
        year = getYear();

        for (int i = 0; i < yearIndex; i++) {
            sb.append(splittedStrings[i] + " ");
        }


        if (sb.length() > 0) {
            // Remove the last space from title
            rawMovieName = sb.toString().substring(0, sb.toString().length() - 1);

            sb.append("(" + year + ")");

            if (sb.toString() != "")
                return sb.toString();
        }

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
            resolution = Resolution.ThreeD;
        }
        else if (isContained("CAM"))
        {
            resolution = Resolution.Cam;
        }
        else if (isContained("BDRip") || isContained("BRRip"))
        {
            resolution = Resolution.Bluray;
        }
        else
        {
            resolution = Resolution.NA;
        }
    }

    protected int getYear()
    {
        for (int i = 0; i < splittedStrings.length; i++) {
            int len = splittedStrings[i].length();

            if (len == 6 && splittedStrings[i].charAt(0) == '(' && splittedStrings[i].charAt(5) == ')')
            {
                try {
                    int year = Integer.parseInt(splittedStrings[i].substring(1, 5));
                    yearIndex = i;
                    return year;
                }
                catch (Exception ex) {
                    Log.e("MovieItem", ex.getMessage());
                    continue;
                }
            }
            else if (len != 4) continue;

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
