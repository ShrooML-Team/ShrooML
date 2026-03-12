package com.shrooml.services;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.shrooml.services.api.TokenResponseFull;

public class GoogleSignInManager {
    public static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private OAuthService oAuthService;

    public interface GoogleSignInCallback {
        void onSuccess(TokenResponseFull response);
        void onError(String errorMessage);
    }

    public GoogleSignInManager(Context context, String googleClientId) {
        this.oAuthService = new OAuthService(true);

        // Configurer Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleClientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    /**
     * Lance l'activité Google Sign-In
     */
    public void signIn(Activity activity) {
        mGoogleSignInClient.signOut(); // Force login à chaque fois
        activity.startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    /**
     * Traiter le résultat depuis onActivityResult
     */
    public void handleSignInResult(int requestCode, int resultCode, android.content.Intent data, GoogleSignInCallback callback) {
        if (requestCode != RC_SIGN_IN) {
            return;
        }

        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            
            if (account != null) {
                String idToken = account.getIdToken();
                // Envoyer le token à l'API backend pour validation et création du JWT
                exchangeGoogleTokenForJWT(idToken, callback);
            } else {
                callback.onError("Compte Google introuvable");
            }
        } catch (ApiException e) {
            callback.onError("Erreur Google Sign-In: " + e.getStatusCode() + " - " + e.getMessage());
        }
    }

    /**
     * Envoyer le Google ID Token au backend pour obtenir un JWT
     */
    private void exchangeGoogleTokenForJWT(String idToken, GoogleSignInCallback callback) {
        // Créer une requête pour envoyer le token Google au backend
        com.shrooml.services.api.GoogleTokenRequest request = 
            new com.shrooml.services.api.GoogleTokenRequest(idToken, "android");

        oAuthService.exchangeGoogleToken(request, new OAuthService.OAuthUserCallback() {
            @Override
            public void onSuccess(TokenResponseFull response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Effectuer la déconnexion
     */
    public void signOut() {
        mGoogleSignInClient.signOut();
    }
}
