package com.mjuaji.retrosquash;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends Activity {
    //declare member variables
    Canvas canvas;
    SquashCourtView squashCourtView;
    //sound
    private SoundPool soundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;
    //get display details like the number of pixels
    Display display;
    Point size;
    int screenWidth;
    int screenHeight;
    //game objects
    int racketWidth;
    int racketHeight;
    Point racketPosition;
    Point ballPosition;
    int ballWidth;
    //for ball movement
    boolean ballIsMovingLeft;
    boolean ballIsMovingRight;
    boolean ballIsMovingUp;
    boolean ballIsMovingDown;
    //for racket movement
    boolean racketIsMovingLeft;
    boolean racketIsMovingRight;
    //stats
    long lastFrameTime;
    int fps;
    int score;
    int lives;
    //android states
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        squashCourtView = new SquashCourtView(this);
        setContentView(squashCourtView);

        //sound </code>
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try{
            //create objects of the 2 required classes
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;
            //create three fx in memory for use
            descriptor = assetManager.openFd("sample1.ogg");
            sample1 = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("sample2.ogg");
            sample2 = soundPool.load(descriptor,0);
            descriptor = assetManager.openFd("sample3.ogg");
            sample3 = soundPool.load(descriptor,0);
            descriptor = assetManager.openFd("sample4.ogg");
            sample4 = soundPool.load(descriptor,0);
        } catch (IOException e){
            //catch the exceptions here
        }
        //dont want just anyone changing the screen size
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        screenWidth = size.x;
        //game objects
        racketPosition = new Point();
        racketPosition.x = screenWidth/2;
        racketPosition.y = screenHeight/2;
        racketWidth = screenWidth/8;
        racketHeight = 10;
        ballWidth = screenWidth/35;
        ballPosition.x = screenWidth/2;
        ballPosition.y = 1 + ballWidth;
        lives = 3;
    }
    class SquashCourtView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSquash;
        Paint paint;

        public SquashCourtView(Context context){
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            ballIsMovingDown = true;
            //send the ball in a random direction
            Random randomNumber = new Random();
            int ballDirection = randomNumber.nextInt(3);
            switch (ballDirection){
                case 0:
                    ballIsMovingLeft = true;
                    ballIsMovingRight = false;
                    break;
                case 1:
                    ballIsMovingRight = true;
                    ballIsMovingLeft = false;
                    break;
                case 3:
                    ballIsMovingLeft = false;
                    ballIsMovingRight = false;
                    break;
            }
        }
        @Override
        public void run(){
            while (playingSquash){
                updateCourt();
                drawCourt();
                controlFPS();
            }
        }
        //updatecourt
        public void updateCourt(){
            if(racketIsMovingRight){
                racketPosition.x = racketPosition.x + 10;
            }
            if(racketIsMovingLeft){
                racketPosition.x = racketPosition.x - 10;
            }
            //detect collision -> hit right side of screen
            if(ballPosition.x + ballWidth > screenWidth){
                ballIsMovingLeft = true;
                ballIsMovingRight = false;
                soundPool.play(sample1,1,1,0,0,1);
            }
            //->hit left side of screen
            if(ballPosition.x < 0){
                ballIsMovingLeft = false;
                ballIsMovingRight = true;
                soundPool.play(sample1,1,1,0,0,1);
            }
            //edge of ball has hit bottom of screen
            if(ballPosition.y > screenHeight - ballWidth){
                lives = lives - 1;
                if(lives == 0){
                    lives = 3;
                    score = 0;
                    soundPool.play(sample4, 1, 1, 0, 0, 1);
                }
                ballPosition.y = 1 + ballWidth;
                //back to the top of the screen - what horizontal direction should we use - for the next falling ball
                Random randomNumber = new Random();
                int startX = randomNumber.nextInt(screenWidth - ballWidth) + 1;
                ballPosition.x = startX + ballWidth;
                //direction of the ball
                int ballDirection = randomNumber.nextInt(3);
                switch (ballDirection){
                    case 0:
                        ballIsMovingLeft = true;
                        ballIsMovingRight = false;
                        break;
                    case 1:
                        ballIsMovingRight = true;
                        ballIsMovingLeft = false;
                        break;
                    case 2:
                        ballIsMovingLeft = false;
                        ballIsMovingRight = true;
                        break;
                }
            }
            //we hit the top of the screen
            if(ballPosition.y <= 0){
                ballIsMovingDown = true;
                ballIsMovingUp = false;
                ballPosition.y = 1;
                soundPool.play(sample2, 1, 1, 0, 0, 1);
            }
            //depending on the two directions we are moving in, adjust x & y positions
            if(ballIsMovingDown){
                ballPosition.y += 6;
            }
            if(ballIsMovingUp){
                ballPosition.y -=10;
            }
            if(ballIsMovingLeft){
                ballPosition.x -=12;
            }
            if(ballIsMovingRight){
                ballPosition.x +=12;
            }
            //has the ball hit the racket?
            if(ballPosition.y + ballWidth >= (racketPosition.y - racketHeight/2)){
                int halfRacket = racketWidth/2;
                if(ballPosition.x + ballWidth > (racketPosition.x - halfRacket)&&ballPosition.x-ballWidth<(racketPosition.x+halfRacket)){
                    //sound, ++score, send ball up
                    soundPool.play(sample3,1,1,0,0,1);
                    score++;
                    ballIsMovingUp = true;
                    ballIsMovingDown = false;
                    //rebound the ball to left or right
                    if(ballPosition.x > racketPosition.x){
                        ballIsMovingRight = true;
                        ballIsMovingLeft = false;
                    }else{
                        ballIsMovingRight = false;
                        ballIsMovingLeft = true;
                    }
                }
            }
        }
        //drawcourt
        public void drawCourt(){
            if(ourHolder.getSurface().isValid()){
                canvas = ourHolder.lockCanvas();
                //paint
                Paint paint = new Paint();
                //background
                canvas.drawColor(Color.BLACK);
                paint.setColor(Color.argb(255,255,255,255));
                paint.setTextSize(45);
                canvas.drawText("Score:"+score+" Lives:"+lives+" fps:"+ fps,20,40,paint);
                //draw the squash racket
                canvas.drawRect(
                        racketPosition.x - (racketWidth/2),
                        racketPosition.y - (racketHeight/2),
                        racketPosition.x + (racketWidth/2),
                        racketPosition.y + racketHeight, paint);
                //draw the ball
                canvas.drawRect(
                        ballPosition.x,
                        ballPosition.y,
                        ballPosition.x+ballWidth,
                        ballPosition.y+ballWidth, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
        //control fps
        public void controlFPS(){
            long timeThisFrame = (System.currentTimeMillis()-lastFrameTime);
            long timeToSleep = 15 - timeThisFrame;
            if(timeThisFrame > 0){
                fps = (int)(1000/timeThisFrame);
            }
            if(timeThisFrame>0){
                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {

                }
            }
            lastFrameTime = System.currentTimeMillis();
        }
        //pause and resume
        public void pause(){
            playingSquash = false;
            try{
                ourThread.join();
            } catch (InterruptedException e){

            }
        }
        public void resume(){
            playingSquash = true;
            ourThread = new Thread(this);
            ourThread.start();
        }
        //surface touch
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent){
            switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    if(motionEvent.getX() >= screenWidth/2){
                        racketIsMovingRight = true;
                        racketIsMovingLeft = false;
                    }else{
                        racketIsMovingLeft = true;
                        racketIsMovingRight = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    racketIsMovingRight = false;
                    racketIsMovingLeft = false;
                    break;
            }
            return true;
        }

    }
    @Override
    protected void onStop(){
        super.onStop();
        while (true) {
            squashCourtView.pause();
            break;
        }
        finish();
    }
    @Override
    protected void onPause(){
        super.onPause();
        squashCourtView.pause();
    }
    @Override
    protected void onResume(){
        super.onResume();
        squashCourtView.resume();
    }
    //handle the back button on the device
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            squashCourtView.pause();
            finish();
            return true;
        }
        return false;
    }
}
