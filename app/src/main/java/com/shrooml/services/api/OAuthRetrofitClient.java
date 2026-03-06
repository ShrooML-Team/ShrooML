package com.shrooml.services.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OAuthRetrofitClient {
    // Pour émulateur Android : 10.0.2.2 correspond à localhost de la machine hôte
    private static final String BASE_URL = "http://10.0.2.2:8000/";
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