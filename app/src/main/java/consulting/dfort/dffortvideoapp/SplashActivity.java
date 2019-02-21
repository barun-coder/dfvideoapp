package consulting.dfort.dffortvideoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by pc on 09/01/2019 10:59.
 * MyApplication
 */
public class SplashActivity extends AppCompatActivity {
    private String TAG = "USBCatch";
    private TextView textView;
    private Context context;
    public static int INT_ORIENTATION = ExifInterface.ORIENTATION_UNDEFINED;
    private static final String SHARED_PREFERENCE_NAME = "DFADSPreference";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        context = this;
        String flavor = BuildConfig.FLAVOR;
        if (BuildConfig.FLAVOR.equalsIgnoreCase("left")) {
            ImageView mDefaultIV = (ImageView) findViewById(R.id.default_iv);
            mDefaultIV.setRotation(270);
        }
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                intent = new Intent(context, PlaybackActivity.class);
                startActivity(intent);
                finish();

            }
        }, 500);

    }


}
