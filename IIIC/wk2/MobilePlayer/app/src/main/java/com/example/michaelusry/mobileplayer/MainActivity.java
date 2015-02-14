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
import android.content.pm.ActivityInfo;
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
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
/*



 */

public class MainActivity extends Activity implements View.OnClickListener, ServiceConnection{

        static String TAG = "MAIN ACTIVITY";

        Button play,stop,forward,back,pause;
        TextView songTitleTV;
        Boolean playPressed;
        Boolean running = true;
        MyService mService;
        Boolean mBound;
        CheckBox mRandom;
        ProgressBar progressBar;
        public static int progress = 0;
        public int seconds = 0;

        ImageView albumCoverIV;

        public static final String EXTRA_RECEIVER = "MainActivity.EXTRA_RECEIVER";
        public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";
        public static final int RESULT_DATA_RETURNED = 0x0101010;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = (Button) findViewById(R.id.btn_play);
            pause = (Button) findViewById(R.id.btn_pause);
            stop = (Button) findViewById(R.id.btn_stop);
            back = (Button) findViewById(R.id.btn_back);
            forward = (Button) findViewById(R.id.btn_forward);
            mRandom = (CheckBox) findViewById(R.id.checkBox);
            progressBar = (SeekBar) findViewById(R.id.progressBar);
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
            mRandom.setOnClickListener(this);

            Intent i = new Intent (this, MyService.class);
            i.putExtra(EXTRA_RECEIVER, new DataReceiver());
            this.startService(i);


        }

        private final Handler handle = new Handler();


        //UI Controls
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_play:
                    running = true;
                    Log.i(TAG, "Play Button");
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
                    running = false;
                    progressBar.setVisibility(View.INVISIBLE);


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
                case R.id.checkBox:
                    Log.i(TAG,"Loop pressed");
                    if(mRandom.isChecked()) {
                        mService.random(true);
                    }else{
                        mService.random(false);
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
        final int totalDuration = MyService.songLength;
//        Log.i(TAG,"totalDuration: " + totalDuration);


//        seconds = (totalDuration / 1000)/30;
//        Log.i(TAG,"seconds: " + seconds);

        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
//                Log.i(TAG,"PRE-progressbar: seconds: " + seconds);

                while (progress < MyService.songLength && running) {

//                        Log.i(TAG, "progressbar: progress: " + progress);
//                        Log.i(TAG, "progressbar: seconds: " + totalDuration);


                    handle.post(new Runnable() {

                        public void run() {
//                                Log.i(TAG, "run: progress: " + progress);
//                                Log.i(TAG, "run: seconds: " + totalDuration);
//                                Log.i(TAG, "run: MyService.songLength: " + MyService.songLength);


                            progressBar.setMax(MyService.songLength);
                            progressBar.setProgress(progress);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress += 1;
//                        Log.i(TAG, "progressbar: progress: " + progress);


                }
            }
        }).start();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MyService.BoundServiceBinder binder = (MyService.BoundServiceBinder)service;
        mService = binder.getService();
        mBound = true;
        mService.sendToForeground();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    public class DataReceiver extends ResultReceiver {
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
            running = false;
            Log.i(TAG,"onStop - unbindService");

        }



    }

}