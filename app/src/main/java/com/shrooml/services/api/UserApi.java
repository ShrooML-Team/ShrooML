package com.shrooml.services.api;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PUT;

public interface UserApi {

    @PUT("users/me")
    Call<UserResponse> updateCurrentUserProfile(@Body UpdateUserRequest request);

    @Multipart
    @POST("users/me/photo")
    Call<UserPhotoUploadResponse> uploadCurrentUserProfilePhoto(@Part MultipartBody.Part photo);
}