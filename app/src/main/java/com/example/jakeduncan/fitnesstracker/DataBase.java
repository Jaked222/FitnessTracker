package com.example.jakeduncan.fitnesstracker;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by jakeduncan on 10/24/16.
 */

public class DataBase extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SQLiteDatabase myDB = null;
        String TableName = "myTable";

        String Data = "";

  /* Create a Database. */
        try {
            myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);

   /* Create a Table in the Database. */
            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + TableName
                    + " (Field1 VARCHAR, Field2 INT(3));");

   /* Insert data to a Table*/
            myDB.execSQL("INSERT INTO "
                    + TableName
                    + " (Field1, Field2)"
                    + " VALUES ('Saranga', 22);");

   /*retrieve data from database */
            Cursor c = myDB.rawQuery("SELECT * FROM " + TableName, null);

            int Column1 = c.getColumnIndex("Field1");
            int Column2 = c.getColumnIndex("Field2");

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    String Name = c.getString(Column1);
                    int Age = c.getInt(Column2);
                    Data = Data + Name + "/" + Age + "\n";
                } while (c.moveToNext());
            }
            TextView tv = new TextView(this);
            tv.setText(Data);
            setContentView(tv);
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        } finally {
            if (myDB != null)
                myDB.close();
        }
    }
}

