package com.shrooml.services;

import com.shrooml.models.MushroomEntity;
import com.shrooml.services.api.OAuthApi;
import com.shrooml.services.api.OAuthRetrofitClient;
import com.shrooml.services.api.TokenResponse;
import com.shrooml.services.api.TokenResponseFull;
import com.shrooml.services.api.LoginRequest;
import com.shrooml.services.api.RegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OAuthService {


    private final OAuthApi api;

    public OAuthService() {
        api = OAuthRetrofitClient.getApi();
    }

    public OAuthService(Boolean user){
        if (user) {
            api = OAuthRetrofitClient.getApiUser();
        } else {
            api = OAuthRetrofitClient.getApi();
        }
    }

    public interface OAuthCallback {
        void onSuccess(String token);
        void onError(String errorMessage);
    }

    public interface OAuthUserCallback {
        void onSuccess(TokenResponseFull response);
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

    public void loginFull(String identifiant, String mot_de_passe, OAuthUserCallback callback) {
        LoginRequest request = new LoginRequest(identifiant, mot_de_passe);
        Call<TokenResponseFull> call = api.login(request);

        call.enqueue(new Callback<TokenResponseFull>() {

            @Override
            public void onResponse(Call<TokenResponseFull> call, Response<TokenResponseFull> response) {

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());

                } else {
                    callback.onError("Erreur login : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TokenResponseFull> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

    public void register(String identifiant, String email, String mot_de_passe, String champignon_prefere, OAuthUserCallback callback) {
        RegisterRequest request = new RegisterRequest(identifiant, email, mot_de_passe, champignon_prefere);
        Call<TokenResponseFull> call = api.register(request);

        call.enqueue(new Callback<TokenResponseFull>() {

            @Override
            public void onResponse(Call<TokenResponseFull> call, Response<TokenResponseFull> response) {

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());

                } else {
                    callback.onError("Erreur inscription : " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<TokenResponseFull> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

    public void exchangeGoogleToken(com.shrooml.services.api.GoogleTokenRequest request, OAuthUserCallback callback) {
        Call<TokenResponseFull> call = api.exchangeGoogleToken(request);

        call.enqueue(new Callback<TokenResponseFull>() {

            @Override
            public void onResponse(Call<TokenResponseFull> call, Response<TokenResponseFull> response) {

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());

                } else {
                    callback.onError("Erreur Google Sign-In : " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<TokenResponseFull> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }
}