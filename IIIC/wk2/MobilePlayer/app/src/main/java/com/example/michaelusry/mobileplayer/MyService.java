/*
    Michael Usry
    MDF III week 2
    December 6, 2014


 */

package com.example.michaelusry.mobileplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.Random;

/**
 * Created by michael on 11/26/14.
 */
public class MyService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "MyService";
    private static final int FOREGROUND_NOTIFICATION = 0x01001;
    Boolean playPressed = false;
    Boolean pressedBackFirst = false;
    MediaPlayer mp;
    public static String songTitle;
    public static int currentTrack = 0;
    public static int songLength = 0;
    public static long currentDuration = 0;
    int mAudioPosition = 0;
    private boolean isRandom = false;
    public static String ac;
    public static Bitmap albumart;
    private final IBinder mBinder = new ServiceBinder();
    Boolean progressBarVisible = false;
    ProgressBar progressBar;
//    int progress = 0;
    public int seconds = 0;
    String procedure;




    String[] songFileName = {"endofthebeginning", "helovesme", "thatsmyking",
            "thingsofthisworld"};
    static String[] songTitleArray = {"End Of The Beginning", "He Loves Me",
            "That's My King", "Things Of This World"};
    static String[] albumCover = {"endofthebeginning","dctalk","thatsmyking","thingsofthisworld"};

//    BoundServiceBinder mBinder;

    public MyService(){}

    public class ServiceBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        procedure = "onCompletion";
        Log.i(TAG, "isRandom = " + isRandom);
        // check if loop is checked for the song

        Log.i(TAG, "onCompletation:currentTrack = " + currentTrack);

        setToZero();
        Uri nextTrack = Uri.parse("android.resource://" + getPackageName()
                + "/raw/" + songFileName[currentTrack]);

        Log.i(TAG, "nextTrack = " + nextTrack);


        try {
            mp.reset();
            mp.setDataSource(MyService.this, nextTrack);
            mp.prepare();
            mp.start();
            setLength(procedure);
            Log.i(TAG, "Song name: " + songFileName[currentTrack]);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(!isRandom)
        {
            currentTrack ++;

            Log.i(TAG,"NOT Random");
            Log.i(TAG,"currentTrack: " + currentTrack);

        }
        sendToForeground();




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
//        mBinder = new BoundServiceBinder();
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
            //check this - removed '= (ResultReceiver)'
            ResultReceiver receiver = intent.getParcelableExtra(MainActivity
                    .EXTRA_RECEIVER);
            Bundle result = new Bundle();
            result.putString(MainActivity.DATA_RETURNED, "SongTitle");
            receiver.send(MainActivity.RESULT_DATA_RETURNED,result);
        }
    }

    // initialize the MediaPlayer
    public void initMP() {
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);

    }

    protected void art(int track){
        Log.i(TAG,"art(track): " + track);
        ac = albumCover[track];

        if (track == 0) {
            albumart = BitmapFactory.decodeResource(getResources(),
                    R.drawable.endofthebeginning);

        } else if (track == 1) {
            albumart = BitmapFactory.decodeResource(getResources(),
                    R.drawable.dctalk);

        } else if (track == 2) {
            albumart = BitmapFactory.decodeResource(getResources(),
                    R.drawable.thatsmyking);

        } else if (track == 3) {
            albumart = BitmapFactory.decodeResource(getResources(),
                    R.drawable.thingsofthisworld);

        }

    }

