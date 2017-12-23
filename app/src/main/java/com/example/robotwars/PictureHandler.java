package com.example.robotwars;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * A class for handling operations
 * regarding photos/images.
 */
public class PictureHandler {

    /**
     * Scales the picture to the target width
     * and height.
     * @param targetW   the target width
     * @param targetH   the target height
     * @return  a Bitmap object
     */
    public static Bitmap scalePicture(String path, int targetW, int targetH) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);

        // The dimensions of the Bitmap
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Decides how much to scale down the picture.
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decodes the image into a Bitmap, which is sized to fill the View.
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        // Most apps should avoid using inPurgeable to allow for a fast and fluid UI.
        // bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(path, bmOptions);
    }
}
