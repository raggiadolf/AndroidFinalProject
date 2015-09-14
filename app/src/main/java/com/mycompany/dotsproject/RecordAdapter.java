package com.mycompany.dotsproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

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

        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        nameView.setText(values.get(position).getName());

        TextView scoreView = (TextView) rowView.findViewById(R.id.score);
        scoreView.setText("" + values.get(position).getScore());

        // TODO: Format the date
        TextView dateView = (TextView) rowView.findViewById(R.id.date);
        dateView.setText(values.get(position).getDate().toString());

        ImageView coolView = (ImageView) rowView.findViewById(R.id.row_cool);
        coolView.setImageResource(R.drawable.timed);

        return rowView;
    }
}
