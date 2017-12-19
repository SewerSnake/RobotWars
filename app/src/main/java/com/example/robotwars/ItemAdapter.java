package com.example.robotwars;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An Adapter for holding information
 * about the creator of the app.
 */
public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    String[] persons;
    String[] descriptions;
    TypedArray image;


    public ItemAdapter(Context c, String[] p, String[] d, TypedArray img) {
        persons = p;
        descriptions = d;
        image = img;
        mInflator =(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return persons.length;
    }

    @Override
    public Object getItem(int p) {
        return persons[p];
    }

    @Override
    public long getItemId(int p) {
        return p;
    }

    @Override
    public View getView(int p, View view, ViewGroup viewGroup) {

        View v = mInflator.inflate(R.layout.detail, null);

        TextView nameTextView = (TextView) v.findViewById(R.id.nameTextView);
        TextView descriptionText = (TextView) v.findViewById(R.id.descriptionText);
        ImageView profile = (ImageView) v.findViewById(R.id.profile);

        String name = persons[p];
        String desc = descriptions[p];

        nameTextView.setText(name);
        descriptionText.setText(desc);
        profile.setImageResource(image.getResourceId(p, -1));

        return v;
    }
}

