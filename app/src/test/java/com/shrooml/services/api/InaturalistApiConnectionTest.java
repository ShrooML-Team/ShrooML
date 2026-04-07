package com.shrooml.services.api;

import com.google.gson.JsonObject;

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

public class InaturalistApiConnectionTest {

    private MockWebServer mockWebServer;
    private InaturalistApi api;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(InaturalistApi.class);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getObservation_callsExpectedEndpointWithQueryParams() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"results\":[{\"photos\":[{\"url\":\"https://img/square.jpg\"}]}]}")
                .addHeader("Content-Type", "application/json"));

        Response<JsonObject> response = api.getObservation("Boletus edulis", true, 1, "research").execute();
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("GET", request.getMethod());
        assertTrue(request.getPath().startsWith("/observations?"));
        assertTrue(request.getPath().contains("taxon_name=Boletus%20edulis"));
        assertTrue(request.getPath().contains("photos=true"));
        assertTrue(request.getPath().contains("per_page=1"));
        assertTrue(request.getPath().contains("quality_grade=research"));

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(1, response.body().getAsJsonArray("results").size());
    }

    @Test
    public void getObservation_whenServerError_returns500AndEncodedQuery() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"upstream\"}")
                .addHeader("Content-Type", "application/json"));

        Response<JsonObject> response = api.getObservation("Cantharellus cibarius", true, 3, "any").execute();
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("GET", request.getMethod());
        assertTrue(request.getPath().startsWith("/observations?"));
        assertTrue(request.getPath().contains("taxon_name=Cantharellus%20cibarius"));
        assertTrue(request.getPath().contains("per_page=3"));
        assertEquals(500, response.code());
        assertTrue(!response.isSuccessful());
    }
}
