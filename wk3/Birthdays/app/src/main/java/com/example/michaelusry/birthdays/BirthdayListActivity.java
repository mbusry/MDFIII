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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by michael on 12/9/14.
 */
public class BirthdayListActivity extends Activity {
    public static final String EXTRA_ITEM = "com.example.michaelusry.birthdays.BirthdayListActivity.EXTRA_ITEM";


    Context m_context = this;
    public static ArrayList<Birthdays> birthdays;
    public static String TAG = "BirthdayListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday_list);
        birthdays = new ArrayList<Birthdays>();
        Log.i(TAG,"onCreate");


        //fileinput

        try {
            FileInputStream fis = openFileInput("savedbirthdays");
            if (fis !=null){
                ObjectInputStream ois = new ObjectInputStream(fis);
                birthdays = (ArrayList<Birthdays>)ois.readObject();
                ois.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main,menu);
    return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    int id = item.getItemId();
        switch (id)
        {
            case R.id.action_add:
            Intent intent = new Intent(m_context, MainActivity.class);
            startActivityForResult(intent, 0);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void saveFile(Context context) {

        Log.i(TAG, "In BirthdayListActivity:writeToFile");
        Log.i(TAG,"birthdays: " + birthdays);

        try {
            FileOutputStream fos = context.openFileOutput("savedbirthdays", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(birthdays);
            oos.close();
            fos.close();

            Log.i(TAG, "WRITE STRING FILE:success");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("FILE NOT FOUND", e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("I/O ERROR", e.toString());

        }
    }


    public static void widgetupdate(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = new int[0]; appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.bd_list);
    }


}
