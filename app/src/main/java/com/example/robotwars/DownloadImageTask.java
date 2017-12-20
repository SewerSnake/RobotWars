package com.example.robotwars;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class for downloading an
 * image from a web page
 * asynchronously.
 */
public class DownloadImageTask extends AsyncTask<String,Void,Bitmap> {

    private ImageDownloadedListener listener;

    private HttpURLConnection connection;

    /**
     * Ensures that DownLoadImageTask only has access
     * to one single method in RankingActivity.
     * @param listener  A RankingActivity object
     */
    public DownloadImageTask(ImageDownloadedListener listener) {
        this.listener = listener;
    }

    /**
     * Uses http GET to retrieve an image from
     * the web page the user provided.
     * @param params    The url for the image
     * @return  a Bitmap object
     */
    @Override
    protected Bitmap doInBackground(String... params) {

        URL url;

        try {
            url = new URL(params[0]);

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(7000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();

            BufferedInputStream bufinput = new BufferedInputStream(input);

            Bitmap bitmap = BitmapFactory.decodeStream(bufinput);
            bufinput.close();

            return bitmap;

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            if (connection != null)
                connection.disconnect();
            return null;
        }
    }

    /**
     * This occurs after the image has been
     * downloaded. The http connection is
     * closed, and the method in
     * RankingActivity is called.
     * @param bitmap    the downloaded Bitmap
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (connection != null)
            connection.disconnect();
        listener.onImageDownloaded(bitmap);
    }

}
