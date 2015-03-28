package michaelusry.com.mdf3wk4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends Activity implements View.OnClickListener {
	
	private static final int REQUEST_TAKE_PICTURE = 0x01001;
    private static final String TAG = "CameraActivity.TAG";

    ImageView mImageView;
	Uri mImageUri;
    ImageButton save;
    EditText title, description;
    Button closeKB;
    String cLocation = null;
    String combined;
    String path;
    public static final String filename = "mapmarks.txt";
    File external;
//    double mLatitude = 0;
//    double mLongitude = 0;



    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form);
		
		mImageView = (ImageView)findViewById(R.id.PictureimageView);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageUri = getImageUri();
        if(mImageUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        }
        startActivityForResult(intent, REQUEST_TAKE_PICTURE);

        save = (ImageButton)findViewById(R.id.btn_save);
        save.setOnClickListener(this);

        closeKB = (Button)findViewById(R.id.close_kb_button);
        closeKB.setOnClickListener(this);

        if(MainFragment.cLocation !=null) {
            cLocation = MainFragment.cLocation;
        }

        //get long and lat from add button

        Intent i = getIntent();

        String lat = i.getStringExtra("lat");
        String longitude = i.getStringExtra("long");

//        mLatitude = Double.valueOf(lat).doubleValue();
//        mLongitude = Double.valueOf(longitude).doubleValue();

        title = (EditText) findViewById(R.id.imageTitleEditText);
        description = (EditText) findViewById(R.id.imageDescriptionEditText);

        external = getExternalFilesDir(null);



    }


    private Uri getImageUri() {

        Log.i(TAG,"getImageUri");
		String imageName = new SimpleDateFormat("MMddyyyy_HHmmss").format(new Date(System.currentTimeMillis()));
		Log.i(TAG,"imageName" + imageName);
		File imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File appDir = new File(imageDir, "MDF3WK4");
		appDir.mkdirs();
		
		File image = new File(appDir, imageName + ".jpg");
		try {
			image.createNewFile();
            Log.i(TAG,"image: " + image);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return Uri.fromFile(image);
	}
	
	private void addImageToGallery(Uri imageUri) {
		Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    scanIntent.setData(imageUri);
	    sendBroadcast(scanIntent);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"*****onActivityResult*****");
        if(requestCode == REQUEST_TAKE_PICTURE && resultCode != RESULT_CANCELED) {
            if(mImageUri != null) {
                mImageView.setImageBitmap(BitmapFactory.decodeFile(mImageUri.getPath()));
                Log.i(TAG,"mImageUri.getPath(): " + mImageUri.getPath());
                Log.i(TAG,"mImageUri: " + mImageUri);
                addImageToGallery(mImageUri);
                path = mImageUri.getPath();
            } else {
                mImageView.setImageBitmap((Bitmap)data.getParcelableExtra("data"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_save:
//TODO check the filename,path(mImageUri.getPath();
                String p = path;
                Double lat = MainFragment.clat;
                Double lng = MainFragment.clng;
                String t = title.getText().toString();
                String d = description.getText().toString();

                writeJSON(p, lat, lng , t, d);

                Log.i(TAG, "*********Save Button**********");
                Log.i(TAG,"path: " + p);
                Log.i(TAG,"lat: " + lat);
                Log.i(TAG,"lng: " + lng);
                Log.i(TAG,"Title: " + title.getText().toString());
                Log.i(TAG,"Description: " + description.getText().toString());
//                Log.i(TAG,"mLatitude: " + mLatitude);
//                Log.i(TAG,"mLongitude: " + mLongitude);

                break;

            case R.id.close_kb_button:

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(description.getWindowToken(), 0);

                break;

        }
    }

    private void writeJSON(String _p, Double _lat, Double _lng, String _title, String _desc) {
        Log.i(TAG,"*****writeJSON*****");

        Log.i(TAG,"p/lat/lng/title/desc" + _p + "/"+ _lat + "/" + _lng + "/" + _title + "/" + _desc);

        JSONObject obj = new JSONObject();
        String jsonstring = obj.toString();
        JSONObject convertedString = new JSONObject();


        if (MainActivity.loadedJSonArray == null) {
            Log.i(TAG, "loadedjsonarray = null");

            MainActivity.loadedJSonArray = new JSONArray();


            try {
                obj.put("path", _p);
                obj.put("lat", _lat);
                obj.put("lng", _lng);
                obj.put("title", _title);
                obj.put("desc", _desc);

                jsonstring = obj.toString();


                convertedString = new JSONObject(jsonstring);

                combined = convertedString.toString();


                Log.i(TAG, "convertedString:" + convertedString);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        } else {

            Log.i(TAG, "loadedJsonArray != null");
            Log.i(TAG, "Entering..MainActivity.loadedJsonArray: " + MainActivity.loadedJSonArray);

            try {
                obj.put("path", _p);
                obj.put("lat", _lat);
                obj.put("lng", _lng);
                obj.put("title", _title);
                obj.put("desc", _desc);

                jsonstring = obj.toString();
                Log.i(TAG, "jsonstring: " + jsonstring);

                convertedString = new JSONObject(jsonstring);

                Log.i(TAG, "MainActivity.loadedJSON: " + MainActivity.loadedJSonArray);
                Log.i(TAG, "convertedString:" + convertedString);


                MainActivity.loadedJSonArray.put(convertedString);
                Log.i(TAG,"MainActivity.loadedJSonArray: " + MainActivity.loadedJSonArray);

                combined = MainActivity.loadedJSonArray.toString();
                Log.i(TAG,"combinded: " + combined);

                Log.i(TAG, "After:MainActivity.loadedJsonArray: " + MainActivity.loadedJSonArray);
                Log.i(TAG, "After:combined: " + combined);

            } catch (JSONException e) {
                e.printStackTrace();

            }

        }

        try {

            OutputStreamWriter opsr = new OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE));
            opsr.write(combined);
            opsr.close();

            new AlertDialog.Builder(this)
                    .setTitle("Save Complete")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                        }

                    })
                    .show();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
