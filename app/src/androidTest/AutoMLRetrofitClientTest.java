package com.shrooml.services.api;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.shrooml.services.api.Requests.LoginRequest;
import com.shrooml.services.api.Response.FitResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * AutoMLRetrofitClientTest
 *
 * Tests unitaires pour vérifier que AutoMLRetrofitClient fonctionne correctement
 *
 * À exécuter avec:
 * ./gradlew connectedAndroidTest
 *
 * Ou dans Android Studio:
 * Right click on AutoMLRetrofitClientTest → Run 'AutoMLRetrofitClientTest'
 */
@RunWith(AndroidJUnit4.class)
public class AutoMLRetrofitClientTest {

    @Mock
    private Context mockContext;

    private AutoMLRetrofitClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        AutoMLRetrofitClient.reset(); // Réinitialiser l'instance singleton
        client = AutoMLRetrofitClient.getInstance();
    }

    /**
     * TEST 1: Vérifier que l'instance Singleton fonctionne
     */
    @Test
    public void testSingletonPattern() {
        // Créer deux instances
        AutoMLRetrofitClient instance1 = AutoMLRetrofitClient.getInstance();
        AutoMLRetrofitClient instance2 = AutoMLRetrofitClient.getInstance();

        // Elles doivent être la même
        assertSame("Les instances doivent être identiques", instance1, instance2);
    }

    /**
     * TEST 2: Vérifier que le client Retrofit est créé correctement
     */
    @Test
    public void testRetrofitClientCreation() {
        // Le client Retrofit ne doit pas être null
        assertNotNull("OkHttpClient ne doit pas être null", client.getHttpClient());
        assertNotNull("Retrofit instance ne doit pas être null", client.getRetrofit());
    }

    /**
     * TEST 3: Vérifier que l'API interface est créée correctement
     */
    @Test
    public void testApiInterfaceCreation() {
        // L'interface AutoMLApi ne doit pas être null
        AutoMLApi api = client.getAutoMLApi();
        assertNotNull("AutoMLApi ne doit pas être null", api);
    }

    /**
     * TEST 4: Vérifier la gestion du token Bearer
     */
    @Test
    public void testTokenManagement() {
        // Au démarrage, pas de token
        assertFalse("Initialement, l'utilisateur ne doit pas être authentifié",
                client.isAuthenticated());
        assertNull("Le token doit être null au démarrage", client.getAuthToken());

        // Définir un token
        String testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature";
        client.setAuthToken(testToken);

        // Vérifier que le token est sauvegardé
        assertTrue("L'utilisateur doit être authentifié après setToken",
                client.isAuthenticated());
        assertEquals("Le token doit correspondre à celui défini",
                testToken, client.getAuthToken());

        // Effacer le token
        client.clearAuthToken();

        // Vérifier que le token est effacé
        assertFalse("L'utilisateur ne doit pas être authentifié après clearToken",
                client.isAuthenticated());
        assertNull("Le token doit être null après clearToken", client.getAuthToken());
    }

    /**
     * TEST 5: Vérifier la base URL
     */
    @Test
    public void testBaseUrl() {
        String baseUrl = client.getBaseUrl();
        assertNotNull("La base URL ne doit pas être null", baseUrl);
        assertTrue("La base URL doit contenir https", baseUrl.contains("https"));
        assertTrue("La base URL doit contenir shrooml", baseUrl.contains("shrooml"));
    }

    /**
     * TEST 6: Vérifier le reset du singleton
     */
    @Test
    public void testSingletonReset() {
        // Obtenir une instance
        AutoMLRetrofitClient instance1 = AutoMLRetrofitClient.getInstance();
        String token = "test_token";
        instance1.setAuthToken(token);

        // Réinitialiser
        AutoMLRetrofitClient.reset();

        // La nouvelle instance doit être différente
        AutoMLRetrofitClient instance2 = AutoMLRetrofitClient.getInstance();
        assertNotSame("Les instances doivent être différentes après reset",
                instance1, instance2);

        // Le token ne doit pas être hérité
        assertNull("Le token ne doit pas être hérité après reset",
                instance2.getAuthToken());
    }
}