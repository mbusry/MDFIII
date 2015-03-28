package michaelusry.com.mdf3wk4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static michaelusry.com.mdf3wk4.CameraActivity.filename;

/**
 * Created by Michael Usry on 3/22/15.
 *
 *
 */
public class MainActivity extends Activity implements LocationListener{

    private static final int REQUEST_ENABLE_GPS = 0x02001;
    private static final String TAG = "MainActivity.TAG" ;
    public static JSONArray loadedJSonArray;

    public double mLongitude;
    public double mLatitude;

    LocationManager mManager;
    private JSONObject convertedToObject;
    private String[] itemsArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        mLatitude = 0;
        mLongitude = 0;


        enableGps();
        readFromFile();
        Log.i(TAG,"itemsArray: " + itemsArray);

        MainFragment frag = new MainFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", mLatitude);
        args.putDouble("long", mLongitude);
        frag.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
    }

    private void enableGps() {

        Log.i(TAG,"enableGps");
        if(mManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

            Location loc = mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc != null) {

                Log.i(TAG,"mLatitude: " + loc.getLatitude());
                Log.i(TAG,"mLongitude: " + loc.getLongitude());

                mLatitude = loc.getLatitude();
                mLongitude = loc.getLongitude();

                mManager.removeUpdates(this);

//                mLatitude.setText("" + loc.getLatitude());
//                mLongitude.setText("" + loc.getLongitude());
            }

        } else {
            new AlertDialog.Builder(this)
                    .setTitle("GPS Unavailable")
                    .setMessage("Please enable GPS in the system settings.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(settingsIntent, REQUEST_ENABLE_GPS);
                        }

                    })
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG,"onActivityResult");

        enableGps();
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableGps();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mManager.removeUpdates(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(TAG,"onLocationChanged");


        Log.i(TAG,"mLatitude: " + location.getLatitude());
        Log.i(TAG,"mLongitude: " + location.getLongitude());

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG,"onProviderDisabled");

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG,"onProviderEnabled");

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG,"onStatusChanged");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_add_info:
                Intent i = new Intent(this, CameraActivity.class);
                startActivity(i);

                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void readFromFile() {
        Log.i(TAG, "readFromFile started");
        loadedJSonArray = new JSONArray();
        convertedToObject = new JSONObject();

        String ret = "";

        try {
            InputStream inputStream = openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                Log.i(TAG, "stringbuilder: " + ret);

                try {

                    loadedJSonArray = new JSONArray(ret);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "loadedJSONArray: " + loadedJSonArray);


                if (loadedJSonArray.length() > 0) {

                    itemsArray = new String[loadedJSonArray.length()];
                }


            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }

}

