package com.shrooml.services.api;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShroomLocRetrofitClient {

    private static final String BASE_URL = "https://api.shrooml.duckdns.org/"; // URL
    private static Retrofit retrofit;

    private static String authToken;

    public static void setToken(String token) {
        authToken = token;
    }
    public static ShroomLocApi getApi() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder();

                    if (authToken != null) {
                        requestBuilder.header("Authorization", "Bearer " + authToken);
                    }

                    return chain.proceed(requestBuilder.build());
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ShroomLocApi.class);
    }
}
