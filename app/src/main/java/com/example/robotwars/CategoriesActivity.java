package com.example.robotwars;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.widget.ExpandableListView;

import java.util.HashMap;
import java.util.List;

/**
 * This activity implements ExpandableListAdapter.java with 10 built in methods that 
 * create the mechanics for expandable ListView.
 * The expandable ListView refers 3 .xml files: activity_categories, list_categories and list_categoryitems.
 * CategoryArray RobotArrays from database + HashMap is needed to form the ExpandableList.
 * The setItems() method fills data and itemizes the ExpandableList.
 * HashMap creates double array with category and item relation,
 * it refers index of header(category) to child(robotname) and keeps them in order.
 */
public class CategoriesActivity extends BottomNavigationBaseActivity {

    private static final String TAG = "CategoriesActivity";

    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;

    private DBHelper mDBHelper;
    private HashMap<String, List<Robot>> hashMap;
    private List<Category> header;

    private ImageView logo;

    private AlertDialog dialog;
    private EditText editTextAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        setupBottomNavigation();
        Toolbar toolbar = findViewById(R.id.toolbarTop);
        setSupportActionBar(toolbar);
        logo = findViewById(R.id.logoImageView);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //ListView object from xml. Setting group indicator null for custom indicator.
        expandableListView = findViewById(R.id.simple_expandable_listview);
        expandableListView.setGroupIndicator(null);

        mDBHelper = new DBHelper(this);

        setItems();

        //Passes the three required parameters; Object of context, header array, children in HashMap.
        adapter = new ExpandableListAdapter(CategoriesActivity.this, header, hashMap);
        expandableListView.setAdapter(adapter);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Intent i = new Intent(getApplicationContext(), InfoActivity.class);

                i.putExtra("RobotID", id);

                i.putExtra("info", 2);

                startActivity(i);

                return true;
            }
        });
    }

    /**
     * Loads the weapon categories into
     * the list.
     */
    private void setItems() {

        header = mDBHelper.getAllCategories();

        hashMap = new HashMap<>();

        // Adds headers + robots to list from Database
        for (int i = 0; i < header.size(); i++) {
            List<Robot> robotsByCategory = mDBHelper.getRobotsByCategory(header.get(i).getName());
            hashMap.put(header.get(i).getName(), robotsByCategory);
        }

    }

    /**
     * Upper toolbar with icons.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.category_activity_menu, menu);
        return true;
    }

    /**
     * Handles what happens if  an icon in the toolbar is clicked.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.aboutIcon) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.ic_camera) {
            Intent cameraIntent = new Intent(this, RankingActivity.class);
            startActivity(cameraIntent);
            return true;
        } else if (id == R.id.ic_addCategory) {
            onAddCategoryClick();
            return true;
        }
        else if (id == R.id.ic_deleteCategory) {
            onDeleteCategoryClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens a dialog box when the user clicks
     * the "Add a weapon category" button.
     *
     */
    public void onAddCategoryClick() {
        dialog = new AlertDialog.Builder(this).create();
        editTextAdd = new EditText(this);
        editTextAdd.setElevation(0);
        dialog.setTitle(R.string.addCategory);
        dialog.setIcon(R.drawable.logo);
        dialog.setView(editTextAdd);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.saveCategory),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editTextAdd.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.noCategoryEntered, Toast.LENGTH_SHORT).show();
                    onAddCategoryClick();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.addedCategory + editTextAdd.getText().toString(),
                            Toast.LENGTH_SHORT).show();

                    mDBHelper.addCategory(editTextAdd.getText().toString());
                    setItems();
                    adapter = new ExpandableListAdapter(CategoriesActivity.this, header, hashMap);
                    expandableListView.setAdapter(adapter);
                }
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Opens a dialog box when user clicks the Delete category button.
     * Gives options regarding the deletion of a category.
     */
    public void onDeleteCategoryClick() {
        dialog = new AlertDialog.Builder(this).create();

        editTextAdd = new EditText(this);
        editTextAdd.setElevation(0);
        dialog.setTitle(R.string.deleteCategory);
        dialog.setIcon(R.drawable.logo);
        dialog.setView(editTextAdd);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getResources().getString(R.string.deleteCategory), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String categoryName = editTextAdd.getText().toString();
                List<Category> allCategories = mDBHelper.getAllCategories();

                boolean notFound = true;

                for (Category c : allCategories) {
                    if (c.getName().equals(categoryName))
                        notFound = false;
                }

                if (notFound) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.categoryNotFound), Toast.LENGTH_SHORT).show();
                    onDeleteCategoryClick();

                } else {
                    if (mDBHelper.getRobotsByCategory(categoryName).size() > 0) {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.categoryNotEmpty), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.categoryDeleteSuccessful) + " " + categoryName, Toast.LENGTH_SHORT).show();
                        mDBHelper.deleteCategory(categoryName);
                        setItems();
                        adapter = new ExpandableListAdapter(CategoriesActivity.this, header, hashMap);
                        expandableListView.setAdapter(adapter);
                    }
                }
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
