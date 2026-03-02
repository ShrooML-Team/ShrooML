package com.shrooml.services.api;

import com.shrooml.models.MushroomEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShroomLocApi {
    @GET("mushrooms/all")
    Call<List<MushroomEntity>> getall();

    @GET("mushrooms")
    Call<List<MushroomEntity>> getMushroomsByLatiLong(
                                            @Query("latitude") int latitude,
                                            @Query("longitude") int longitude
    );
    @GET("mushrooms/{name}")
    Call<List<MushroomEntity>> getMushroomsByName(
                                            @Path("name") String name
    );
}
