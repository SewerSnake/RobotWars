package com.example.robotwars;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * The activity that shows top
 * ranking robots. This is the
 * average of the ranking and price.
 */
public class TopListActivity extends BottomNavigationBaseActivity {

    private static final String TAG = "TopListActivity";

    private TopListCursorAdapter cursorAdapter;
    private ListView topListView;
    private DBHelper dbHelper = new DBHelper(this);
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_list);

        topListView = findViewById(R.id.robotTopList);

        Toolbar toolbar = findViewById(R.id.toolbarTop);
        setSupportActionBar(toolbar);

        logo = findViewById(R.id.logoImageView);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initialize();

        /**
         * When clicking on any robot, this sends intent to the item that is pressed.
         */
        topListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra("RobotID", id);
                intent.putExtra("info", 3);
                startActivity(intent);
            }
        });

        // When the "Robot Wars" logo in the toolbar is clicked, MainActivity is launched.
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (TopListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initializes the activity.
     */
    private void initialize() {
        setupBottomNavigation();
        createCursorAdapter();
    }

    /**
     * Sets up the Cursor Adapter.
     */
    private void createCursorAdapter() {
        Cursor cursor = dbHelper.getTopListCursor();
        cursorAdapter = new TopListCursorAdapter(this, cursor);
        topListView.setAdapter(cursorAdapter);
    }

    /**
     * Upper toolbar with icons.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.camera_icon, menu);
        return true;
    }

    /**
     * Handles what happens an icon in the toolbar is clicked.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.cameraIcon2) {
            Intent cameraIntent = new Intent(this, RankingActivity.class);
            startActivity(cameraIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
