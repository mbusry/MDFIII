package com.example.michaelusry.birthdays;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;

/**
 * Created by michael on 12/8/14.
 */
public class WidgetViewFactory implements RemoteViewsFactory {

    private static final String TAG = "WidgetViewFactory";
    private static final int ID_CONSTANT = 0x0101010;
    private ArrayList<Birthdays> birthdays;
    private Context mContext;
    public String filePath = Environment.getExternalStorageDirectory() + "/MDF3/";

    public WidgetViewFactory(Context context) {
        mContext = context;
//        birthdays = new ArrayList<Birthdays>();
    }

    @Override
    public void onCreate(){

    Log.i(TAG,"onCreate");
//        birthdays = BirthdayListActivity.birthdays;

    }

    @Override
    public void onDataSetChanged() {
        Log.i(TAG,"onDataSetChanged");


    }

    @Override
    public void onDestroy(){
    Log.i(TAG,"onDestory");
        birthdays.clear();
    }


    @Override
    public int getCount() {
        Log.i(TAG,"getCount");
        if (birthdays != null) {
            return birthdays.size();
        }else {
        return 0;
        }
    }


    @Override
    public RemoteViews getViewAt(int position) {
        Log.i(TAG,"RemoteViews");
        Birthdays birthday = birthdays.get(position);

        //create birthday info
        RemoteViews bdv = new RemoteViews(mContext.getPackageName(),R.layout.birthday_list);

        bdv.setTextViewText(R.id.fn_birthday_list, birthday.getFirstName());
        bdv.setTextViewText(R.id.ln_birthday_list, birthday.getLastName());
        bdv.setTextViewText(R.id.dob_birthday_list, birthday.getDOB());

        Intent intent = new Intent();
        intent.putExtra(WidgetProvider.EXTRA_ITEM, birthday);
        bdv.setOnClickFillInIntent(R.id.birthday_list,intent);

        return bdv;

    }



    @Override
    public int getViewTypeCount() {
        Log.i(TAG,"getViewTypeCount");
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        Log.i(TAG,"getItemId");

        return ID_CONSTANT + position;
    }

    @Override
    public boolean hasStableIds() {
        Log.i(TAG,"hasStableIds");

        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public RemoteViews getLoadingView() {
        Log.i(TAG,"getLoadingView");

        return null;
    }


}
