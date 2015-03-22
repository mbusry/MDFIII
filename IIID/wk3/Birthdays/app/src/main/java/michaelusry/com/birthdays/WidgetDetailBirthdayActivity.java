package michaelusry.com.birthdays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by michael on 2/16/15.
 */
public class WidgetDetailBirthdayActivity extends Activity {

    public static final String EXTRA_ITEM = "com.michaelusry.birthdays.DetailsActivity.EXTRA_ITEM";
    private static final String TAG = "WidgetDetailBirthdayActivity.TAG";

    String firstname, lastname, dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday_details);
        Log.i(TAG,"onCreate");


        Intent intent = getIntent();
        Birthday article = (Birthday)intent.getSerializableExtra(EXTRA_ITEM);
        if(article == null) {
            Log.i(TAG,"Birthday article == null");
            finish();
            return;
        }

        //set the views
        TextView bds = (TextView) findViewById(R.id.firstname);
        bds.setText(article.getFirstName());

        bds = (TextView) findViewById(R.id.lastname);
        bds.setText(article.getLastName());

        bds = (TextView) findViewById(R.id.dateofbirth);
        bds.setText(article.getDateOfBirth());


    }
}
