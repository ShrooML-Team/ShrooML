package com.shrooml.services.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * AutoMLRetrofitClient - Avec OAuth2 Bearer Token
 *
 * Gère automatiquement l'authentification Bearer pour tous les appels API.
 *
 * Utilisation:
 *   // Après login
 *   AutoMLRetrofitClient.getInstance().setAuthToken(token);
 *
 *   // Avant les appels /fit, /predict, /eval
 *   if (AutoMLRetrofitClient.getInstance().isAuthenticated()) {
 *       // Les headers Bearer sont ajoutés automatiquement!
 *   }
 */
public class AutoMLRetrofitClient {

    private static final String TAG = "AutoMLRetrofitClient";
    private static final String BASE_URL = "https://automl.shrooml.duckdns.org/";

    private static AutoMLRetrofitClient instance;
    private static Retrofit retrofit;
    private AutoMLApi api;

    // Gestion du token
    private static String authToken = null;

    private AutoMLRetrofitClient() {
        retrofit = getRetrofit();
        api = retrofit.create(AutoMLApi.class);
    }

    public static synchronized AutoMLRetrofitClient getInstance() {
        if (instance == null) {
            instance = new AutoMLRetrofitClient();
        }
        return instance;
    }

    private static Retrofit getRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // ============================================================
        // Interceptor Bearer Token (OAuth2)
        // ============================================================
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                // Si on a un token, ajouter le header Bearer
                if (authToken != null && !authToken.isEmpty()) {
                    Request requestWithToken = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + authToken)
                            .build();

                    Log.d(TAG, "Header Bearer ajouté automatiquement");
                    return chain.proceed(requestWithToken);
                }

                // Sinon, envoyer la requête sans authentification
                return chain.proceed(originalRequest);
            }
        });

        // Support des redirects
        builder.followRedirects(true);
        builder.followSslRedirects(true);

        // Timeouts
        builder.connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS);
        builder.readTimeout(60, java.util.concurrent.TimeUnit.SECONDS);
        builder.writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS);

        // HTTP Logging pour debug
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            Log.d(TAG, message);
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);

        // JSON Parser
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * Obtenir l'instance de l'API
     */
    public AutoMLApi getAutoMLApi() {
        return api;
    }

    /**
     * Obtenir l'URL de base
     */
    public String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * ============================================================
     * GESTION DU TOKEN BEARER
     * ============================================================
     */

    /**
     * Définir le token Bearer (après login)
     * @param token Le token JWT reçu du serveur
     */
    public void setAuthToken(String token) {
        this.authToken = token;
        Log.d(TAG, "Token Bearer défini: " + (token != null ? token.substring(0, Math.min(50, token.length())) + "..." : "null"));
    }

    /**
     * Récupérer le token Bearer actuel
     * @return Le token, ou null si non défini
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Vérifier si l'utilisateur est authentifié
     * @return true si un token valide existe
     */
    public boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }

    /**
     * Effacer le token Bearer (logout)
     */
    public void clearAuthToken() {
        this.authToken = null;
        Log.d(TAG, "Token Bearer effacé");
    }

    /**
     * Réinitialiser le client (déconnexion totale)
     */
    public void reset() {
        instance = null;
        retrofit = null;
        Log.d(TAG, "AutoMLRetrofitClient réinitialisé");
    }

    /**
     * ============================================================
     * DEBUG
     * ============================================================
     */

    /**
     * Afficher l'état du client (pour debug)
     */
    public void printStatus() {
        Log.d(TAG, "=== AutoMLRetrofitClient Status ===");
        Log.d(TAG, "Base URL: " + BASE_URL);
        Log.d(TAG, "Authentifié: " + isAuthenticated());
        Log.d(TAG, "Token: " + (authToken != null ? authToken.substring(0, Math.min(50, authToken.length())) + "..." : "null"));
        Log.d(TAG, "====================================");
    }
}