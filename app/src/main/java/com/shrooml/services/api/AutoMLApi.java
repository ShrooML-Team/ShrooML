package com.shrooml.services.api;

import com.shrooml.services.api.Requests.*;
import com.shrooml.services.api.Response.*;
import com.shrooml.services.api.FitAAResponse;
import com.shrooml.services.api.EvalAAResponse;

import retrofit2.Call;
import retrofit2.http.*;
public interface AutoMLApi {
    @FormUrlEncoded
    @POST("login")
    Call<LoginAAResponse> login(
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password,
            @Field("scope") String scope,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret
    );
    @POST("register")
    Call<LoginAAResponse> register(@Body RegisterAARequest registerRequest);

    @POST("fit")
    Call<FitAAResponse> fit(@Body FitAARequest fitRequest);
    @POST("predict")
    Call<PredictAAResponse> predict(@Body PredictAARequest PredictAARequest);

    @POST("eval")
    Call<EvalAAResponse> evaluate(@Body EvalAARequest EvalRequest);


}
