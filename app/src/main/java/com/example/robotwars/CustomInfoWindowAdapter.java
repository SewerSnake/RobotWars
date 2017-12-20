package com.example.robotwars;

import com.google.android.gms.maps.GoogleMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow2;
    private Context mContext;

    /**
     * Constructor for the customized check in window.
     * @param context   the context of the app
     */
    public CustomInfoWindowAdapter(Context context) {
        this.mContext = mContext;
        mWindow2 = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view){
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);

        //Sets the markers title to the title of the window
        tvTitle.setText(title);

        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.snippet);

        //Sets the markers title to the title of the window
        if(!snippet.equals("")) {
            tvSnippet.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow2);
        return mWindow2;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow2);
        return mWindow2;
    }

}

