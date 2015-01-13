package com.cryptocodes.thorrent;

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
        getTitle();
    }

    @Override
    protected int getYear() {
        return super.getYear();
    }

    @Override
    protected void getImdbData() {
        //super.getImdbData();
    }

    @Override
    protected String getTitle() {
        StringBuilder sb = new StringBuilder();

        formattedTitle = sb.append(getShowTitle()).append(" ").append(SEtext).toString();
        parseItem(getShowTitle());
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

        JSONObject jsonObject = null;
        try {
            jsonObject = JSONReader.readJsonFromUrl(buildJsonUrl(name, season, episodeNumber));
            if (jsonObject.getString("Response").equals("False")) return;

            posterUrl = jsonObject.getString("Poster");
            plot = jsonObject.getString("Plot");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String buildJsonUrl(String tvShowName, int season, int episodeNumber) {
        return "http://www.omdbapi.com/?t=" + tvShowName.replace(" ", "%20") + "&plot=short&r=json";
    }

    protected String getSeasonAndEpisode() {
        int i = 0;
        for (String s : splittedStrings) {
            String lower = s.toLowerCase();
            if (lower.matches("(s\\d+e\\d+)")) {
                seIndex = i;
                try {
                    season = Integer.parseInt(lower.substring(1, s.indexOf('e') - 1));
                    episodeNumber = Integer.parseInt(lower.substring(s.indexOf('e'), s.length()));
                    return s;
                } catch (Exception ex) {
                    Log.e("MovieItem", ex.getMessage());
                }
            }

            i++;
        }

        return "";
    }
}
