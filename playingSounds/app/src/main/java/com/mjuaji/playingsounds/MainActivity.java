package com.mjuaji.playingsounds;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SoundPool soundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load sound into memory
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try{
            //create objects of the two required classes
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            //create 3 fx in memory, ready for use
            descriptor = assetManager.openFd("sample1.ogg");
            sample1 = soundPool.load(descriptor,0);

            descriptor = assetManager.openFd("sample2.ogg");
            sample2 = soundPool.load(descriptor,0);

            descriptor = assetManager.openFd("sample3.ogg");
            sample3 = soundPool.load(descriptor,0);
        }catch(IOException e){
            //catch exceptions
        }

        //grab a reference to buttons in the UI and listen to clicks
        Button button1 = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);

        //Make each of them listen for clicks
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //play music
        switch (v.getId()){
            case R.id.button:
                soundPool.play(sample1,1,1,0,0,1);
                break;

            case R.id.button2:
                soundPool.play(sample2,1,1,0,0,1);
                break;

            case R.id.button3:
                soundPool.play(sample3,1,1,0,0,1);
                break;
        }
    }
}
