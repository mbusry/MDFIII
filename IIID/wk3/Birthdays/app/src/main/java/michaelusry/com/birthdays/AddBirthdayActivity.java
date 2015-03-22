package michaelusry.com.birthdays;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Michael Usry  on 3/16/15.
 */
public class AddBirthdayActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "AddBirthdayActivity";
    public static final String filename = "birthday.txt";
    private ArrayList<String> readArrayList;
    Boolean converted = false;
    File external;

    EditText fn, ln, dob;
    String f, l, d;
    String combined;
    Context m_context;
    Button save, done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Log.i(TAG, "NOW Started.  loadedJsonArray" + MainActivity.loadedJSonArray);
        external = getExternalFilesDir(null);

        fn = (EditText) findViewById(R.id.fn_et);
        ln = (EditText) findViewById(R.id.ln_et);
        dob = (EditText) findViewById(R.id.bd_et);

        save = (Button) findViewById(R.id.btn_save);
        save.setOnClickListener(this);

        done = (Button) findViewById(R.id.btn_done);
        done.setOnClickListener(this);

        m_context = this;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

//        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#333333")));


        return true;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_save:

                Log.i(TAG, "Save Button pressed");

                f = fn.getText().toString();
                l = ln.getText().toString();
                d = dob.getText().toString();

                Log.i(TAG, "b4 writetofile: f " + f);
                Log.i(TAG, "b4 writetofile: l " + l);
                Log.i(TAG, "b4 writetofile: d " + d);

                writeJSON(f, l, d);
                updateWidget();


                break;

            case R.id.btn_done:

                Log.i(TAG, "Done Button pressed");

                Intent hit = new Intent(this, MainActivity.class);
                startActivity(hit);
                finish();

                break;


        }
    }


    private void writeJSON(String _fn, String _ln, String _dob) {

        JSONObject obj = new JSONObject();
        String jsonstring = obj.toString();
        JSONObject convertedString = new JSONObject();
        JSONObject combinedString = new JSONObject();


        if (MainActivity.loadedJSonArray == null) {
            Log.i(TAG, "loadedjsonarray = null");

            MainActivity.loadedJSonArray = new JSONArray();


            try {
                obj.put("firstname", _fn);
                obj.put("lastname", _ln);
                obj.put("dob", _dob);

                jsonstring = obj.toString();


                convertedString = new JSONObject(jsonstring);


                Log.i(TAG, "convertedString:" + convertedString);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        } else {

            Log.i(TAG, "loadedJsonArray != null");
            Log.i(TAG, "Entering..MainActivity.loadedJsonArray: " + MainActivity.loadedJSonArray);

            try {
                obj.put("firstname", _fn);
                obj.put("lastname", _ln);
                obj.put("dob", _dob);

                jsonstring = obj.toString();
                Log.i(TAG, "jsonstring: " + jsonstring);

                convertedString = new JSONObject(jsonstring);

                Log.i(TAG, "MainActivity.loadedJSON: " + MainActivity.loadedJSonArray);
                Log.i(TAG, "convertedString:" + convertedString);


                MainActivity.loadedJSonArray.put(convertedString);

                combined = MainActivity.loadedJSonArray.toString();
                converted = true;

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


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateWidget() {

        Log.i(TAG, "updateWidget");
        AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());
        int[] wid = awm.getAppWidgetIds(new ComponentName(getApplicationContext(), CollectionWidgetProvider.class));
        if (wid.length > 0) {
            new CollectionWidgetProvider().onUpdate(getApplicationContext(), awm, wid);
        }

    }



}


