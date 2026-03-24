package com.shrooml.activities;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.shrooml.R;
import com.shrooml.TokenManager;
import com.shrooml.services.api.AutoMLApi;
import com.shrooml.services.api.AutoMLRetrofitClient;
import com.shrooml.services.api.FitResponse;
import com.shrooml.services.api.Requests.*;
import com.shrooml.services.api.Response.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class TestActivity extends AppCompatActivity {

    private TextView resultText;
    private TextView authStatus;
    private AutoMLApi autoMLApi;
    private AutoMLRetrofitClient apiClient;
    private TokenManager tokenManager;

    private static final String[] COLUMN_NAMES = {
            "cap-shape", "cap-surface", "cap-color", "bruises", "odor",
            "gill-attachment", "gill-spacing", "gill-size", "gill-color", "stalk-shape",
            "stalk-root", "stalk-surface-above-ring", "stalk-surface-below-ring",
            "stalk-color-above-ring", "stalk-color-below-ring", "veil-type", "veil-color",
            "ring-number", "ring-type", "spore-print-color", "population", "habitat"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initViews();
        initApiClient();
        updateAuthStatus();
    }

    private void initViews() {
        resultText = findViewById(R.id.result_text);
        authStatus = findViewById(R.id.auth_status);
        resultText.setMovementMethod(new ScrollingMovementMethod());

        // Tests Authentification
        findViewById(R.id.btn_test_login).setOnClickListener(v -> testLogin());
        findViewById(R.id.btn_test_token_direct).setOnClickListener(v -> testTokenDirect());
        findViewById(R.id.btn_test_token_manager).setOnClickListener(v -> testTokenManagerIntegrity());

        // Tests API
        findViewById(R.id.btn_fit).setOnClickListener(v -> testFit());
        findViewById(R.id.btn_predict).setOnClickListener(v -> testPredict());
        findViewById(R.id.btn_eval).setOnClickListener(v -> testEval());

        // Debug
        findViewById(R.id.btn_clear_token).setOnClickListener(v -> clearToken());
        findViewById(R.id.btn_print_status).setOnClickListener(v -> printStatus());
    }

    private void initApiClient() {
        try {
            apiClient = AutoMLRetrofitClient.getInstance(this);
            autoMLApi = apiClient.getAutoMLApi();
            tokenManager = TokenManager.getInstance(this);
            appendResult("✅ API Client initialisé\n");
        } catch (GeneralSecurityException | IOException e) {
            appendResult("❌ Erreur initialisation: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void updateAuthStatus() {
        boolean isAuth = apiClient.isAuthenticated();
        String token = apiClient.getAuthToken();
        String status = isAuth ? "✅ Authentifié" : "❌ Non authentifié";
        if (isAuth && token != null) {
            status += "\nToken: " + getTokenPreview(token);
        }
        authStatus.setText("🔐 Statut: " + status);
    }

    // ==================== TESTS AUTHENTIFICATION ====================

    private void testLogin() {
        appendResult("\n🔐 TEST LOGIN\n");
        appendResult("Tentative de connexion avec admin/admin123...\n");

        LoginRequest request = new LoginRequest("admin", "admin123");
        Call<LoginResponse> call = autoMLApi.login(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    appendResult("✅ Login réussi !\n");
                    appendResult("Token reçu: " + getTokenPreview(token) + "\n");
                    appendResult("Longueur token: " + token.length() + "\n");

                    // Sauvegarder le token
                    try {
                        tokenManager.saveToken(token, 1, "admin");
                        apiClient.setAuthToken(token);
                        appendResult("✅ Token sauvegardé dans TokenManager\n");
                        updateAuthStatus();
                    } catch (Exception e) {
                        appendResult("❌ Erreur sauvegarde token: " + e.getMessage() + "\n");
                    }
                } else {
                    appendResult("❌ Login échoué: " + response.code() + "\n");
                    try {
                        if (response.errorBody() != null) {
                            appendResult("Erreur: " + response.errorBody().string() + "\n");
                        }
                    } catch (IOException e) {
                        appendResult("Erreur lecture erreur: " + e.getMessage() + "\n");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                appendResult("❌ Erreur réseau: " + t.getMessage() + "\n");
            }
        });
    }

    private void testTokenDirect() {
        appendResult("\n🔑 TEST TOKEN DIRECT (hardcoded)\n");

        // Token valide obtenu précédemment
        String hardcodedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc3MzY2NDQxN30.wtbMkjEUeDiic5hYXD4N9U1XbvJs-yrQnnUR1zqZrCY";

        appendResult("Token hardcoded: " + getTokenPreview(hardcodedToken) + "\n");
        apiClient.setAuthToken(hardcodedToken);
        updateAuthStatus();

        // Tester immédiatement avec fit
        appendResult("Test de /fit avec token hardcoded...\n");
        performFit();
    }

    private void testTokenManagerIntegrity() {
        appendResult("\n🔍 TEST INTÉGRITÉ TOKENMANAGER\n");

        try {
            String testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test123";
            appendResult("Token de test: " + testToken + "\n");
            appendResult("Longueur test: " + testToken.length() + "\n");

            // Sauvegarder
            tokenManager.saveToken(testToken, 1, "test");
            appendResult("✅ Token sauvegardé\n");

            // Récupérer
            String retrieved = tokenManager.getToken();
            appendResult("Token récupéré: " + retrieved + "\n");
            appendResult("Longueur récupéré: " + (retrieved != null ? retrieved.length() : 0) + "\n");

            // Comparer
            boolean isSame = testToken.equals(retrieved);
            appendResult("Tokens identiques: " + (isSame ? "✅ OUI" : "❌ NON") + "\n");

            if (!isSame && retrieved != null) {
                appendResult("\n⚠️ DIFFÉRENCES DÉTECTÉES:\n");
                appendResult("Original bytes: " + bytesToHex(testToken.getBytes()) + "\n");
                appendResult("Retrieved bytes: " + bytesToHex(retrieved.getBytes()) + "\n");
            }

            // Nettoyer
            tokenManager.clearToken();
            appendResult("✅ Token nettoyé\n");

        } catch (Exception e) {
            appendResult("❌ Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    // ==================== TESTS API ====================

    private void testFit() {
        if (!apiClient.isAuthenticated()) {
            appendResult("❌ Non authentifié. Testez d'abord le login.\n");
            Toast.makeText(this, "Veuillez d'abord vous authentifier", Toast.LENGTH_SHORT).show();
            return;
        }
        performFit();
    }

    private void performFit() {
        appendResult("\n🚀 TRAIN MODEL (/fit)\n");
        appendResult("Préparation des données...\n");

        List<Map<String, Integer>> trainingSamples = createTrainingSamples();
        List<Integer> labels = createLabels();

        appendResult("Samples: " + trainingSamples.size() + "\n");
        appendResult("Labels: " + labels.size() + "\n");

        FitRequest request = new FitRequest(trainingSamples, labels, new HashMap<>());
        Call<FitResponse> call = autoMLApi.fit(request);

        appendResult("Envoi de la requête...\n");

        call.enqueue(new Callback<FitResponse>() {
            @Override
            public void onResponse(Call<FitResponse> call, Response<FitResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    appendResult("✅ FIT réussi !\n");
                    appendResult("Status: " + status + "\n");
                    Toast.makeText(TestActivity.this, "Model trained!", Toast.LENGTH_SHORT).show();
                } else {
                    appendResult("❌ FIT échoué: " + response.code() + "\n");
                    try {
                        if (response.errorBody() != null) {
                            appendResult("Erreur: " + response.errorBody().string() + "\n");
                        }
                    } catch (IOException e) {
                        appendResult("Erreur lecture: " + e.getMessage() + "\n");
                    }
                }
            }

            @Override
            public void onFailure(Call<FitResponse> call, Throwable t) {
                appendResult("❌ Erreur réseau: " + t.getMessage() + "\n");
                t.printStackTrace();
            }
        });
    }

    private void testPredict() {
        if (!apiClient.isAuthenticated()) {
            appendResult("❌ Non authentifié. Testez d'abord le login.\n");
            Toast.makeText(this, "Veuillez d'abord vous authentifier", Toast.LENGTH_SHORT).show();
            return;
        }

        appendResult("\n🔮 PREDICTION (/predict)\n");

        List<Map<String, Integer>> testSamples = new ArrayList<>();
        testSamples.add(createMushroomSample(
                5, 2, 4, 1, 6, 1, 0, 1, 4, 0, 3, 2, 2, 7, 7, 0, 2, 1, 4, 2, 3, 5
        ));
        testSamples.add(createMushroomSample(
                5, 2, 9, 1, 0, 1, 0, 0, 4, 0, 2, 2, 2, 7, 7, 0, 2, 1, 4, 3, 2, 1
        ));

        PredictRequest request = new PredictRequest(testSamples);
        Call<PredictResponse> call = autoMLApi.predict(request);

        call.enqueue(new Callback<PredictResponse>() {
            @Override
            public void onResponse(Call<PredictResponse> call, Response<PredictResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Integer> predictions = response.body().getPredictions();
                    appendResult("✅ PRÉDICTIONS:\n");
                    for (int i = 0; i < predictions.size(); i++) {
                        int pred = predictions.get(i);
                        String type = (pred == 0) ? "🍄 Edible" : "☠️ Poisonous";
                        appendResult("Sample " + (i+1) + ": " + type + "\n");
                    }
                    Toast.makeText(TestActivity.this, "Prediction successful!", Toast.LENGTH_SHORT).show();
                } else {
                    appendResult("❌ PREDICT échoué: " + response.code() + "\n");
                }
            }

            @Override
            public void onFailure(Call<PredictResponse> call, Throwable t) {
                appendResult("❌ Erreur réseau: " + t.getMessage() + "\n");
            }
        });
    }

    private void testEval() {
        if (!apiClient.isAuthenticated()) {
            appendResult("❌ Non authentifié. Testez d'abord le login.\n");
            Toast.makeText(this, "Veuillez d'abord vous authentifier", Toast.LENGTH_SHORT).show();
            return;
        }

        appendResult("\n📊 EVALUATION (/eval)\n");
        appendResult("Fonctionnalité à implémenter selon votre API\n");
        // Implémentez selon votre endpoint /eval
    }

    // ==================== DEBUG ====================

    private void clearToken() {
        appendResult("\n🗑️ CLEAR TOKEN\n");
        tokenManager.clearToken();
        apiClient.clearAuthToken();
        updateAuthStatus();
        appendResult("✅ Token effacé\n");
        Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show();
    }

    private void printStatus() {
        appendResult("\n📊 STATUS\n");
        appendResult("Authentifié: " + apiClient.isAuthenticated() + "\n");
        String token = apiClient.getAuthToken();
        appendResult("Token présent: " + (token != null) + "\n");
        if (token != null) {
            appendResult("Token preview: " + getTokenPreview(token) + "\n");
            appendResult("Token length: " + token.length() + "\n");
        }
        apiClient.printStatus();
    }

    // ==================== UTILITAIRES ====================

    private List<Map<String, Integer>> createTrainingSamples() {
        List<Map<String, Integer>> samples = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            samples.add(createMushroomSample(5, 2, 4, 1, 6, 1, 0, 1, 4, 0, 3, 2, 2, 7, 7, 0, 2, 1, 4, 2, 3, 5));
        }
        for (int i = 0; i < 25; i++) {
            samples.add(createMushroomSample(5, 2, 9, 1, 0, 1, 0, 0, 4, 0, 2, 2, 2, 7, 7, 0, 2, 1, 4, 3, 2, 1));
        }
        return samples;
    }

    private List<Integer> createLabels() {
        List<Integer> labels = new ArrayList<>();
        for (int i = 0; i < 25; i++) labels.add(0);
        for (int i = 0; i < 25; i++) labels.add(1);
        return labels;
    }

    private Map<String, Integer> createMushroomSample(int... values) {
        Map<String, Integer> sample = new LinkedHashMap<>();
        for (int i = 0; i < COLUMN_NAMES.length && i < values.length; i++) {
            sample.put(COLUMN_NAMES[i], values[i]);
        }
        return sample;
    }

    private String getTokenPreview(String token) {
        if (token == null) return "null";
        if (token.length() <= 30) return token;
        return token.substring(0, 15) + "..." + token.substring(token.length() - 15);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, 50); i++) {
            sb.append(String.format("%02x ", bytes[i]));
        }
        return sb.toString();
    }

    private void appendResult(String text) {
        runOnUiThread(() -> {
            resultText.append(text);
            // Auto-scroll vers le bas
            final int scrollAmount = resultText.getLayout() != null ?
                    resultText.getLayout().getLineTop(resultText.getLineCount()) - resultText.getHeight() : 0;
            if (scrollAmount > 0) {
                resultText.scrollTo(0, scrollAmount);
            }
        });
    }
}