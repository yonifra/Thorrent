package com.cryptocodes.thorrent;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by jonathanf on 12/11/2014.
 */
public class MovieItem extends ThorrentItem {
    public Resolution resolution;
    public String posterUrl = "";
    public int year;
    public String rating = "";
    public String imdbUrl = "";
    public String plot = "";

    protected String[] splittedStrings;
    protected int yearIndex;
    public String rawMovieName;

    // Used to validate the year range
    private final int MAX_YEAR = 2100;
    private final int MIN_YEAR = 1900;

    private static final String LOG_TAG = "MovieItemClass";

    public MovieItem()
    {

    }

    public MovieItem(ThorrentItem baseItem)
    {
        this();

        formattedTitle = title = baseItem.title;
        category = baseItem.category;
        creator = baseItem.creator;
        description = baseItem.description;
        time = baseItem.time;

        splittedStrings = title.split(" ");

        getResolution();
        getTitle();
        getImdbData();

        StringBuilder sb = new StringBuilder();

        if (plot.length() > 1) {
            sb.append(plot);
            sb.append("\n");
        }

        // Add resolution string (Only if it was decoded successfully)
        if (resolution != Resolution.NA) {
            sb.append("[").append(ThorrentApp.getContext().getString(R.string.quality_text)).append(": ").append(getResolutionString(resolution)).append("]");
        }

        // Don't add rating if it's zero
        if (rating != "0") {
            if (resolution != Resolution.NA)
                sb.append(" ");

            sb.append("[").append(ThorrentApp.getContext().getString(R.string.rating_text)).append(": ").append(rating).append("/10]");
        }

        description = sb.toString();
    }

    private String getResolutionString(Resolution resolution) {
        switch (resolution)
        {
            case Bluray:
                return "Bluray";
            case Cam:
                return "CAM";
            case FourK:
                return "4K UHD";
            case TwoK:
                return "2K";
            case DVD:
                return "DVD";
            case FullHD:
                return "1080p";
            case HDReady:
                return "720p";
            case ThreeD:
                return "3D";
            case Telesync:
                return "Telesync";
            case NA:
                return "N/A";
        }

        return "";
    }

    // This method takes most of the time. If you encounter slowness, check this method
    protected void getImdbData() {
        try {
            JSONObject jsonObject = JSONReader.readJsonFromUrl(buildJsonUrl(rawMovieName, year));

            if (jsonObject == null)
                return;

            if (jsonObject.getString("Response") == "False") return;

            posterUrl = jsonObject.getString("Poster");

            if (posterUrl == null)
            {
                posterUrl = "";
            }

            String ratingStr = jsonObject.getString("imdbRating");

            if(ratingStr != null || ratingStr != "N/A") {
                rating = ratingStr;
            }

            imdbUrl = "http://www.imdb.com/title/" + jsonObject.getString("imdbID");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildJsonUrl(String rawMovieName, int year) {
        StringBuffer sb = new StringBuffer();

        sb.append("http://www.omdbapi.com/?t=");
        sb.append(rawMovieName.replace(" ", "%20"));
        sb.append("&y=");
        sb.append(year);
        sb.append("&plot=full&r=json");

        return sb.toString();
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

            if (sb.toString() != "") {
                formattedTitle = sb.toString();
            }
        }

        return formattedTitle;
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
        else if (isContained("BDRip") || isContained("BRRip") || isContained("bluray"))
        {
            resolution = Resolution.Bluray;
        }
        else if (isContained("CAM"))
        {
            resolution = Resolution.Cam;
        }
        else if (isContained("TS") || isContained("TELESYNC"))
        {
            resolution = Resolution.Telesync;
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

            // If year is in "(2014)" format
            if (len == 6 && splittedStrings[i].charAt(0) == '(' && splittedStrings[i].charAt(5) == ')')
            {
                try {
                    int year = Integer.parseInt(splittedStrings[i].substring(1, 5));

                    if (year >= MIN_YEAR && year <= MAX_YEAR) {
                        yearIndex = i;
                        return year;
                    }
                }
                catch (Exception ex) {
                    Log.e("MovieItem", ex.getMessage());
                    continue;
                }
            }
            else if (len != 4) continue;

            // Length of string is exactly 4, so try to parse it to an int;
            try {
                int year = Integer.parseInt(splittedStrings[i].substring(len - 4));

                // We found the year only if it's a valid year
                if (year >= MIN_YEAR && year <= MAX_YEAR) {
                    yearIndex = i;
                    return year;
                }
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
