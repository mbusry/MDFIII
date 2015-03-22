package michaelusry.com.birthdays;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Michael Usry  on 3/16/15.
 */
public class CollectionWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CollectionWidgetViewFactory(getApplicationContext());

    }
}
