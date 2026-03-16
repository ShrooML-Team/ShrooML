package com.shrooml;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.shrooml.services.api.UserResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {
    private static final String TAG = "TokenManager";
    private static final String PREFERENCES_FILE = "shrooml_auth";
    private static final String TOKEN_KEY = "access_token";
    private static final String USER_ID_KEY = "user_id";
    private static final String USER_IDENTIFIANT_KEY = "user_identifiant";
    private static final String USER_EMAIL_KEY = "user_email";
    private static final String USER_PHOTO_PROFIL_KEY = "user_photo_profil";
    private static final String USER_DESCRIPTION_KEY = "user_description";
    private static final String USER_CHAMPIGNON_PREFERE_KEY = "user_champignon_prefere";
    private static final String USER_SCORING_KEY = "user_scoring";
    private static final String USER_STREAK_KEY = "user_streak";
    private static final String USER_NIVEAU_KEY = "user_niveau";
    private static final String USER_CREATED_AT_KEY = "user_created_at";
    private static final String USER_IS_ACTIVE_KEY = "user_is_active";
    private static final String USER_LOCAL_PHOTO_URI_KEY = "user_local_photo_uri";

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

    public void saveUserProfile(UserResponse user) {
        if (user == null) {
            return;
        }

        try {
            encryptedSharedPref.edit()
                    .putString(USER_IDENTIFIANT_KEY, user.getIdentifiant())
                    .putString(USER_EMAIL_KEY, user.getEmail())
                    .putString(USER_PHOTO_PROFIL_KEY, user.getPhoto_profil())
                    .putString(USER_DESCRIPTION_KEY, user.getDescription())
                    .putString(USER_CHAMPIGNON_PREFERE_KEY, user.getChampignon_prefere())
                    .putFloat(USER_SCORING_KEY, user.getScoring())
                    .putInt(USER_STREAK_KEY, user.getStreak())
                    .putInt(USER_NIVEAU_KEY, user.getNiveau())
                    .putString(USER_CREATED_AT_KEY, user.getCreated_at())
                    .putBoolean(USER_IS_ACTIVE_KEY, user.isIs_active())
                    .apply();
        } catch (Exception e) {
            Log.e(TAG, "ERREUR lors de la sauvegarde du profil", e);
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

    public String getUserEmail() {
        return encryptedSharedPref.getString(USER_EMAIL_KEY, null);
    }

    public String getUserPhotoProfil() {
        return encryptedSharedPref.getString(USER_PHOTO_PROFIL_KEY, null);
    }

    public String getUserLocalPhotoUri() {
        return encryptedSharedPref.getString(USER_LOCAL_PHOTO_URI_KEY, null);
    }

    public String getUserDescription() {
        return encryptedSharedPref.getString(USER_DESCRIPTION_KEY, null);
    }

    public String getUserChampignonPrefere() {
        return encryptedSharedPref.getString(USER_CHAMPIGNON_PREFERE_KEY, null);
    }

    public float getUserScoring() {
        return encryptedSharedPref.getFloat(USER_SCORING_KEY, 0f);
    }

    public int getUserStreak() {
        return encryptedSharedPref.getInt(USER_STREAK_KEY, 0);
    }

    public int getUserNiveau() {
        return encryptedSharedPref.getInt(USER_NIVEAU_KEY, 0);
    }

    public String getUserCreatedAt() {
        return encryptedSharedPref.getString(USER_CREATED_AT_KEY, null);
    }

    public boolean isUserActive() {
        return encryptedSharedPref.getBoolean(USER_IS_ACTIVE_KEY, false);
    }

    public void setUserEmail(String email) {
        encryptedSharedPref.edit().putString(USER_EMAIL_KEY, email).apply();
    }

    public void setUserChampignonPrefere(String champignonPrefere) {
        encryptedSharedPref.edit().putString(USER_CHAMPIGNON_PREFERE_KEY, champignonPrefere).apply();
    }

    public void setUserPhotoProfil(String photoProfil) {
        encryptedSharedPref.edit().putString(USER_PHOTO_PROFIL_KEY, photoProfil).apply();
    }

    public void setUserLocalPhotoUri(String photoUri) {
        encryptedSharedPref.edit().putString(USER_LOCAL_PHOTO_URI_KEY, photoUri).apply();
    }

    public void clearUserLocalPhotoUri() {
        encryptedSharedPref.edit().remove(USER_LOCAL_PHOTO_URI_KEY).apply();
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
                    .remove(USER_EMAIL_KEY)
                    .remove(USER_PHOTO_PROFIL_KEY)
                    .remove(USER_DESCRIPTION_KEY)
                    .remove(USER_CHAMPIGNON_PREFERE_KEY)
                    .remove(USER_SCORING_KEY)
                    .remove(USER_STREAK_KEY)
                    .remove(USER_NIVEAU_KEY)
                    .remove(USER_CREATED_AT_KEY)
                    .remove(USER_IS_ACTIVE_KEY)
                    .remove(USER_LOCAL_PHOTO_URI_KEY)
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
