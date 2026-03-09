package com.shrooml.services.api;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shrooml.services.api.Requests.FitRequest;
import com.shrooml.services.api.Requests.LoginRequest;
import com.shrooml.services.api.Requests.PredictRequest;
import com.shrooml.services.api.Response.ErrorResponse;
import com.shrooml.services.api.Response.FitResponse;
import com.shrooml.services.api.Response.PredictResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExempleUtilisationAutoMLClient
 *
 * Exemples d'utilisation du AutoMLRetrofitClient amélioré
 * Montre comment gérer les erreurs correctement
 */
public class ExempleUtilisationAutoMLClient {

    private static final String TAG = "ExempleAutoML";
    private Activity activity;
    private Gson gson = new Gson();

    public ExempleUtilisationAutoMLClient(Activity activity) {
        this.activity = activity;
    }

    /**
     * EXEMPLE 1: Login avec gestion d'erreurs complète
     */
    public void exemplerLogin() {
        Log.d(TAG, "=== EXEMPLE 1: Login ===");

        AutoMLApi api = AutoMLRetrofitClient.getInstance().getAutoMLApi();
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");

        api.login(loginRequest).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "Login - Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    // ✅ Succès
                    String token = response.body();
                    Log.i(TAG, "Login réussi! Token: " + token.substring(0, 20) + "...");

                    // Sauvegarder le token
                    AutoMLRetrofitClient.getInstance().setAuthToken(token);

                    showToast("Connexion réussie!");

                } else {
                    // ❌ Erreur HTTP
                    handleHttpError(response.code(), response);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // ❌ Erreur réseau
                Log.e(TAG, "Login échoué: " + t.getMessage());
                handleNetworkError(t);
            }
        });
    }

    /**
     * EXEMPLE 2: Fit (entraînement) avec gestion d'erreurs
     */
    public void exempleFit() {
        Log.d(TAG, "=== EXEMPLE 2: Fit ===");

        // Vérifier que l'utilisateur est authentifié
        if (!AutoMLRetrofitClient.getInstance().isAuthenticated()) {
            Log.e(TAG, "Erreur: Pas d'authentification. Connectez-vous d'abord!");
            showToast("Vous devez d'abord vous connecter");
            return;
        }

        // Préparer les données
        List<Map<String, Object>> X = new java.util.ArrayList<>();
        X.add(createMushroomFeatures("convex", "brown", "narrow", "fishy"));
        X.add(createMushroomFeatures("bell", "white", "broad", "almond"));

        List<String> y = new java.util.ArrayList<>();
        y.add("poisonous");
        y.add("edible");

        Map<String, Object> params = new HashMap<>();
        params.put("time_budget", 60);
        params.put("metric", "accuracy");

        FitRequest fitRequest = new FitRequest(X, y, params);

        // Faire la requête
        AutoMLApi api = AutoMLRetrofitClient.getInstance().getAutoMLApi();
        api.fit(fitRequest).enqueue(new Callback<FitResponse>() {
            @Override
            public void onResponse(Call<FitResponse> call, Response<FitResponse> response) {
                Log.d(TAG, "Fit - Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    // ✅ Succès
                    String status = response.body().getStatus();
                    Log.i(TAG, "Fit réussi! Status: " + status);
                    showToast("Entraînement réussi!");

                } else if (response.code() == 422) {
                    // ❌ Erreur de validation
                    handleValidationError(response);

                } else if (response.code() == 401) {
                    // ❌ Erreur authentification (token expiré?)
                    Log.e(TAG, "Token expiré. Reconnexion nécessaire.");
                    showToast("Votre session a expiré. Reconnectez-vous.");
                    AutoMLRetrofitClient.getInstance().clearAuthToken();

                } else {
                    // ❌ Autre erreur HTTP
                    handleHttpError(response.code(), response);
                }
            }

            @Override
            public void onFailure(Call<FitResponse> call, Throwable t) {
                // ❌ Erreur réseau
                Log.e(TAG, "Fit échoué: " + t.getMessage());
                handleNetworkError(t);
            }
        });
    }

    /**
     * EXEMPLE 3: Predict (prédiction) avec gestion d'erreurs
     */
    public void examplePredict() {
        Log.d(TAG, "=== EXEMPLE 3: Predict ===");

        if (!AutoMLRetrofitClient.getInstance().isAuthenticated()) {
            showToast("Vous devez d'abord vous connecter");
            return;
        }

        List<Map<String, Object>> X = new java.util.ArrayList<>();
        X.add(createMushroomFeatures("convex", "brown", "narrow", "fishy"));

        PredictRequest predictRequest = new PredictRequest(X);

        AutoMLApi api = AutoMLRetrofitClient.getInstance().getAutoMLApi();
        api.predict(predictRequest).enqueue(new Callback<PredictResponse>() {
            @Override
            public void onResponse(Call<PredictResponse> call, Response<PredictResponse> response) {
                Log.d(TAG, "Predict - Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    // ✅ Succès
                    List<String> predictions = response.body().getPredictions();
                    Log.i(TAG, "Prédictions: " + predictions);
                    showToast("Prédictions: " + String.join(", ", predictions));

                } else if (response.code() == 422) {
                    handleValidationError(response);
                } else if (response.code() == 401) {
                    showToast("Session expirée. Reconnectez-vous.");
                    AutoMLRetrofitClient.getInstance().clearAuthToken();
                } else {
                    handleHttpError(response.code(), response);
                }
            }

            @Override
            public void onFailure(Call<PredictResponse> call, Throwable t) {
                Log.e(TAG, "Predict échoué: " + t.getMessage());
                handleNetworkError(t);
            }
        });
    }

    /**
     * Gérer les erreurs HTTP (400, 404, 500, etc.)
     */
    private void handleHttpError(int code, Response<?> response) {
        String errorBody = "";

        try {
            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
                Log.e(TAG, "Erreur HTTP " + code + ": " + errorBody);
            }
        } catch (IOException e) {
            Log.e(TAG, "Impossible de lire l'erreur: " + e.getMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse(code, response.message());

        // Afficher un message user-friendly
        String userMessage = errorResponse.getUserFriendlyMessage();
        showToast(userMessage);

        if (errorResponse.isServerError()) {
            Log.e(TAG, "Erreur serveur! Détails: " + errorBody);
        }
    }

    /**
     * Gérer les erreurs de validation (422)
     */
    private void handleValidationError(Response<?> response) {
        String errorBody = "";

        try {
            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
            }
        } catch (IOException e) {
            Log.e(TAG, "Impossible de lire l'erreur: " + e.getMessage());
            return;
        }

        // Essayer de parser comme ValidationError
        try {
            ValidationError validationError = gson.fromJson(errorBody, ValidationError.class);

            if (validationError.getDetail() != null) {
                StringBuilder message = new StringBuilder("Erreurs de validation:\n");

                for (ValidationError.ValidationDetail detail : validationError.getDetail()) {
                    message.append("• ")
                            .append(detail.getLoc())
                            .append(": ")
                            .append(detail.getMsg())
                            .append("\n");
                }

                Log.e(TAG, message.toString());
                showToast("Vérifiez vos données:\n" + message.toString().substring(0, Math.min(100, message.length())));
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur parsing validation error: " + e.getMessage());
            showToast("Erreur de validation. Vérifiez vos données.");
        }
    }

    /**
     * Gérer les erreurs réseau
     */
    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "Erreur réseau: " + t.getMessage(), t);

        if (t instanceof java.net.ConnectException) {
            showToast("Impossible de se connecter au serveur");
        } else if (t instanceof java.net.SocketTimeoutException) {
            showToast("Délai d'attente dépassé. Réessayez.");
        } else {
            showToast("Erreur réseau: " + t.getMessage());
        }
    }

    /**
     * Créer un objet de features pour un champignon
     */
    private Map<String, Object> createMushroomFeatures(
            String capShape, String capColor, String gillSize, String odor) {

        Map<String, Object> features = new HashMap<>();
        features.put("cap_shape", capShape);
        features.put("cap_color", capColor);
        features.put("gill_size", gillSize);
        features.put("odor", odor);

        return features;
    }

    /**
     * Afficher un Toast (message temporaire)
     */
    private void showToast(String message) {
        activity.runOnUiThread(() ->
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        );
    }
}