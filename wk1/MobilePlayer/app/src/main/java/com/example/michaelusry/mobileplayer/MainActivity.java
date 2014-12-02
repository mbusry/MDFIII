/*
    Michael Usry
    MDF III week 1
    December 1, 2014


 */


package com.example.michaelusry.mobileplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    ImageView albumCover;
    TextView songTitleTV;
    Boolean playPressed;
    Boolean pressedBackFirst = false;
    MyService mService;
    MyService.BoundServiceBinder binder;
    Boolean mBound;

    public static final String EXTRA_RECEIVER = "MainActivity.EXTRA_RECEIVER";
    public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";
    public static final int RESULT_DATA_RETURNED = 0x0101010;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        play = (Button) findViewById(R.id.btn_play);
        pause = (Button) findViewById(R.id.btn_pause);
        stop = (Button) findViewById(R.id.btn_stop);
        back = (Button) findViewById(R.id.btn_back);
        forward = (Button) findViewById(R.id.btn_forward);

        songTitleTV = (TextView) findViewById(R.id.songtitletextview);
        songTitleTV.setText("");

        playPressed = false;


        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        forward.setOnClickListener(this);
        back.setOnClickListener(this);
        pause.setOnClickListener(this);

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

                break;
            case R.id.btn_forward:
                Log.i(TAG,"Forward Button");
//                songTitleTV.setText("Forward");
                mService.forward();

                break;
            case R.id.btn_back:
                Log.i(TAG,"Back Button");
//                songTitleTV.setText("Back");
                mService.back();

                break;

        }
        songTitle();

    }

//    public void onConfigurationChanged(Configuration newConfig){
//        super.onConfigurationChanged(newConfig);
//    }



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

    public void songTitle(){
        songTitleTV.setText(MyService.songTitle);
        Log.i(TAG,"MainActivity songTitle");
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
