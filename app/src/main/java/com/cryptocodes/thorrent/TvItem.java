package com.cryptocodes.thorrent;

import android.util.Log;

import com.omertron.fanarttvapi.enumeration.FTArtworkType;
import com.omertron.fanarttvapi.model.FTArtwork;
import com.omertron.fanarttvapi.model.FTSeries;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.thetvdbapi.model.Series;
import com.omertron.tvrageapi.model.ShowInfo;

import java.util.List;

/**
 * Created by jonathanf on 13/11/2014.
 */
public class TvItem extends MovieItem {
    private int season;
    private int episodeNumber;
    private String episodeName;
    private int seIndex = -1; // index for season / episode number (e.g. S03E12)
    private String SEtext;

    @Override
    protected int getYear() {
        return super.getYear();
    }

    @Override
    protected String getTitle() {
        StringBuilder sb = new StringBuilder();

        title = sb.append(getShowTitle()).append(" ").append(SEtext).toString();
        return title;
    }

    public TvItem(ThorrentItem baseItem)
    {
        super(baseItem);
    }

    protected String getShowTitle() {
        SEtext = getSeasonAndEpisode();

        if (seIndex >= 0) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < seIndex; i++)
            {
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
    protected void getImdbData() {
        posterUrl = "";

       // ShowInfo series = MovieManager.getInstance().getSeries(getShowTitle());
        //if (series != null)
       // {
        String showTitle = getShowTitle();
        FTSeries tvArtwork = MovieManager.getInstance().getTvArtwork(getShowTitle());

        if (tvArtwork != null)
        {
            List<FTArtwork> artworks = tvArtwork.getArtwork(FTArtworkType.TVPOSTER);
            if (!artworks.isEmpty()) {
                posterUrl = artworks.get(0).getUrl();
            }
        }

       // }
    }

    protected String getSeasonAndEpisode() {
        int i = 0;
        for(String s : splittedStrings) {
            String lower = s.toLowerCase();
            if (lower.length() >= 6
                    && lower.startsWith("s")
                    && lower.charAt(3) == 'e'
                    && isDigit(lower.charAt(1)) && isDigit(lower.charAt(2))
                    && isDigit(lower.charAt(4)) && isDigit(lower.charAt(5))) {
                seIndex = i;
                try {
                    season = Integer.parseInt(lower.substring(1,2));
                    episodeNumber = Integer.parseInt(lower.substring(4, s.length()));
                    return s;
                }
                catch (Exception ex) {
                    Log.e("MovieItem", ex.getMessage());
                }
            }

            i++;
        }

        return "";
    }

    private boolean isDigit(Character c) {
        if (c >= 48 && c <= 57) {
            return true;
        }

        return false;
    }
}
