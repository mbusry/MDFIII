package michaelusry.com.birthdays;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*
* Created by Michael Usry  on 3/16/15.
*/


public class MainActivity extends Activity {

    private static final int ADDMYBD = 0011;
    private static final String TAG = "MainActivity.tag";
    private static final String filename = "birthday.txt";
    public static JSONArray loadedJSonArray;
    ArrayList mBirthday = new ArrayList<Birthday>();

    ArrayList<String> items;
    String itemsArray[];
    JSONObject convertedToObject;
    JSONObject arrayElement;
    Context m_context;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_context = this;
        Log.i(TAG, "onCreate, started!");

        lv = (ListView) findViewById(R.id.listView);

        updateWidget();
        readFromFile();
        updateView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == ADDMYBD) {


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                Intent birthdayIntent = new Intent(this, AddBirthdayActivity.class);
                startActivityForResult(birthdayIntent, ADDMYBD);

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
//                    convertedToObject = new JSONObject(ret);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "loadedJSONArray: " + loadedJSonArray);
//                Log.i(TAG,"convertedToObject: " + convertedToObject);


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


    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");

        super.onPause();

//        unregisterReceiver(mReceiver);
    }

    public void updateView() {
        Log.i(TAG, "updateView started");
        if (loadedJSonArray.length() > 0) {
            Log.i(TAG, "loadedJsonArray != null");
            Log.i(TAG, "loadedJsonArray.length: " + loadedJSonArray.length());

            String fn, ln, dob;
            items = new ArrayList<String>();
            for (int i = 0; i < loadedJSonArray.length(); i++) {
                String firstname = null;
                String lastname = null;
                String birth = null;


                try {
                    JSONObject json_data = loadedJSonArray.getJSONObject(i);
                    firstname = json_data.getString("firstname");
                    lastname = json_data.getString("lastname");
                    birth = json_data.getString("dob");

                    Log.i(TAG, "firstname: " + firstname);
                    Log.i(TAG, "lastname: " + lastname);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                items.add(firstname + " " + lastname);
                mBirthday.add(new Birthday(firstname, lastname, birth));


                Log.i(TAG, "items.toString()" + items.toString());
            }

            Log.i(TAG,"mBirthday.size: " +mBirthday.size());

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(TAG, "onItemClick: " + position);
//                    Log.i(TAG,"onItemClick: arrayList: " + arrayList);

                    try {
                        Log.i(TAG, "loadedJSonArray.getString(position): " + loadedJSonArray.getString(position));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        arrayElement = loadedJSonArray.getJSONObject(position);
                    } catch (JSONException e) {
                        Log.i(TAG, "Problem getting the object at position: \n");
                        e.printStackTrace();
                    }
                    try {
                        Log.i(TAG, "firstname: " + arrayElement.getString("firstname"));
                        Log.i(TAG, "lastname: " + arrayElement.getString("lastname"));
                        Log.i(TAG, "dob: " + arrayElement.getString("dob"));

                        String firstname = arrayElement.getString("firstname");
                        String lastname = arrayElement.getString("lastname");
                        String dob = arrayElement.getString("dob");


                        Intent i = new Intent(getApplicationContext(), DetailBirthdayActivity.class);
                        i.putExtra("firstname", firstname);
                        i.putExtra("lastname", lastname);
                        i.putExtra("dob", dob);
                        startActivity(i);


                    } catch (JSONException e) {
                        Log.i(TAG, "Problem getting the arrayElements: \n");

                        e.printStackTrace();
                    }

                }
            });
        }

    }


}

