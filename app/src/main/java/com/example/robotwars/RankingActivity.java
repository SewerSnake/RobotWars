package com.example.robotwars;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class for taking a photo
 * of a robot, or download an image from
 * a website. The user then ranks how
 * good the robot is, as well as its price.
 * The user chooses a weapon category. There
 * are 11 categories in total. A comment must
 * also be added.
 */
public class RankingActivity extends AppCompatActivity implements ImageDownloadedListener {

    private static final String TAG = "RankingActivity";

    private ImageView robotImage;

    private SeekBar tasteBar;
    private SeekBar priceBar;

    private EditText robotComment;

    private EditText robotName;

    private ImageButton mapButton;
    private Spinner categorySpinner;
    private TextView tasteRateNumber;
    private TextView priceRateNumber;

    private Uri selectedImage;
    
    private String location = null;
    private double lat = 0.0;
    private double lng = 0.0;
    private LatLng latLng;
    
    static final int REQUEST_TAKE_PHOTO = 1337;
    static final int RESULT_LOAD_IMAGE = 217;
    static final int REQUEST_CODE = 7175;
    
    private String name;
    private String comment;
    private double taste;
    private double price;
    private int categoryId;
    private String picture;
    
    private Category category = new Category();

    private String address;
    private String phoneNumber;
    private String website;
    private String smallPicture;

    private Bitmap littleImage;
    private Bitmap largeImage;
    
    private String mCurrentPhotoPath;

