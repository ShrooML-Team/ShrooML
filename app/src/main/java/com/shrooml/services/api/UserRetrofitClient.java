package com.shrooml.services.api;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRetrofitClient {
    private static final String BASE_URL = "https://shroomleur.shrooml.duckdns.org/";

    public static UserApi getApi(String authToken) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder();

                    if (authToken != null && !authToken.isEmpty()) {
                        String headerValue = "Bearer " + authToken;
                        Log.d("RESEAU_DEBUG", "Header envoyé : [" + headerValue + "]");
                        requestBuilder.header("Authorization", headerValue);
                    }

                    return chain.proceed(requestBuilder.build());
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(UserApi.class);
    }
}