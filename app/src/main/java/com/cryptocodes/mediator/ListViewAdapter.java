package com.cryptocodes.mediator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ListViewAdapter extends ArrayAdapter<ThorrentItem> {

    private Context activityContext;

    public ListViewAdapter(Context context, ArrayList<ThorrentItem> items) {
        super(context, 0, items);

        activityContext = context;

            CalligraphyContextWrapper.wrap(activityContext);
    }

    public static Date getDateFromString(String date) {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        Date formattedDate = null;

        // Convert from String to Date
        try {
            formattedDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return formattedDate;
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listview_item_template, null);
        }

        ThorrentItem p = getItem(position);

        if (p != null) {
            TextView headerTv = (TextView) v.findViewById(R.id.mediaHeaderTextView);
            TextView genresTv = (TextView) v.findViewById(R.id.mediaGenresTextView);
            TextView pgRatingTv = (TextView) v.findViewById(R.id.audienceRatingTextView);
            TextView directorTv = (TextView) v.findViewById(R.id.directorTextView);
            TextView runtimeTv = (TextView) v.findViewById(R.id.mediaRuntimeTextView);
            TextView ratingTv = (TextView) v.findViewById(R.id.mediaRatingTextView);
            TextView yearTv = (TextView) v.findViewById(R.id.mediaYearTextView);
            ImageView imgView = (ImageView) v.findViewById(R.id.mediaImageView);
            TextView resolutionsTv = (TextView) v.findViewById(R.id.resolutionsTextView);

            if (headerTv != null) {
                headerTv.setText(p.formattedTitle);
            }

            if (p instanceof MovieItem) {
                MovieItem item = (MovieItem) p;
                runtimeTv.setText(item.runtime);
                //  descriptionTv.setText(item.);

                if (item.rating != null && !item.rating.isEmpty() && !Objects.equals(item.rating, "N/A")) {
                    ratingTv.setText(item.rating);
                }

                if (genresTv != null) {
                    genresTv.setText(item.genres);
                }

                if (directorTv != null) {
                    directorTv.setText(item.director);
                }

                if (pgRatingTv != null) {
                    pgRatingTv.setText(item.pgRating);
                }

                if (yearTv != null && item.year > 1950) {
                    yearTv.setText(String.format("%d", item.year));
                }

                if (resolutionsTv != null && item.resolution != null) {
                    resolutionsTv.setText(ConvertToResString(item.resolution));
                }
            }

            switch (p.category) {
                case APPLICATION:
                    imgView.setImageResource(R.drawable.app);
                    break;
                case TV:
                    final TvItem tvShow = (TvItem) p;

                    if (tvShow.posterUrl != null && !tvShow.posterUrl.replace(" ", "").equals("")) {
                        imgView.setImageURI(Uri.parse(tvShow.posterUrl));
                    } else {
                        imgView.setImageResource(R.drawable.tv);
                    }

                    break;
                case MOVIE:
                    final MovieItem movie = (MovieItem) p;

                    if (movie.posterUrl != null && !movie.posterUrl.replace(" ", "").equals("")) {
                        imgView.setImageURI(Uri.parse(movie.posterUrl));
                    } else {
                        imgView.setImageResource(R.drawable.movie);
                    }
                    break;
                case BOOK:
                    imgView.setImageResource(R.drawable.books);
                    break;
                case GAME:
                    imgView.setImageResource(R.drawable.games);
                    break;
                case MUSIC:
                    imgView.setImageResource(R.drawable.music);
                    break;
                case NONE:
                    imgView.setImageResource(R.drawable.ic_error_black_48dp);
                    break;
            }

            if (p.getClass() == MovieItem.class || p.getClass() == TvItem.class) {

                String posterUrl = ((MovieItem) p).posterUrl;

                if (posterUrl != null && !Objects.equals(posterUrl, "")) {
                    Picasso.with(getContext())
                            .load(((MovieItem) p).posterUrl)
                            .into(imgView);
                }
            }
        }

        final ThorrentItem item = p;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item == null || (item.getClass() != MovieItem.class && item.getClass() != TvItem.class)) {
                    return;
                }

                String posterUrl = ((MovieItem) item).posterUrl;

                if (posterUrl == null || Objects.equals(posterUrl, "")) {
                    Snackbar.make(view, "No information available", Snackbar.LENGTH_LONG).show();
                } else {
                    Intent movieDetailsIntent = new Intent(view.getContext(), MediaDetailActivity.class);
                    boolean flag = true;


                    if (item.getClass() == MovieItem.class) {
                        MovieItem movie = (MovieItem) item;
                        movieDetailsIntent.putExtra("MOVIE_NAME", movie.getRawMovieName());
                        movieDetailsIntent.putExtra("MOVIE_YEAR", String.valueOf(movie.getYear()));

                    }

                    if (item.getClass() == TvItem.class) {
                        flag = false;
                        TvItem episode = (TvItem) item;
                        movieDetailsIntent.putExtra("MOVIE_NAME", episode.getRawMovieName());
                        movieDetailsIntent.putExtra("MOVIE_YEAR", String.valueOf(episode.getYear()));
                        movieDetailsIntent.putExtra("TV_SEASON", String.valueOf(episode.getSeason()));
                        movieDetailsIntent.putExtra("TV_EPISODE", String.valueOf(episode.getEpisodeNumber()));
                    }

                    movieDetailsIntent.putExtra("IS_MOVIE", String.valueOf(flag));
                    movieDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    activityContext.startActivity(movieDetailsIntent);
                }
            }
        });

        return v;
    }

    private String ConvertToResString(Resolution resolution) {
        switch (resolution) {
            case Bluray:
                return "Bluray";
            case EightK:
                return "8K UHD";
            case FullHD:
                return "1080p";
            case HDReady:
                return "720p";
            case DVD:
                return "DVD";
            case FourK:
                return "4K UHD";
            case TwoK:
                return "2K";
            case Telesync:
                return "Telesync";
            case ThreeD:
                return "3D";
            case Cam:
                return "Camcorder";
            default:
            case NA:
                return "Unknown resolution";
        }
    }
}
