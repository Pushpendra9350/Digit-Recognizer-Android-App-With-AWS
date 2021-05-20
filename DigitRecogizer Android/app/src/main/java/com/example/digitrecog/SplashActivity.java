package com.example.digitrecog;
/**
 * This is an Splash Activity names as SplashActivity
 * In this activity we will show you a 2.5 seconds of screen
 * which is a starting point of the application
 *
 * @Author: Pushpendra Kumar
 * @since 10-05-2021
 */

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

// This is the main activity class
public class SplashActivity extends AppCompatActivity {
    private  static int SPLASH_TIME_OUT =2500;

    /**
     * @name onCreate function will automatically called when
     * this activity will starts
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // These new two lines will hide the Navigation bar we don't need it
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // This line set the content of the layout to the activity
        setContentView(R.layout.activity_splash);

        // This is a thread to wait this activity for 2500 milliSeconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This will change/intent you from one activity to next activity
                Intent intent = new Intent(SplashActivity.this,PortIpActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}

