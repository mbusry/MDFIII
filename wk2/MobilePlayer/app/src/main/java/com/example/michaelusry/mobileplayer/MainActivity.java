/*
    Michael Usry
    MDF III week 2
    December 6, 2014


 */


package com.example.michaelusry.mobileplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
/*

Instructions
For your assignment, you will be building an Android application that
demonstrates your understanding of the service and notification fundamentals.
Minimally, your app must do the following things:

From a service, play multiple audio files in sequence that are loaded via
resource identifier.
Service and media playback supports starting, stopping, and pausing playback.
Application shows a notification of the current song that is playing.
Clicking the app's notification will open the application if it is closed.
While open, the application must display UI controls for play and pause as well
as the name of the current audio track that is playing.


In addition to the above minimum requirements, the following functionality is
required to demonstrate mastery of the week one topics:

Audio files are loaded and played using a resource URI instead of the resource identifier.
Application utilizes a bound service to control audio playback.
Notification is used to run the underlying service in the foreground.
While open, the application UI also has controls to skip through audio tracks
both forwards and backwards.


 */

public class MainActivity extends Activity implements View.OnClickListener, ServiceConnection{

    static String TAG = "MAIN ACTIVITY";

    Button play,stop,forward,back,pause;
    TextView songTitleTV;
    Boolean playPressed;
    MyService mService;
    Boolean mBound;
    CheckBox mLoop;
    ProgressBar progressBar;
    int songLength = 0;
    int progress = 0;
    ImageView albumCoverIV;

    public static final String EXTRA_RECEIVER = "MainActivity.EXTRA_RECEIVER";
    public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";
    public static final int RESULT_DATA_RETURNED = 0x0101010;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration c = getResources().getConfiguration();
        if(c.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_main_land_fragment);
        }else{
            setContentView(R.layout.activity_main_fragment);
        }


        play = (Button) findViewById(R.id.btn_play);
        pause = (Button) findViewById(R.id.btn_pause);
        stop = (Button) findViewById(R.id.btn_stop);
        back = (Button) findViewById(R.id.btn_back);
        forward = (Button) findViewById(R.id.btn_forward);
        mLoop = (CheckBox) findViewById(R.id.checkBox);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        albumCoverIV = (ImageView) findViewById(R.id.albumcover);

        songTitleTV = (TextView) findViewById(R.id.songtitletextview);
        songTitleTV.setText("");

        progressBarVisible(Boolean.FALSE);
        playPressed = false;


        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        forward.setOnClickListener(this);
        back.setOnClickListener(this);
        pause.setOnClickListener(this);
        mLoop.setOnClickListener(this);

        Intent i = new Intent (this, MyService.class);
        i.putExtra(EXTRA_RECEIVER, new DataReceiver());
        startService(i);


    }

    private final Handler handle = new Handler();


    //UI Controls
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                Log.i(TAG,"Play Button");
//                    songTitleTV.setText("Play");
                    playPressed = true;
                    play.setText("Play");
                try {
                    mService.play();

                    progressBar.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_pause:
                Log.i(TAG,"Pause Button");
//                songTitleTV.setText("Pause");
                mService.pause();


                break;

            case R.id.btn_stop:
                Log.i(TAG,"Stop Button");
//                songTitleTV.setText("Stop");
                mService.stop();
                progress = 0;
                progressBar.setVisibility(View.INVISIBLE);


                break;
            case R.id.btn_forward:
                Log.i(TAG,"Forward Button");
//                songTitleTV.setText("Forward");
                progress = 0;
                mService.forward();

                break;
            case R.id.btn_back:
                Log.i(TAG,"Back Button");
//                songTitleTV.setText("Back");
                mService.back();

                break;

            case R.id.checkBox:
                Log.i(TAG,"Loop pressed");
                if(mLoop.isChecked()) {
                    mService.loop(true);
                }else{
                    mService.loop(false);
                }
                break;

        }
        songTitle();
        cover();
        progressbar();


    }

    public void progressBarVisible(Boolean v){

        if(v == Boolean.TRUE) {
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    public void progressbar(){

        // get songlength
        songLength = MyService.songLength;
        Log.i(TAG,"progressbar: songlength: " + songLength);
        final int sl = (songLength / 100000)/100;
        Log.i(TAG,"progressbar: sl: " + sl);

        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progress < sl) {
                    Log.i(TAG,"progressbar: progress: " + progress);

                    handle.post(new Runnable() {
                        public void run() {
                            progressBar.setMax(sl);
                            progressBar.setProgress(progress);
//                            textView.setText(progressStatus+"/"+progressBar.getMax());
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress += 1;

                }
            }
        }).start();
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MyService.BoundServiceBinder binder = (MyService.BoundServiceBinder)service;
        mService = binder.getService();
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
        mBound = false;
    }

    public class DataReceiver extends ResultReceiver{
        public DataReceiver(){
            super(handle);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData){
            if(resultData != null && resultData.containsKey(DATA_RETURNED)){
                songTitleTV.setText(resultData.getString(DATA_RETURNED,"SongTitle"));
            }
        }
    }

    //Receiver for Service binder
    @Override
    protected void onStart(){
        super.onStart();
        Log.i(TAG,"MainActivity, onStart");
        Intent intent = new Intent(this,MyService.class);
        intent.putExtra(EXTRA_RECEIVER, new DataReceiver());
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        startService(intent);

    }

    //set song title, retrieved from the service
    public void songTitle(){
        songTitleTV.setText(MyService.songTitle);
        Log.i(TAG,"MainActivity songTitle");
    }

    public void cover(){
        albumCoverIV.setImageBitmap(MyService.albumart);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mBound) {
            unbindService(this);
            Log.i(TAG,"onStop - unbindService");

        }
    }



}
