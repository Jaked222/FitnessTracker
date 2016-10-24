package com.example.jakeduncan.fitnesstracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import static com.example.jakeduncan.fitnesstracker.R.id.pass;
import static com.example.jakeduncan.fitnesstracker.UserTable.TABLE_NAME;


public class MainActivity extends AppCompatActivity {

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
        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              userName =  userField.getText().toString();
              passWord = passField.getText().toString();


              SignInOrRegister(userName, passWord);
            }
        });
    }
    public void SignInOrRegister(String user, String pass){
        //check db for username. if doesnt exist, create it with given pass. Open fitness page from 0
        //if exists, open user info with tracked fitness info.
        setContentView(R.layout.database_list);
        ListView productList = (ListView) findViewById(R.id.products_list);


        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        ContentValues values = new ContentValues();
        values.put(UserTable.NAME, user);
        values.put(UserTable.DISTANCE, 50);
        values.put(UserTable.PASSWORD, pass);

        databaseHelper.getWritableDatabase().insert(TABLE_NAME, null, values);

       if( verification("jakedddd")){

           Log.d("yes", "true");
       }else{
           Log.d("no", "false ");
       }


        String[] from = new String[] {UserTable.NAME, UserTable.DISTANCE, UserTable.PASSWORD};
        int[] to = new int[] {R.id.user_name, R.id.user_distance, R.id.user_pass};

        productList.setAdapter(new SimpleCursorAdapter(this, R.layout.product_row, databaseHelper.getProductCursor(), from, to));
    }
    public boolean verification(String _username) throws SQLException {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase dataBase = databaseHelper.getReadableDatabase();
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT COUNT(*) FROM "
                    + TABLE_NAME + " WHERE " + UserTable.NAME + " = ?";
            c = dataBase.rawQuery(query, new String[] {_username});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            return count > 0;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }
}
