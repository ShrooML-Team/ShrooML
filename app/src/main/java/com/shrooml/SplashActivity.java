package com.shrooml;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SplashActivity extends Activity {

    private static final int SPLASH_DURATION = 10000; // 2 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(null); // ← clé magique

        setContentView(R.layout.activity_splash);

        View logo = findViewById(R.id.logo);
        View title = findViewById(R.id.title);
        View loading = findViewById(R.id.loadingText);

        // Fade logo
        logo.animate()
                .alpha(1f)
                .setDuration(2200)
                .start();

        // Fade titre légèrement après
        title.animate()
                .alpha(1f)
                .setStartDelay(400)
                .setDuration(2200)
                .start();

        // Fade loading encore après
        loading.animate()
                .alpha(1f)
                .setStartDelay(800)
                .setDuration(2200)
                .start();

        new android.os.Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, ShroomLocateActivity.class));
            finish();
        }, 6000);
    }
}