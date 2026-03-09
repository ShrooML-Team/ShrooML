package com.shrooml.services.api;
import com.shrooml.services.api.Requests.*;
import com.shrooml.services.api.Response.*;

import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.Call;
import retrofit2.http.*;
import retrofit2.http.Query;
public interface AutoMLApi {
    @POST("/login")
    Call<String> login(@Body LoginRequest loginRequest);
    @POST("/fit")
    Call<FitResponse> fit(@Body FitRequest fitRequest);
    @POST("/predict")
    Call<PredictResponse> predict(@Body PredictRequest PredictRequest);

    @POST("/eval")
    Call<EvalResponse> evaluate(@Body EvalRequest EvalRequest);


}
