package com.example.robotwars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is specifically for working with the database.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "ROBOTWARS_DATABASE";
    private static final String ROBOT_TABLE = "RobotRanking";
    private static final String CATEGORY_TABLE = "RobotCategories";

    
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Creates the database for the first time.
     *
     * @param db    the SQLite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //TABLE: Robot
        String sql_robot = "CREATE TABLE " + ROBOT_TABLE + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "COL_ROBOT_NAME TEXT NOT NULL," +
                "COL_ROBOT_CATEGORY INTEGER," +
                "COL_ROBOT_PRICE INTEGER," +
                "COL_ROBOT_TASTE INTEGER," +
                "COL_ROBOT_AVERAGE INTEGER," +
                "COL_ROBOT_COMMENT TEXT," +
                "COL_ROBOT_IMAGE_PATH BLOB," +
                "COL_ROBOT_LOCATION TEXT," +
                "COL_ROBOT_LAT INTEGER," +
                "COL_ROBOT_LNG INTEGER," +
                "COL_ROBOT_ADD TEXT," +
                "COL_ROBOT_TEL TEXT," +
                "COL_ROBOT_WEB TEXT," +
                "COL_ROBOT_IMAGE_SMALL TEXT);";

        //TABLE: Categories
        String sql_categories = "CREATE TABLE " + CATEGORY_TABLE + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "COL_CATEGORY_NAME TEXT NOT NULL);";

        db.execSQL(sql_robot);
        db.execSQL(sql_categories);

        //Insert initial values into CATEGORY_TABLE
        ContentValues values = new ContentValues();
        values.put("COL_CATEGORY_NAME", "Unknown");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Flipper");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Lifter");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Axe");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Hammer");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Vertical crusher");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Horizontal crusher");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Vertical spinner");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Horizontal spinner");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Thwack");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Gripper");
        db.insert(CATEGORY_TABLE, null, values);
        values.put("COL_CATEGORY_NAME", "Rammer");
        db.insert(CATEGORY_TABLE, null, values);
    }

    /**
     * Recreates all tables in the database.
     *
     * @param db         Database
     * @param oldVersion the old version of the database
     * @param newVersion the new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ROBOT_TABLE);
        onCreate(db);
    }

    /**
     * Adds a robot to the database.
     *
     * @param robot the robot to add
     */
    public void addRobot(Robot robot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("COL_ROBOT_NAME", robot.getName());
        values.put("COL_ROBOT_CATEGORY", robot.getCategoryId());
        values.put("COL_ROBOT_PRICE", robot.getPrice());
        values.put("COL_ROBOT_TASTE", robot.getTaste());
        values.put("COL_ROBOT_AVERAGE", robot.getAverage());
        values.put("COL_ROBOT_COMMENT", robot.getComment());
        values.put("COL_ROBOT_IMAGE_PATH", robot.getPhotoPath());
        values.put("COL_ROBOT_LOCATION", robot.getLocation());
        values.put("COL_ROBOT_LAT", robot.getLat());
        values.put("COL_ROBOT_LNG", robot.getLng());
        values.put("COL_ROBOT_ADD", robot.getAddress());
        values.put("COL_ROBOT_TEL", robot.getTel());
        values.put("COL_ROBOT_WEB", robot.getWeb());
        values.put("COL_ROBOT_IMAGE_SMALL", robot.getPhotoPathSmall());

        //Insert() returns an id -> set this as the robot's id number
        long id = db.insert(ROBOT_TABLE, null, values);
        robot.setId(id);
        //Get the categoryName for the robot based on its categoryId
        robot.setCategoryName(getRobotCategoryName(robot));
    }

    /**
     * Adds a new category to the database.
     *
     * @param categoryName Name of the new category
     */
    public void addCategory(String categoryName) {
        Category category = new Category();

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("COL_CATEGORY_NAME", categoryName);
        long id = db.insert(CATEGORY_TABLE, null, values);
        category.setId(id);
        category.setName(categoryName);
    }

    /**
     * Returns the category name of the robot and sets the value of robot's category.
     *
     * @param robot the robot
     * @return name of the category
     */
    public String getRobotCategoryName(Robot robot) {
        int id = robot.getCategoryId();

        String selection = "_id=?";
        String[] selectionArgs = new String[]{Integer.toString(id)};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(CATEGORY_TABLE, null, selection,
                selectionArgs, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(1);
            robot.setCategoryName(name);
        }
        cursor.close();
        return robot.getCategoryName();
    }

    /**
     * Returns a robot from the database by its id.
     *
     * @param id id of the robot
     * @return a Robot object
     */
    public Robot getRobotById(long id) {
        String selection = "_id=?";
        String[] selectionArgs = new String[]{Long.toString(id)};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(ROBOT_TABLE, null, selection,
                selectionArgs, null, null, null);

        boolean success = cursor.moveToFirst();

        Robot robot = new Robot();

        if (success) {
            do {
                robot.setId(cursor.getLong(0));
                robot.setName(cursor.getString(1));
                robot.setCategoryId(cursor.getInt(2));
                robot.setPrice(cursor.getFloat(3));
                robot.setTaste(cursor.getFloat(4));
                robot.setAverage(cursor.getFloat(5));
                robot.setComment(cursor.getString(6));
                robot.setPhotoPath(cursor.getString(7));
                robot.setLocation(cursor.getString(8));
                robot.setLat(cursor.getDouble(9));
                robot.setLng(cursor.getDouble(10));
                robot.setAddress(cursor.getString(11));
                robot.setTel(cursor.getString(12));
                robot.setWeb(cursor.getString(13));
                robot.setPhotoPathSmall(cursor.getString(14));

                //See which category name the id stands for
                getRobotCategoryName(robot);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return robot;
    }

    /**
     * Returns a Cursor that points at a row in the database.
     *
     * @return cursor pointing at top ranked robots
     */
    public Cursor getTopListCursor() {
        SQLiteDatabase db = getReadableDatabase();

        long rows = DatabaseUtils.queryNumEntries(db, ROBOT_TABLE);
        //Log.d("Database", "Rows: " + rows);

        if (rows >= 10) {
            return db.query(ROBOT_TABLE, null, null,
                    null, null, null, "COL_ROBOT_AVERAGE DESC LIMIT 10");
        } else if (rows < 10) {
            //Log.d("Database", "Less than 10");
            return db.query(ROBOT_TABLE, null, null,
                    null, null, null, "COL_ROBOT_AVERAGE DESC LIMIT " + rows);
        } else {
            return null;
        }
    }

    /**
     * Returns a Cursor that points at individual robots within a certain robot category.
     *
     * @param categoryName Name of the category that one wants to get robots from.
     * @return a Cursor with robots within the given robot category.
     */
    public Cursor getRobotsByCategoryCursor(String categoryName) {
        SQLiteDatabase db = getReadableDatabase();

        //STEP 1. What id/row does the category name have?
        String selection = "COL_CATEGORY_NAME=?";
        String[] selectionArgs = new String[]{categoryName};

        Cursor cursor = db.query(CATEGORY_TABLE, null, selection,
                selectionArgs, null, null, null);

        int id = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }

        cursor.close();

        //STEP 2. Use the id from STEP 1 to return all robots within that category.
        String selectionTwo = "COL_ROBOT_CATEGORY=?";
        String[] selectionArgsTwo = new String[]{Integer.toString(id)};

        return db.query(ROBOT_TABLE, null, selectionTwo,
                selectionArgsTwo, null, null,
                "COL_ROBOT_AVERAGE DESC");
    }

    /**
     * Returns all robots within a certain category and ranks them according to their average score.
     *
     * @param categoryName Name of the robot category
     * @return List of robots within category
     */
    public List<Robot> getRobotsByCategory(String categoryName) {
        List<Robot> robotList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        //STEP 1. What id/row nr does the category name have?
        String selection = "COL_CATEGORY_NAME=?";
        String[] selectionArgs = new String[]{categoryName};

        Cursor cursor = db.query(CATEGORY_TABLE, null, selection,
                selectionArgs, null, null, null);

        int id = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }

        //STEP 2. Use id from STEP 1 to return all robots within said category.
        String selectionTwo = "COL_ROBOT_CATEGORY=?";
        String[] selectionArgsTwo = new String[]{Integer.toString(id)};

        cursor = db.query(ROBOT_TABLE, null, selectionTwo,
                selectionArgsTwo, null, null, "COL_ROBOT_AVERAGE DESC");

        boolean success = cursor.moveToFirst();

        if (success) {
            do {
                Robot robot = new Robot();

                robot.setId(cursor.getLong(0));
                robot.setName(cursor.getString(1));
                robot.setCategoryId(cursor.getInt(2));
                robot.setPrice(cursor.getFloat(3));
                robot.setTaste(cursor.getFloat(4));
                robot.setAverage(cursor.getFloat(5));
                robot.setComment(cursor.getString(6));
                robot.setPhotoPath(cursor.getString(7));
                robot.setLocation(cursor.getString(8));
                robot.setLat(cursor.getDouble(9));
                robot.setLng(cursor.getDouble(10));
                robot.setAddress(cursor.getString(11));
                robot.setTel(cursor.getString(12));
                robot.setWeb(cursor.getString(13));
                robot.setPhotoPathSmall(cursor.getString(14));

                robot.setCategoryName(getRobotCategoryName(robot));

                robotList.add(robot);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return robotList;
    }

    /**
     * Get all robots from the database.
     *
     * @return A List containing all robots
     */
    public List<Robot> getAllRobots() {
        List<Robot> robotList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(ROBOT_TABLE, null, null,
                null, null, null, null);

        boolean success = cursor.moveToFirst();

        if (success) {
            do {
                Robot robot = new Robot();

                robot.setId(cursor.getLong(0));
                robot.setName(cursor.getString(1));
                robot.setCategoryId(cursor.getInt(2));
                robot.setPrice(cursor.getFloat(3));
                robot.setTaste(cursor.getFloat(4));
                robot.setAverage(cursor.getFloat(5));
                robot.setComment(cursor.getString(6));
                robot.setPhotoPath(cursor.getString(7));
                robot.setLocation(cursor.getString(8));
                robot.setLat(cursor.getDouble(9));
                robot.setLng(cursor.getDouble(10));
                robot.setAddress(cursor.getString(11));
                robot.setTel(cursor.getString(12));
                robot.setWeb(cursor.getString(13));
                robot.setPhotoPathSmall(cursor.getString(14));

                //Add category name of robot
                robot.setCategoryName(getRobotCategoryName(robot));

                //Add robot to array
                robotList.add(robot);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return robotList;
    }

    /**
     * Returns a Cursor with category names in alphabetical order.
     *
     * @return a Cursor with category names in alphabetical order.
     */
    public Cursor getAllCategoriesCursor() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(CATEGORY_TABLE, null, null,
                null, null, null, "COL_CATEGORY_NAME COLLATE NOCASE");
    }

    /**
     * Returns all category names in alphabetical order.
     *
     * @return ArrayList with category names in alphabetical order
     */
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(CATEGORY_TABLE, null, null,
                null, null, null, null);

        boolean success = cursor.moveToFirst();

        if (success) {
            do {
                Category category = new Category();
                category.setId(cursor.getLong(0));
                category.setName(cursor.getString(1));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoryList;
    }

    /**
     * Removes a robot from the database.
     *
     * @param id id of the robot to be removed.
     * @return Returns true if a row was deleted. If nothing deleted, returns false.
     */
    public boolean removeRobot(long id) {
        SQLiteDatabase db = getWritableDatabase();

        String[] selectionArgs = new String[]{Long.toString(id)};
        int result = db.delete(ROBOT_TABLE, "_id=?", selectionArgs);

        if (result == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Edit the comment for a robot in the database.
     *
     * @param id      The id of the robot.
     * @param comment The new comment.
     * @return true if update successful, false if update failed.
     */
    public boolean editRobot(long id, String comment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("COL_ROBOT_COMMENT", comment);

        String selection = "_id=?";
        String[] selectionArgs = new String[]{Long.toString(id)};
        int result = db.update(ROBOT_TABLE, values, selection, selectionArgs);

        return result != 0;
    }

    /**
     * Edit the name of a robot in the database.
     *
     * @param id   Id of the robot to be edited.
     * @param name the new name of the robot.
     * @return true if update successful, false if update failed.
     */
    public boolean editRobotName(long id, String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("COL_ROBOT_NAME", name);

        String selection = "_id=?";
        String[] selectionArgs = new String[]{Long.toString(id)};
        int result = db.update(ROBOT_TABLE, values, selection, selectionArgs);

        return result != 0;
    }

    /**
     * Delete a robot category from the database.
     *
     * @param name Name of the category to delete.
     * @return true if update successful, false if update failed.
     */
    public boolean deleteCategory(String name) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = "COL_CATEGORY_NAME=?";
        String[] selectionArgs = new String[]{name.trim()};
        int result = db.delete(CATEGORY_TABLE, selection, selectionArgs);

        return result != 0;
    }
}
