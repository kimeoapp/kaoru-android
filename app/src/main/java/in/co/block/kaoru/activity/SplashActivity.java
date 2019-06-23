package in.co.block.kaoru.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import in.co.block.kaoru.R;
import in.co.block.kaoru.utils.AppPreferenceHelper;
import in.co.block.kaoru.utils.Constants;

public class SplashActivity extends AppCompatActivity implements Runnable{

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        handler = new Handler();
        handler.postDelayed(this, Constants.SPLASH_TIME);
    }
    @Override
    public void run() {
        if (AppPreferenceHelper.getInstance(SplashActivity.this).getBoolean( Constants.PrefKey.IS_LOGGED_IN, false)) {

            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        }
        else{
            startActivity(new Intent(SplashActivity.this, UserOnboardingActivity.class));
        }
        finish();
    }
}

