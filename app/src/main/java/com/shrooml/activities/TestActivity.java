package com.shrooml.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shrooml.R;
import com.shrooml.services.api.AutoMLApi;
import com.shrooml.services.api.AutoMLRetrofitClient;
import com.shrooml.services.api.Requests.FitRequest;
import com.shrooml.services.api.Requests.LoginRequest;
import com.shrooml.services.api.Requests.PredictRequest;
import com.shrooml.services.api.Response.FitResponse;
import com.shrooml.services.api.Response.PredictResponse;
import com.shrooml.services.api.ValidationError;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TestActivity
 *
 * Activity pour tester manuellement tous les endpoints de l'API AutoML
 * Affiche les résultats en temps réel dans un TextView
 *
 * À utiliser pendant le développement pour vérifier que l'API fonctionne correctement
 */
public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";

    private TextView textViewResults;
    private ScrollView scrollView;
    private AutoMLApi api;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Initialiser les vues
        textViewResults = findViewById(R.id.textView_results);
        scrollView = findViewById(R.id.scrollView);

        Button btnLogin = findViewById(R.id.btn_test_login);
        Button btnFit = findViewById(R.id.btn_test_fit);
        Button btnPredict = findViewById(R.id.btn_test_predict);
        Button btnClear = findViewById(R.id.btn_clear_logs);

        // Initialiser l'API
        api = AutoMLRetrofitClient.getInstance().getAutoMLApi();

        addLog("=== TEST ACTIVITY DÉMARRÉE ===");
        addLog("Base URL: " + AutoMLRetrofitClient.getInstance().getBaseUrl());
        addLog("Authentifié: " + AutoMLRetrofitClient.getInstance().isAuthenticated());
        addLog("");

        // Listeners des boutons
        btnLogin.setOnClickListener(v -> testLogin());
        btnFit.setOnClickListener(v -> testFit());
        btnPredict.setOnClickListener(v -> testPredict());
        btnClear.setOnClickListener(v -> clearLogs());
    }

    /**
     * TEST 1: Login
     */
    private void testLogin() {
        addLog("\n>>> TEST LOGIN");
        addLog("Envoi: POST /login");

        LoginRequest request = new LoginRequest("admin", "admin123");
        addLog("  username: admin");
        addLog("  password: admin123");

        api.login(request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                addLog("✓ Réponse reçue - Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body();
                    addLog("✓ Login RÉUSSI!");
                    addLog("  Token: " + token.substring(0, Math.min(50, token.length())) + "...");

                    // Sauvegarder le token
                    AutoMLRetrofitClient.getInstance().setAuthToken(token);
                    addLog("  Token sauvegardé pour les requêtes futures");

                    showToast("Login réussi!");
                } else {
                    addLog("✗ Login ÉCHOUÉ");
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                addLog("✗ Login ERREUR RÉSEAU");
                addLog("  " + t.getMessage());
                showToast("Erreur réseau: " + t.getMessage());
            }
        });
    }

    /**
     * TEST 2: Fit (Entraînement)
     */
    private void testFit() {
        addLog("\n>>> TEST FIT");

        if (!AutoMLRetrofitClient.getInstance().isAuthenticated()) {
            addLog("✗ Pas authentifié! Faites le login d'abord.");
            showToast("Faites le login d'abord!");
            return;
        }

        addLog("Envoi: POST /fit");

        // Préparer les données
        List<Map<String, Object>> X = new ArrayList<>();
        X.add(createMushroomFeatures("convex", "brown", "narrow", "fishy"));
        X.add(createMushroomFeatures("bell", "white", "broad", "almond"));
        X.add(createMushroomFeatures("flat", "brown", "narrow", "none"));
        X.add(createMushroomFeatures("convex", "red", "broad", "anise"));
        X.add(createMushroomFeatures("bell", "yellow", "narrow", "fishy"));

        List<String> y = new ArrayList<>();
        y.add("poisonous");
        y.add("edible");
        y.add("poisonous");
        y.add("edible");
        y.add("poisonous");

        Map<String, Object> params = new HashMap<>();
        params.put("time_budget", 60);
        params.put("metric", "accuracy");

        addLog("  Samples: " + X.size());
        addLog("  Labels: " + String.join(", ", y));
        addLog("  Params: time_budget=60, metric=accuracy");

        FitRequest fitRequest = new FitRequest(X, y, params);

        api.fit(fitRequest).enqueue(new Callback<FitResponse>() {
            @Override
            public void onResponse(Call<FitResponse> call, Response<FitResponse> response) {
                addLog("✓ Réponse reçue - Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    addLog("✓ Fit RÉUSSI!");
                    addLog("  Status: " + status);
                    showToast("Fit réussi! Status: " + status);
                } else if (response.code() == 422) {
                    addLog("✗ Fit ÉCHOUÉ - Erreur de validation");
                    handleValidationError(response);
                } else if (response.code() == 401) {
                    addLog("✗ Fit ÉCHOUÉ - Authentification requise");
                    addLog("  Token expiré ou invalide");
                    AutoMLRetrofitClient.getInstance().clearAuthToken();
                } else {
                    addLog("✗ Fit ÉCHOUÉ");
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<FitResponse> call, Throwable t) {
                addLog("✗ Fit ERREUR RÉSEAU");
                addLog("  " + t.getMessage());
            }
        });
    }

    /**
     * TEST 3: Predict (Prédiction)
     */
    private void testPredict() {
        addLog("\n>>> TEST PREDICT");

        if (!AutoMLRetrofitClient.getInstance().isAuthenticated()) {
            addLog("✗ Pas authentifié! Faites le login d'abord.");
            showToast("Faites le login d'abord!");
            return;
        }

        addLog("Envoi: POST /predict");

        List<Map<String, Object>> X = new ArrayList<>();
        X.add(createMushroomFeatures("convex", "brown", "narrow", "fishy"));
        X.add(createMushroomFeatures("bell", "white", "broad", "almond"));
        X.add(createMushroomFeatures("flat", "red", "narrow", "none"));

        addLog("  Samples: " + X.size());

        PredictRequest predictRequest = new PredictRequest(X);

        api.predict(predictRequest).enqueue(new Callback<PredictResponse>() {
            @Override
            public void onResponse(Call<PredictResponse> call, Response<PredictResponse> response) {
                addLog("✓ Réponse reçue - Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<String> predictions = response.body().getPredictions();
                    addLog("✓ Predict RÉUSSI!");
                    addLog("  Prédictions: " + String.join(", ", predictions));
                    showToast("Prédictions: " + String.join(", ", predictions));
                } else if (response.code() == 422) {
                    addLog("✗ Predict ÉCHOUÉ - Erreur de validation");
                    handleValidationError(response);
                } else if (response.code() == 401) {
                    addLog("✗ Predict ÉCHOUÉ - Authentification requise");
                    AutoMLRetrofitClient.getInstance().clearAuthToken();
                } else {
                    addLog("✗ Predict ÉCHOUÉ");
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<PredictResponse> call, Throwable t) {
                addLog("✗ Predict ERREUR RÉSEAU");
                addLog("  " + t.getMessage());
            }
        });
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
            addLog("  Impossible de lire l'erreur: " + e.getMessage());
            return;
        }

        addLog("  Body: " + errorBody);

        try {
            ValidationError validationError = gson.fromJson(errorBody, ValidationError.class);

            if (validationError.getDetail() != null) {
                addLog("  Détails des erreurs:");
                for (ValidationError.ValidationDetail detail : validationError.getDetail()) {
                    addLog("    - Localisation: " + detail.getLoc());
                    addLog("      Message: " + detail.getMsg());
                    addLog("      Type: " + detail.getType());
                }
            }
        } catch (Exception e) {
            addLog("  Erreur parsing validation error: " + e.getMessage());
        }
    }

    /**
     * Gérer les erreurs HTTP génériques
     */
    private void handleErrorResponse(Response<?> response) {
        String errorBody = "";

        try {
            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
            }
        } catch (IOException e) {
            addLog("  Impossible de lire l'erreur");
            return;
        }

        addLog("  Code: " + response.code());
        addLog("  Message: " + response.message());
        if (!errorBody.isEmpty()) {
            addLog("  Body: " + errorBody);
        }
    }

    /**
     * Créer des features pour un champignon
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
     * Ajouter une ligne de log au TextView
     */
    private void addLog(String message) {
        Log.d(TAG, message);
        textViewResults.append(message + "\n");

        // Scroll vers le bas
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    /**
     * Effacer les logs
     */
    private void clearLogs() {
        textViewResults.setText("");
        addLog("=== LOGS EFFACÉS ===\n");
    }

    /**
     * Afficher un Toast
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}