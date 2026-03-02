package com.shrooml.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shrooml.services.api.InaturalistApi;
import com.shrooml.services.api.InaturalistRetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InaturalistService {

    private final InaturalistApi api;

    public InaturalistService() {
        api = InaturalistRetrofitClient.getApi();
    }

    public interface ImageCallback {
        void onSuccess(String imageUrl);
        void onError(String errorMessage);
    }

    public void getMushroomImage(String specieName, ImageCallback callback) {

        Call<JsonObject> call = api.getObservation(
                specieName,
                true,
                1,
                "research"
        );

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Erreur API iNaturalist");
                    return;
                }

                try {
                    JsonObject root = response.body();
                    JsonArray results = root.getAsJsonArray("results");

                    if (results.size() == 0) {
                        callback.onError("Aucun résultat trouvé");
                        return;
                    }

                    JsonObject firstResult = results.get(0).getAsJsonObject();
                    JsonArray photos = firstResult.getAsJsonArray("photos");

                    if (photos.size() == 0) {
                        callback.onError("Pas de photo disponible");
                        return;
                    }

                    String photoUrl = photos
                            .get(0)
                            .getAsJsonObject()
                            .get("url")
                            .getAsString()
                            .replace("square", "large");

                    callback.onSuccess(photoUrl);

                } catch (Exception e) {
                    callback.onError("Erreur parsing JSON");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}