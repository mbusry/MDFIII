package com.example.michaelusry.birthdays;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by michael on 12/8/14.
 */
public class NewAppWidget extends AppWidgetProvider{

    public static final String EXTRA_ITEM = "com.example.michaelusry.birthdays.NewAppWidget.EXTRA_ITEM";
    public static final String ACTION_VIEW_CREATE = "com.example.michaelusry.birthdays.NewAppWidget.ACTION_VIEW_CREATE";
    public static final String ACTION_VIEW_DETAILS = "com.example.michaelusry.birthdays.NewAppWidget.ACTION_VIEW_DETAILS";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int i = 0; i < appWidgetIds.length; i++) {

            int widgetId = appWidgetIds[i];
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId,R.id.birthday_list);

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            widgetView.setRemoteAdapter(R.id.birthday_list,intent);
            widgetView.setEmptyView(R.id.birthday_list,R.id.empty);

            Intent dIntent = new Intent(ACTION_VIEW_DETAILS);
            PendingIntent pIntent = PendingIntent.getBroadcast(context,0,dIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            widgetView.setPendingIntentTemplate(R.id.birthday_list,pIntent);

            Intent cIntent = new Intent(ACTION_VIEW_CREATE);
            PendingIntent pcIntent = PendingIntent.getBroadcast(context,0,cIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            widgetView.setOnClickPendingIntent(R.id.btn_add_widget, pcIntent);


            appWidgetManager.updateAppWidget(widgetId, widgetView);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(ACTION_VIEW_DETAILS)){
            Birthdays bd = (Birthdays)intent.getSerializableExtra(EXTRA_ITEM);
            if(bd != null){
                Intent det = new Intent(context, BirthdayListActivity.class);
                det.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                det.putExtra(BirthdayListActivity.EXTRA_ITEM, bd);
            }
        }

        super.onReceive(context, intent);
    }
}
