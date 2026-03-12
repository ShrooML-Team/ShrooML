package com.shrooml.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shrooml.R;
import com.shrooml.services.api.AutoMLRetrofitClient;
import com.shrooml.services.api.Requests.LoginRequest;
import com.shrooml.services.api.Response.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LoginActivity - Avec OAuth2
 *
 * Flows:
 * 1. Utilisateur rentre username + password
 * 2. Appel POST /login
 * 3. Reçoit le token JWT
 * 4. SAUVEGARDE le token: setAuthToken(token)
 * 5. Token utilisé automatiquement pour /fit, /predict, /eval
 *
 * Changements clés:
 * - AutoMLRetrofitClient.getInstance().setAuthToken(token)
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button loginButton;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.et_username);
        passwordField = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);

        loginButton.setOnClickListener(v -> performLogin());
    }

    /**
     * Effectuer le login et sauvegarder le token
     */
    private void performLogin() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Login tentative: " + username);

        // ============================================================
        // Créer la requête /login
        // ============================================================
        LoginRequest loginRequest = new LoginRequest(username, password);

        Call<LoginResponse> call = AutoMLRetrofitClient.getInstance()
                .getAutoMLApi()
                .login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // ============================================================
                    // ✅ LOGIN RÉUSSI - SAUVEGARDER LE TOKEN
                    // ============================================================

                    String token = response.body().getAccessToken();
                    String tokenType = response.body().getTokenType();

                    Log.d(TAG, "✅ Login réussi!");
                    Log.d(TAG, "Token type: " + tokenType);
                    Log.d(TAG, "Token: " + token.substring(0, Math.min(50, token.length())) + "...");

                    // 🔐 SAUVEGARDER LE TOKEN - C'EST LA CLEF!
                    AutoMLRetrofitClient.getInstance().setAuthToken(token);

                    // (Optionnel) Afficher le statut
                    AutoMLRetrofitClient.getInstance().printStatus();

                    Toast.makeText(LoginActivity.this,
                            "Login réussi! Bienvenue " + username,
                            Toast.LENGTH_SHORT).show();

                    // Aller à l'écran suivant
                    startActivity(new Intent(LoginActivity.this, CustomerHomeActivity.class));
                    finish();

                } else {
                    // ============================================================
                    // ❌ LOGIN ÉCHOUÉ
                    // ============================================================

                    String errorMsg = "Erreur: " + response.code();
                    if (response.code() == 401) {
                        errorMsg = "Identifiants invalides";
                    }

                    Log.e(TAG, "❌ Login échoué: " + response.code());
                    Log.e(TAG, "Body: " + response.errorBody());

                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // ============================================================
                // ❌ ERREUR RÉSEAU
                // ============================================================

                String errorMsg = "Erreur réseau: " + t.getMessage();

                Log.e(TAG, "❌ Erreur réseau: " + t.getMessage());
                t.printStackTrace();

                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * (Optionnel) Afficher l'état de l'authentification
     */
    private void checkAuthStatus() {
        boolean authenticated = AutoMLRetrofitClient.getInstance().isAuthenticated();
        String token = AutoMLRetrofitClient.getInstance().getAuthToken();

        Log.d(TAG, "Auth Status: " + authenticated);
        Log.d(TAG, "Token: " + (token != null ? token.substring(0, 50) + "..." : "null"));
    }

    /**
     * (Optionnel) Logout (utiliser dans une autre activity)
     */
    public static void logout() {
        AutoMLRetrofitClient.getInstance().clearAuthToken();
        Log.d("Logout", "Utilisateur déconnecté");
    }
}