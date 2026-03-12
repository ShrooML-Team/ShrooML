package com.shrooml.services;

import com.shrooml.models.IdentificationEntity;
import com.shrooml.services.api.KindwiseApi;
import com.shrooml.services.api.KindwiseRetrofitClient;


import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KindwiseService {

    private final KindwiseApi api;

    private static final String API_KEY = "YSVv2gSPJmgB0MEg47r4KpCyBTSY4x2sM0MPXLlG8wm1nsDFMb";

    public KindwiseService() {
        api = KindwiseRetrofitClient.getApi();
    }

    public interface IdentificationCallback {
        void onSuccess(IdentificationEntity identification);
        void onError(String errorMessage);
    }

    public void identificationImg(String imagePath, KindwiseService.IdentificationCallback callback) {

        File file = new File(imagePath);

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/*"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("images", file.getName(), requestFile);

        Call<IdentificationEntity> call = api.identificationImg(API_KEY, body); // ton endpoint Retrofit

        call.enqueue(new Callback<IdentificationEntity>() {
            @Override
            public void onResponse(Call<IdentificationEntity> call, Response<IdentificationEntity> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erreur serveur : " + response.code());
                }
            }

            @Override
            public void onFailure( Call<IdentificationEntity> call,  Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }
}