    private TextView checkInText;
    private Boolean edited = false;

    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        findViews();
        cameraLauncher();
        setSeekbars(tasteBar, tasteRateNumber);
        setSeekbars(priceBar, priceRateNumber);
        findRobotCategories();
    }

    /**
     * Shows the number that the SeekBar
     * is currently on (0-10). The user doesn't
     * need to guess the value.
     */
    public void setSeekbars(SeekBar seekBar, final TextView textView) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String output = "" + progress;
                textView.setText(output);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * Fills the Spinner with all
     * weapon categories.
     */
    public void findRobotCategories() {

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                dbHelper.getAllCategories());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);
    }

    /**
     * Finds the views and adjusts the
     * elevation of robotComment and
     * robotName.
     */
    private void findViews() {
        robotImage = findViewById(R.id.robotImage);
        tasteBar = findViewById(R.id.tasteBar);
        priceBar = findViewById(R.id.priceBar);
        robotComment = findViewById(R.id.robotComment);

        robotName = findViewById(R.id.robotName);

        categorySpinner = findViewById(R.id.categorySpinner);
        tasteRateNumber = findViewById(R.id.tasteRateNumber);
        priceRateNumber = findViewById(R.id.priceRateNumber);
        mapButton = findViewById(R.id.mapButton);
        checkInText = findViewById(R.id.checkInText);

        robotComment.setElevation(0);
        robotName.setElevation(0);
    }

    /**
     * The method controls that everything is
     * in order before putting the values
     * into the database and InfoViews.
     * @param view  the view
     */
    public void onSaveButtonClick(View view) {
        name = saveRobotName();
        comment = saveRobotComment();
        taste = saveTaste();
        price = savePrice();
        category = (Category)categorySpinner.getSelectedItem();
        categoryId = (int) category.getId();

        if (name == null || name.isEmpty()) {
            makeToast(getApplicationContext().getString(R.string.nameTheRobot));
        } else if (comment == null || comment.isEmpty()) {
            makeToast(getApplicationContext().getString(R.string.commentTheRobot));
        } else if (picture == null || picture.isEmpty()) {
            makeToast(getApplicationContext().getString(R.string.pictureTheRobot));
        } else if (category.getName().equals("Unknown")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(RankingActivity.this);
            builder.setIcon(R.drawable.logo);
            builder.setMessage(getApplicationContext().getString(R.string.saveToUnknown)).setCancelable(false)
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK &&
                                    event.getAction() == KeyEvent.ACTION_UP &&
                                    !event.isCanceled()) {
                                finish();
                                return true;
                            }
                            return false;
                        }
                    })
                    .setPositiveButton(getApplicationContext().getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addRobot();

                        }
                    })
                    .setNegativeButton(getApplicationContext().getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        /*
                        //For later use, if we want to upload image from gallery
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                        */

                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getApplicationContext().getString(R.string.lastChange));
            alert.show();
        } else {
            addRobot();
        }
    }

    /**
     * Adds a robot to the database and
     * then proceeds to send the user to InfoActivity.
     */
    public void addRobot() {

        Robot robot = new Robot(name, categoryId, price,
                taste, comment, picture,
                location, lat, lng,
                address, phoneNumber, website,
                smallPicture);

        dbHelper.addRobot(robot);
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("RobotID", robot.getId());
        intent.putExtra("info",1);
        startActivity(intent);
        finish();
    }

    /**
     * A method which creates a dialog box.
     * The user enters an url in the EditText,
     * and presses the "Download" button. A
     * Toast appears if the user left the
     * field blank. By pressing the "Cancel"
     * button, the dialog box vanishes.
     * @param view  the view
     */
    public void onDownloadClick(View view) {

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        final EditText editText = new EditText(this);
        editText.setElevation(0);
        dialog.setTitle(R.string.downloadTitle);
        dialog.setIcon(R.drawable.logo);
        dialog.setView(editText);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getResources().getString(R.string.downloadButton),
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.nourlEntered, Toast.LENGTH_SHORT).show();
                } else {
                    download(editText.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                getResources().getString(R.string.cancelDownloadButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    /**
     * A method that sends the user to the gps menu,
     * upon clicking the "Check in" button.
     * @param view  the view
     */
    public void onCheckinClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("ID", 1);
        startActivityForResult(intent, 1);
    }

    /**
     * (Prepares an url for the task.)
     * Calls the 'magic' execute method.
     * @param url  the full http path of the picture
     */
    private void download(String url) {
        /*String url = "http://robotwars.wikia.com/wiki/"
                + name
                + "?file="
                + name
                + ".png";*/
        DownloadImageTask downloadImageTask = new DownloadImageTask(this);
        downloadImageTask.execute(url);
    }

    /**
     * Shows the downloaded picture. Saves
     * the picture the same way as when taking
     * a picture with the camera.
     * @param bitmap    the downloaded bitmap
     */
    public void onImageDownloaded(Bitmap bitmap) {
        if (bitmap == null) {
            makeToast(getResources().getString(R.string.error_download));
        } else {
            File photoFile = null;

            try {
                photoFile = createImageFile("");
            } catch (IOException ioException) {
                //Error occurred while creating the File
                makeToast(getResources().getString(R.string.error_file));
            }

            saveBitmapToFile(bitmap, photoFile);

            saveImageToFiles();
        }
    }

    /**
     * A method to restart the camera and give
     * the user a chance to take a new picture.
     * @param view  the view
     */
    public void onEditButtonClick(View view) {
        edited = true;
        cameraLauncher();
    }

    /**
     * A method to send the user back to the main page of the app
     * if he/she doesn't want to rank a robot anymore.
     * @param view  the view
     */
    public void onDiscardButtonClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Starts a dialog with two options:
     * Take a picture with the phone
     * Upload picture from gallery
     */
    public void cameraLauncher() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(RankingActivity.this);

        builder.setIcon(R.drawable.logo);

        builder.setMessage(getApplicationContext().getString(R.string.chooseAlternative)).setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            if (edited) {
                                dialog.dismiss();
                            } else {
                                finish();
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .setPositiveButton(getApplicationContext().getString(R.string.takePhoto),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dispatchTakePictureIntent();

                    }
                })
                .setNegativeButton(getApplicationContext().getString(R.string.noPhoto),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        /*
                        //For later use, to upload an image from the gallery
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                        */
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getApplicationContext().getString(R.string.timeForCombat));
        alert.show();
    }

    /**
     * To create and invoke the Intent for the picture.
     * First, ensure that there is a camera activity
     * to handle the intent.
     */
    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Creates a file to put the picture in.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile("");
            } catch (IOException ioException) {
                //Error occurred while creating the File
                makeToast(getResources().getString(R.string.error_file));
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.robotwars.FileProvider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap robotPhoto = (Bitmap) extras.get("data");
            robotImage.setImageBitmap(robotPhoto);

        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            saveImageToFiles();

        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = findViewById(R.id.robotImage);

            Bitmap bmp;

            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                bmp = null;
            }
            if (bmp != null)
                imageView.setImageBitmap(bmp);

        } else if (requestCode == 1 && resultCode == 1) {

            location = data.getStringExtra("location");

            latLng = new LatLng(data.getDoubleExtra("lat",0.0),
                    data.getDoubleExtra("lng",0.0));

            lat = latLng.latitude;
            lng = latLng.longitude;

            website = data.getStringExtra("website");
            address = data.getStringExtra("address");
            phoneNumber = data.getStringExtra("phoneNumber");

            //Not the best solution... Change this to access mipmap later
            mapButton.setImageResource(R.drawable.logo);

            mapButton.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.blackbrew));

            checkInText.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Takes the current photo and creates two new ones;
     * One large one, 960 x 960. This is also set in robotImage.
     * One small one, 500 x 500, which is used in other activities.
     */
    private void saveImageToFiles() {
        largeImage = scalePicture(960,960);

        littleImage = scalePicture(500,500);

        try {
            File fBig = createImageFile("big");
            File fSmall = createImageFile("small");

            saveBitmapToFile(largeImage, fBig);
            saveBitmapToFile(littleImage, fSmall);

            picture = fBig.getAbsolutePath();
            smallPicture = fSmall.getAbsolutePath();

        } catch (IOException e) {
            picture = null;
            smallPicture = null;
        }

        robotImage.setImageBitmap(largeImage);
    }

    /**
     * Saves the bitmap object to the
     * selected file.
     * @param bitmap    the bitmap
     * @param file  the file
     */
    private void saveBitmapToFile(Bitmap bitmap, File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            out = null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets a Bitmap via an uri
     * (Uniform Resource Identifier).
     * @param uri   the uri
     * @return  a Bitmap object
     * @throws IOException  if an exception occurs while reading from file
     */
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    /**
     * Creates a collision-resistant name for the image file.
     * @return the image with the new name
     * @throws IOException - if something goes wrong
     */
    public File createImageFile(String postfix) throws IOException {

        String dateStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // Creates a name for the image file
        String imageFileName = "JPEG_" + dateStamp + "_" + postfix;

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Parameters: prefix, suffix, directory
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Saves the path for usage with ACTION_VIEW intents.
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    /**
     * Adds the picture to the Android phones gallery.
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
     * Scales the picture to the target width
     * and height.
     * @param targetW   the target width
     * @param targetH   the target height
     * @return  a Bitmap object
     */
    private Bitmap scalePicture(int targetW, int targetH) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

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

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    /**
     * Creates and shows a Toast.
     * @param text  the text to appear in the Toast
     */
    public void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * A method to get the price rate.
     * @return a double with the price rate.
     */
    private double savePrice() {
        return Double.parseDouble(priceRateNumber.getText().toString());
    }

    /**
     * A method to get the rank of the robot.
     * @return a double with the ranking value
     */
    private double saveTaste() {
        return Double.parseDouble(tasteRateNumber.getText().toString());
    }

    /**
     * Saving the name of the robot.
     * @return a String for the name of the robot.
     */
    private String saveRobotName() {
        return robotName.getText().toString();
    }

    /**
     * Saving the comment for the robot.
     * @return a String for the comment to the robot.
     */
    private String saveRobotComment() {
        return robotComment.getText().toString();
    }

}

