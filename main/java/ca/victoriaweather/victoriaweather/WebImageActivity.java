package ca.victoriaweather.victoriaweather;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class WebImageActivity extends AppCompatActivity implements BitmapRetainerFragment.PostExecuteCallback {
    public static final String ARG_TITLE = "ARG_WEB_IMAGE_ACTIVITY_TITLE";
    public static final String ARG_URL = "ARG_WEB_IMAGE_ACTIVITY_URL";
    private static final String ERR_KEY = "WEB_IMAGE_ACTIVITY_ERR";

    private boolean mErr = false;
    private BitmapRetainerFragment mBitmapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_image);

        if(savedInstanceState == null) {
            Bundle args = getIntent().getExtras();
            mBitmapFragment = new BitmapRetainerFragment();
            mBitmapFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(mBitmapFragment, BitmapRetainerFragment.FM_TAG).commit();
            setTitle(args.getString(ARG_TITLE));
        } else {
            mErr = savedInstanceState.getBoolean(ERR_KEY);
            mBitmapFragment = (BitmapRetainerFragment)getSupportFragmentManager().findFragmentByTag(BitmapRetainerFragment.FM_TAG);
            try {
                if (mBitmapFragment.getBitmap() != null) {
                    ImageView imageView = (ImageView) findViewById(R.id.web_image_main);
                    imageView.setImageBitmap(mBitmapFragment.getBitmap());
                }
            } catch (NullPointerException e) {
                Log.d("WebImageActivity", "onCreate() after configuration change there was no BitmapRetainerFragment found by the manager");
            }
            setTitle(savedInstanceState.getString(ARG_TITLE));
        }

        if(!mErr) {
            TextView errText = (TextView) findViewById(R.id.web_image_err_text);
            errText.setVisibility(View.GONE);
        }
    }

    //TODO see if meta can be changed based on settings to allow users to avoid dialogs


    @Override
    protected void onStart() {
        super.onStart();
        // In order to not be too narrow, set the window size based on the screen resolution:
        final int screen_width = getResources().getDisplayMetrics().widthPixels;
        final int screen_height = getResources().getDisplayMetrics().heightPixels;
        int new_window_width = screen_height;
        int new_window_height = screen_width;
        if(new_window_width < new_window_height) {
            new_window_height = new_window_width;
        } else {
            new_window_width = new_window_height;
        }
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.width = Math.max(layout.width, new_window_width);
        layout.height = Math.max(layout.height, new_window_height);
        getWindow().setAttributes(layout);
    }

    @Override
    public void onSaveInstanceState(Bundle outState ) {
        outState.putString(ARG_TITLE, (String)getTitle());
        outState.putBoolean(ERR_KEY, mErr);
        super.onSaveInstanceState(outState);
    }

    public void onPostExecute() {
        ImageView imageView = (ImageView) findViewById(R.id.web_image_main);
        Bitmap bmp = mBitmapFragment.getBitmap();
        if(bmp != null) {
            imageView.setImageBitmap(bmp);
        } else {
            TextView errText = (TextView) findViewById(R.id.web_image_err_text);
            mErr = true;
            errText.setVisibility(View.VISIBLE);
        }
    }

    //Activity -> dialog code thanks to stackoverflow user Kyle Clegg (http://stackoverflow.com/questions/5776851/load-image-from-url)
    //http://stackoverflow.com/questions/25856303/narrow-dialog-when-starting-new-activity-in-theme-holo-dialogwhenlarge-mode
}
