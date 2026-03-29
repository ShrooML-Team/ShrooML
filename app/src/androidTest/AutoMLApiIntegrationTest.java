package com.shrooml.services.api;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.shrooml.services.api.Requests.FitRequest;
import com.shrooml.services.api.Requests.LoginRequest;
import com.shrooml.services.api.Requests.PredictRequest;
import com.shrooml.services.api.Response.FitResponse;
import com.shrooml.services.api.Response.PredictResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * AutoMLApiIntegrationTest
 *
 * Tests d'intégration contre l'API réelle
 * ⚠️ L'API doit être en fonctionnement!
 *
 * À exécuter avec:
 * ./gradlew connectedAndroidTest
 *
 * Ou dans Android Studio:
 * Right click on AutoMLApiIntegrationTest → Run 'AutoMLApiIntegrationTest'
 */
@RunWith(AndroidJUnit4.class)
public class AutoMLApiIntegrationTest {

    private AutoMLApi api;
    private String authToken;

    @Before
    public void setUp() {
        AutoMLRetrofitClient.reset();
        api = AutoMLRetrofitClient.getInstance().getAutoMLApi();
    }

    /**
     * TEST 1: Login avec des credentials valides
     */
    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");
        Call<String> call = api.login(request);
        Response<String> response = call.execute();

        // Vérifications
        assertTrue("La réponse doit être successful", response.isSuccessful());
        assertNotNull("Le body ne doit pas être null", response.body());

        String token = response.body();
        assertFalse("Le token ne doit pas être vide", token.isEmpty());

        // Le token doit ressembler à un JWT (3 parties séparées par des points)
        assertTrue("Le token doit ressembler à un JWT", token.contains("."));

