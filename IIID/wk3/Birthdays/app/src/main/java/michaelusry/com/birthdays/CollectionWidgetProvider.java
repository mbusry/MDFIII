package michaelusry.com.birthdays;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by Michael Usry  on 3/16/15.
 */
public class CollectionWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "com.michaelusry.birthdays.CollectionWidgetProvider.EXTRA_ITEM";
    public static final String ACTION_VIEW_DETAILS = "com.michaelusry.birthdays.CollectionWidgetProvider.ACTION_VIEW_DETAILS";
    public static final String TAG = "CollectionWidgetProvider.TAG";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.i(TAG, "onUpdate");

        for (int i = 0; i < appWidgetIds.length; i++) {

            int widgetId = appWidgetIds[i];

            Intent intent = new Intent(context, CollectionWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            widgetView.setRemoteAdapter(R.id.birthday_list, intent);
            widgetView.setEmptyView(R.id.birthday_list, R.id.empty);

            Intent detailIntent = new Intent(ACTION_VIEW_DETAILS);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            widgetView.setPendingIntentTemplate(R.id.birthday_list, pIntent);

            appWidgetManager.updateAppWidget(widgetId, widgetView);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive");

        if(intent.getAction().equals(ACTION_VIEW_DETAILS)) {
            Birthday article = (Birthday)intent.getSerializableExtra(EXTRA_ITEM);
            if(article != null) {
                Intent details = new Intent(context, WidgetDetailBirthdayActivity.class);
                details.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                details.putExtra(WidgetDetailBirthdayActivity.EXTRA_ITEM, article);
                context.startActivity(details);
            }
        }

        super.onReceive(context, intent);
    }

}
