package com.example.robotwars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.VideoView;

/**
 * Multimedia class combining animations, sounds
 * and a video background.
 */
public class Multimedia {

    private static final String TAG = "Multimedia";
    private Activity activity;

    /**
     * Receives an Activity and places
     * it in the instance variable activity.
     * @param activity  the Activity object
     */
    public Multimedia(Activity activity) {
        this.activity = activity;
    }

    /**
     * Sets the background video, the sound upon
     * clicking the "Robot Wars" logo, as well as
     * rotating the logo itself.
     * @param context   the context of the app
     */
    protected void setMultimedia(Context context) {
        videoBackground();
        countdownSoundClick(context);
        rotateLogo();
    }

    /**
     * Plays the background video directly from
     * the raw directory. Ensures that the video
     * loops like a gif does.
     */
    public void videoBackground() {

        VideoView videoview = this.activity.findViewById(R.id.videoView);

        Uri uri = Uri.parse("android.resource://" + this.activity.getPackageName()
                + "/" + R.raw.matilda_oota);

        videoview.setVideoURI(uri);
        videoview.start();

        videoview.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    /**
     * Rotates the "Robot Wars" logo,
     * as well as plays a introductory
     * sound bit.
     */
    public void rotateLogo() {
        //Rotates the logo
        ImageView logoRotate = this.activity.findViewById(R.id.robotwars_main_image);
        final Animation animRotate = AnimationUtils.loadAnimation(this.activity, R.anim.animation);
        logoRotate.startAnimation(animRotate);

        //Plays the sound file threetwoone.mp3
        MediaPlayer mp = MediaPlayer.create(this.activity, R.raw.threetwoone);
        mp.start();
    }

    /**
     *  Plays a sound upon clicking
     *  the logo. Also scales up the
     *  logo via an animation.
     * @param context   the context of the app
     */
    public void countdownSoundClick(Context context) {

        final Context _context = context;
        final ImageView robotSound = this.activity.findViewById(R.id.robotwars_main_image);

        final MediaPlayer mp = MediaPlayer.create(this.activity, R.raw.click_logo);

        robotSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.setVolume(1.0f, 1.0f);
                mp.start();

                final Animation animScale =
                        AnimationUtils.loadAnimation(_context, R.anim.scale_up);

                robotSound.startAnimation(animScale);

                Intent cameraIntent =
                        new Intent(_context.getApplicationContext(), FlowActivity.class);

                _context.startActivity(cameraIntent);
            }
        });

        AnimationUtils.loadAnimation(_context, R.anim.scale_up);

    }

}
