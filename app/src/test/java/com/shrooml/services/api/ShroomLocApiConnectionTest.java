package com.shrooml.services.api;

import com.shrooml.models.MushroomCompleteEntity;
import com.shrooml.models.MushroomEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ShroomLocApiConnectionTest {

    private MockWebServer mockWebServer;
    private ShroomLocApi api;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ShroomLocApi.class);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getAll_callsExpectedEndpointAndParsesResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"scientific_name\":\"Boletus edulis\",\"common_name\":\"Cep\",\"edibility\":\"edible\",\"season\":[\"autumn\"],\"min_temp\":10,\"max_temp\":20,\"min_humidity\":65,\"habitat\":[\"forest\"]}]")
                .addHeader("Content-Type", "application/json"));

        Response<List<MushroomEntity>> response = api.getall().execute();
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/mushrooms/all", request.getPath());
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(1, response.body().size());
        assertEquals("Boletus edulis", response.body().get(0).getScientific_name());
        assertEquals("Cep", response.body().get(0).getCommon_name());
    }

    @Test
    public void getMushroomsByLocation_sendsLatitudeAndLongitudeQueryParams() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[]")
                .addHeader("Content-Type", "application/json"));

        Response<List<MushroomCompleteEntity>> response = api.getMushroomsByLatiLong(48.8566, 2.3522).execute();
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("GET", request.getMethod());
        assertTrue(request.getPath().startsWith("/mushrooms?"));
        assertTrue(request.getPath().contains("latitude=48.8566"));
        assertTrue(request.getPath().contains("longitude=2.3522"));
        assertTrue(response.isSuccessful());
    }

    @Test
    public void getMushroomByName_callsNameBasedEndpoint() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"scientific_name\":\"Amanita muscaria\",\"common_name\":\"Amanite tue-mouches\"}")
                .addHeader("Content-Type", "application/json"));

        Response<MushroomCompleteEntity> response = api.getMushroomsByName("Amanita%20muscaria").execute();
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/mushrooms/Amanita%20muscaria", request.getPath());
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("Amanita muscaria", response.body().getScientificName());
    }

    @Test
    public void getMushroomByName_whenNotFound_returnsHttp404() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"detail\":\"Not found\"}")
                .addHeader("Content-Type", "application/json"));

        Response<MushroomCompleteEntity> response = api.getMushroomsByName("Unknown%20name").execute();
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/mushrooms/Unknown%20name", request.getPath());
        assertEquals(404, response.code());
        assertTrue(!response.isSuccessful());
    }
}
