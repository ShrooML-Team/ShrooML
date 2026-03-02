package com.shrooml.services.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InaturalistApi {
    @GET("observations")
    Call<JsonObject> getObservation(
            @Query("taxon_name") String taxonName,
            @Query("photos") boolean photos,
            @Query("per_page") int perPage,
            @Query("quality_grade") String qualityGrade
    );
}
