package michaelusry.com.birthdays;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Michael Usry  on 3/16/15.
 */
public class DetailBirthdayActivity extends Activity {

    public static final String EXTRA_ITEM = "com.michaelusry.birthdays.DetailsActivity.EXTRA_ITEM";

    String firstname, lastname, dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday_details);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            firstname = extras.getString("firstname");
            lastname = extras.getString("lastname");
            dob = extras.getString("dob");

        }

//        set the views
        TextView bds = (TextView) findViewById(R.id.firstname);
        bds.setText(firstname);

        bds = (TextView) findViewById(R.id.lastname);
        bds.setText(lastname);

        bds = (TextView) findViewById(R.id.dateofbirth);
        bds.setText(dob);


    }
}
