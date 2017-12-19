package com.example.robotwars;

import android.graphics.Bitmap;

interface ImageDownloadedListener {

    /**
     * Sets the image in RankingActivity and
     * saves it for further use.
     * @param bitmap    the bitmap in question
     */
    void onImageDownloaded(Bitmap bitmap);

}
