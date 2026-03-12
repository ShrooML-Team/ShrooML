package com.shrooml.services;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.shrooml.models.MushroomCompleteEntity;
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

    public interface MushroomsLocationCallBack {
        void onSucces(List<MushroomCompleteEntity> mushrooms);

        void onError(String errorMessage);
    }

    public interface MushroomDetailsCallback {
        void onSuccess(MushroomCompleteEntity mushroom);
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

    public void getMushroomsByLocation(double latitude, double longitude,MushroomsLocationCallBack callBack){
        Call<List<MushroomCompleteEntity>> call = api.getMushroomsByLatiLong(latitude,longitude);

        call.enqueue(new Callback<List<MushroomCompleteEntity>>() {
            @Override
            public void onResponse(Call<List<MushroomCompleteEntity>> call, Response<List<MushroomCompleteEntity>> response) {
                Log.d("API_JSON", new Gson().toJson(response.body()));

                if(response.isSuccessful() && response.body() != null) {
                    callBack.onSucces(response.body());
                    String json = new Gson().toJson(response.body());

                    int maxLogSize = 1000;
                    for (int i = 0; i <= json.length() / maxLogSize; i++) {
                        int start = i * maxLogSize;
                        int end = Math.min((i + 1) * maxLogSize, json.length());
                        Log.d("API_JSON", json.substring(start, end));
                    }

                } else {
                    callBack.onError("erreur status : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MushroomCompleteEntity>> call, Throwable t) {
                callBack.onError("Erreur réseaux" + t.getMessage());
            }
        });
    }

    public void getMushroomDetailsByName(String name, MushroomDetailsCallback callback) {

        Call<MushroomCompleteEntity> call = api.getMushroomsByName(name);
        Log.d("API_DEBUG", "Nom envoyé à l’API = " + name);
        Log.d("API_DEBUG", "URL = " + call.request().url());

        call.enqueue(new Callback<MushroomCompleteEntity>() {
            @Override
            public void onResponse(Call<MushroomCompleteEntity> call, Response<MushroomCompleteEntity> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erreur serveur : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MushroomCompleteEntity> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

}