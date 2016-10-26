package com.example.jakeduncan.fitnesstracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by jakeduncan on 10/24/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "Retail", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(UserTable.CREATE_QUERY);
        //    seedProducts(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int prevVersion, int newVersion) {
        sqLiteDatabase.execSQL(UserTable.DROP_QUERY);
        sqLiteDatabase.execSQL(UserTable.CREATE_QUERY);
    }


    public Cursor getUserCursor() {
        return this.getWritableDatabase().rawQuery(UserTable.SElECT_QUERY, null);
    }
}
