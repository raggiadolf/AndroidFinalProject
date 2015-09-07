package com.mycompany.dotsproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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

        /**
         * Setup the view for our high-score table
         */

        return rowView;
    }
}
