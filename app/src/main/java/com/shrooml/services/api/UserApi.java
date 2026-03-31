package com.shrooml.services.api;

import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PUT;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserApi {

    @GET("users/me")
    Call<UserResponse> getCurrentUserProfile();

    @PUT("users/me")
    Call<UserResponse> updateCurrentUserProfile(@Body UpdateUserRequest request);

    @Multipart
    @POST("users/me/photo")
    Call<UserPhotoUploadResponse> uploadCurrentUserProfilePhoto(@Part MultipartBody.Part photo);

    @GET("users/top-ranking")
    Call<List<UserResponse>> getTopRanking(@Query("limit") int limit);

    @GET("users/me/history")
    Call<List<IdentificationHistoryResponse>> getCurrentUserHistory(
            @Query("skip") int skip,
            @Query("limit") int limit
    );

        @POST("users/me/history")
        Call<IdentificationHistoryResponse> createIdentificationHistory(
            @Body IdentificationHistoryCreateRequest request
        );
}