package com.example.jakeduncan.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TextView userView = (TextView) findViewById(R.id.userView);
        Intent intent = getIntent();
        String value = intent.getStringExtra("namekey");

        userView.setText(value);

    }
}
