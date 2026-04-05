package com.shrooml.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.shrooml.LoginActivity;
import com.shrooml.R;

/**
 * SplashActivity
 *
 * Écran de démarrage (splash screen)
 * Affiche le logo pendant 2 secondes puis redirige vers LoginActivity
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Rediriger vers LoginActivity après 2 secondes
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}