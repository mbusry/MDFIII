/*
    Michael Usry
    MDF III week 1
    December 1, 2014


 */

package com.example.michaelusry.mobileplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MyService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public MyService() {
    }

    // define Variables

    private static final String TAG = "MyService";
    private static final int FOREGROUND_NOTIFICATION = 0x01001;
    private static final int NOTIFY_ID = 1;
    private static final String ACTION_PLAY = "com.example.michaelusry.mobileplayer.action.PLAY";

    Boolean playPressed = false;
    Boolean pressedBackFirst = false;
    MediaPlayer mp = null;
    public static String songTitle;
    public static int currentTrack = 0;
    int mAudioPosition = 0;
    ResultReceiver mReceiver;


    String[] songFileName = {"endofthebeginning", "helovesme", "thatsmyking",
            "thingsofthisworld"};
    static String[] songTitleArray = {"End Of The Beginning", "He Loves Me",
            "That's My King", "Things Of This World"};



    public class ServiceBinder extends Binder {

    public MyService getService(){
        return MyService.this;

    }}

    ServiceBinder mBinder;

        @Override
        public void onCreate() {
            Log.i(TAG, "MyService - onCreate");

            super.onCreate();
//        mBinder = new BoundServiceBinder();
            Log.i(TAG, "Service Created");

            Notification.Builder mBuilder =
                    new Notification.Builder(this)
                            .setSmallIcon(R.drawable.speaker)
                            .setContentTitle("MyPlayer")
                            .setContentText("Touch me and let's go!")
                            .setAutoCancel(false)
                            .setOngoing(true);

            startForeground(FOREGROUND_NOTIFICATION, mBuilder.build());


            mp = new MediaPlayer();

            initMP();
            try {
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



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

//    @Override
//    public boolean onError(MediaPlayer mp, int what, int extra) {
//        return false;
//    }


    public class BoundServiceBinder extends Binder {
        public MyService getService() {

            return MyService.this;
        }
    }

    //Service binder
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "MyService - onBind");

        return mBinder;
    }




    //Start service
//    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "MyService - onStartCommand");
        if (intent.getAction().equals(ACTION_PLAY)){
            initMP();
            mp.setOnPreparedListener(this);
            mp.prepareAsync();
        }

        if(intent.hasExtra(MainActivity.EXTRA_RECEIVER)) {
                mReceiver = (ResultReceiver)intent.getParcelableExtra(MainActivity.EXTRA_RECEIVER);
        }
        mp.start();
        return Service.START_STICKY;
    }

    //Send SongTitle to MainActivity
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "MyService - onHandleIntent");

        if (intent.hasExtra(MainActivity.EXTRA_RECEIVER)) {
            ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra(MainActivity
                    .EXTRA_RECEIVER);
            Bundle result = new Bundle();
            result.putString(MainActivity.DATA_RETURNED, "SongTitle");
            receiver.send(MainActivity.RESULT_DATA_RETURNED, result);
        }
    }

    // initialize the MediaPlayer
    public void initMP() {
        Log.i(TAG, "MyService - initMP");

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
        } else {
            currentTrack++;
        }
        mAudioPosition = 0;
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

            Log.i(TAG, "!pressedBackFirst");

            currentTrack--;
            pressedBackFirst = true;

        }

        mp.pause();

        if (currentTrack < 0) {
            currentTrack = 3;
        } else {
            currentTrack--;
            if (currentTrack < 0) {
                currentTrack = 3;
            }
        }
        mAudioPosition = 0;
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
        Log.i(TAG, "Pause:audioposition: " + mAudioPosition);
        if (mp != null && mp.isPlaying()) {
            mAudioPosition = mp.getCurrentPosition();
            Log.i(TAG, "Pause:audioposition: " + mAudioPosition);

            Log.i(TAG,"Pause:pre-pause CurrentTrack = " + currentTrack);
            int ct = currentTrack;
            currentTrack = ct;
            Log.i(TAG,"Pause:post-pause CurrentTrack = " + currentTrack);

            mp.pause();

        }

    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "Service Stopped");
        super.onDestroy();
        if (mp != null) {
            mp.stop();
            mp.release();
            stopForeground(true);
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


    //    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mp) {
//        songTitle = songTitleArray[this.currentTrack];
        Log.i(TAG, "onPrepared: " + songTitle);
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(i.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

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

    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if (mp != null) {
            try {
                mp.stop();
                mp.release();
            } finally {
                mp = null;
            }
        }
        return false;

    }


}

