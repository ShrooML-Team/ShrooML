package com.shrooml;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.shrooml.services.OAuthService;
import com.shrooml.services.api.ShroomLocRetrofitClient;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DURATION = 6000; // 6 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() appelé");

        try {
            getWindow().setBackgroundDrawable(null); // ← clé magique
            Log.d(TAG, "Background drawable défini");

            setContentView(R.layout.activity_splash);
            Log.d(TAG, "Layout activity_splash chargé");

            View logo = findViewById(R.id.logo);
            View title = findViewById(R.id.title);
            View loading = findViewById(R.id.loadingText);
            
            Log.d(TAG, "Vues trouvées - logo: " + (logo != null) + ", title: " + (title != null) + ", loading: " + (loading != null));

            // Fade logo
            if (logo != null) {
                logo.animate()
                        .alpha(1f)
                        .setDuration(2200)
                        .start();
                Log.d(TAG, "Animation logo démarrée");
            }

            // Fade titre légèrement après
            if (title != null) {
                title.animate()
                        .alpha(1f)
                        .setStartDelay(400)
                        .setDuration(2200)
                        .start();
                Log.d(TAG, "Animation title démarrée");
            }

            // Fade loading encore après
            if (loading != null) {
                loading.animate()
                        .alpha(1f)
                        .setStartDelay(800)
                        .setDuration(2200)
                        .start();
                Log.d(TAG, "Animation loading démarrée");
            }

            Log.d(TAG, "Programmation de la navigation dans " + SPLASH_DURATION + "ms");
            new android.os.Handler().postDelayed(() -> {
                Log.d(TAG, "Délai terminé, appel de checkAuthenticationAndNavigate()");
                checkAuthenticationAndNavigate();
            }, SPLASH_DURATION);

        } catch (Exception e) {
            Log.e(TAG, "ERREUR dans onCreate()", e);
            e.printStackTrace();
            // Naviguer quand même
            checkAuthenticationAndNavigate();
        }
    }

    private void checkAuthenticationAndNavigate() {
        Log.d(TAG, "checkAuthenticationAndNavigate() appelé");
        try {
            Log.d(TAG, "Tentative d'obtention du TokenManager...");
            TokenManager tokenManager = TokenManager.getInstance(this);
            Log.d(TAG, "TokenManager obtenu avec succès");
            
            boolean isValid = tokenManager.isTokenValid();
            Log.d(TAG, "isTokenValid() retourné: " + isValid);
            
            if (isValid) {
                Log.d(TAG, "Token valide -> Navigation vers ChoiceIdentifyActivity");
                new OAuthService().login("admin", "password123", new OAuthService.OAuthCallback() {
                    @Override
                    public void onSuccess(String token) {
                        ShroomLocRetrofitClient.setToken(token);
                        startActivity(new Intent(SplashActivity.this, ChoiceIdentifyActivity.class));
                        Log.d(TAG, "Appel de finish()");
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });

            } else {
                Log.d(TAG, "Pas de token valide -> Navigation vers LoginActivity");
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                Log.d(TAG, "Appel de finish()");
                finish();
            }
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "ERREUR GeneralSecurityException dans checkAuthenticationAndNavigate()", e);
            e.printStackTrace();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            Log.d(TAG, "Appel de finish()");
            finish();
        } catch (IOException e) {
            Log.e(TAG, "ERREUR IOException dans checkAuthenticationAndNavigate()", e);
            e.printStackTrace();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            Log.d(TAG, "Appel de finish()");
            finish();
        } catch (Exception e) {
            Log.e(TAG, "ERREUR INATTENDUE dans checkAuthenticationAndNavigate()", e);
            e.printStackTrace();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            Log.d(TAG, "Appel de finish()");
            finish();
        }
    }
}