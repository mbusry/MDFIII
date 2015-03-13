package michaelusry.com.mdf3wk1finalv2;

// MDF 3
// Michael Usry
// Term 1503

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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends Activity implements View.OnClickListener, ServiceConnection{

    static String TAG = "MainActivity.TAG";

    public static final String EXTRA_RECEIVER = "MainActivity.EXTRA_RECEIVER";
    public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";
    public static int progress = 0;


    ImageButton play,stop,forward,backward,pause;
    static ImageView albumcover;
    TextView songTitle;
    Boolean playPressed;
    MyService mService;
    Boolean mBound;
    Boolean loop;
    public static Boolean isRunning = false;
    CheckBox mCheckBox;
    public static ProgressBar progressBar;
    int sl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = (ImageButton) findViewById(R.id.playButton);
        play.setOnClickListener(this);

        pause = (ImageButton) findViewById(R.id.pauseButton);
        pause.setOnClickListener(this);

        stop = (ImageButton) findViewById(R.id.stopButton);
        stop.setOnClickListener(this);

        backward = (ImageButton) findViewById(R.id.backwardButton);
        backward.setOnClickListener(this);

        forward = (ImageButton) findViewById(R.id.forwardButton);
        forward.setOnClickListener(this);


        songTitle = (TextView) findViewById(R.id.songtitletextview);
        songTitle.setText("");

        mCheckBox = (CheckBox) findViewById(R.id.loopBox);
        mCheckBox.setOnClickListener(this);

        albumcover = (ImageView) findViewById(R.id.AlbumCover);

        progressBar = (SeekBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        playPressed = false;
        loop = false;

        Intent i = new Intent (this, MyService.class);
        i.putExtra(EXTRA_RECEIVER, new DataReceiver());
        startService(i);

    }

    private final Handler handle = new Handler();


    //UI Controls
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButton:
                Log.i(TAG,"Play Button");
                playPressed = true;
                isRunning = true;
                progressBar.setVisibility(View.VISIBLE);

                try {
                    mService.play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                progressBar();

                break;

            case R.id.forwardButton:
                Log.i(TAG,"Forward Button");
                isRunning = false;
                progress = 0;
                sl = 0;
                MyService.mAudioPosition = 0;
                try {
                    mService.forward();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.VISIBLE);
                progressBar();


                break;


            case R.id.pauseButton:
                Log.i(TAG, "Pause Button");
                isRunning = false;
                mService.pause();
                break;

            case R.id.stopButton:
                Log.i(TAG, "Stop Button");
                mService.stop();
                isRunning = false;
                progress = 0;
                progressBar.setVisibility(View.INVISIBLE);
                break;

            case R.id.backwardButton:
                Log.i(TAG,"Back Button");
                isRunning = false;
                progress = 0;
                sl = 0;
                try {
                    mService.backward();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.VISIBLE);
                progressBar();

                break;
            case R.id.loopBox:
                Log.i(TAG,"looping");
                if (mCheckBox.isChecked()) {
                    MyService.loop = true;
                    Log.i(TAG,"MyService.loop = " + MyService.loop);
                }else {
                    MyService.loop = false;
                    Log.i(TAG,"MyService.loop = " + MyService.loop);
                }
        }
        songTitle();
        albumCover();

    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onResume() {
        isRunning = true;
        songTitle();
        albumCover();
        progressBar();
        super.onResume();
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

    public class DataReceiver extends ResultReceiver{
        public DataReceiver(){
            super(handle);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData){
            if(resultData != null && resultData.containsKey(DATA_RETURNED)){
                songTitle.setText(resultData.getString(DATA_RETURNED,"SongTitle"));
            }
        }
    }

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
        songTitle.setText(MyService.songTitle);
        Log.i(TAG,"MainActivity songTitle");
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(this);
            isRunning = false;
            Log.i(TAG, "onStop - unbindService");

        }
    }

    public void albumCover(){
        albumcover.setImageBitmap(MyService.albumart);
    }

    public void progressBar(){
        Log.i(TAG,"progressBar");

        sl = MyService.seconds;

        Log.i(TAG,"sl: " + sl);


        new Thread(new Runnable() {
            public void run() {


                while (progress < sl && isRunning == true) {
                    Log.i(TAG,"while loop: sl: " + sl);

                    handle.post(new Runnable() {

                        public void run() {
                            Log.i(TAG,"handle: sl: " + sl);

                            progressBar.setMax(sl);
                            progressBar.setProgress(progress);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress += 1;

                    Log.i(TAG,"progess: " + progress);

                }
            }
        }).start();



    }

}
