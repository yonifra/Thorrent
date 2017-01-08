package com.cryptocodes.mediator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    SharedPreferences settings;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

//    public static void showDialog(Activity context) {
//        progress = ProgressDialog.show(context,
//                context.getString(R.string.loading_dialog_header),
//                context.getString(R.string.loading_dialog_content),
//                true);
//    }
//
//    public static void dismissDialog() {
//        progress.dismiss();
//    }

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: Refresh the items here
                new GetAndroidPitRssFeedTask().execute();
                onItemsLoadComplete();
            }
        });

        GetValuesFromSettings();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void GetValuesFromSettings() {
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

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
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

        switch (id) {
            case R.id.menu_item_share:
                doShare();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
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
    public static class PlaceholderFragment extends android.support.v4.app.Fragment {
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
            //showDialog(getActivity());
            // TODO: Refresh the current feed

            // Run the Async task
            new GetAndroidPitRssFeedTask().execute();
        }
    }
}
