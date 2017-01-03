package com.cryptocodes.thorrent2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yonifra on 3/1/17.
 */

public class ListViewAdapter extends ArrayAdapter<ThorrentItem> {

    public ListViewAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListViewAdapter(Context context, int resource, List<ThorrentItem> items) {
        super(context, resource, items);
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
            ImageView imgView = (ImageView) v.findViewById(R.id.mediaImageView);

            if (headerTv != null) {
                headerTv.setText(p.title);
            }

            if (dateTv != null) {
                dateTv.setText(p.time);
            }

            if (p.getClass() == MovieItem.class) {
                if (imgView != null) {
                    Picasso.with(getContext())
                            .load(((MovieItem) p).posterUrl)
                            .into(imgView);
                }
            }
        }

        return v;
    }

}
