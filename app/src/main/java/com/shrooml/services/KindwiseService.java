package com.shrooml.services;

import android.widget.Toast;

import com.shrooml.BuildConfig;
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

    private int num_API_KEY = 0;

    private static  String API_KEY;

    public KindwiseService() {
        api = KindwiseRetrofitClient.getApi();
    }

    public interface IdentificationCallback {
        void onSuccess(IdentificationEntity identification);
        void onError(String errorMessage);
    }

    public void identificationImg(String imagePath, KindwiseService.IdentificationCallback callback) {
        switch(num_API_KEY){
            case 0: API_KEY= BuildConfig.KINDWISE_API_KEY_1 ;break;
            case 1: API_KEY= BuildConfig.KINDWISE_API_KEY_2 ;break;
            case 2: API_KEY= BuildConfig.KINDWISE_API_KEY_3 ;break;
        }
        num_API_KEY = (num_API_KEY + 1) % 3;
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
