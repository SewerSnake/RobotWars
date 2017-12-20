package com.example.robotwars;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * A class to setup and handle the BottomNavigation.
 * Uses a GitHub project for execution. All credit to
 * ittianyu!
 */
public class BottomNavigationBaseActivity extends AppCompatActivity {

    private BottomNavigationViewEx bottomNavigationViewEx;
    private Context context = BottomNavigationBaseActivity.this;

    /**
     * A method that sets up the bottom navigation.
     */
    protected void setupBottomNavigation() {
        bottomNavigationViewEx = findViewById(R.id.bottomNavigation);
        manipulateBottomNavigation();
        activateBottomNavigation();
    }

    /**
     * A method that when called manipulates some of
     * the settings for the bottom navigation.
     */
    public void manipulateBottomNavigation(){
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);
    }

    /**
     * A method that handles clicks on
     * the different icons in the navigation view.
     */
    private void activateBottomNavigation() {

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.ic_home:
                        item.setChecked(true);
                        Intent mainIntent = new Intent(context, MainActivity.class);
                        startActivity(mainIntent);
                        break;
                    case R.id.ic_category:
                        item.setChecked(true);
                        Intent categoriesIntent = new Intent(context, CategoriesActivity.class);
                        categoriesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(categoriesIntent);
                        break;
                    case R.id.ic_toplist:
                        item.setChecked(true);
                        Intent topListIntent = new Intent(context, TopListActivity.class);
                        topListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(topListIntent);
                        break;
                    case R.id.ic_mapview:
                        item.setChecked(true);
                        //Toast.makeText(getApplicationContext(),
                        // R.string.gpsNotImplemented, Toast.LENGTH_SHORT).show();
                        Intent mapIntent = new Intent(context, MapActivity.class);
                        mapIntent.putExtra("ID", 3);
                        startActivity(mapIntent);
                        break;
                }
                return false;
            }
        });
    }
}

