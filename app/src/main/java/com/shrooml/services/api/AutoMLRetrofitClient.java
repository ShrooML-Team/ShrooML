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
import java.util.concurrent.TimeUnit;

/**
 * AutoMLRetrofitClient
 *
 * Client Retrofit pour communiquer avec l'API AutoML (Shrooml)
 * Gère:
 * - Singleton pattern (une seule instance)
 * - Token d'authentification Bearer
 * - Logging des requêtes/réponses
 * - Timeouts appropriés
 * - Intercepteurs pour les erreurs
 *
 * Usage:
 * AutoMLApi api = AutoMLRetrofitClient.getInstance().getAutoMLApi();
 * AutoMLRetrofitClient.getInstance().setAuthToken(token);
 */
public class AutoMLRetrofitClient {

    private static final String TAG = "AutoMLRetrofitClient";
    private static final String BASE_URL = "https://automl.shrooml.duckdns.org/";

    // Timeouts (en secondes)
    private static final long CONNECT_TIMEOUT = 30;
    private static final long READ_TIMEOUT = 60;
    private static final long WRITE_TIMEOUT = 60;

    private static AutoMLRetrofitClient instance;
    private AutoMLApi autoMLApi;
    private String authToken;
    private OkHttpClient httpClient;
    private Retrofit retrofit;

    /**
     * Constructeur privé (Singleton)
     * Configure Retrofit avec tous les intercepteurs et options
     */
    private AutoMLRetrofitClient() {
        // 1. Créer le HttpLoggingInterceptor pour voir les requêtes/réponses
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            Log.d(TAG, message);
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 2. Créer un interceptor pour ajouter le token Bearer
        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();

            // Ajouter le token si disponible
            if (authToken != null && !authToken.isEmpty()) {
                Request authorizedRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + authToken)
                        .build();
                Log.d(TAG, "Token Bearer ajouté au header");
                return chain.proceed(authorizedRequest);
            }

            return chain.proceed(originalRequest);
        };

        // 3. Créer un interceptor pour gérer les erreurs
        Interceptor errorInterceptor = chain -> {
            Request request = chain.request();

            try {
                Response response = chain.proceed(request);

                // Log les erreurs HTTP
                if (!response.isSuccessful()) {
                    logHttpError(response);
                }

                return response;
            } catch (IOException e) {
                Log.e(TAG, "Erreur réseau: " + e.getMessage(), e);
                throw e;
            }
        };

        // 4. Configurer OkHttpClient avec tous les intercepteurs
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                // Timeouts
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                // Interceptors (l'ordre est important)
                .addInterceptor(authInterceptor)      // Ajouter le token en premier
                .addInterceptor(errorInterceptor)     // Gérer les erreurs
                .addInterceptor(loggingInterceptor);  // Logger en dernier

        this.httpClient = httpClientBuilder.build();

        // 5. Configurer Gson avec les options appropriées
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // 6. Créer l'instance Retrofit
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(this.httpClient)
                .build();

        // 7. Créer l'interface AutoMLApi
        this.autoMLApi = retrofit.create(AutoMLApi.class);

        Log.i(TAG, "AutoMLRetrofitClient initialisé sur " + BASE_URL);
    }

    /**
     * Obtenir l'instance singleton du client
     * Thread-safe avec synchronized
     */
    public static synchronized AutoMLRetrofitClient getInstance() {
        if (instance == null) {
            instance = new AutoMLRetrofitClient();
            Log.i(TAG, "Nouvelle instance AutoMLRetrofitClient créée");
        }
        return instance;
    }

    /**
     * Obtenir l'interface AutoMLApi pour faire des requêtes
     */
    public AutoMLApi getAutoMLApi() {
        return autoMLApi;
    }

    /**
     * Définir le token d'authentification Bearer
     * À appeler après un login réussi
     *
     * @param token Le JWT token reçu du serveur
     */
    public void setAuthToken(String token) {
        this.authToken = token;
        Log.i(TAG, "Token Bearer défini: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
    }

    /**
     * Obtenir le token actuel
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Vérifier si l'utilisateur est authentifié
     */
    public boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }

    /**
     * Effacer le token (logout)
     */
    public void clearAuthToken() {
        this.authToken = null;
        Log.i(TAG, "Token Bearer effacé (logout)");
    }

    /**
     * Réinitialiser le client (pour les tests ou changement de serveur)
     */
    public static void reset() {
        instance = null;
        Log.i(TAG, "Instance AutoMLRetrofitClient réinitialisée");
    }

    /**
     * Obtenir la base URL
     */
    public String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * Log les erreurs HTTP avec détails
     */
    private void logHttpError(Response response) {
        String errorBody = "";
        try {
            if (response.body() != null) {
                errorBody = response.body().string();
            }
        } catch (IOException e) {
            errorBody = "Impossible de lire le body: " + e.getMessage();
        }

        String logMessage = String.format(
                "Erreur HTTP %d: %s\nURL: %s\nBody: %s",
                response.code(),
                response.message(),
                response.request().url(),
                errorBody
        );

        Log.e(TAG, logMessage);
    }

    /**
     * Obtenir le OkHttpClient (pour des cas avancés)
     */
    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Obtenir l'instance Retrofit (pour des cas avancés)
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }
}