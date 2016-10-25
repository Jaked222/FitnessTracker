package com.example.jakeduncan.fitnesstracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.jakeduncan.fitnesstracker.R.id.pass;
import static com.example.jakeduncan.fitnesstracker.UserTable.TABLE_NAME;


public class MainActivity extends AppCompatActivity {
    Button dev;
    Button signIn;
    EditText userField;
    EditText passField;

    private String userName;
    private String passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userField = (EditText) findViewById(R.id.userName);
        passField = (EditText) findViewById(pass);

        dev = (Button) findViewById(R.id.dev);
        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                userName = userField.getText().toString();
                passWord = passField.getText().toString();


                SignInOrRegister(userName, passWord);
            }
        });
        dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DatabaseShow.class);
                startActivity(intent);
            }
        });
    }

    public void SignInOrRegister(String user, String pass) {

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        if (verification(user)) {

          if (checkPassword(user, pass)){
              Toast.makeText(this, "User exists, password correct.. Logging in.", Toast.LENGTH_LONG).show();
              Intent intent = new Intent(this, UserActivity.class);
              intent.putExtra("namekey", user);
              startActivity(intent);
          }
          else{
              Toast.makeText(this, "Password incorrect for given user.", Toast.LENGTH_LONG).show();
          }


        } else {
            ContentValues values = new ContentValues();
            values.put(UserTable.NAME, user);
            values.put(UserTable.DISTANCE, 50);
            values.put(UserTable.PASSWORD, pass);

            databaseHelper.getWritableDatabase().insert(TABLE_NAME, null, values);
            databaseHelper.close();
            Toast.makeText(this, "Created and stored new user.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra("namekey", user);
            startActivity(intent);

        }


    }

    //this should be SQLInjection-proof.
    public boolean verification(String _username) throws SQLException {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase dataBase = databaseHelper.getReadableDatabase();
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT COUNT(*) FROM "
                    + TABLE_NAME + " WHERE " + UserTable.NAME + " = ?";
            c = dataBase.rawQuery(query, new String[]{_username});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            return count > 0;
        } finally {
            if (c != null) {
                c.close();
                dataBase.close();
            }
        }
    }

    public boolean checkPassword(String userName, String passWord) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase dataBase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = dataBase.rawQuery("SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.NAME + " = " + "\"" + userName + "\"", null);

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                return (passWord.equals(cursor.getString(cursor.getColumnIndex(UserTable.PASSWORD))));
            }

        } finally {

            if (cursor != null) {
                cursor.close();
                dataBase.close();
            }
        }
        Log.d("pass", "checkPassword: some unknown error ");
        return false;
    }
}
