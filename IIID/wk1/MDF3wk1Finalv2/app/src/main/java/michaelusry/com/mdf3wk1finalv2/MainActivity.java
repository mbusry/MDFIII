package michaelusry.com.mdf3wk1finalv2;

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
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends Activity implements View.OnClickListener, ServiceConnection{

    static String TAG = "MainActivity.TAG";

    public static final int RESULT_DATA_RETURNED = 0x0101010;
    public static final String EXTRA_RECEIVER = "MainActivity.EXTRA_RECEIVER";
    public static final String DATA_RETURNED = "MainActivity.DATA_RETURNED";


    ImageButton play,stop,forward,backward,pause;
    TextView songTitle;
    Boolean playPressed;
    MyService mService;
    Boolean mBound;

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

        playPressed = false;

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
                try {
                    mService.play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.pauseButton:
                Log.i(TAG, "Pause Button");
                mService.pause();
                break;

            case R.id.stopButton:
                Log.i(TAG, "Stop Button");
                mService.stop();
                break;

            case R.id.forwardButton:
                Log.i(TAG,"Forward Button");
                try {
                    mService.forward();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.backwardButton:
                Log.i(TAG,"Back Button");
                try {
                    mService.backward();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
        }
        songTitle();

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

        if(mBound) {
            unbindService(this);
            Log.i(TAG,"onStop - unbindService");

        }
    }
}
