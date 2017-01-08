package com.cryptocodes.mediator;

import java.util.ArrayList;

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
        resolutions = new ArrayList<>();
    }

    public void parseItem(String name){}
}

