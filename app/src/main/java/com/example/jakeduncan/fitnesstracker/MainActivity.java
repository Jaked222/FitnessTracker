package com.example.jakeduncan.fitnesstracker;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import static android.R.attr.value;
import static com.example.jakeduncan.fitnesstracker.R.id.pass;


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


             //  sign(userName, passWord);
              SignInOrRegister(userName, 50);
            }
        });
    }
    public void sign(String user, String pass){
        Intent myIntent = new Intent(this, UserActivity.class);
        myIntent.putExtra("key", value); //Optional parameters
        startActivity(myIntent);
    }
    public void SignInOrRegister(String user, int pass){
        //check db for username. if doesnt exist, create it with given pass. Open fitness page from 0
        //if exists, open user info with tracked fitness info.
        setContentView(R.layout.database_list);
        ListView productList = (ListView) findViewById(R.id.products_list);



        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        ContentValues values = new ContentValues();
        values.put(UserTable.NAME, user);
        values.put(UserTable.DISTANCE, pass);

        databaseHelper.getWritableDatabase().insert(UserTable.TABLE_NAME, null, values);


        String[] from = new String[] {UserTable.NAME, UserTable.DISTANCE};
        int[] to = new int[] {R.id.product_name, R.id.product_price};

        productList.setAdapter(new SimpleCursorAdapter(this, R.layout.product_row, databaseHelper.getProductCursor(), from, to));
    }
}
