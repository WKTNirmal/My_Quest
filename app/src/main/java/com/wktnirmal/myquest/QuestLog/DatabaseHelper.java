package com.wktnirmal.myquest.QuestLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyQuest.db";
    public static final String TABLE_NAME = "QuestLog";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "QuestTitle";
    public static final String COL_3 = "QuestDescription";
    public static final String COL_4 = "StartLat";
    public static final String COL_5 = "StartLng";
    public static final String COL_6 = "EndLat";
    public static final String COL_7 = "EndLng";
    public static final String COL_8 = "Distance";
    public static final String COL_9 = "QuestStatus";
    public static final String COL_10 = "Repetitive";


    //database constructor (after context, those are extra by the template)
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, QuestTitle TEXT, QuestDescription TEXT, StartLat DOUBLE, StartLng DOUBLE, EndLat DOUBLE, EndLng DOUBLE, Distance INTEGER, QuestStatus TEXT, Repetitive TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    //insert data to the database
    public boolean insertData(String title, String description, double startLat, double startLng, double endLat, double endLng, int distance, String status, String repetitive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, title);
        values.put(COL_3, description);
        values.put(COL_4, startLat);
        values.put(COL_5, startLng);
        values.put(COL_6, endLat);
        values.put(COL_7, endLng);
        values.put(COL_8, distance);
        values.put(COL_9, status);
        values.put(COL_10, repetitive);


        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    //get map locations
    public Cursor getQuestLocations(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor locations = db.rawQuery("SELECT QuestTitle, EndLat, EndLng FROM " + TABLE_NAME, null);
        return locations;
    }
}
