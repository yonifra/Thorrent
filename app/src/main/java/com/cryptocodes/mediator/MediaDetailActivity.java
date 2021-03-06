package com.cryptocodes.mediator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MediaDetailActivity extends ActionBarActivity {
    private final String TAG = "MediaDetailsActivity";
    TextView plot;
    TextView actors;
    TextView director;
    TextView runtime;
    TextView genre;
    //TextView releaseYear;
    //TextView movieName;
    Toolbar activityToolbar;
    TextView imdbRating;
    TextView metascoreRating;
    TextView country;
    // TextView imdbVotes;
    private ImageView posterImageView;
    private ImageView backdropImageView;

    public static Drawable LoadImageFromUrl(String url) {
        try {
            return new RetrievePoster().execute(url).get();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        plot = (TextView) findViewById(R.id.movieDetailsPlot);
        actors = (TextView) findViewById(R.id.movieDetailsActors);
        director = (TextView) findViewById(R.id.movieDetailsDirector);
        runtime = (TextView) findViewById(R.id.movieDetailsRuntime);
        genre = (TextView) findViewById(R.id.movieDetailsGenre);
        // releaseYear = (TextView) findViewById(R.id.movieDetailsReleaseYear);
        // movieName = (TextView) findViewById(R.id.movieDetailsMovieName);
        imdbRating = (TextView) findViewById(R.id.movieDetailsRating);
        metascoreRating = (TextView) findViewById(R.id.movieDetailsMetascoreRating);
        posterImageView = (ImageView) findViewById(R.id.mediaPosterImageView);
        backdropImageView = (ImageView) findViewById(R.id.backdropImageView);
        country = (TextView) findViewById(R.id.movieDetailsCountry);
        // imdbVotes = (TextView) findViewById(R.id.mediaDetailsImdbVotes);
        activityToolbar = (Toolbar) findViewById(R.id.main_toolbar);

        String movieName = getIntent().getStringExtra("MOVIE_NAME");
        String year = getIntent().getStringExtra("MOVIE_YEAR");
        String isMovie = getIntent().getStringExtra("IS_MOVIE");
        String season = getIntent().getStringExtra("TV_SEASON");
        String episode = getIntent().getStringExtra("TV_EPISODE");

        getImdbData(movieName, year, isMovie, season, episode);
    }

    protected void getImdbData(String name, String year, String isMovie, String season, String episode) {
        MovieDetail md = null;
        try {
            if (episode == null) {
                md = new RetrieveMovieDetails().execute(name, year).get();
            }
            else {
                md = new RetrieveTvShowDetails().execute(name, season, episode).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        if (md == null) return;

        Drawable posterDrawable = LoadImageFromUrl(md.posterUrl);
        Drawable backdropDrawable = LoadImageFromUrl(md.backdropUrl);

        if (posterDrawable != null) {
            posterImageView.setImageDrawable(posterDrawable);
            backdropImageView.setImageDrawable(backdropDrawable);
        }

        plot.setText(md.plot);
        runtime.setText(md.runtime);
        director.setText(md.director);
        actors.setText(md.actors);
        genre.setText(md.genre);

        activityToolbar.setTitle(md.name);
        // releaseYear.setText("(" + md.year + ")");
        // movieName.setText(md.name);
        metascoreRating.setText(md.metascore);
        imdbRating.setText(md.rating);
        country.setText(md.country);
        //  imdbVotes.setText(md.imdbVotes);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

    protected MovieDetail doInBackground(String... movies) {
        try {
            JSONObject jsonObject = JSONReader.readJsonFromUrl(MovieItem.buildJsonUrl(movies[0], Integer.parseInt(movies[1])));

            if (jsonObject != null) {
                MovieDetail md = new MovieDetail();
                md.actors = jsonObject.getString("Actors");
                md.posterUrl = jsonObject.getString("Poster");
                md.backdropUrl = jsonObject.getString("Poster");
                md.plot = jsonObject.getString("Plot");
                md.genre = jsonObject.getString("Genre");
                md.director = jsonObject.getString("Director");
                md.rating = jsonObject.getString("imdbRating");
                md.runtime = jsonObject.getString("Runtime");
                md.country = jsonObject.getString("Country");
                md.metascore = jsonObject.getString("Metascore");
                md.year = jsonObject.getString("Year");
                md.name = jsonObject.getString("Title");
                md.imdbVotes = jsonObject.getString("imdbVotes");

                return md;
            }

            return null;
        } catch (Exception e) {
            Log.e("RetrieveMovieDetails", e.getMessage());
            return null;
        }
    }

    protected void onPostExecute(MovieDetail object) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}

class RetrieveTvShowDetails extends AsyncTask<String, Void, MovieDetail> {

    protected MovieDetail doInBackground(String... params) {
        try {
            JSONObject jsonObject = JSONReader.readJsonFromUrl(TvItem.buildJsonUrl(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2])));

            if (jsonObject != null) {
                MovieDetail md = new MovieDetail();
                md.actors = jsonObject.getString("Actors");
                md.posterUrl = jsonObject.getString("Poster");
                md.backdropUrl = jsonObject.getString("Poster");
                md.plot = jsonObject.getString("Plot");
                md.genre = jsonObject.getString("Genre");
                md.director = jsonObject.getString("Director");
                md.rating = jsonObject.getString("imdbRating");
                md.runtime = jsonObject.getString("Runtime");
                md.country = jsonObject.getString("Country");
                md.metascore = jsonObject.getString("Metascore");
                md.year = jsonObject.getString("Year");
                md.name = jsonObject.getString("Title");
                md.imdbVotes = jsonObject.getString("imdbVotes");

                return md;
            }

            return null;
        } catch (Exception e) {
            Log.e("MediaDetailsActivity", e.getMessage());
            return null;
        }
    }

    protected void onPostExecute(MovieDetail object) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}

class RetrievePoster extends AsyncTask<String, Void, Drawable> {

    protected Drawable doInBackground(String... urls) {
        try {
            InputStream is = (InputStream) new URL(urls[0]).getContent();
            return Drawable.createFromStream(is, "src name");
        } catch (Exception e) {
            Log.e("RetrievePoster", e.getMessage());
            return null;
        }
    }

    protected void onPostExecute(MovieDetail object) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}