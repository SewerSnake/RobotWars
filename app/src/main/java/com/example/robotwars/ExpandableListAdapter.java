package com.example.robotwars;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * This class bestows mechanics upon expandable listview.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<Category> header;
    private HashMap<String, List<Robot>> child;

    /**
     * Constructor
     */

    public ExpandableListAdapter(Context context, List<Category> listDataHeader,
                                 HashMap<String, List<Robot>> listChildData) {
        this._context = context;
        this.header = listDataHeader;
        this.child = listChildData;
    }

    /**
     * Standard methods for ExpandableList Adapter
     */

    @Override
    public int getGroupCount() {
        // Get header size
        return this.header.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // return children count
        return this.child.get(this.header.get(groupPosition).getName()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // Get header position
        return this.header.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // This will return the child
        return this.child.get(this.header.get(groupPosition).getName()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return this.header.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return this.child.get(this.header.get(groupPosition).getName()).get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Most important filler for category/headers, changes the value of "convert" view.
     */

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        // Getting header title
        String headerTitle = ((Category) getGroup(groupPosition)).getName();

        // Inflating header xml layout and setting text
        if (convertView == null) {
            LayoutInflater infalInflater =
                    (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = infalInflater.inflate(R.layout.list_categories, parent, false);
        }

        //set content for the "parent"/header views
        TextView header_text = (TextView) convertView.findViewById(R.id.header);
        header_text.setText(headerTitle);

        // If group is expanded then change the text into bold and change the arrow icon up/down
        if (isExpanded) {
            header_text.setTypeface(null, Typeface.BOLD);
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less_black_48dp, 0);
        } else {
            // If group is not "expanded" then change the text and icon back into normal

            header_text.setTypeface(null, Typeface.NORMAL);
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_black_48dp, 0);
        }

        return convertView;
    }


    /**
     * Most important filler for robot/items changes the value of "convert" view
     */

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Getting child text
        final String childText = ((Robot)getChild(groupPosition, childPosition)).getName();
        // Inflating child layout and setting textview, no else for convertview unsure about optimization?
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_categoryitems, parent, false);
        }

        // Set content in the child views
        TextView child_text = (TextView) convertView.findViewById(R.id.child);

        child_text.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

