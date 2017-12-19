package com.example.robotwars;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * For aiding bitmap operations.
 */
public class BitmapHelper extends AppCompatActivity {

    private String mCurrentPhotoPath;

    public static Bitmap decodeSampledBitmapFromFile(String string,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(string, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(string, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    /**
     * Creates a collision-resistant name for the image file
     * @return the image with the new name
     * @throws IOException - if something goes wrong
     */
    public File createImageFile() throws IOException {
        // Create a name for the image file
        String dateStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + dateStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //within parentheses: prefix, suffix, directory
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        //Save the file, path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        //Log.d(TAG, "createImageFile: this works");
        return image;
    }

    /**
     * Adds the picture to the gallery.
     */
    public void addPictureToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        //Log.d(TAG, "addPictureToGallery: this works");
    }

    /**
     * Method for making a String appear in a toast.
     * @param text  the text to be written
     */
    public void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public static int calculateInSampleSizeInside(int width, int height, int maxWidth, int maxHeight) {
        int inSampleSize = 1;

        int heightRatio = (int)Math.ceil((float)height/(float)maxHeight);
        int widthRatio = (int)Math.ceil((float)width/(float)maxWidth);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                inSampleSize = heightRatio;
            } else {
                inSampleSize = widthRatio;
            }
        }
        return inSampleSize;
    }

}

