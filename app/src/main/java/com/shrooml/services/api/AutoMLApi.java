package com.shrooml.services.api;
import com.shrooml.services.api.Requests.*;
import com.shrooml.services.api.Response.*;

import retrofit2.Call;
import retrofit2.http.*;
public interface AutoMLApi {
    @POST("login")
    Call<com.shrooml.services.api.Response.LoginResponse> login(@Body com.shrooml.services.api.Requests.LoginRequest loginRequest);
    @POST("fit")
    Call<com.shrooml.services.api.FitResponse> fit(@Body com.shrooml.services.api.Requests.FitRequest fitRequest);
    @POST("predict")
    Call<com.shrooml.services.api.Response.PredictResponse> predict(@Body com.shrooml.services.api.Requests.PredictRequest PredictRequest);

    @POST("eval")
    Call<com.shrooml.services.api.EvalResponse> evaluate(@Body EvalRequest EvalRequest);


}
