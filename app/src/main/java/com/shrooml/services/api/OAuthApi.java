package com.shrooml.services.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OAuthApi {

    @POST("auth/login")
    Call<TokenResponseFull> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<TokenResponseFull> register(@Body RegisterRequest request);

    @POST("auth/google/idtoken")
    Call<TokenResponseFull> exchangeGoogleToken(@Body GoogleTokenRequest request);
}