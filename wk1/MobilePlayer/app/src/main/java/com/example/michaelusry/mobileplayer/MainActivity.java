/*
    Michael Usry
    MDF III week 1
    December 1, 2014


 */


package com.example.michaelusry.mobileplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {

    static String TAG = "MAIN ACTIVITY";

    Button play, stop, forward, back, pause;
    TextView songTitleTV;
    Boolean playPressed;
    MyService mService;
    Boolean mBound;
    Intent resultIntent;
    PendingIntent resultPendingIntent;
    MediaPlayer mp;


    public static final String EXTRA_RECEIVER = "MainActivity.EXTRA_RECEIVER";
    public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";
    public static final int RESULT_DATA_RETURNED = 0x0101010;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "MainActivity - onCreate");


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

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.speaker)
                        .setContentTitle("MyPlayer")
                        .setContentText("Touch me and let's go!");


        Intent i = new Intent (this, MyService.class);
//        getActivity().bindService(i,mConnection, Context.BIND_AUTO_CREATE);

        mp = new MediaPlayer();

        i.putExtra(EXTRA_RECEIVER, new DataReceiver());
        startService(i);
//
//        bindService(i, this, Context.BIND_AUTO_CREATE);
//        startService(i);


        resultIntent = new Intent(this, MainActivity.class);
        resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        001,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);


    }

    private final Handler handle = new Handler();


    //UI Controls
    @Override
    public void onClick(View v) {
//        Intent intent = new Intent(getActivity(), MyService.class);

        switch (v.getId()) {
            case R.id.btn_play:
                Log.i(TAG, "Play Button");
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
                Log.i(TAG, "Pause Button");
//                songTitleTV.setText("Pause");
                mService.pause();


                break;

            case R.id.btn_stop:
                Log.i(TAG, "Stop Button");
//                songTitleTV.setText("Stop");
                mService.stop();

                break;
            case R.id.btn_forward:
                Log.i(TAG, "Forward Button");
//                songTitleTV.setText("Forward");
                mService.forward();

                break;
            case R.id.btn_back:
                Log.i(TAG, "Back Button");
//                songTitleTV.setText("Back");
                mService.back();

                break;

        }
        songTitle();

    }

    private ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "MainActivity - onServiceConnected");

            MyService.BoundServiceBinder binder = (MyService.BoundServiceBinder) service;
            mService = binder.getService();
            
            mBound = true;

        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };



    public class DataReceiver extends ResultReceiver {
        public DataReceiver() {
            super(handle);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData != null && resultData.containsKey(DATA_RETURNED)) {
                songTitleTV.setText(resultData.getString(DATA_RETURNED, "SongTitle"));
            }
        }
    }

    //Receiver for Service binder
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "MainActivity, onStart");


//        Intent intent = new Intent(this,MyService.class);
//        intent.putExtra(EXTRA_RECEIVER, new DataReceiver());
//        bindService(intent, this, Context.BIND_AUTO_CREATE);
//        startService(intent);


    }

    //set song title, retrieved from the service
    public void songTitle() {
        songTitleTV.setText(MyService.songTitle);
        Log.i(TAG, "MainActivity songTitle");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService((ServiceConnection) this);
            Log.i(TAG, "onStop - unbindService");

        }
    }

}


