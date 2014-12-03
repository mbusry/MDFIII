/*
    Michael Usry
    MDF III week 1
    December 1, 2014


 */

package com.example.michaelusry.mobileplayer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;

/**
 * Created by michael on 11/26/14.
 */
public class MyService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "MyService";
    private static final int NOTIFY_ID = 1;
    Boolean playPressed = false;
    Boolean pressedBackFirst = false;
    MediaPlayer mp;
    public static String songTitle;
    public static int currentTrack = 0;
    int mAudioPosition = 0;


    String[] songFileName = {"endofthebeginning", "helovesme", "thatsmyking",
            "thingsofthisworld"};
    static String[] songTitleArray = {"End Of The Beginning", "He Loves Me",
            "That's My King", "Things Of This World"};

    BoundServiceBinder mBinder;

    @Override
    public void onCompletion(MediaPlayer mp) {
        mAudioPosition = 0;
        Log.i(TAG, "currentTrack = " + currentTrack);

        Uri nextTrack = Uri.parse("android.resource://" + getPackageName()
                + "/raw/" + songFileName[currentTrack]);

        Log.i(TAG, "nextTrack = " + nextTrack);


        try {
            mp.reset();
            mp.setDataSource(MyService.this, nextTrack);
            mp.prepare();
            mp.start();
            Log.i(TAG, "Song name: " + songFileName[currentTrack]);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        currentTrack = (currentTrack + 1) % songFileName.length;


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }


    public class BoundServiceBinder extends Binder {
        public MyService getService() {

            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new BoundServiceBinder();
        Log.i(TAG,"Service Created");
        mp = new MediaPlayer();

        initMP();

    }

    //Service binder
    @Override
    public IBinder onBind(Intent intent) {
        return new BoundServiceBinder();
    }

    //Start service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"Service Started");
        return super.onStartCommand(intent, flags, startId);
    }

    //Send SongTitle to MainActivity
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra(MainActivity.EXTRA_RECEIVER)) {
            ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra(MainActivity
                    .EXTRA_RECEIVER);
            Bundle result = new Bundle();
            result.putString(MainActivity.DATA_RETURNED, "SongTitle");
            receiver.send(MainActivity.RESULT_DATA_RETURNED,result);
        }
    }

    // initialize the MediaPlayer
    public void initMP() {
//        mp.setWakeMode(getApplicationContext(),
//                PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);

    }

// UI methods for buttons
// song play
    protected void play() throws IOException {
        Log.i(TAG, "play()");
//        mp.reset();


            Uri file = Uri.parse("android.resource://" + getPackageName()
                    + "/raw/" + songFileName[currentTrack]);
            Log.i(TAG, "Uri file = " + file);
            songTitle = songTitleArray[currentTrack];
            Log.i(TAG, "songTitle: " + songTitle);

            try {
                mp.setDataSource(this, file);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

//            mp.prepareAsync();
        mp.start();

        }

    protected void stop() {
        Log.i(TAG, "stop()");
        mAudioPosition = 0;
        mp.stop();
        stopForeground(true);


    }

    protected void forward() {
        Log.i(TAG, "forward()");
        mp.pause();

        if (currentTrack >= 3) {
            currentTrack = 0;
        }else{
            currentTrack++;
        }
        mAudioPosition=0;
        try {
            mp.reset();
            mp.setDataSource(
                    this,
                    Uri.parse("android.resource://" + getPackageName()
                            + "/raw/" + songFileName[currentTrack]));
            mp.prepare();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mp.start();
        songTitle = songTitleArray[currentTrack];



    }

    protected void back() {
        Log.i(TAG, "back()");
        if (!pressedBackFirst) {

            Log.i(TAG,"!pressedBackFirst");

            currentTrack--;
            pressedBackFirst = true;

        }

        mp.pause();

        if (currentTrack <0 ) {
            currentTrack = 3;
        }else{
            currentTrack--;
            if (currentTrack <0){
                currentTrack = 3;
            }
        }
        mAudioPosition=0;
        try {
            mp.reset();
            mp.setDataSource(
                    this,
                    Uri.parse("android.resource://" + getPackageName()
                            + "/raw/" + songFileName[currentTrack]));
            mp.prepare();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mp.start();
        songTitle = songTitleArray[currentTrack];



    }

    protected void pause() {
        Log.i(TAG, "pause()");
        Log.i(TAG,"audioposition: " + mAudioPosition);
        if (mp != null && mp.isPlaying()) {
            mAudioPosition = mp.getCurrentPosition();
            Log.i(TAG,"audioposition: " + mAudioPosition);

            mp.pause();
        }

    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "Service Stopped");
        super.onDestroy();
        if (mp != null) {
            mp.release();
            stopForeground(true);
            currentTrack = 0;
            mAudioPosition = 0;
        }
    }

   //unbind service
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service Unbound");
        mp.stop();
        mp.release();

        return false;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mp){
//        songTitle = songTitleArray[this.currentTrack];
        Log.i(TAG,"onPrepared: " + songTitle);
        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(i.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pi)
                .setSmallIcon(R.drawable.speaker)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);

        Notification notif = builder.build();

        startForeground(NOTIFY_ID, notif);



    }



}

