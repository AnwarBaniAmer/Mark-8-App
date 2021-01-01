package com.mark8.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.mark8.R;
import com.mark8.storage.LoginUtils;

import static com.mark8.storage.LanguageUtils.loadLocale;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        setContentView(R.layout.activity_splash);

        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, ProductActivity.class);
                startActivity(i);

                // Close this activity
                finish();
                // If user does not log in before, go to LoginActivity
                if(!LoginUtils.getInstance(SplashActivity.this).isLoggedIn()) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
        }, SPLASH_TIME_OUT);
    }
}
