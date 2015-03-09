package michaelusry.com.mdf3wk1finalv2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Michael Usry on 3/8/15.
 */
public class MyService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener{

    private static final String TAG = "MyService.TAG";
    private static final int FOREGROUND_NOTIFICATION = 0x01001;
    public static String songTitle;
    public static int currentTrack = 0;
    int mAudioPosition = 0;
    Boolean pressedBackFirst = false;
    MediaPlayer mp;

    String[] songFileName = {"endofthebeginning", "helovesme", "thatsmyking",
            "thingsofthisworld"};
    static String[] songTitleArray = {"End Of The Beginning", "He Loves Me",
            "That's My King", "Things Of This World"};

    @Override
    public IBinder onBind(Intent intent) {
        return new BoundServiceBinder();
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
            e.printStackTrace();
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    public MyService(){}


    public class BoundServiceBinder extends Binder {
        public MyService getService() {

            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"Service Created");
        mp = new MediaPlayer();

        initMP();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"Service Started");

        return super.onStartCommand(intent, flags, startId);
    }


    public void initMP() {
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);

    }

    protected void play() throws IOException, IllegalArgumentException, SecurityException, IllegalStateException   {
        Log.i(TAG, "play()");

        if (mp != null) {

            Uri file = Uri.parse("android.resource://" + getPackageName()
                    + "/raw/" + songFileName[currentTrack]);

            Log.i(TAG, "Uri file = " + file);
            songTitle = songTitleArray[currentTrack];
            Log.i(TAG, "songTitle: " + songTitle);
            mp.reset();
            mp.setDataSource(this, file);
            mp.prepare();
            mp.seekTo(mAudioPosition);
            mp.start();
        }

    }

    protected void stop() {
        Log.i(TAG, "stop()");
        mAudioPosition = 0;
        mp.stop();

    }

    protected void forward() throws IOException, IllegalArgumentException, SecurityException, IllegalStateException{
        Log.i(TAG, "forward()");

        mp.pause();

            if (currentTrack >= 3) {
                currentTrack = 0;
            } else {
                currentTrack++;
            }
        mAudioPosition=0;
            mp.reset();
            mp.setDataSource(
                    this,
                    Uri.parse("android.resource://" + getPackageName()
                            + "/raw/" + songFileName[currentTrack]));
            mp.prepare();

        mp.start();
        songTitle = songTitleArray[currentTrack];

    }

    protected void backward() throws IOException, IllegalArgumentException, SecurityException, IllegalStateException{
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
        mAudioPosition=0;
            mp.reset();
            mp.setDataSource(
                    this,
                    Uri.parse("android.resource://" + getPackageName()
                            + "/raw/" + songFileName[currentTrack]));
            mp.prepare();

        mp.start();
        songTitle = songTitleArray[currentTrack];

    }

    protected void pause() {
        Log.i(TAG, "pause()");

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
        }
    }

    public void sendToForeground(){
        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(i.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pi)
                .setSmallIcon(R.drawable.music_icon)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);

        startForeground(FOREGROUND_NOTIFICATION, builder.build());

    }


}
