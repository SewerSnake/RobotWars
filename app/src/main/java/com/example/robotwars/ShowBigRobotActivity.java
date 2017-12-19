package com.example.robotwars;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

/**
 * Shows a larger image of the robot.
 */
public class ShowBigRobotActivity extends AppCompatActivity {

    private static final String TAG = "RobotPhotoActivity";
    private BitmapHelper bitmapHelper;
    private String photoPath;
    private Bitmap image;
    private ImageView ivBigRobot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate: robotphoto");
        setContentView(R.layout.activity_show_big_robot);
        showBigPicture();
    }

    /**
     * A method that shows the picture of
     * the robot in a larger version.
     */
    private void showBigPicture() {
        Intent intent = getIntent();
        photoPath = intent.getStringExtra("photoPath");
        ivBigRobot = findViewById(R.id.ivBigRobot);

        image = BitmapFactory.decodeFile(photoPath);
        ivBigRobot.setImageBitmap(image);

        //ivBigRobot.setAdjustViewBounds(true);
        //ivBigRobot.setImageBitmap(bitmapHelper.decodeSampledBitmapFromFile(photoPath, 960, 960));
    }
}

