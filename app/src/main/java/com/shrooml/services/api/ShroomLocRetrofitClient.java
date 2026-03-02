package com.shrooml.services.api;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShroomLocRetrofitClient {

    private static final String BASE_URL = "https://api.shrooml.duckdns.org/"; // URL
    private static Retrofit retrofit;

    public static ShroomLocApi getApi(String username, String password) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String credentials = Credentials.basic(username, password);
                    Request request = chain.request().newBuilder()
                            .header("Authorization", credentials)
                            .build();
                    return chain.proceed(request);
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
