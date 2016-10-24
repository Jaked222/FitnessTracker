package com.example.jakeduncan.fitnesstracker;

import android.provider.BaseColumns;

/**
 * Created by jakeduncan on 10/24/16.
 */


public class UserTable implements BaseColumns {
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String TABLE_NAME = "products";

    public static final String CREATE_QUERY = "create table " + TABLE_NAME + " (" +
            _ID + " INTEGER, " +
            NAME + " TEXT, " +
            PRICE + " INTEGER)";

    public static final String DROP_QUERY = "drop table " + TABLE_NAME;
    public static final String SElECT_QUERY = "select * from " + TABLE_NAME;
}