package com.example.robotwars;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class for downloading an
 * image from robotwarswikia.com
 * asynchronously.
 */
public class DownloadImageTask extends AsyncTask<String,Void,Bitmap> {

    private ImageDownloadedListener listener;

    private HttpURLConnection connection;

    public DownloadImageTask(ImageDownloadedListener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        URL url;

        try {
            url = new URL(params[0]);
            //url = new URL("http://www.battlebotsupdate.com/wp-content/uploads/2017/11/rwuks10e1_donnie.png");

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

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (connection != null)
            connection.disconnect();
        listener.onImageDownloaded(bitmap);
    }

}
