package com.example.robotwars;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

/**
 * A class for showing a homage for
 * the tenth season of "Robot Wars"
 * on Youtube.
 */
public class CreditActivity extends AppCompatActivity {

    private static final String TAG = "CreditActivity";

    private static final String PATH = "https://www.youtube.com/watch?v=p63WNi_yYwM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
    }

    /**
     * Upon pressing the button,
     * the video plays.
     * @param view  the view
     */
    public void videoLink(View view) {

        Uri uri = Uri.parse(PATH);

        // With this line the Youtube application, if installed, will launch immediately.
        // Without it you will be prompted with a list of the application to choose.
        uri = Uri.parse("vnd.youtube:"  + uri.getQueryParameter("v"));

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
