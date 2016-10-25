package com.example.jakeduncan.fitnesstracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class DatabaseShow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_list);

        //displays all the database fields in a listview for testing purposes.
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        String[] from = new String[] {UserTable.NAME, UserTable.DISTANCE, UserTable.PASSWORD};
        int[] to = new int[] {R.id.user_name, R.id.user_distance, R.id.user_pass};

        ListView productList = (ListView) findViewById(R.id.products_list);
        productList.setAdapter(new SimpleCursorAdapter(this, R.layout.user_row, databaseHelper.getProductCursor(), from, to));
        databaseHelper.close();
    }
}
