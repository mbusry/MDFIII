package com.example.michaelusry.birthdays;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity implements View.OnClickListener{

    public static final String EXTRA_ITEM = "com.example.michaelusry.MainActivity.EXTRA_ITEM";
    private static final String TAG = "MainActivity";
    private static final String filename = "birthdays.txt";


    EditText fn, ln, dob;
    Context m_context;
    JSONArray loadedJsonArray;
    Birthdays birthdays;
    ListView lv;
    SimpleAdapter adapter;
    static ArrayList<HashMap<String, String>> birthdayList = new ArrayList<HashMap<String, String>>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m_context = this;
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listView);
        adapter = new SimpleAdapter(m_context, birthdayList,
                R.layout.list_row,
                new String[] {"fn"},
                new int[] {R.id.FirstName});




        try {
            ReadFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        updateWidget();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            // start AddActivity
            Intent i = new Intent(MainActivity.this, BirthdayAdd.class);
            startActivity(i);

            return true;}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_save:
                Log.i(TAG,"btn_save");
                Log.i(TAG, "fn: " + fn.getText());
                Log.i(TAG, "ln: " + ln.getText());
                Log.i(TAG, "DOB: " + dob.getText());

                Log.i(TAG, "Save button pressed");

                // add input to a file


                BirthdayListActivity.saveFile(m_context);
                BirthdayListActivity.widgetupdate(m_context);

                break;


        }
    }

    public void updateWidget(){
        Log.i(TAG,"updateWidget");
        AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());
        int [] widgetID = awm.getAppWidgetIds(new ComponentName(getApplicationContext(), NewAppWidget.class));
        if(widgetID.length > 0){
            new NewAppWidget().onUpdate(getApplicationContext(),awm, widgetID);
        }
    }

    public void ReadFile() throws IOException, JSONException {
        Log.i(TAG, "readFile");
        File file = getBaseContext().getFileStreamPath(filename);
        String fn = null;
        String ln = null;
        String dateofbirth = null;

        ArrayList<HashMap<String, String>> birthdayArrayList = new ArrayList<HashMap<String, String>>();

        if (file.exists()) {


            FileInputStream fis = openFileInput(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuffer b = new StringBuffer();
            while (bis.available() != 0) {
                char c = (char) bis.read();
                b.append(c);
            }
            bis.close();
            fis.close();

            loadedJsonArray = new JSONArray(b.toString());
            Log.i(TAG, "jsonarray data.length: " + loadedJsonArray.length());
            Log.i(TAG, "jsonarray data: " + loadedJsonArray);

            //parse the JSONObject
            StringBuffer birthdayBuffer = new StringBuffer();
            for (int i = 0; i < loadedJsonArray.length(); i++) {
                fn = loadedJsonArray.getJSONObject(i).getString("firstName");
                ln = loadedJsonArray.getJSONObject(i).getString("lastName");
                dateofbirth = loadedJsonArray.getJSONObject(i).getString("dateOfBirth");

                birthdayBuffer.append(fn + " " + ln + " " + dob + "\n");
                HashMap<String, String> birthdayList = new HashMap<String, String>();

                birthdayList.put("firstName", fn);
//                birthdayList.put("dateOfBirth", dateofbirth);
                Log.i(TAG,"birthdayList: " + birthdayList);

                birthdayArrayList.add(birthdayList);

            }
            lv.setAdapter(adapter);


            Log.i(TAG, "Birthdays read: " + birthdayBuffer.toString());
        } else {
            Log.i(TAG,"No File exist");
        }


    }




}
