package com.example.jakeduncan.fitnesstracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LeaderboardShow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_list);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        String[] from = new String[] {UserTable.NAME, UserTable.DISTANCE};
        int[] to = new int[] {R.id.user_name, R.id.user_distance};

        ListView productList = (ListView) findViewById(R.id.products_list);
        productList.setAdapter(new SimpleCursorAdapter(this, R.layout.user_row, databaseHelper.getUserCursor(), from, to));
        databaseHelper.close();
    }
}
