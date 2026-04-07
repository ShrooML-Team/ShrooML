package com.shrooml.services.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OAuthRetrofitClient {
    private static final String BASE_URL_USER = "https://shroomleur.shrooml.duckdns.org/";
    private static final String BASE_URL_API = "https://shroomloc.shrooml.duckdns.org/";
    private static Retrofit retrofit_api;
    private static Retrofit retrofit_user;

    public static OAuthApi getApi() {

        if (retrofit_api == null) {
            retrofit_api = new Retrofit.Builder()
                    .baseUrl(BASE_URL_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit_api.create(OAuthApi.class);
    }

    public static OAuthApi getApiUser() {

        if (retrofit_user == null){
            retrofit_user = new Retrofit.Builder()
                    .baseUrl(BASE_URL_USER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit_user.create(OAuthApi.class);
    }
}