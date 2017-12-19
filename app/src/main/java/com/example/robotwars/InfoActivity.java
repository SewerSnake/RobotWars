package com.example.robotwars;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An activity that shows a specific robot and its information.
 */
public class InfoActivity extends BottomNavigationBaseActivity {

    private static final String TAG = "InfoActivity";
    private Context context = InfoActivity.this;

    private Toolbar toolbar;

    private ImageView logo;
    private ImageView ivRobot;
    private ImageView ivLocation;

    private BitmapHelper bitmapHelper;

    private TextView tvRobotName;
    private TextView tvCategory;
    private TextView tvPriceScore;
    private TextView tvTasteScore;
    private TextView tvRateScore;
    private TextView tvInfo;
    private TextView tvLocation;

    private Robot robot;
    private DBHelper helper;
    private Long id;
    private int caller;

    private AlertDialog dialog;
    private EditText etInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        startInitialize();
    }

    /**
     * A method that triggers the setup of InfoActivity.
     */
    private void startInitialize() {
        findViews();
        enableScrollFunction();
        setupBottomNavigation();
        getSpecificRobot();
        setupRobotInfo();
        addSharePhotoFragment();
        setupLogo();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * A method that finds all the views and binds them to the code.
     */
    private void findViews() {
        ivRobot = findViewById(R.id.ivRobot);
        ivLocation = findViewById(R.id.ivLocation);
        logo = findViewById(R.id.logoImageView);
        tvRobotName = findViewById(R.id.tvRobotName);
        tvCategory = findViewById(R.id.tvCategory);
        tvPriceScore = findViewById(R.id.tvPriceScore);
        tvTasteScore = findViewById(R.id.tvTasteScore);
        tvRateScore = findViewById(R.id.tvRateScore);
        tvInfo = findViewById(R.id.tvInfo);
        tvLocation = findViewById(R.id.tvLocation);
        toolbar = findViewById(R.id.toolbarTop);
    }

    /**
     * A method that handles what happens
     * when the logo is clicked.
     */
    private void setupLogo() {
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    /**
     * Upper toolbar with icons.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.info_activity_menu, menu);
        return true;
    }

    /**
     * Handles what happens when a
     * icon in the toolbar is clicked.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_camera:
                Intent cameraIntent = new Intent(this, RankingActivity.class);
                startActivity(cameraIntent);
                break;
            case R.id.editComment:
                onEditClick();
                break;
            case R.id.editRobotName:
                onEditRobotNameClick();
                break;
            case R.id.deleteRobot:
                onDeleteClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A method that receives the Id for a specific robot and
     * puts its information in an instance of an Robot object.
     */
    private void getSpecificRobot() {
        Intent intent = getIntent();
        id = intent.getLongExtra("RobotID", 0);
        caller = intent.getIntExtra("info", 0);
        helper = new DBHelper(context);
        robot = helper.getRobotById(id);
    }

    /**
     * A method to show all the information about the specific robot.
     */
    private void setupRobotInfo() {

        Bitmap image = BitmapFactory.decodeFile(robot.getPhotoPath());
        String output;

        ivRobot.setImageBitmap(image);

        ivRobot.setAdjustViewBounds(true);
        ivRobot.setScaleType(ImageView.ScaleType.CENTER_CROP);

        tvRobotName.setText(robot.getName());
        tvCategory.setText(robot.getCategoryName());

        int newPrice = ((int) robot.getPrice());
        int newTaste = ((int) robot.getTaste());

        output = getResources().getString(R.string.price) + " " + newPrice;
        tvPriceScore.setText(output);
        output = getResources().getString(R.string.taste) + " " + newTaste;
        tvTasteScore.setText(output);
        output = getResources().getString(R.string.rate) + " " + String.valueOf(robot.getAverage());
        tvRateScore.setText(output);

        tvInfo.setText(robot.getComment());

        if (robot.getLocation() == null) {
            ivLocation.setVisibility(View.GONE);
        } else {
            tvLocation.setText(robot.getLocation());
        }
    }

    /**
     * A method that shows a bigger robot picture.
     *
     * @param view - the picture being clicked
     */
    public void onRobotImageClick(View view) {
        //Log.d(TAG, "onRobotImageClick: true");
        Intent intent = new Intent(context, ShowBigRobotActivity.class);
        intent.putExtra("photoPath", robot.getPhotoPath());
        startActivity(intent);
    }

    /**
     * A method that adds the fragment SharePhotoFragment
     * to the layout and places it in the btnShare view.
     */
    private void addSharePhotoFragment() {
        //Log.d(TAG, "addSharePhotoFragment: adds the SharePhotoFragment");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("photoPath", robot.getPhotoPath());
        SharePhotoFragment spf = new SharePhotoFragment();
        spf.setArguments(bundle);
        fragmentTransaction.add(R.id.btnShare, spf);
        fragmentTransaction.commit();
    }

    /**
     * A method that enables the scroll function in the TextView tvInfo.
     */
    private void enableScrollFunction() {
        //Log.d(TAG, "enableScrollFunction: enables scroll function in tvInfo view");
        tvInfo.setMovementMethod(new ScrollingMovementMethod());
    }

    /**
     * A method that takes the user back to the previous Activity.
     *
     * @param view - the view being clicked and calling the method
     */
    public void onNavBackClick(View view) {
        //Log.d(TAG, "onNavBackClick: nav back clicked");
        onBackPressed();
    }

    /**
     * A method that enables the editing of the robot name,
     * shown in an AlertDialog.
     * The new robot name is saved into the database.
     */
    private void onEditRobotNameClick() {
        //Log.d(TAG, "editRobotName: ");
        makeAlertDialog(getResources().getString(R.string.editRobotName), null,
                getResources().getString(R.string.cancel), robot.getId());
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getResources().getString(R.string.saveRobot),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                robot.setName(etInfo.getText().toString());
                helper.editRobotName(robot.getId(), robot.getName());
                tvRobotName.setText(robot.getName());
            }
        });
        etInfo.setText(robot.getName());
        dialog.show();
    }

    /**
     * A method that enables edit for the robot comment, shown in an AlertDialog.
     * The new comment gets saved into the database.
     */
    public void onEditClick() {
        //Log.d(TAG, "onEditClick: edit clicked.");
        makeAlertDialog(getResources().getString(R.string.edit), null,
                getResources().getString(R.string.cancel), robot.getId());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getResources().getString(R.string.saveRobot),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                robot.setComment(etInfo.getText().toString());
                helper.editRobot(robot.getId(), robot.getComment());
                tvInfo.setText(robot.getComment());
            }
        });
        etInfo.setText(robot.getComment());
        dialog.show();
    }

    /**
     * A method that takes help from the DBHelper to removes all
     * the information about a specific robot that the user wants to delete
     * and then checks for the activity that triggered InfoActivity.
     */
    public void onDeleteClick() {
        //Log.d(TAG, "onDeleteClick: clicked");
        makeAlertDialog(getResources().getString(R.string.delete),
                getResources().getString(R.string.deleting),
                getResources().getString(R.string.no), 0);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.removeRobot(id);
                checkCaller();
            }
        });
        dialog.show();
    }

    /**
     * A method that helps create an AlertDialog.
     *
     * @param title   - the Title of the dialog
     * @param message - the message of the dialog
     * @param button  - the text on the button
     * @param id      - id of the robot to edit
     */
    private void makeAlertDialog(String title, String message, String button, long id) {
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(title);
        dialog.setIcon(R.drawable.logo);
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (id != 0) {
            etInfo = new EditText(this);
            etInfo.setElevation(0);
            dialog.setView(etInfo);
        }
    }

    /**
     * A method that checks which activity that triggered InfoActivity
     * and takes us back to that activity after deleting the specific robot.
     * If it was RankingActivity, we want the user to go back to MainActivity.
     */
    private void checkCaller() {
        switch (caller) {
            case 1:
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                break;
            case 2:
                Intent categoriesIntent = new Intent(context, CategoriesActivity.class);
                categoriesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(categoriesIntent);
                break;
            case 3:
                Intent topListIntent = new Intent(context, TopListActivity.class);
                topListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(topListIntent);
                break;
        }
    }

    /**
     * A method that shows on a map where the user checked in the specific robot.
     *
     * @param view - the view being clicked and calling the method
     */
    public void onLocationClick(View view) {
        /*Log.d(TAG, "onLocationClick: location clicked.");
        Intent mapIntent = new Intent(InfoActivity.this, MapActivity.class);
        mapIntent.putExtra("robotId", robot.getId());
        mapIntent.putExtra("ID", 2);
        startActivity(mapIntent);*/
    }
}
