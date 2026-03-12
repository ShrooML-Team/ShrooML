package com.shrooml.services.api;

import com.shrooml.models.IdentificationEntity;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface KindwiseApi {
    @Multipart
    @POST("identification")
    Call<IdentificationEntity> identificationImg(
            @Header("Api-Key") String apiKey,
            @Part MultipartBody.Part images
    );
}