// UI methods for buttons
// song play
    protected void play() throws IOException {
        procedure = "play";
        if (mp.isPlaying()) {
            Log.i(TAG, "Already playing ");
        }else{
            mp.reset();
            setToZero();
            if (isRandom) {
                randomNumber();
            }
            Log.i(TAG, "play()");
            Log.i(TAG, "isRandom = " + isRandom);
            Log.i(TAG, "currentTrack: " + currentTrack);

            Uri file = Uri.parse("android.resource://" + getPackageName()
                    + "/raw/" + songFileName[currentTrack]);

            Log.i(TAG, "Uri file = " + file);
            songTitle = songTitleArray[currentTrack];
            Log.i(TAG, "songTitle: " + songTitle);
            sendToForeground();
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

            mp.prepareAsync();
            mp.start();
            songLength = mp.getDuration() / 1000;
            art(currentTrack);
        }

//        Log.i(TAG,"albumCover: "+albumCover);


    }


    protected void stop() {
//        Log.i(TAG, "stop()");
//        Log.i(TAG, "isRandom = " + isRandom);

        procedure = "stop";
        mAudioPosition = 0;
        mp.stop();
        mp.reset();
//        stopForeground(true);



    }

    protected void forward() {
        Log.i(TAG, "forward()");
        Log.i(TAG, "isRandom = " + isRandom);
        Log.i(TAG, "for:currenttrack = " + currentTrack);


        mp.reset();
        setToZero();
        if(isRandom) {

        randomNumber();

        }
        if (currentTrack > (songTitleArray.length -1)){
            currentTrack = 0;
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
        songLength=mp.getDuration()/1000;
        art(currentTrack);
        songTitle = songTitleArray[currentTrack];
//        setLength(procedure);
        Log.i(TAG, "songLength: " + songLength);

        sendToForeground();



    }

    protected void back() {
        Log.i(TAG, "back()");
        Log.i(TAG, "isRandom = " + isRandom);
        setToZero();
        procedure = "back";
        if(isRandom) {
            randomNumber();
            Log.i(TAG, "currentTrack = " + currentTrack);

        }
        Log.i(TAG, "pre currentTrack = " + currentTrack);

        if (currentTrack < 0){
            currentTrack = songTitleArray.length -1;
            Log.i(TAG, "currentTrack = " + currentTrack);

        } else if (currentTrack >= songTitleArray.length){
            currentTrack = 0;
        }
        Log.i(TAG, "post currentTrack = " + currentTrack);


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
        Log.i(TAG,"songLength: "+songLength);

        mp.start();
        songLength=mp.getDuration()/1000;
//        setLength(procedure);
        art(currentTrack);
        songTitle = songTitleArray[currentTrack];
        sendToForeground();



    }

    protected void pause() {
        Log.i(TAG, "pause()");
//        Log.i(TAG, "isRandom = " + isRandom);
//
//        Log.i(TAG,"audioposition: " + mAudioPosition);
        if (mp != null && mp.isPlaying()) {
            mAudioPosition = mp.getCurrentPosition();
            Log.i(TAG,"audioposition: " + mAudioPosition);

            mp.pause();
        }

    }

    protected void random(Boolean r){

        isRandom = r;


    }

    protected void randomNumber(){

        Log.i(TAG, "isRandom = " + isRandom);
        int min = 1;
        int max = songFileName.length;

        Random rand = new Random();
        currentTrack = (rand.nextInt(max - min + 1) + min) - 1;
        Log.i(TAG,"rand number = " + currentTrack);


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



    @Override
    public void onPrepared(MediaPlayer mp){
//        songTitle = songTitleArray[this.currentTrack];
        Log.i(TAG,"onPrepared: " + songTitle);
        if(!isRandom)
        {
//            currentTrack = (currentTrack + 1) % songFileName.length;
            currentTrack ++;

            Log.i(TAG,"NOT Random");
            Log.i(TAG,"currentTrack: " + currentTrack);

        }



    }

    public void sendToForeground(){
        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(i.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pi)
                .setSmallIcon(R.drawable.speaker)
                .setLargeIcon(albumart)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);


        startForeground(FOREGROUND_NOTIFICATION, builder.build());


    }

    public void setToZero(){
        mAudioPosition=0;
        currentDuration = 0;
        songLength = 0;
        MainActivity.progress = 0;

    }

    public void setLength(String procedure){
        songLength = mp.getDuration()/1000;
        currentDuration = mp.getCurrentPosition();
//        Log.i(procedure, "songLength " + songLength);
//        Log.i(procedure, "currentDuration " + currentDuration);

    }


}

