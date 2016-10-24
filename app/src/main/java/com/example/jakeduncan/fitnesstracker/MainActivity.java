package com.example.jakeduncan.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

              SignInOrRegister(userName, passWord);
            }
        });
    }
    public void SignInOrRegister(String user, String pass){
        //check db for username. if doesnt exist, create it with given pass. Open fitness page from 0
        //if exists, open user info with tracked fitness info.
        Intent intent = new Intent(this, DataBase.class);
        startActivity(intent);
    }
}
