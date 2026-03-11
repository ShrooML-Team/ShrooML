package com.shrooml.services.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OAuthRetrofitClient {
    private static final String BASE_URL = "https://shroomloc.shrooml.duckdns.org/";
    private static Retrofit retrofit;

    public static OAuthApi getApi() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(OAuthApi.class);
    }
}