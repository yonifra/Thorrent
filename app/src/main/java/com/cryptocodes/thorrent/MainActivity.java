package com.cryptocodes.thorrent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TV_SHOWS_FEED_URL = "http://www.scnsrc.me/category/tv/feed/";
    private static final String MOVIES_FEED_URL = "http://www.scnsrc.me/category/films/feed/";
    private static final String MUSIC_FEED_URL = "http://www.scnsrc.me/category/new-music/feed/";
    private static final String GAMES_FEED_URL = "http://www.scnsrc.me/category/games/feed/";
    private static final String ALL_FEED_URL = "http://feeds.feedburner.com/scnsrc/rss?format=xml";
    private static final String BOOKS_FEED_URL = "http://www.scnsrc.me/category/ebooks/feed/";
    private static final String APPLICATIONS_FEED_URL = "http://www.scnsrc.me/category/applications/feed/";
    public static String CURRENT_RSS_FEED = null;

    public static Boolean displayInformation;
    public static Boolean friendlyName;

    private static ProgressDialog progress;
    private final String LOG_TAG = "MainActivity";
    private ShareActionProvider mShareActionProvider;

    SharedPreferences settings;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public static void showDialog(Activity context) {
        progress = ProgressDialog.show(context,
                context.getString(R.string.loading_dialog_header),
                context.getString(R.string.loading_dialog_content),
                true);
    }

    public static void dismissDialog() {
        progress.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        GetValuesFromSettings();
    }

    private void GetValuesFromSettings()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        displayInformation = settings.getBoolean("enable_info_setting", true);
        friendlyName = settings.getBoolean("show_friendly_name", true);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                // All categories
                mTitle = getString(R.string.Everything);
                refreshFeed(ALL_FEED_URL, false);
                break;
            case 2:
                // TV Shows was selected
                mTitle = getString(R.string.TVShows);
                refreshFeed(TV_SHOWS_FEED_URL, false);
                break;
            case 3:
                // Movies was selected
                Log.i(LOG_TAG, "Starting get on info for movies");

                mTitle = getString(R.string.Movies);
                refreshFeed(MOVIES_FEED_URL, false);

                Log.i(LOG_TAG, "Finished refreshing info for movies");
                break;
            case 4:
                // Games
                mTitle = getString(R.string.Games);
                refreshFeed(GAMES_FEED_URL, false);
                break;
            case 5:
                // Music
                mTitle = getString(R.string.Music);
                refreshFeed(MUSIC_FEED_URL, false);
                break;
            case 6:
                // Books
                mTitle = getString(R.string.Books);
                refreshFeed(BOOKS_FEED_URL, false);
                break;
            case 7:
                // Applications
                mTitle = getString(R.string.Applications);
                refreshFeed(APPLICATIONS_FEED_URL, false);
                break;
            case 8:
                // About was selected
                //mTitle = getString(R.string.About);
                // Start the about activity
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void refreshFeed(String rssFeed, boolean isRefresh) {
        CURRENT_RSS_FEED = rssFeed;

        if (isRefresh) {
            // Handle the refresh
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.menu_item_share);

            // Fetch and store ShareActionProvider
         //   mShareActionProvider = (ShareActionProvider) item.getActionProvider();

            restoreActionBar();

            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.menu_item_share:
                doShare();
                return true;
            case R.id.action_settings:
                Intent settingsIntent= new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
//            case R.id.action_example:
//                // Handle refresh here
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doShare() {
        // populate the share intent with data
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String shareBody = getString(R.string.share_body_message);

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_header_text));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via_header)));
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public static String getAndroidPitRssFeed() throws IOException {
            InputStream in = null;
            String rssFeed = null;
            try {
                URL url;

                if (CURRENT_RSS_FEED == null) {
                    url = new URL(ALL_FEED_URL);
                } else {
                    url = new URL(CURRENT_RSS_FEED);
                }
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                in = conn.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int count; (count = in.read(buffer)) != -1; ) {
                    out.write(buffer, 0, count);
                }
                byte[] response = out.toByteArray();
                rssFeed = new String(response, "UTF-8");
            } catch (Exception ex) {
                rssFeed = null;
                Log.e("GetRSSFeed", ex.getMessage());
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            return rssFeed;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Show the "Loading" dialog
            showDialog(getActivity());

            // Run the Async task
            new GetAndroidPitRssFeedTask().execute();
        }

        public class GetAndroidPitRssFeedTask extends AsyncTask<Void, Void, List<ThorrentItem>> {

            @Override
            protected List<ThorrentItem> doInBackground(Void... voids) {
                List<ThorrentItem> result = null;
                try {
                    String feed = getAndroidPitRssFeed();

                    if (feed == null) {
                        return new ArrayList<ThorrentItem>();
                    } else {
                        result = parse(feed);
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            private List<ThorrentItem> parse(String rssFeed) throws XmlPullParserException, IOException {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(rssFeed));
                xpp.nextTag();
                return readRss(xpp);
            }

            private List<ThorrentItem> readRss(XmlPullParser parser)
                    throws XmlPullParserException, IOException {
                List<ThorrentItem> items = new ArrayList<ThorrentItem>();
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
                List<ThorrentItem> items = new ArrayList<ThorrentItem>();
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

                            if (!added)
                            {
                                items.add(newItem);
                            }
                        }
                        else {
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
                    if (name.equals("title")) {
                        result.title = readTitle(parser);
                        result.formattedTitle = result.title;
                    } else if (name.equals("pubDate")) {
                        // get pubdate
                        result.time = readPubDate(parser);
                    } else if (name.equals("dc:creator")) {
                        // get creator
                        result.creator = readCreator(parser);
                    } else if (name.equals("category")) {
                        result.category = readCategory(parser, result.category);

                        if (result.category == Category.MOVIE) {
                            result = new MovieItem(result);
                        } else if (result.category == Category.TV) {
                            result = new TvItem(result);
                        }
                    } else {
                        skip(parser);
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
                if (rssFeed != null) {
                    if (rssFeed.size() == 0) {
                        Toast.makeText(getActivity(), getString(R.string.CheckInternetConn), Toast.LENGTH_LONG).show();
                        return;
                    }

                    ArrayList<Card> cards = new ArrayList<Card>();

                    for (int i = 0; i < rssFeed.size(); i++) {
                        // Create a Card
                        Card card = new Card(getActivity());

                        // Create a CardHeader
                        CardHeader header = new CardHeader(getActivity());

                        ThorrentItem currentItem = rssFeed.get(i);

                        // Add Header to card
                        header.setTitle(currentItem.formattedTitle);

                        card.addCardHeader(header);
                        CardThumbnail thumb = new CardThumbnail(getActivity());

                        switch (currentItem.category) {
                            case APPLICATION:
                                SetCardTitle(card, currentItem, true);
                                thumb.setDrawableResource(R.drawable.app);
                                break;
                            case TV:
                                SetCardTitle(card, currentItem, false);

                                final TvItem tvShow = (TvItem) currentItem;
                                if (tvShow.posterUrl != null && !tvShow.posterUrl.replace(" ", "").equals("")) {
                                    thumb.setUrlResource(tvShow.posterUrl);
                                }
                                else {
                                    thumb.setDrawableResource(R.drawable.tv);
                                }

                                StartDetailsActivity(card, tvShow);
                                break;
                            case MOVIE:
                                SetCardTitle(card, currentItem, false);

                                final MovieItem movie = (MovieItem) currentItem;

                                if (movie.posterUrl != null && !movie.posterUrl.replace(" ", "").equals("")) {
                                    thumb.setUrlResource(movie.posterUrl);
                                }
                                else
                                {
                                    thumb.setDrawableResource(R.drawable.movie);
                                }

                                StartDetailsActivity(card, movie);
                                break;
                            case BOOK:
                                SetCardTitle(card, currentItem, true);
                                thumb.setDrawableResource(R.drawable.books);
                                break;
                            case GAME:
                                SetCardTitle(card, currentItem, true);
                                thumb.setDrawableResource(R.drawable.games);
                                break;
                            case MUSIC:
                                SetCardTitle(card, currentItem, true);
                                thumb.setDrawableResource(R.drawable.music);
                                break;
                            case NONE:
                                SetCardTitle(card, currentItem, true);
                                thumb.setDrawableResource(R.drawable.ic_error_black_48dp);
                                break;
                        }

                        card.addCardThumbnail(thumb);
                        cards.add(card);
                    }

                    CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
                    CardListView listView = (CardListView) getActivity().findViewById(R.id.myList);
                    if (listView != null) {
                        listView.setAdapter(mCardArrayAdapter);
                    }

                    ((MainActivity) getActivity()).dismissDialog();
                }
            }

            private void StartDetailsActivity(Card card, final MovieItem media) {
                //Set onClick listener
                card.setOnClickListener(new Card.OnCardClickListener() {
                    @Override
                    public void onClick(Card card, View view) {
                        if (media.year < 1) {
                            Toast.makeText(getActivity(), "No info", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent movieDetailsIntent = new Intent(getActivity(), MediaDetailActivity.class);
                            movieDetailsIntent.putExtra("MOVIE_NAME", media.rawMovieName);
                            movieDetailsIntent.putExtra("MOVIE_YEAR", String.valueOf(media.year));
                            movieDetailsIntent.putExtra("IS_MOVIE", String.valueOf(media instanceof MovieItem));

                            if (media instanceof TvItem)
                            {
                                movieDetailsIntent.putExtra("TV_SEASON",  String.valueOf(((TvItem) media).getSeason()));
                                movieDetailsIntent.putExtra("TV_EPISODE", String.valueOf(((TvItem) media).getEpisodeNumber()));
                            }
                            else
                            {
                                movieDetailsIntent.putExtra("TV_SEASON", String.valueOf(-1));
                                movieDetailsIntent.putExtra("TV_EPISODE", String.valueOf(-1));
                            }

                            startActivity(movieDetailsIntent);
                        }
                    }
                });
            }

            private void SetCardTitle(Card card, ThorrentItem currentItem, boolean isLong) {
                if (isLong) {
                    card.setTitle(currentItem.time + "\n" + getActivity().getString(R.string.by_user_text) + " " + currentItem.creator + "\n" + currentItem.description);
                }
                else {
                    card.setTitle(currentItem.time + "\n" + currentItem.description);
                }
            }
        }
    }
}
