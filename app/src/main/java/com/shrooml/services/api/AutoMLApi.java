package com.shrooml.services.api;
import com.shrooml.services.api.Requests.*;
import com.shrooml.services.api.Response.*;

import retrofit2.Call;
import retrofit2.http.*;
public interface AutoMLApi {
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password,
            @Field("scope") String scope,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret
    );    @POST("fit")
    Call<com.shrooml.services.api.FitResponse> fit(@Body com.shrooml.services.api.Requests.FitRequest fitRequest);
    @POST("predict")
    Call<com.shrooml.services.api.Response.PredictResponse> predict(@Body com.shrooml.services.api.Requests.PredictRequest PredictRequest);

    @POST("eval")
    Call<com.shrooml.services.api.EvalResponse> evaluate(@Body EvalRequest EvalRequest);


}
