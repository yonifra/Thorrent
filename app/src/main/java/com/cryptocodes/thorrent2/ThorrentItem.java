package com.cryptocodes.thorrent2;

import java.util.ArrayList;

/**
 * Created by Jake on 23/10/2014.
 */
public class ThorrentItem {

    public String title = "";
    public String formattedTitle = "";
    public String description = "";
    public Category category;
    public String time;
    public String creator = "";
    public ArrayList<Resolution> resolutions;

    public ThorrentItem() {

        category = Category.NONE;
        resolutions = new ArrayList<Resolution>();
    }

    public void parseItem(String name){}
}

