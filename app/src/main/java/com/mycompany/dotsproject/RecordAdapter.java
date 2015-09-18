package com.mycompany.dotsproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Handles populating the lists with our record data from local storage.
 */
public class RecordAdapter extends ArrayAdapter<Record> {
    private final Context context;
    private final List<Record> values;

    public RecordAdapter(Context context, List<Record> objects) {
        super(context, -1, objects);
        this.context = context;
        this.values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);

        TextView scoreView = (TextView) rowView.findViewById(R.id.score);
        scoreView.setText("" + values.get(position).getScore());

        TextView dateView = (TextView) rowView.findViewById(R.id.date);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date d =  values.get(position).getDate();
        String recordDate = df.format(d);
        dateView.setText(recordDate);


        ImageView playerView = (ImageView) rowView.findViewById(R.id.row_player);
        playerView.setImageResource(R.drawable.winner);

        return rowView;
    }
}
