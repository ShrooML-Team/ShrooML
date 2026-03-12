package com.shrooml.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shrooml.R;
import com.shrooml.services.api.AutoMLRetrofitClient;
import com.shrooml.services.api.Requests.PredictRequest;
import com.shrooml.services.api.Response.PredictResponse;
import com.shrooml.services.encoding.MushroomEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TestActivity_PREDICT_AVEC_LOGIN - Test /predict rapidement
 *
 * ✅ Avec bouton LOGIN intégré!
 *
 * Flux:
 * 1. Si pas authentifié → Affiche "Go to Login"
 * 2. Si authentifié → Affiche "Test Predict 1" et "Test Predict 2"
 */
public class TestActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnTestPredict1;
    private Button btnTestPredict2;
    private Button btnCheckAuth;
    private Button btnClearLogs;
    private TextView resultTextView;
    private static final String TAG = "TestActivity_PREDICT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btnLogin = findViewById(R.id.btn_test_login);
        btnTestPredict1 = findViewById(R.id.btn_test_fit);
        btnTestPredict2 = findViewById(R.id.btn_test_predict);
        btnClearLogs = findViewById(R.id.btn_clear_logs);
        resultTextView = findViewById(R.id.textView_results);

        // Initialiser
        initializeUI();

        resultTextView.setText("🍄 TEST PREDICT RAPIDE\n");
        resultTextView.append("═════════════════════\n\n");
        resultTextView.append("Vérification de l'authentification...\n");

        checkAuthentication();
    }

    /**
     * Vérifier l'authentification et ajuster l'UI
     */
    private void checkAuthentication() {
        boolean isAuth = AutoMLRetrofitClient.getInstance().isAuthenticated();
        String token = AutoMLRetrofitClient.getInstance().getAuthToken();

        Log.d(TAG, "isAuthenticated: " + isAuth);
        Log.d(TAG, "token: " + (token != null ? token.substring(0, Math.min(50, token.length())) + "..." : "NULL"));

        if (!isAuth || token == null) {
            // ❌ Pas authentifié
            resultTextView.setText("🍄 TEST PREDICT RAPIDE\n");
            resultTextView.append("═════════════════════\n\n");
            resultTextView.append("❌ STATUT: PAS AUTHENTIFIÉ\n");
            resultTextView.append("\n");
            resultTextView.append("SOLUTION:\n");
            resultTextView.append("1. Clique 'Go to Login'\n");
            resultTextView.append("2. Login avec admin/admin123\n");
            resultTextView.append("3. Reviens ici\n");
            resultTextView.append("4. Clique 'Test Predict 1' ou 2\n");

            // Afficher le bouton Login
            btnLogin.setText("Go to Login");
            btnLogin.setEnabled(true);

            // Masquer les boutons de test
            btnTestPredict1.setEnabled(false);
            btnTestPredict2.setEnabled(false);
            btnTestPredict1.setAlpha(0.5f);
            btnTestPredict2.setAlpha(0.5f);

        } else {
            // ✅ Authentifié
            resultTextView.setText("🍄 TEST PREDICT RAPIDE\n");
            resultTextView.append("═════════════════════\n\n");
            resultTextView.append("✅ STATUT: AUTHENTIFIÉ\n");
            resultTextView.append("\n");
            resultTextView.append("Token: " + token.substring(0, 50) + "...\n");
            resultTextView.append("\n");
            resultTextView.append("Clique sur un bouton de test:\n");
            resultTextView.append("• Test Predict 1 (EDIBLE)\n");
            resultTextView.append("• Test Predict 2 (POISONOUS)\n");
            resultTextView.append("\n");

            // Afficher les boutons de test
            btnLogin.setText("Already Logged In");
            btnLogin.setEnabled(false);

            btnTestPredict1.setEnabled(true);
            btnTestPredict2.setEnabled(true);
            btnTestPredict1.setAlpha(1.0f);
            btnTestPredict2.setAlpha(1.0f);
        }
    }

    /**
     * Initialiser les boutons
     */
    private void initializeUI() {
        btnLogin.setOnClickListener(v -> goToLoginActivity());
        btnTestPredict1.setText("Test Predict 1 (EDIBLE)");
        btnTestPredict1.setOnClickListener(v -> testPredictSerie1());

        btnTestPredict2.setText("Test Predict 2 (POISONOUS)");
        btnTestPredict2.setOnClickListener(v -> testPredictSerie2());

        btnClearLogs.setOnClickListener(v -> {
            resultTextView.setText("Logs effacés.\n");
            checkAuthentication();
        });
    }

    /**
     * Aller à LoginActivity
     */
    private void goToLoginActivity() {
        Log.d(TAG, "Redirection vers LoginActivity");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Tester /predict avec SÉRIE 1 (EDIBLE)
     */
    private void testPredictSerie1() {
        resultTextView.setText("⏳ Test Predict SÉRIE 1 (EDIBLE) en cours...\n\n");

        Map<String, Object> sample = new HashMap<>();

        sample.put("cap-shape", MushroomEncoder.encodeCapShape("x"));
        sample.put("cap-surface", MushroomEncoder.encodeCapSurface("s"));
        sample.put("cap-color", MushroomEncoder.encodeCapColor("n"));
        sample.put("bruises", MushroomEncoder.encodeBruises("t"));
        sample.put("odor", MushroomEncoder.encodeOdor("n"));
        sample.put("gill-attachment", MushroomEncoder.encodeGillAttachment("d"));
        sample.put("gill-spacing", MushroomEncoder.encodeGillSpacing("c"));
        sample.put("gill-size", MushroomEncoder.encodeGillSize("n"));
        sample.put("gill-color", MushroomEncoder.encodeGillColor("g"));
        sample.put("stalk-shape", MushroomEncoder.encodeStalkShape("e"));
        sample.put("stalk-root", MushroomEncoder.encodeStalkRoot("e"));
        sample.put("stalk-surface-above-ring", MushroomEncoder.encodeStalkSurfaceAboveRing("k"));
        sample.put("stalk-surface-below-ring", MushroomEncoder.encodeStalkSurfaceBelowRing("k"));
        sample.put("stalk-color-above-ring", MushroomEncoder.encodeStalkColorAboveRing("w"));
        sample.put("stalk-color-below-ring", MushroomEncoder.encodeStalkColorBelowRing("w"));
        sample.put("veil-type", MushroomEncoder.encodeVeilType("p"));
        sample.put("veil-color", MushroomEncoder.encodeVeilColor("w"));
        sample.put("ring-number", MushroomEncoder.encodeRingNumber("o"));
        sample.put("ring-type", MushroomEncoder.encodeRingType("p"));
        sample.put("spore-print-color", MushroomEncoder.encodeSporeColor("b"));
        sample.put("population", MushroomEncoder.encodePopulation("s"));
        sample.put("habitat", MushroomEncoder.encodeHabitat("w"));

        callPredict(sample, "SÉRIE 1 (EDIBLE)");
    }

    /**
     * Tester /predict avec SÉRIE 2 (POISONOUS)
     */
    private void testPredictSerie2() {
        resultTextView.setText("⏳ Test Predict SÉRIE 2 (POISONOUS) en cours...\n\n");

        Map<String, Object> sample = new HashMap<>();

        sample.put("cap-shape", MushroomEncoder.encodeCapShape("x"));
        sample.put("cap-surface", MushroomEncoder.encodeCapSurface("y"));
        sample.put("cap-color", MushroomEncoder.encodeCapColor("b"));
        sample.put("bruises", MushroomEncoder.encodeBruises("t"));
        sample.put("odor", MushroomEncoder.encodeOdor("f"));
        sample.put("gill-attachment", MushroomEncoder.encodeGillAttachment("f"));
        sample.put("gill-spacing", MushroomEncoder.encodeGillSpacing("c"));
        sample.put("gill-size", MushroomEncoder.encodeGillSize("n"));
        sample.put("gill-color", MushroomEncoder.encodeGillColor("l"));
        sample.put("stalk-shape", MushroomEncoder.encodeStalkShape("t"));
        sample.put("stalk-root", MushroomEncoder.encodeStalkRoot("u"));
        sample.put("stalk-surface-above-ring", MushroomEncoder.encodeStalkSurfaceAboveRing("f"));
        sample.put("stalk-surface-below-ring", MushroomEncoder.encodeStalkSurfaceBelowRing("f"));
        sample.put("stalk-color-above-ring", MushroomEncoder.encodeStalkColorAboveRing("b"));
        sample.put("stalk-color-below-ring", MushroomEncoder.encodeStalkColorBelowRing("b"));
        sample.put("veil-type", MushroomEncoder.encodeVeilType("u"));
        sample.put("veil-color", MushroomEncoder.encodeVeilColor("o"));
        sample.put("ring-number", MushroomEncoder.encodeRingNumber("n"));
        sample.put("ring-type", MushroomEncoder.encodeRingType("e"));
        sample.put("spore-print-color", MushroomEncoder.encodeSporeColor("k"));
        sample.put("population", MushroomEncoder.encodePopulation("s"));
        sample.put("habitat", MushroomEncoder.encodeHabitat("c"));

        callPredict(sample, "SÉRIE 2 (POISONOUS)");
    }

    /**
     * Appeler l'API /predict
     */
    private void callPredict(Map<String, Object> sample, String seriesName) {
        if (!AutoMLRetrofitClient.getInstance().isAuthenticated()) {
            resultTextView.setText("❌ PAS AUTHENTIFIÉ!\n");
            resultTextView.append("Clique 'Go to Login' d'abord.\n");
            Toast.makeText(this, "Pas authentifié!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer la liste X
        List<Map<String, Object>> X = new ArrayList<>();
        X.add(sample);

        // Créer la requête
        PredictRequest request = new PredictRequest(X, new ArrayList<>());

        Log.d(TAG, "Appel /predict avec " + seriesName);

        // Appeler l'API
        Call<PredictResponse> call = AutoMLRetrofitClient.getInstance()
                .getAutoMLApi()
                .predict(request);

        call.enqueue(new Callback<PredictResponse>() {
            @Override
            public void onResponse(Call<PredictResponse> call, Response<PredictResponse> response) {
                Log.d(TAG, "Réponse: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<String> predictions = response.body().getPredictions();

                    if (predictions != null && !predictions.isEmpty()) {
                        String prediction = predictions.get(0);

                        Log.d(TAG, "✅ Prédiction: " + prediction);

                        resultTextView.setText("✅ SUCCÈS!\n\n");
                        resultTextView.append("Série: " + seriesName + "\n");
                        resultTextView.append("Prédiction: " + prediction.toUpperCase() + "\n");

                        if ("edible".equals(prediction)) {
                            resultTextView.append("Résultat: ✅ EDIBLE (Comestible)\n");
                        } else if ("poisonous".equals(prediction)) {
                            resultTextView.append("Résultat: ❌ POISONOUS (Toxique)\n");
                        }

                        resultTextView.append("\n");

                        Toast.makeText(TestActivity.this,
                                "Prédiction: " + prediction, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "❌ Erreur: " + response.code());

                    resultTextView.setText("❌ ERREUR!\n\n");
                    resultTextView.append("Code: " + response.code() + "\n");
                    resultTextView.append("Message: at least one array or dtype is required\n");
                    resultTextView.append("\n");
                    resultTextView.append("Logs détaillés: Voir Logcat\n");

                    Toast.makeText(TestActivity.this,
                            "Erreur: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PredictResponse> call, Throwable t) {
                Log.e(TAG, "❌ Erreur réseau: " + t.getMessage());

                resultTextView.setText("❌ ERREUR RÉSEAU!\n\n");
                resultTextView.append("Message: " + t.getMessage() + "\n");
                resultTextView.append("\n");

                Toast.makeText(TestActivity.this,
                        "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Quand on revient de LoginActivity, vérifier à nouveau l'auth
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - Vérifier authentification");
        checkAuthentication();
    }
}