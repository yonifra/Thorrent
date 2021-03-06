package com.cryptocodes.mediator;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.cryptocodes.mediator.MainActivity.PlaceholderFragment.getAndroidPitRssFeed;
import static com.cryptocodes.mediator.ThorrentApp.getContext;

class RssFeedRetrieverAsync extends AsyncTask<Void, Void, List<ThorrentItem>> {

    private Activity mCallingActivity;

    RssFeedRetrieverAsync(Activity callingActivity) {
        mCallingActivity = callingActivity;
    }

    @Override
    protected List<ThorrentItem> doInBackground(Void... voids) {
        List<ThorrentItem> result = null;
        try {
            String feed = getAndroidPitRssFeed();

            if (feed == null) {
                return new ArrayList<>();
            } else {
                result = parse(feed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<ThorrentItem> parse(String rssFeed) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(rssFeed));
        xpp.nextTag();
        return readRss(xpp);
    }

    private List<ThorrentItem> readRss(XmlPullParser parser)
            throws Exception {
        List<ThorrentItem> items = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("channel")) {
                items.addAll(readChannel(parser));
            } else {
                skip(parser);
            }
        }
        return items;
    }

    private List<ThorrentItem> readChannel(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        List<ThorrentItem> items = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "channel");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("item")) {
                ThorrentItem newItem = readItem(parser);
                boolean added = false;

                if (newItem.getClass() == MovieItem.class) {
                    for (ThorrentItem item : items) {
                        if (item.formattedTitle.equals(newItem.formattedTitle)) {
                            item.resolutions.add(((MovieItem) newItem).resolution);
                            added = true;
                            break;
                        }
                    }

                    if (!added) {
                        items.add(newItem);
                    }
                } else {
                    items.add(newItem);
                }
            } else {
                skip(parser);
            }
        }
        return items;
    }

    private ThorrentItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        ThorrentItem result = new ThorrentItem();
        parser.require(XmlPullParser.START_TAG, null, "item");

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            switch (name) {
                case "title":
                    result.title = readTitle(parser);
                    result.formattedTitle = result.title;
                    break;
                case "pubDate":
                    result.time = readPubDate(parser);
                    break;
                case "dc:creator":
                    result.creator = readCreator(parser);
                    break;
                case "category":
                    result.category = readCategory(parser, result.category);

                    if (result.category == Category.MOVIE) {
                        result = new MovieItem(result);
                    } else if (result.category == Category.TV) {
                        result = new TvItem(result);
                    }
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return result;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");

        return title;
    }

    private String readCreator(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "dc:creator");
        String creator = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "dc:creator");

        return creator.replace("&#124;", "&");
    }

    private String readPubDate(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "pubDate");
        String dateStr = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "pubDate");

        Date date;
        try {
            date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(dateStr);
            return date.toString();
        } catch (ParseException e) {
            return dateStr.substring(0, dateStr.length() - 6);
        }
    }

    private Category readCategory(XmlPullParser parser, Category currentCategory)
            throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "category");
        String category = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "category");

        if (category == null || currentCategory != Category.NONE) {
            return currentCategory;
        }

        if (category.contains("TV")) {
            return Category.TV;
        } else if (category.contains("Movies")) {
            return Category.MOVIE;
        } else if (category.contains("Music")) {
            return Category.MUSIC;
        } else if (category.contains("Applications")) {
            return Category.APPLICATION;
        } else if (category.contains("eBooks")) {
            return Category.BOOK;
        } else if (category.contains("Games")
                || category.contains("PS3")
                || category.contains("DOX")
                || category.contains("XBOX")
                || category.contains("ISO")) {
            return Category.GAME;
        }

        // for the extreme case we couldn't parse the category
        return Category.NONE;
    }

    private String readText(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    @Override
    protected void onPostExecute(List<ThorrentItem> rssFeed) {
        if (rssFeed != null && mCallingActivity != null) {
                if (rssFeed.size() == 0) {
                    Snackbar.make(mCallingActivity.findViewById(R.id.container),
                            getContext().getString(R.string.CheckInternetConn),
                            Snackbar.LENGTH_LONG).show();
                    return;
                }

                ListViewAdapter adapter = new ListViewAdapter(getContext(), new ArrayList<>(rssFeed));

            ListView listView = (ListView) mCallingActivity.findViewById(R.id.listRecyclerView);

                if (listView != null) {
                    listView.setAdapter(adapter);
                }
        }
    }
}
