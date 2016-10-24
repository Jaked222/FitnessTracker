package com.example.jakeduncan.fitnesstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import static java.util.Arrays.asList;


/**
 * Created by jakeduncan on 10/24/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public DatabaseHelper(Context context) {
        super(context, "Retail", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(UserTable.CREATE_QUERY);
        seedProducts(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int prevVersion, int newVersion) {
        sqLiteDatabase.execSQL(UserTable.DROP_QUERY);
        sqLiteDatabase.execSQL(UserTable.CREATE_QUERY);
    }

    private void seedProducts(SQLiteDatabase sqLiteDatabase){
        List<User> users = asList(
                new User("jake", 5000),
                new User("sadie", 15000),
                new User("mike", 10000),
                new User("roger", 13000),
                new User("May", 4000));

        for (User user : users) {
            ContentValues values = new ContentValues();
            values.put(UserTable.NAME, user.getName());
            values.put(UserTable.DISTANCE, user.getDistanceWalked());

            sqLiteDatabase.insert(UserTable.TABLE_NAME, null, values);
        }
    }

    public Cursor getProductCursor() {
        return this.getWritableDatabase().rawQuery(UserTable.SElECT_QUERY, null);
    }
}