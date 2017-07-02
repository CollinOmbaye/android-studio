package com.mjuaji.persistence;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    
    String dataName = "MyData";
    String stringName = "MyString";
    String defaultString = ":-(";
    String currentString = ""; //empty
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize 2 SharedPreferences objects
        prefs = getSharedPreferences(dataName, MODE_PRIVATE);
        editor = prefs.edit();

        //either load our string or our default string
        currentString = prefs.getString(stringName, defaultString);

        //make a button
        button1 = (Button) findViewById(R.id.button);

        //make button listen for clicks
        button1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //get a random number (0-9)
        Random randInt = new Random();
        int ourRandom = randInt.nextInt(10);

        //add random number to end of current string
        currentString = currentString + ourRandom;

        //save currentString to a file in case user suddenly quits or gets a phone call
        editor.putString(stringName,currentString);
        editor.commit();

        //update the button text
        button1.setText(currentString);
    }
}
