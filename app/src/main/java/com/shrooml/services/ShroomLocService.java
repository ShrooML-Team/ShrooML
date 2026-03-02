package com.shrooml.services;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.shrooml.models.MushroomEntity;
import com.shrooml.R;
import com.shrooml.services.api.ShroomLocApi;
import com.shrooml.services.api.ShroomLocRetrofitClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShroomLocService {
    private List<MushroomEntity> mushrooms;

    private final ShroomLocApi api;

    public ShroomLocService() {
        api = ShroomLocRetrofitClient.getApi();
    }

    public interface MushroomsCallback {
        void onSuccess(List<MushroomEntity> mushrooms);
        void onError(String errorMessage);
    }

    public void getAll(MushroomsCallback callback) {

        Call<List<MushroomEntity>> call = api.getall(); // ton endpoint Retrofit

        call.enqueue(new Callback<List<MushroomEntity>>() {
            @Override
            public void onResponse(Call<List<MushroomEntity>> call, Response<List<MushroomEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                        callback.onError("Erreur serveur : " + response.code());
                }
            }

            @Override
            public void onFailure( Call<List<MushroomEntity>> call,  Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }
}
