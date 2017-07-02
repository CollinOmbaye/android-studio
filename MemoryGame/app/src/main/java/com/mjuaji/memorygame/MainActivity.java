package com.mjuaji.memorygame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends Activity implements View.OnClickListener{
    //for our hiscore(phase 4)
    SharedPreferences prefs;
    String dataName = "MyData";
    String intName = "MyInt";
    int defaultInt = 0;
    //both activities can see this score
    public static int hiScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //for our high score (phase 4)
        //initialize our two sharedPreferences objects
        prefs = getSharedPreferences(dataName, MODE_PRIVATE);
        //Either load our High Score or if not available our default of 0
        hiScore = prefs.getInt(intName, defaultInt);
        //make a reference to the Hiscore textview in our layout
        TextView textHiScore = (TextView)findViewById(R.id.textHiScore);
        //Display the hi score
        textHiScore.setText("Hi: " + hiScore);

        //make a button from the button in our layout
        Button button = (Button) findViewById(R.id.button);
        //make it listen for clicks
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //add an intent to take the player to the game activity when player clicks
        Intent i;
        i = new Intent(this, GameActivity.class);
        startActivity(i);
    }
}
