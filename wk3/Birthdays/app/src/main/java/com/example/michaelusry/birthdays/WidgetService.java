package com.example.michaelusry.birthdays;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by michael on 12/8/14.
 */
public class WidgetService extends RemoteViewsService{
    public static final String TAG = "WidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i(TAG,"RemoteViewsFactory");
        return new WidgetViewFactory(getApplicationContext());
    }


}
