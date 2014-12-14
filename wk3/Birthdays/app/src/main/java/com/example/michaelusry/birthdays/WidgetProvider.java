package com.example.michaelusry.birthdays;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by michael on 12/8/14.
 */
public class WidgetProvider extends AppWidgetProvider{

    public static final String EXTRA_ITEM = "com.example.michaelusry.birthdays.WidgetProvider.EXTRA_ITEM";
    public static final String TAG = "WidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.i(TAG,"onUpdate");
        for(int i = 0; i < appWidgetIds.length; i++) {

            int widgetId = appWidgetIds[i];

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            PendingIntent pIntent = PendingIntent.getActivity(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            widgetView.setOnClickPendingIntent(R.id.empty, pIntent);

            appWidgetManager.updateAppWidget(widgetId, widgetView);
        }
    }
}
