package com.example.robotwars;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

public class CreditActivity extends AppCompatActivity {

    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
    }

    public void videoLink(View view) {
        String video_path = "https://www.youtube.com/watch?v=p63WNi_yYwM";
        Uri uri = Uri.parse(video_path);

        // With this line the Youtube application, if installed, will launch immediately.
        // Without it you will be prompted with a list of the application to choose.
        uri = Uri.parse("vnd.youtube:"  + uri.getQueryParameter("v"));

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
