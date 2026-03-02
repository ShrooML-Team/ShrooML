package com.shrooml.services.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OAuthApi {

    @FormUrlEncoded
    @POST("login")
    Call<TokenResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );
}