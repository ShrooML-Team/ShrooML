package com.shrooml;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {
    private static final String TAG = "TokenManager";
    private static final String PREFERENCES_FILE = "shrooml_auth";
    private static final String TOKEN_KEY = "access_token";
    private static final String USER_ID_KEY = "user_id";
    private static final String USER_IDENTIFIANT_KEY = "user_identifiant";

    private static TokenManager instance;
    private final SharedPreferences encryptedSharedPref;

    private TokenManager(Context context) throws GeneralSecurityException, IOException {
        Log.d(TAG, "TokenManager constructor appelé");
        try {
            Log.d(TAG, "Création de la MasterKey...");
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            Log.d(TAG, "MasterKey créée avec succès");

            Log.d(TAG, "Création des EncryptedSharedPreferences...");
            encryptedSharedPref = EncryptedSharedPreferences.create(
                    context,
                    PREFERENCES_FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            Log.d(TAG, "EncryptedSharedPreferences créées avec succès");
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "ERREUR GeneralSecurityException dans TokenManager init", e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "ERREUR IOException dans TokenManager init", e);
            throw e;
        }
    }

    public static synchronized TokenManager getInstance(Context context) throws GeneralSecurityException, IOException {
        Log.d(TAG, "getInstance() appelé");
        if (instance == null) {
            Log.d(TAG, "Instance est null, création d'une nouvelle TokenManager...");
            instance = new TokenManager(context);
            Log.d(TAG, "Nouvelle TokenManager créée");
        } else {
            Log.d(TAG, "Réutilisation de l'instance existante");
        }
        return instance;
    }

    public void saveToken(String token, int userId, String identifiant) {
        Log.d(TAG, "saveToken() appelé - userId: " + userId + ", identifiant: " + identifiant);
        try {
            encryptedSharedPref.edit()
                    .putString(TOKEN_KEY, token)
                    .putInt(USER_ID_KEY, userId)
                    .putString(USER_IDENTIFIANT_KEY, identifiant)
                    .apply();
            Log.d(TAG, "Token sauvegardé avec succès");
        } catch (Exception e) {
            Log.e(TAG, "ERREUR lors de la sauvegarde du token", e);
        }
    }

    public String getToken() {
        String token = encryptedSharedPref.getString(TOKEN_KEY, null);
        Log.d(TAG, "getToken() - token présent: " + (token != null));
        return token;
    }

    public int getUserId() {
        int userId = encryptedSharedPref.getInt(USER_ID_KEY, -1);
        Log.d(TAG, "getUserId() - userId: " + userId);
        return userId;
    }

    public String getUserIdentifiant() {
        String identifiant = encryptedSharedPref.getString(USER_IDENTIFIANT_KEY, null);
        Log.d(TAG, "getUserIdentifiant() - identifiant présent: " + (identifiant != null));
        return identifiant;
    }

    public boolean isTokenValid() {
        String token = getToken();
        boolean isValid = token != null && !token.isEmpty();
        Log.d(TAG, "isTokenValid() retourne: " + isValid);
        return isValid;
    }

    public void clearToken() {
        Log.d(TAG, "clearToken() appelé");
        try {
            encryptedSharedPref.edit()
                    .remove(TOKEN_KEY)
                    .remove(USER_ID_KEY)
                    .remove(USER_IDENTIFIANT_KEY)
                    .apply();
            Log.d(TAG, "Token effacé avec succès");
        } catch (Exception e) {
            Log.e(TAG, "ERREUR lors de l'effacement du token", e);
        }
    }

    public void logout() {
        Log.d(TAG, "logout() appelé");
        clearToken();
    }
}
