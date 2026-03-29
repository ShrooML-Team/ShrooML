package com.shrooml.services;

import com.shrooml.services.api.UpdateUserRequest;
import com.shrooml.services.api.UserApi;
import com.shrooml.services.api.UserPhotoUploadResponse;
import com.shrooml.services.api.UserResponse;
import com.shrooml.services.api.UserRetrofitClient;

import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {

    private static final String SESSION_EXPIRED_MESSAGE = "Session expirée, reconnectez-vous";

    public interface UserProfileCallback {
        void onSuccess(UserResponse user);
        void onError(String errorMessage);
    }

    public interface UsersListCallback {
        void onSuccess(List<UserResponse> users);
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

                if (response.code() == 401) {
                    callback.onError("UPDATE"+SESSION_EXPIRED_MESSAGE);
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

    public void getCurrentUserProfile(UserProfileCallback callback) {
        Call<UserResponse> call = api.getCurrentUserProfile();

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }

                if (response.code() == 401) {
                    callback.onError(SESSION_EXPIRED_MESSAGE + "GET");
                    return;
                }

                callback.onError("Erreur lecture profil : " + response.code());
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Erreur reseau : " + t.getMessage());
            }
        });
    }

    public void addPoints(float pointsToAdd, UserProfileCallback callback) {
        getCurrentUserProfile(new UserProfileCallback() {
            @Override
            public void onSuccess(UserResponse user) {
                float updatedScoring = Math.max(0f, user.getScoring() + pointsToAdd);
                UpdateUserRequest request = new UpdateUserRequest(
                        null,
                        null,
                        null,
                        updatedScoring
                );

                updateCurrentUserProfile(request, callback);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
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

                if (response.code() == 401) {
                    callback.onError(SESSION_EXPIRED_MESSAGE);
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

    public void getTopRanking(int limit, UsersListCallback callback) {
        Call<List<UserResponse>> call = api.getTopRanking(limit);

        call.enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }

                if (response.code() == 401) {
                    callback.onError(SESSION_EXPIRED_MESSAGE);
                    return;
                }

                callback.onError("Erreur recuperation classement : " + response.code());
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                callback.onError("Erreur reseau : " + t.getMessage());
            }
        });
    }
}