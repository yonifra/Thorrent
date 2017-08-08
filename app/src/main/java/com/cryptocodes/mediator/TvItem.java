package com.cryptocodes.mediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TvItem extends MovieItem {
    private int season;
    private int episodeNumber;
    private String episodeTitle;
    private String seriesId;
    private int seIndex = -1; // index for season / episode number (e.g. S03E12)
    private String SEtext;

    public TvItem(ThorrentItem baseItem) {
        super(baseItem);
        // if (MainActivity.friendlyName) {
            title = getTitle();
        // }
    }

    static String buildJsonUrl(String tvShowName, int season, int episodeNumber) {
        return "http://www.omdbapi.com/?t=" + tvShowName.replace(" ", "%20") +
                "&Season=" + season + "&Episode=" + episodeNumber +
                "&plot=short&r=json";
    }

    @Override
    protected int getYear() {
        return super.getYear();
    }

    public int getSeason(){
        return season;
    }

    public int getEpisodeNumber(){
        return episodeNumber;
    }

    @Override
    protected String getTitle() {
        SEtext = TitleParser.parseSeasonAndEpisode(title);
        season = TitleParser.parseSeasonNumber(SEtext);
        episodeNumber = TitleParser.parseEpisodeNumber(SEtext);
        formattedTitle = getShowTitle() + " " + SEtext;

        return formattedTitle;
    }

    protected String getShowTitle() {
        return TitleParser.parseTitle(title);
    }

    @Override
    public void parseItem(String name) {
        // name is the name of the TV Show
        // by now, we should have the season number and episode number
        // This method sets the info in the TvItem in the shows list, NOT the shows' details activity

        JSONObject jsonObject;

        try {
            jsonObject = JSONReader.readJsonFromUrl(buildJsonUrl(getShowTitle(), season, episodeNumber));
            if (jsonObject.getString("Response").equals("False")) return;

            posterUrl = jsonObject.getString("Poster");
            plot = jsonObject.getString("Plot");
            rating = jsonObject.getString("imdbRating");
            episodeTitle = jsonObject.getString("Title");
            seriesId = jsonObject.getString("seriesID");
            imdbUrl = jsonObject.getString("imdbID");
            director = jsonObject.getString("Director");
            genres = jsonObject.getString("Genre");
            pgRating = jsonObject.getString("Rated");
            year = Integer.parseInt(jsonObject.getString("Year").substring(0,4));
            season = Integer.parseInt(jsonObject.getString("Season"));
            episodeNumber = Integer.parseInt(jsonObject.getString("Episode"));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
