package com.shrooml.services.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InaturalistRetrofitClient {
    private static final String BASE_URL = "https://api.inaturalist.org/v1/";
    private static Retrofit retrofit;
    public static InaturalistApi getApi() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(InaturalistApi.class);
    }
}