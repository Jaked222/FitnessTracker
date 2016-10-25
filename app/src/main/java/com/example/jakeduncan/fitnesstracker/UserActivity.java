package com.example.jakeduncan.fitnesstracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TextView userView = (TextView) findViewById(R.id.userView);
        TextView distanceView = (TextView) findViewById(R.id.distanceView);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("namekey");

        userView.setText(userName);
        distanceView.setText("" + getDistance(userName));

    }
    public int getDistance(String userName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase dataBase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        int distance = 0;
        try{

            cursor = dataBase.rawQuery("SELECT * FROM "+UserTable.TABLE_NAME+ " WHERE "+ UserTable.NAME + " = " + "\""+ userName + "\"", null);

            if(cursor.getCount() > 0) {

                cursor.moveToFirst();
                distance = cursor.getInt(cursor.getColumnIndex(UserTable.DISTANCE));
            }

            return distance;
        }finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
