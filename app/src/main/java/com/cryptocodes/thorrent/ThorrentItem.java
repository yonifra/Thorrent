package com.cryptocodes.thorrent;

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
    public ThorrentItem() {
        category = Category.NONE;
    }

    public void parseItem(String name){}
}

