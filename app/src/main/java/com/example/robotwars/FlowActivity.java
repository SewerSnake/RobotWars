package com.example.robotwars;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

/**
 * A class that contains
 * one image. The image shows
 * a flow chart, for greater
 * understanding of the app.
 * There is also one button to allow
 * the user to proceed
 * to RankingActivity.
 */
public class FlowActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);

        imageView = findViewById(R.id.flowChart);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;

        imageView.setImageResource(R.drawable.flow_chart_2);

        int newHeight = 600; // New height in pixels
        int newWidth = screenWidth; // New width in pixels

                /*
                    requestLayout()
                        Call this when something has changed which has
                        invalidated the layout of this view.
                */
        imageView.requestLayout();

                /*
                    getLayoutParams()
                        Get the LayoutParams associated with this view.
                */

        // Apply the new height for ImageView programmatically
        imageView.getLayoutParams().height = newHeight;

        // Apply the new width for ImageView programmatically
        imageView.getLayoutParams().width = newWidth;

        // Set the scale type for ImageView image scaling
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

    }

    public void ok(View view) {
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

}
