package com.example.michaelusry.birthdays;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by michael on 12/11/14.
 */
public class CollectionWidgetProvider  extends AppWidgetProvider{

    public static final String ACTION_VIEW_DETAILS = "com.example.michaelusry.ACTIONS_VIEW_DETAILS";
    public static final String EXTRA_ITEM = "com.example.michaelusry.CollectionWidgetProvider";
    public static final String TAG = "WidgetProvider";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        Log.i(TAG, "onUpdate");

        for(int i = 0; i < appWidgetIds.length; i++) {

            int widgetId = appWidgetIds[i];

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            widgetView.setRemoteAdapter(R.id.birthday_list, intent);
            widgetView.setEmptyView(R.id.birthday_list, R.id.empty);

            Intent dIntent = new Intent(ACTION_VIEW_DETAILS);
            PendingIntent pIntent = PendingIntent.getBroadcast(context,0,dIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            widgetView.setPendingIntentTemplate(R.id.birthday_list, pIntent);

            appWidgetManager.updateAppWidget(widgetId, widgetView);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive");

        if(intent.getAction().equals(ACTION_VIEW_DETAILS)) {
            Birthdays bd = (Birthdays) intent.getSerializableExtra(EXTRA_ITEM);
            if (bd != null) {
                //
            }
        }

        super.onReceive(context, intent);
    }
}