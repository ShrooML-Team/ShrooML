package com.shrooml;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.shrooml.services.OAuthService;
import com.shrooml.services.GoogleSignInManager;
import com.shrooml.services.api.AutoMLApi;
import com.shrooml.services.api.AutoMLRetrofitClient;
import com.shrooml.services.api.Requests.RegisterAARequest;
import com.shrooml.services.api.Response.LoginAAResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText identifiantInput;
    private EditText motDePasseInput;
    private Button loginButton;
    private Button registerButton;
    private ImageButton googleSignInButton;
    private OAuthService oAuthService;
    private TokenManager tokenManager;
    private GoogleSignInManager googleSignInManager;
    private AutoMLApi autoMLApi;
    private AutoMLRetrofitClient autoMLClient;



    // Google OAuth Client ID (REMPLACER PAR VOTRE CLIENT ID ANDROID)
    private static final String GOOGLE_CLIENT_ID = "169318318099-gutemkeimf2vho3hafrsdmuv23lnfdhm.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
        
        try {
            setContentView(R.layout.activity_login);
            Log.d(TAG, "Layout activity_login chargé");

            // Initialize views
            identifiantInput = findViewById(R.id.identifiant_input);
            motDePasseInput = findViewById(R.id.mot_de_passe_input);
            loginButton = findViewById(R.id.login_button);
            registerButton = findViewById(R.id.register_button);
            googleSignInButton = findViewById(R.id.google_signin_button);
            
            Log.d(TAG, "Vues trouvées:");
            Log.d(TAG, "  - identifiantInput: " + (identifiantInput != null));
            Log.d(TAG, "  - motDePasseInput: " + (motDePasseInput != null));
            Log.d(TAG, "  - loginButton: " + (loginButton != null));
            Log.d(TAG, "  - registerButton: " + (registerButton != null));
            Log.d(TAG, "  - googleSignInButton: " + (googleSignInButton != null));

            // Initialize services
            oAuthService = new OAuthService(true);
            Log.d(TAG, "OAuthService créé");
            
            try {
                tokenManager = TokenManager.getInstance(this);
                Log.d(TAG, "TokenManager obtenu avec succès");
            } catch (GeneralSecurityException | IOException e) {
                Log.e(TAG, "ERREUR lors de l'initialisation du TokenManager", e);
                e.printStackTrace();
                Toast.makeText(this, "Erreur d'initialisation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            try {
                autoMLClient = AutoMLRetrofitClient.getInstance(this);
                autoMLApi = autoMLClient.getAutoMLApi();
                Log.d(TAG, "AutoML API client initialisé");
            } catch (Exception e) {
                Log.e(TAG, "ERREUR AutoML client", e);
            }


            // Initialize Google Sign-In Manager
            googleSignInManager = new GoogleSignInManager(this, GOOGLE_CLIENT_ID);
            Log.d(TAG, "GoogleSignInManager créé");

            // Google Sign-In est maintenant activé
            Log.d(TAG, "Google Sign-In activé");
            googleSignInButton.setEnabled(true);

            // Set up login button click listener
            loginButton.setOnClickListener(v -> {
                Log.d(TAG, "Bouton Login cliqué");
                handleLogin();
            });

            // Set up register button click listener
            registerButton.setOnClickListener(v -> {
                Log.d(TAG, "Bouton Register cliqué");
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            });

            // Set up Google Sign-In button click listener
            googleSignInButton.setOnClickListener(v -> {
                Log.d(TAG, "Bouton Google Sign-In cliqué");
                googleSignInManager.signIn(LoginActivity.this);
            });
            
            Log.d(TAG, "onCreate() terminé avec succès");

        } catch (Exception e) {
            Log.e(TAG, "ERREUR CRITIQUE dans onCreate()", e);
            e.printStackTrace();
            Toast.makeText(this, "ERREUR: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Traiter le résultat Google Sign-In
        googleSignInManager.handleSignInResult(requestCode, resultCode, data, new GoogleSignInManager.GoogleSignInCallback() {
            @Override
            public void onSuccess(com.shrooml.services.api.TokenResponseFull response) {
                try {
                    tokenManager.saveToken(
                            response.getAccess_token(),
                            response.getUser().getId(),
                            response.getUser().getIdentifiant()
                    );
                        tokenManager.saveUserProfile(response.getUser());
                    Toast.makeText(LoginActivity.this, "Connexion Google réussie!", Toast.LENGTH_SHORT).show();


                    String identifiant = identifiantInput.getText().toString().trim();
                    String motDePasse = motDePasseInput.getText().toString().trim();
                    syncWithAutoML(identifiant,motDePasse);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLogin() {
        Log.d(TAG, "handleLogin() appelé");
        
        String identifiant = identifiantInput.getText().toString().trim();
        String motDePasse = motDePasseInput.getText().toString().trim();
        
        Log.d(TAG, "Valeurs saisies - identifiant: " + (identifiant.isEmpty() ? "VIDE" : "OK") + 
                           ", motDePasse: " + (motDePasse.isEmpty() ? "VIDE" : "OK"));

        if (identifiant.isEmpty() || motDePasse.isEmpty()) {
            Log.w(TAG, "Tentative de connexion avec champs vides");
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Connexion...");
        Log.d(TAG, "Appel de oAuthService.loginFull()");

        oAuthService.loginFull(identifiant, motDePasse, new OAuthService.OAuthUserCallback() {
            @Override
            public void onSuccess(com.shrooml.services.api.TokenResponseFull response) {
                Log.d(TAG, "Succès de la connexion");
                try {
                    if (response == null) {
                        Log.e(TAG, "Response est null!");
                        Toast.makeText(LoginActivity.this, "Erreur: Réponse vide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    Log.d(TAG, "Sauvegarde du token...");
                    tokenManager.saveToken(
                            response.getAccess_token(),
                            response.getUser().getId(),
                            response.getUser().getIdentifiant()
                    );
                        tokenManager.saveUserProfile(response.getUser());
                    Log.d(TAG, "Token sauvegardé avec succès");

                    syncWithAutoML(identifiant, motDePasse);

                } catch (Exception e) {
                    Log.e(TAG, "ERREUR lors de la sauvegarde du token", e);
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Se connecter");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "ERREUR de connexion: " + errorMessage);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                loginButton.setEnabled(true);
                loginButton.setText("Se connecter");
            }
        });
    }
    private void syncWithAutoML(String username, String password) {
        if (autoMLApi == null) {
            Log.e(TAG, "AutoML API non initialisée");
            goToMainActivity();
            return;
        }

        Log.d(TAG, "Synchronisation avec AutoML pour: " + username);

        // Tentative de login AutoML
        Call<LoginAAResponse> loginCall = autoMLApi.login(
                "password",
                username,
                password,
                "",
                "",
                ""
        );

        loginCall.enqueue(new Callback<LoginAAResponse>() {
            @Override
            public void onResponse(Call<LoginAAResponse> call, Response<LoginAAResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Login AutoML réussi
                    String token = response.body().getAccessToken();
                    autoMLClient.setAuthToken(token);
                    Log.d(TAG, "AutoML login réussi");
                    goToMainActivity();
                } else if (response.code() == 401) {
                    // Utilisateur n'existe pas → créer un compte
                    Log.d(TAG, "Utilisateur AutoML inexistant, création...");
                    registerInAutoML(username, password);
                } else {
                    Log.e(TAG, "AutoML login échoué: " + response.code());
                    goToMainActivity(); // On continue quand même
                }
            }

            @Override
            public void onFailure(Call<LoginAAResponse> call, Throwable t) {
                Log.e(TAG, "AutoML login error: " + t.getMessage());
                goToMainActivity(); // On continue quand même
            }
        });
    }

    private void registerInAutoML(String username, String password) {
        RegisterAARequest registerAARequest = new RegisterAARequest(username, password);
        Call<LoginAAResponse> registerCall = autoMLApi.register(registerAARequest);

        registerCall.enqueue(new Callback<LoginAAResponse>() {
            @Override
            public void onResponse(Call<LoginAAResponse> call, Response<LoginAAResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    autoMLClient.setAuthToken(token);
                    Log.d(TAG, "AutoML compte créé avec succès");
                    Toast.makeText(LoginActivity.this, "Compte AutoML créé", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "AutoML registration failed: " + response.code());
                }
                goToMainActivity();
            }

            @Override
            public void onFailure(Call<LoginAAResponse> call, Throwable t) {
                Log.e(TAG, "AutoML registration error: " + t.getMessage());
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, ChoiceIdentifyActivity.class);
        startActivity(intent);
        finish();
    }
}
