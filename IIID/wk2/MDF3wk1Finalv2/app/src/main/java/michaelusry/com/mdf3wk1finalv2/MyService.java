package michaelusry.com.mdf3wk1finalv2;

// MDF 3
// Michael Usry
// Term 1503


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
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Michael Usry on 3/8/15.
 */
public class MyService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "MyService.TAG";
    private static final int FOREGROUND_NOTIFICATION = 0x01001;
    public static String songTitle;
    public static int currentTrack = 0;
    static int mAudioPosition = 0;
    Boolean pressedBackFirst = false;
    public static Boolean loop = false;
    MediaPlayer mp;
    String ac;
    static Bitmap albumart;
    public static int seconds = 0;

    String[] songFileName = {"endofthebeginning", "helovesme", "thatsmyking",
            "thingsofthisworld"};
    static String[] songTitleArray = {"End Of The Beginning", "He Loves Me",
            "That's My King", "Things Of This World"};
    static String[] albumCover = {"endofthebeginning", "dctalk", "thatsmyking", "thingsofthisworld"};


    @Override
    public IBinder onBind(Intent intent) {
        return new BoundServiceBinder();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "Enter onCompletion");
        Log.i(TAG, "currentTrack: " + currentTrack);

        mp.reset();
        if (!loop) {
            currentTrack++;
            Log.i(TAG,"!loop");
            Log.i(TAG, "currentTrack++: " + currentTrack);

            if (currentTrack > 3) {
                currentTrack = 0;
                Log.i(TAG, "currentTrack > 3 now: " + currentTrack);

            }
        }

        zeroOut();

        Uri nextTrack = Uri.parse("android.resource://" + getPackageName()
                + "/raw/" + songFileName[currentTrack]);

        Log.i(TAG, "nextTrack = " + nextTrack);

        try {
            mp.setDataSource(MyService.this, nextTrack);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.seekTo(mAudioPosition);
        mp.start();
        Log.i(TAG,"currentTrack: " + currentTrack);
        albumArt(currentTrack);
        MainActivity.albumcover.setImageBitmap(albumart);

        setProgress();
        sendToForeground();

        Log.i(TAG,"end of Completion CurrentTrack: " + currentTrack);

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    public MyService() {
    }


    public class BoundServiceBinder extends Binder {
        public MyService getService() {

            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service Created");
        mp = new MediaPlayer();

        initMP();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service Started");

        return super.onStartCommand(intent, flags, startId);
    }


    public void initMP() {
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);

    }

    protected void play() throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        Log.i(TAG, "play()");

        if (mp != null) {

            zeroOut();

            Uri file = Uri.parse("android.resource://" + getPackageName()
                    + "/raw/" + songFileName[currentTrack]);

            Log.i(TAG, "Uri file = " + file);
            Log.i(TAG, "songTitle: " + songTitle);
            mp.reset();
            mp.setDataSource(this, file);
            mp.prepare();
            mp.seekTo(mAudioPosition);
            mp.start();
            setProgress();
            Log.i(TAG,"currentTrack: " + currentTrack);
            albumArt(currentTrack);
            sendToForeground();

        }

    }

    protected void songLength(int length) {
        seconds = (length / 1000);
        Log.i(TAG, "seconds: " + seconds);


    }

    protected void stop() {
        Log.i(TAG, "stop()");
        zeroOut();
        mp.stop();

    }

    protected void forward() throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        Log.i(TAG, "forward()");
        mp.stop();
        mp.reset();
        zeroOut();
        Log.i(TAG, "currentTrack: " + currentTrack);

        if (!loop) {
            Log.i(TAG, "!loop: ");


            if (currentTrack >= 3) {
                Log.i(TAG, "currentTrack: " + currentTrack);

                currentTrack = 0;
            } else {
                Log.i(TAG, "BEFORE ++ currentTrack: " + currentTrack);

                currentTrack++;
                Log.i(TAG, "currentTrack++: " + currentTrack);

            }
        }
        Log.i(TAG, "OUTSIDE OF !loop: currentTrack: " + currentTrack);
        mp.setDataSource(
                this,
                Uri.parse("android.resource://" + getPackageName()
                        + "/raw/" + songFileName[currentTrack]));
        mp.prepare();
        mp.seekTo(mAudioPosition);
        mp.start();
        setProgress();
        Log.i(TAG,"currentTrack: " + currentTrack);
        albumArt(currentTrack);
        sendToForeground();


    }

    protected void backward() throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        Log.i(TAG, "back()");

        mp.reset();
        zeroOut();

        if (!pressedBackFirst) {
            if (!loop) {
                Log.i(TAG, "!pressedBackFirst");

                currentTrack--;
                pressedBackFirst = true;

            }
        }
        if (!loop) {


            if (currentTrack < 0) {
                currentTrack = 3;
            } else {
                currentTrack--;
                if (currentTrack < 0) {
                    currentTrack = 3;
                }
            }
        }
        mp.setDataSource(
                this,
                Uri.parse("android.resource://" + getPackageName()
                        + "/raw/" + songFileName[currentTrack]));
        mp.prepare();
        mp.start();
        setProgress();
        Log.i(TAG,"currentTrack: " + currentTrack);
        albumArt(currentTrack);
        sendToForeground();


    }

    protected void pause() {
        Log.i(TAG, "pause()");

        if (mp != null && mp.isPlaying()) {
            mAudioPosition = mp.getCurrentPosition();
            Log.i(TAG, "audioposition: " + mAudioPosition);

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
            zeroOut();
        }
    }

    public void sendToForeground() {
        Log.i(TAG, "sendToForeground:");
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(i.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pi)
                .setSmallIcon(R.drawable.music_icon)
                .setLargeIcon(albumart)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);

        startForeground(FOREGROUND_NOTIFICATION, builder.build());

    }

    public void albumArt(int song) {
        Log.i(TAG,"albumArt(song) " + song);
        songTitle = songTitleArray[song];


//        ac = albumCover[song];

        if (song == 0) {
            albumart = BitmapFactory.decodeResource(getResources(),
                    R.drawable.endofthebeginning);

        } else if (song == 1) {
            albumart = BitmapFactory.decodeResource(getResources(),
                    R.drawable.dctalk);

        } else if (song == 2) {
            albumart = BitmapFactory.decodeResource(getResources(),
                    R.drawable.thatsmyking);

        } else if (song == 3) {
            albumart = BitmapFactory.decodeResource(getResources(),
                    R.drawable.thingsofthisworld);

        }

    }

    public void zeroOut() {

        mAudioPosition = 0;
        MainActivity.progress = 0;
        seconds = 0;

    }

    public void setProgress() {
        Log.i(TAG,"setProgress");
        MainActivity.isRunning = true;
        songLength(mp.getDuration());
        MainActivity.progressBar.setProgress(0);
        Log.i(TAG, "setProgress: " + MainActivity.progressBar.getProgress());
//        MainActivity.progressBar.setMax(seconds);
//        Log.i(TAG, "seconds: " + seconds);


    }


}
