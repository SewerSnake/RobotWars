package com.example.robotwars;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;

/**
 * A class that handles the sharing of the photo to Facebook.
 */

public class SharePhotoFragment extends Fragment {

    private static final String TAG = "SharePhotoFragment";
    private ShareButton btnShare;

    /**
     * A method that makes the fragment instantiate and return its user
     * interface view ShareButton.
     *
     * @param inflater           - Object to help to inflate the view
     * @param container          - parent ViewGroup from InfoActivity's layout
     * @param savedInstanceState - containing data about the previous instance of the fragment
     * @return the view ShareButton
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            return inflater.inflate(R.layout.share_fragment, container, false);
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * A method being called right after onCreateView has returned ShareButton view.
     *
     * @param view               - ShareButton
     * @param savedInstanceState - containing data about the previous instance of the fragment
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.d(TAG, "onViewCreated: ");
        setSharePhoto(view);
    }

    /**
     * A method that builds the photo to share on Facebook.
     *
     * @param view - ShareButton
     */
    private void setSharePhoto(View view) {
        //Log.d(TAG, "setSharePhoto: ");
        Bitmap image = BitmapFactory.decodeFile(getArguments().getString("photoPath"));
        SharePhoto sharePhoto = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                .addPhoto(sharePhoto)
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#RobotWars")
                        .build())
                .build();

        btnShare = view.findViewById(R.id.btnShare);
        btnShare.setShareContent(sharePhotoContent);
    }
}
