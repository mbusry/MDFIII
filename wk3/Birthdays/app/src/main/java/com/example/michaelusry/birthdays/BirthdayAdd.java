package com.example.michaelusry.birthdays;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by michael on 12/11/14.
 */
public class BirthdayAdd extends Activity implements View.OnClickListener{

    private static final String TAG = "BirthdayAdd";
    private static final String filename = "birthdays.txt";

    EditText fn, ln, dob;
    String firstName, lastName, dateofbirth;
    Context m_context;
    Birthdays birthdays;
    Button save;
    File files_dir;
    static JSONArray loadedJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        fn = (EditText) findViewById(R.id.fn_et);
        ln = (EditText) findViewById(R.id.ln_et);
        dob = (EditText) findViewById(R.id.bd_et);
        save = (Button) findViewById(R.id.btn_save);
        save.setOnClickListener(this);

        m_context = this;

        files_dir = getFilesDir();
        String path = files_dir.getAbsolutePath();

        Log.i(TAG,"file path: " + path);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_save:

                //
                Log.i(TAG,"btn_save");
                Log.i(TAG, "fn: " + fn.getText());
                Log.i(TAG, "ln: " + ln.getText());
                Log.i(TAG, "DOB: " + dob.getText());

                Log.i(TAG, "Save button pressed");

                // add input to a file

                firstName = fn.getText().toString();
                lastName = ln.getText().toString();
                dateofbirth = dob.getText().toString();


                try {
                    saveFile(filename, firstName, lastName, dateofbirth);

                    ReadFile(this);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                BirthdayListActivity.widgetupdate(m_context);


                break;


        }
    }

    public void saveFile (String filename, String firstName, String lastName, String dateofbirth) throws IOException, JSONException {
        Log.i(TAG, "writeToFile");

        File file = getBaseContext().getFileStreamPath(filename);
        JSONArray jsonDataArray = new JSONArray();
        JSONObject birthdayObject = new JSONObject();
        loadedJsonArray = null;


        if (file.exists()) {
            Log.i(TAG,"file.exists");
            ReadFile(this);
            Log.i(TAG, "loadedjsonarray: " + loadedJsonArray);
            jsonDataArray = loadedJsonArray;
            birthdayObject.put("firstName", firstName);
            birthdayObject.put("lastName", lastName);
            birthdayObject.put("dateOfBirth", dateofbirth);

            jsonDataArray.put(birthdayObject);
            Log.i(TAG,"new jsonDataArray: " + jsonDataArray);

        } else {
            Log.i(TAG,"file does NOT exists");

            birthdayObject.put("firstName", firstName);
            birthdayObject.put("lastName", lastName);
            birthdayObject.put("dateOfBirth", dateofbirth);
            jsonDataArray.put(birthdayObject);
        }

            String data = jsonDataArray.toString();

            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            Log.i(TAG, "data: " + data.toString());

    }


    public void ReadFile(Context context) throws  IOException, JSONException{
        Log.i(TAG,"readFile");
    FileInputStream fis = openFileInput(filename);
    BufferedInputStream bis = new BufferedInputStream(fis);
    StringBuffer b = new StringBuffer();
    while (bis.available() !=0) {
        char c = (char) bis.read();
        b.append(c);
    }
    bis.close();
    fis.close();

    loadedJsonArray = new JSONArray(b.toString());
        Log.i(TAG,"jsonarray data.length: " + loadedJsonArray.length());
        Log.i(TAG,"jsonarray data: " + loadedJsonArray);

        //parse the JSONObject
    StringBuffer birthdayBuffer = new StringBuffer();
    for(int i = 0; i < loadedJsonArray.length(); i++){
        String fn = loadedJsonArray.getJSONObject(i).getString("firstName");
        String ln = loadedJsonArray.getJSONObject(i).getString("lastName");
        String dob = loadedJsonArray.getJSONObject(i).getString("dateOfBirth");
        birthdayBuffer.append(fn +" " + ln + " " + dob + "\n");
    }

    Log.i(TAG,"Birthdays read: " + birthdayBuffer.toString());
}


}
