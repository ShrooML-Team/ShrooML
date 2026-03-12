package com.shrooml.services.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KindwiseRetrofitClient {
    private static final String BASE_URL = "https://mushroom.kindwise.com/api/v1/";

    private static Retrofit retrofit;

    public static KindwiseApi getApi() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(KindwiseApi.class);
    }
}
