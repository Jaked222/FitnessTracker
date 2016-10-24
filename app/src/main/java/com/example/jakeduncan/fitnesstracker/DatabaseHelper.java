package com.example.jakeduncan.fitnesstracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by jakeduncan on 10/24/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String dbName = "Database";
    private static final String USERNAME = "name";
    private static final String PASSWORD = "pass";
    private static final int WALKED = 1000;

    private static final String dbCreateTable =  "CREATE TABLE " + dbName + " (" +
            USERNAME + " TEXT, " +
            PASSWORD + " TEXT, " +
            WALKED   + " INTEGER);";


    public DatabaseHelper(Context context) {
        super(context, dbName, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL(dbCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

