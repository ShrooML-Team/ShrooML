package com.shrooml.services;

import com.shrooml.models.MushroomEntity;
import com.shrooml.services.api.OAuthApi;
import com.shrooml.services.api.OAuthRetrofitClient;
import com.shrooml.services.api.TokenResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OAuthService {

    private List<MushroomEntity> mushrooms;

    private final OAuthApi api;

    public OAuthService() {
        api = OAuthRetrofitClient.getApi();
    }

    public interface OAuthCallback {
        void onSuccess(String token);
        void onError(String errorMessage);
    }

    public void login(String username, String password, OAuthService.OAuthCallback callback) {

        Call<TokenResponse> call = api.login(username, password);

        call.enqueue(new Callback<TokenResponse>() {

            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getAccessToken());

                } else {
                    callback.onError("Erreur login : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

}