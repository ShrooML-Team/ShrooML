package com.shrooml.services;

import com.shrooml.services.api.UpdateUserRequest;
import com.shrooml.services.api.UserApi;
import com.shrooml.services.api.UserPhotoUploadResponse;
import com.shrooml.services.api.UserResponse;
import com.shrooml.services.api.UserRetrofitClient;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {

    public interface UserProfileCallback {
        void onSuccess(UserResponse user);
        void onError(String errorMessage);
    }

    public interface UserPhotoCallback {
        void onSuccess(UserPhotoUploadResponse response);
        void onError(String errorMessage);
    }

    private final UserApi api;

    public UserService(String authToken) {
        api = UserRetrofitClient.getApi(authToken);
    }

    public void updateCurrentUserProfile(UpdateUserRequest request, UserProfileCallback callback) {
        Call<UserResponse> call = api.updateCurrentUserProfile(request);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }

                callback.onError("Erreur mise a jour profil : " + response.code());
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Erreur reseau : " + t.getMessage());
            }
        });
    }

    public void uploadCurrentUserProfilePhoto(MultipartBody.Part photoPart, UserPhotoCallback callback) {
        Call<UserPhotoUploadResponse> call = api.uploadCurrentUserProfilePhoto(photoPart);

        call.enqueue(new Callback<UserPhotoUploadResponse>() {
            @Override
            public void onResponse(Call<UserPhotoUploadResponse> call, Response<UserPhotoUploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }

                callback.onError("Erreur upload photo : " + response.code());
            }

            @Override
            public void onFailure(Call<UserPhotoUploadResponse> call, Throwable t) {
                callback.onError("Erreur reseau : " + t.getMessage());
            }
        });
    }
}