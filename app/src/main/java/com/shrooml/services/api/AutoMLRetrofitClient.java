package com.shrooml.services.api;

import android.content.Context;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shrooml.TokenManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class AutoMLRetrofitClient {

    private static final String TAG = "AutoMLRetrofitClient";
    private static final String BASE_URL = "https://automl.shrooml.duckdns.org/";

    private static AutoMLRetrofitClient instance;
    private static Retrofit retrofit;
    private AutoMLApi api;
    private static volatile String autoMLAuthToken;

    // Gestion du token
    private static TokenManager tokenManager;
    private static Context appContext;



    private AutoMLRetrofitClient(Context context) {
        appContext = context.getApplicationContext();
        try{
            tokenManager = TokenManager.getInstance(appContext);
            Log.d(TAG, "TokenManager initialisé");
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Erreur lors de l'initialisation du TokenManager", e);
            tokenManager = null;
        }
        retrofit = getRetrofit();
        api = retrofit.create(AutoMLApi.class);
    }

    public static synchronized AutoMLRetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new AutoMLRetrofitClient(context);
        }
        return instance;
    }

    private static Retrofit getRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept( Chain chain) throws IOException {
                Request original = chain.request();

                String token = autoMLAuthToken;
                if(token != null && !token.isEmpty()){
                    Log.d(TAG, "Token length: " + token.length());
                    Log.d(TAG, "Token bytes: " + Arrays.toString(token.getBytes()));
                    Log.d(TAG, "Token starts with 'eyJ': " + token.startsWith("eyJ"));

                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                    Log.d(TAG, "Token Bearer ajouté à la requête: " + token.substring(0, Math.min(50, token.length())) + "...");
                    return chain.proceed(request);
                } else {
                    Log.d(TAG, "Aucun token Bearer trouvé");
                    return chain.proceed(original);
                }
            }
        });


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
        if (token != null && !token.isEmpty()) {
            autoMLAuthToken = token;
            Log.d(TAG, "Token Bearer défini: " + token.substring(0, Math.min(50, token.length())) + "...");
        }
    }

    /**
     * Récupérer le token Bearer actuel
     * @return Le token, ou null si non défini
     */
    public String getAuthToken() {
        Log.d(TAG, "getAuthToken:Token Bearer: " + autoMLAuthToken.substring(0, Math.min(50, autoMLAuthToken.length())) + "...");
        return autoMLAuthToken;
    }

    /**
     * Vérifier si l'utilisateur est authentifié
     * @return true si un token valide existe
     */
    public boolean isAuthenticated() {
        String authToken = getAuthToken();
        return authToken != null && !authToken.isEmpty();
    }

    /**
     * Effacer le token Bearer (logout)
     */
    public void clearAuthToken() {
        autoMLAuthToken = null;
        Log.d(TAG, "Token Bearer effacé");
    }

    /**
     * Réinitialiser le client (déconnexion totale)
     */
    public void reset() {
        clearAuthToken();
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
    private String getTokenPreview(String token) {
        if (token == null) return "null";
        if (token.length() <= 30) return token;
        return token.substring(0, 15) + "..." + token.substring(token.length() - 15);
    }

    public AutoMLApi getApi() {
        return api;
    }

}