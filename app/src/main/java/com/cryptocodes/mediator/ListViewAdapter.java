package com.cryptocodes.mediator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

public class ListViewAdapter extends ArrayAdapter<ThorrentItem> {

    public ListViewAdapter(Context context, ArrayList<ThorrentItem> items) {
        super(context, 0, items);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listview_item_template, null);
        }

        ThorrentItem p = getItem(position);

        if (p != null) {
            TextView headerTv = (TextView) v.findViewById(R.id.mediaHeaderTextView);
            TextView dateTv = (TextView) v.findViewById(R.id.mediaDateTextView);
            TextView descriptionTv = (TextView) v.findViewById(R.id.mediaPlotTextView);
            TextView runtimeTv = (TextView) v.findViewById(R.id.mediaRuntimeTextView);
            TextView ratingTv = (TextView) v.findViewById(R.id.mediaRatingTextView);
            ImageView imgView = (ImageView) v.findViewById(R.id.mediaImageView);

            if (headerTv != null) {
                headerTv.setText(p.formattedTitle);
            }

            if (p instanceof MovieItem) {
                MovieItem item = (MovieItem) p;
                runtimeTv.setText(item.runtime);
                descriptionTv.setText(item.plot);

                if (item.rating != null && !item.rating.isEmpty() && !Objects.equals(item.rating, "N/A")) {
                    ratingTv.setText(item.rating + " / 10");
                }

            }

            if (dateTv != null) {
                dateTv.setText(p.time);
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

            if (p.getClass() == MovieItem.class) {
                    Picasso.with(getContext())
                            .load(((MovieItem) p).posterUrl)
                            .into(imgView);
            }
        }

        final ThorrentItem item = p;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert item != null;

                Date mediaDate = getDateFromString(item.time);

                if (mediaDate == null) {
                    Snackbar.make(view, "No info available", Snackbar.LENGTH_LONG).show();
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
                        movieDetailsIntent.putExtra("TV_SEASON", String.valueOf(((TvItem) item).getSeason()));
                        movieDetailsIntent.putExtra("TV_EPISODE", String.valueOf(((TvItem) item).getEpisodeNumber()));
                    }

                    movieDetailsIntent.putExtra("IS_MOVIE", String.valueOf(flag));

                    view.getContext().startActivity(movieDetailsIntent);
                }
            }
        });

        return v;
    }

}
