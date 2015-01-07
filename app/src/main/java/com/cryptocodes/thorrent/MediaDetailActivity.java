package com.cryptocodes.thorrent;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class MediaDetailActivity extends ActionBarActivity {
    TextView plot;
    TextView actors;
    TextView director;
    TextView runtime;
    TextView genre;
    TextView releaseYear;
    LinearLayout mainLayout;
    TextView movieName;
    TextView imdbRating;
    TextView metascoreRating;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        plot = (TextView) findViewById(R.id.movieDetailsPlot);
        actors = (TextView) findViewById(R.id.movieDetailsActors);
        director = (TextView) findViewById(R.id.movieDetailsDirector);
        runtime = (TextView) findViewById(R.id.movieDetailsRuntime);
        genre = (TextView) findViewById(R.id.movieDetailsGenre);
        releaseYear = (TextView) findViewById(R.id.movieDetailsReleaseYear);
        movieName = (TextView) findViewById(R.id.movieDetailsMovieName);
        imdbRating = (TextView) findViewById(R.id.movieDetailsRating);
        metascoreRating = (TextView) findViewById(R.id.movieDetailsMetascoreRating);
        scrollView = (ScrollView)findViewById(R.id.movieDetailsScrollView);

        // Set the background of the activity to be the poster
        //mainLayout.setBackground();

        String movieName = getIntent().getStringExtra("MOVIE_NAME");

        getImdbData(movieName);
    }

    protected void getImdbData(String name) {
        MovieDetail md = null;
        try {
            md = new RetrieveMovieDetails().execute(name).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (md == null) return;

        Drawable posterDrawable = LoadImageFromUrl(md.posterUrl);

        if (posterDrawable != null) {
            scrollView.setBackground(posterDrawable);
        }

        plot.setText(md.plot);
        runtime.setText(md.runtime);
        director.setText(md.director);
        actors.setText(md.actors);
        genre.setText(md.genre);
        releaseYear.setText(md.year);
        movieName.setText(md.name);
        metascoreRating.setText(md.metascore);
        imdbRating.setText(md.rating);
    }

    public static Drawable LoadImageFromUrl(String url) {
        try {
            Drawable drawable = new RetrievePoster().execute(url).get();

            drawable.setAlpha(40);
            //drawable.setColorFilter(0, PorterDuff.Mode.DARKEN);
            //drawable.mutate().setColorFilter( 0xff000000, PorterDuff.Mode.OVERLAY);

            return drawable;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_media_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class RetrieveMovieDetails extends AsyncTask<String, Void, MovieDetail> {

    private Exception exception;

    protected MovieDetail doInBackground(String... movies) {
        try {
            String s = "http://www.omdbapi.com/?t=" + movies[0].replace(" ", "%20");
            JSONObject jsonObject = JSONReader.readJsonFromUrl(s);

            if (jsonObject != null) {
                MovieDetail md = new MovieDetail();
                md.actors = jsonObject.getString("Actors");
                md.posterUrl = jsonObject.getString("Poster");
                md.plot = jsonObject.getString("Plot");
                md.genre = jsonObject.getString("Genre");
                md.director = jsonObject.getString("Director");
                md.rating = jsonObject.getString("imdbRating");
                md.runtime = jsonObject.getString("Runtime");
                md.country = jsonObject.getString("Country");
                md.metascore = jsonObject.getString("Metascore");
                md.year = jsonObject.getString("Year");
                md.name = jsonObject.getString("Title");

                return md;
            }

            return null;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(MovieDetail object) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}

class RetrievePoster extends AsyncTask<String, Void, Drawable> {

    private Exception exception;

    protected Drawable doInBackground(String... urls) {
        try {
            InputStream is = (InputStream) new URL(urls[0]).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    protected void onPostExecute(MovieDetail object) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}