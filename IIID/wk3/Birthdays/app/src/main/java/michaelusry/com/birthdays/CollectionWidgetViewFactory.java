package michaelusry.com.birthdays;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Michael Usry  on 3/16/15.
 */
public class CollectionWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final int ID_CONSTANT = 0x0101010;
    private static final String TAG = "CollectionWidgetViewFactory.TAG";
    private final ArrayList<Birthday> mBirthdayArrayList;

    private String filename = "birthday.txt";
    private Context mContext;
    private JSONArray loadedJSonArray;
    String itemsArray[];


    public CollectionWidgetViewFactory(Context context) {

        mContext = context;
        mBirthdayArrayList = new ArrayList<Birthday>();
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"onCreate");

        readFromFile();
        Log.i(TAG, "After readFromFile: itemsArray: " + itemsArray.toString());
        Log.i(TAG, "After readFromFile: loadedJSONArray: " + loadedJSonArray);


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
                mBirthdayArrayList.add(new Birthday(firstname,lastname,birth));
            }


    }

    @Override
    public void onDataSetChanged() {
        Log.i(TAG,"onDataSetChanged");


        readFromFile();

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        loadedJSonArray = null;
        itemsArray = null;
        mBirthdayArrayList.clear();

    }

    @Override
    public int getCount() {
        Log.i(TAG,"getCount");
        Log.i(TAG,"getCount = " + mBirthdayArrayList.size());

/*
        if (itemsArray != null) {
            Log.i(TAG, "itemsArray ! null.  itemsArray.length = " + itemsArray.length);
            return itemsArray.length;
        } else {
            return 0;

        }
        */
        return mBirthdayArrayList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.i(TAG," RemoteViews getViewAt");

        Birthday bd = mBirthdayArrayList.get(position);
        Log.i(TAG,"Birthday bd: " + bd);
        RemoteViews bdView = new RemoteViews(mContext.getPackageName(), R.layout.birthday_item);

        bdView.setTextViewText(R.id.firstname, bd.getFirstName());
        bdView.setTextViewText(R.id.lastname, bd.getLastName());
        bdView.setTextViewText(R.id.dateofbirth, bd.getDateOfBirth());

        Intent i = new Intent();
        i.putExtra(CollectionWidgetProvider.EXTRA_ITEM, bd);
        bdView.setOnClickFillInIntent(R.id.birthday_list, i);

        return bdView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {

        return ID_CONSTANT + position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void readFromFile() {
        Log.i(TAG, "readFromFile started");
        loadedJSonArray = new JSONArray();
//        convertedToObject = new JSONObject();

        String ret = "";

        try {
            InputStream inputStream = mContext.openFileInput(filename);

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


}