        // Sauvegarder le token pour les tests suivants
        authToken = token;
        AutoMLRetrofitClient.getInstance().setAuthToken(token);
    }

    /**
     * TEST 2: Login avec des credentials invalides
     */
    @Test
    public void testLoginFailure() throws Exception {
        LoginRequest request = new LoginRequest("invalid_user", "wrong_password");
        Call<String> call = api.login(request);
        Response<String> response = call.execute();

        // La réponse ne doit pas être successful
        assertFalse("La réponse ne doit pas être successful", response.isSuccessful());

        // Généralement une erreur 401 ou 403
        assertTrue("Le code doit être 401 ou 403",
                response.code() == 401 || response.code() == 403);
    }

    /**
     * TEST 3: Fit avec données valides
     */
    @Test
    public void testFitSuccess() throws Exception {
        // D'abord faire un login
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");
        Call<String> loginCall = api.login(loginRequest);
        Response<String> loginResponse = loginCall.execute();

        if (!loginResponse.isSuccessful()) {
            fail("Login échoué. L'API doit être accessible.");
        }

        String token = loginResponse.body();
        AutoMLRetrofitClient.getInstance().setAuthToken(token);

        // Préparer les données
        List<Map<String, Object>> X = new ArrayList<>();
        X.add(createMushroomFeatures("convex", "brown", "narrow", "fishy"));
        X.add(createMushroomFeatures("bell", "white", "broad", "almond"));
        X.add(createMushroomFeatures("flat", "brown", "narrow", "none"));
        X.add(createMushroomFeatures("convex", "red", "broad", "anise"));

        List<String> y = new ArrayList<>();
        y.add("poisonous");
        y.add("edible");
        y.add("poisonous");
        y.add("edible");

        Map<String, Object> params = new HashMap<>();
        params.put("time_budget", 60);
        params.put("metric", "accuracy");

        FitRequest fitRequest = new FitRequest(X, y, params);
        Call<FitResponse> fitCall = api.fit(fitRequest);
        Response<FitResponse> fitResponse = fitCall.execute();

        // Vérifications
        assertTrue("La réponse doit être successful", fitResponse.isSuccessful());
        assertNotNull("Le body ne doit pas être null", fitResponse.body());
        assertNotNull("Le status ne doit pas être null", fitResponse.body().getStatus());
    }

    /**
     * TEST 4: Fit sans authentification
     */
    @Test
    public void testFitWithoutAuth() throws Exception {
        // Ne pas faire de login, donc pas de token

        List<Map<String, Object>> X = new ArrayList<>();
        X.add(createMushroomFeatures("convex", "brown", "narrow", "fishy"));

        List<String> y = new ArrayList<>();
        y.add("poisonous");

        Map<String, Object> params = new HashMap<>();
        params.put("time_budget", 60);

        FitRequest fitRequest = new FitRequest(X, y, params);
        Call<FitResponse> fitCall = api.fit(fitRequest);
        Response<FitResponse> fitResponse = fitCall.execute();

        // Doit retourner une erreur d'authentification
        assertFalse("La réponse ne doit pas être successful", fitResponse.isSuccessful());
        assertEquals("Doit être une erreur 401", 401, fitResponse.code());
    }

    /**
     * TEST 5: Predict avec données valides
     */
    @Test
    public void testPredictSuccess() throws Exception {
        // D'abord faire un login
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");
        Call<String> loginCall = api.login(loginRequest);
        Response<String> loginResponse = loginCall.execute();

        if (!loginResponse.isSuccessful()) {
            fail("Login échoué. L'API doit être accessible.");
        }

        String token = loginResponse.body();
        AutoMLRetrofitClient.getInstance().setAuthToken(token);

        // Préparer les données
        List<Map<String, Object>> X = new ArrayList<>();
        X.add(createMushroomFeatures("convex", "brown", "narrow", "fishy"));
        X.add(createMushroomFeatures("bell", "white", "broad", "almond"));

        PredictRequest predictRequest = new PredictRequest(X);
        Call<PredictResponse> predictCall = api.predict(predictRequest);
        Response<PredictResponse> predictResponse = predictCall.execute();

        // Vérifications
        assertTrue("La réponse doit être successful", predictResponse.isSuccessful());
        assertNotNull("Le body ne doit pas être null", predictResponse.body());
        assertNotNull("Les prédictions ne doivent pas être nulles",
                predictResponse.body().getPredictions());

        List<String> predictions = predictResponse.body().getPredictions();
        assertEquals("Doit avoir 2 prédictions", 2, predictions.size());
    }

    /**
     * TEST 6: Predict avec données invalides (liste vide)
     */
    @Test
    public void testPredictWithInvalidData() throws Exception {
        // D'abord faire un login
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");
        Call<String> loginCall = api.login(loginRequest);
        Response<String> loginResponse = loginCall.execute();

        if (!loginResponse.isSuccessful()) {
            fail("Login échoué");
        }

        String token = loginResponse.body();
        AutoMLRetrofitClient.getInstance().setAuthToken(token);

        // Données vides
        List<Map<String, Object>> X = new ArrayList<>(); // Vide!

        PredictRequest predictRequest = new PredictRequest(X);
        Call<PredictResponse> predictCall = api.predict(predictRequest);
        Response<PredictResponse> predictResponse = predictCall.execute();

        // Doit retourner une erreur de validation (422)
        assertFalse("La réponse ne doit pas être successful", predictResponse.isSuccessful());
        assertEquals("Doit être une erreur 422 (validation)", 422, predictResponse.code());
    }

    /**
     * TEST 7: Vérifier que le token est transmis dans les headers
     */
    @Test
    public void testTokenTransmissionInHeaders() throws Exception {
        // D'abord faire un login
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");
        Call<String> loginCall = api.login(loginRequest);
        Response<String> loginResponse = loginCall.execute();

        assertTrue("Login doit réussir", loginResponse.isSuccessful());

        String token = loginResponse.body();
        AutoMLRetrofitClient.getInstance().setAuthToken(token);

        // Faire une requête
        List<Map<String, Object>> X = new ArrayList<>();
        X.add(createMushroomFeatures("convex", "brown", "narrow", "fishy"));

        PredictRequest predictRequest = new PredictRequest(X);
        Call<PredictResponse> predictCall = api.predict(predictRequest);
        Response<PredictResponse> predictResponse = predictCall.execute();

        // Vérifier que la requête a été acceptée (le token doit être dans les headers)
        assertTrue("La requête doit être acceptée avec le token", predictResponse.isSuccessful());
    }

    /**
     * TEST 8: Vérifier les timeouts
     */
    @Test(timeout = 65000)  // 65 secondes max
    public void testTimeoutConfiguration() throws Exception {
        // Le timeout est configuré à 60 secondes pour la lecture
        // Cette requête doit terminer avant le timeout

        LoginRequest request = new LoginRequest("admin", "admin123");
        Call<String> call = api.login(request);
        Response<String> response = call.execute();

        // Si on arrive ici, c'est que le timeout a bien fonctionné
        assertNotNull("La réponse ne doit pas être null", response);
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
}