package michaelusry.com.mdf3wk4;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by michael on 3/27/15.
 */
public class DetailsActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "DetailsActivity.TAG";
    Button done;
    TextView imageTitle, imageDescription;
    ImageView picture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        done = (Button)findViewById(R.id.btn_done);
        done.setOnClickListener(this);

        imageTitle = (TextView)findViewById(R.id.DetailsImageTitleTextView);
        imageDescription = (TextView)findViewById(R.id.DetailsImageDescriptionTextView);

        Intent i = getIntent();

        String title = i.getStringExtra("title");
        String desc = i.getStringExtra("desc");
        String path = i.getStringExtra("path");
        Log.i(TAG,"path: " + path);


        File imageFile = new File(path);
        if(imageFile.exists()){
            Bitmap myBitMap = BitmapFactory.decodeFile(path);
            picture = (ImageView)findViewById(R.id.DetailsPictureimageView);
            picture.setImageBitmap(myBitMap);
        }

        imageTitle.setText(title);
        imageDescription.setText(desc);




    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_done:
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);

                break;


        }
    }
}
