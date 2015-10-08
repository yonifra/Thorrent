package com.cryptocodes.thorrent2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by jonathanf on 13/11/2014.
 */
public class TvItem extends MovieItem {
    private int season;
    private int episodeNumber;
    private int seIndex = -1; // index for season / episode number (e.g. S03E12)
    private String SEtext;

    public TvItem(ThorrentItem baseItem) {
        super(baseItem);
        if (MainActivity.friendlyName) {
            title = getTitle();
        }
    }

    @Override
    protected int getYear() {
        return super.getYear();
    }

    @Override
    protected void getImdbData() {
        //super.getImdbData();
    }

    public int getSeason(){
        return season;
    }

    public int getEpisodeNumber(){
        return episodeNumber;
    }

    @Override
    protected String getTitle() {
        StringBuilder sb = new StringBuilder();
        String showTitle = getShowTitle();

        if (MainActivity.displayInformation) {
            parseItem(showTitle);
        }

        rawMovieName = showTitle;
        formattedTitle = sb.append(showTitle).append(" ").append(SEtext).toString();

        return formattedTitle;
    }

    protected String getShowTitle() {
        SEtext = getSeasonAndEpisode();

        if (seIndex >= 0) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < seIndex; i++) {
                sb.append(splittedStrings[i] + " ");
            }

            String titleString = sb.toString();

            if (titleString.length() > 0) {
                return sb.toString().substring(0, titleString.length() - 1);
            }
        }

        return title;
    }

    @Override
    public void parseItem(String name) {
        // name is the name of the TV Show
        // by now, we should have the season number and episode number
        // This method sets the info in the TvItem in the shows list, NOT the shows' details activity

        JSONObject jsonObject;

        try {
            jsonObject = JSONReader.readJsonFromUrl(buildJsonUrl(name, season, episodeNumber));
            if (jsonObject.getString("Response").equals("False")) return;

            // Set the poster of the series
            posterUrl = jsonObject.getString("Poster");

            // Get the plot of the series
            plot = jsonObject.getString("Plot");

            // Set the rating of the show
            rating = jsonObject.getString("imdbRating");

            imdbUrl = jsonObject.getString("imdbID");

            year = Integer.parseInt(jsonObject.getString("Year").substring(0,4));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String buildJsonUrl(String tvShowName, int season, int episodeNumber) {
        return "http://www.omdbapi.com/?t=" + tvShowName.replace(" ", "%20") +
                "&Season=" + season + "&Episode=" + episodeNumber +
                "&plot=short&r=json";
    }

    protected String getSeasonAndEpisode() {
        int i = 0;
        for (String s : splittedStrings) {
            String lower = s.toLowerCase();
            if (lower.matches("(s\\d+e\\d+)")) {
                seIndex = i;
                try {
                    season = Integer.parseInt(lower.substring(1, lower.indexOf('e')));
                    episodeNumber = Integer.parseInt(lower.substring(lower.indexOf('e') + 1, lower.length()));

                    if (season > 0 && episodeNumber > 0) {
                        description += "\nSeason: " + season +"\nEpisode: " + episodeNumber;
                        return season + "x" + episodeNumber;
                    }

                    return "";
                } catch (Exception ex) {
                    Log.e("MovieItem", ex.getMessage());
                }
            }

            i++;
        }

        return "";
    }
}
