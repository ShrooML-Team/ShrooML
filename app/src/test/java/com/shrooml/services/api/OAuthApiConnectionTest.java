package com.shrooml.services.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OAuthApiConnectionTest {

    private MockWebServer mockWebServer;
    private OAuthApi api;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(OAuthApi.class);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void loginFull_postsJsonBodyToAuthLogin() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"abc123\",\"token_type\":\"bearer\",\"user\":{\"id\":42,\"identifiant\":\"alice\"}}")
                .addHeader("Content-Type", "application/json"));

        Response<TokenResponseFull> response = api.login(new LoginRequest("alice", "secret")).execute();
        RecordedRequest request = mockWebServer.takeRequest();
        String body = request.getBody().readUtf8();

        assertEquals("POST", request.getMethod());
        assertEquals("/auth/login", request.getPath());
        assertTrue(request.getHeader("Content-Type").contains("application/json"));
        assertTrue(body.contains("\"identifiant\":\"alice\""));
        assertTrue(body.contains("\"mot_de_passe\":\"secret\""));

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("abc123", response.body().getAccess_token());
        assertEquals(42, response.body().getUser().getId());
    }

    @Test
    public void loginForm_postsUrlEncodedFieldsToLogin() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"legacy\",\"token_type\":\"bearer\"}")
                .addHeader("Content-Type", "application/json"));

        Response<TokenResponse> response = api.login("bob", "pwd").execute();
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals("/login", request.getPath());
        assertTrue(request.getHeader("Content-Type").contains("application/x-www-form-urlencoded"));
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("username=bob"));
        assertTrue(body.contains("password=pwd"));

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("legacy", response.body().getAccessToken());
    }

    @Test
    public void register_postsJsonBodyToAuthRegister() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody("{\"access_token\":\"new-token\",\"token_type\":\"bearer\",\"user\":{\"id\":7,\"identifiant\":\"new_user\"}}")
                .addHeader("Content-Type", "application/json"));

        RegisterRequest registerRequest = new RegisterRequest("new_user", "mail@test.com", "pwd", "Cep");
        Response<TokenResponseFull> response = api.register(registerRequest).execute();
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals("/auth/register", request.getPath());
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("\"identifiant\":\"new_user\""));
        assertTrue(body.contains("\"email\":\"mail@test.com\""));
        assertTrue(body.contains("\"champignon_prefere\":\"Cep\""));

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("new-token", response.body().getAccess_token());
        assertEquals("new_user", response.body().getUser().getIdentifiant());
    }

    @Test
    public void exchangeGoogleToken_postsJsonBodyToGoogleEndpoint() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"google-token\",\"token_type\":\"bearer\",\"user\":{\"id\":99,\"identifiant\":\"google_user\"}}")
                .addHeader("Content-Type", "application/json"));

        Response<TokenResponseFull> response = api.exchangeGoogleToken(new GoogleTokenRequest("id-token-value", "android")).execute();
        RecordedRequest request = mockWebServer.takeRequest();
        String body = request.getBody().readUtf8();

        assertEquals("POST", request.getMethod());
        assertEquals("/auth/google/idtoken", request.getPath());
        assertTrue(body.contains("\"idToken\":\"id-token-value\""));
        assertTrue(body.contains("\"platform\":\"android\""));

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("google-token", response.body().getAccess_token());
        assertEquals("google_user", response.body().getUser().getIdentifiant());
    }

    @Test
    public void loginFull_whenUnauthorized_returnsErrorResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"detail\":\"Unauthorized\"}")
                .addHeader("Content-Type", "application/json"));

        Response<TokenResponseFull> response = api.login(new LoginRequest("alice", "bad-password")).execute();
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals("/auth/login", request.getPath());
        assertTrue(response.code() == 401);
        assertTrue(!response.isSuccessful());
        assertNotNull(response.errorBody());
    }
}
