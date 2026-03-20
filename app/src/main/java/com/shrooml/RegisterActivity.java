package com.shrooml;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.shrooml.services.OAuthService;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class RegisterActivity extends AppCompatActivity {

    private EditText identifiantInput;
    private EditText emailInput;
    private EditText motDePasseInput;
    private EditText motDePasseConfirmInput;
    private Button registerButton;
    private Button backButton;
    private OAuthService oAuthService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        identifiantInput = findViewById(R.id.identifiant_input);
        emailInput = findViewById(R.id.email_input);
        motDePasseInput = findViewById(R.id.mot_de_passe_input);
        motDePasseConfirmInput = findViewById(R.id.champignon_prefere_input);
        registerButton = findViewById(R.id.register_button);
        backButton = findViewById(R.id.back_button);

        // Initialize services
        oAuthService = new OAuthService(true);
        try {
            tokenManager = TokenManager.getInstance(this);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur d'initialisation", Toast.LENGTH_SHORT).show();
        }

        // Set up register button click listener
        registerButton.setOnClickListener(v -> handleRegister());

        // Set up back button click listener
        backButton.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String identifiant = identifiantInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String motDePasse = motDePasseInput.getText().toString().trim();
        String motDePasseConfirm = motDePasseConfirmInput.getText().toString().trim();

        // Validation
        if (identifiant.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        if (identifiant.length() < 3) {
            Toast.makeText(this, "L'identifiant doit avoir au moins 3 caractères", Toast.LENGTH_SHORT).show();
            return;
        }

        if (motDePasse.length() < 8) {
            Toast.makeText(this, "Le mot de passe doit avoir au moins 8 caractères", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Veuillez entrer une adresse email valide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!motDePasse.equals(motDePasseConfirm)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        registerButton.setEnabled(false);
        registerButton.setText("Inscription...");

        oAuthService.register(identifiant, email, motDePasse, null, 
            new OAuthService.OAuthUserCallback() {
            @Override
            public void onSuccess(com.shrooml.services.api.TokenResponseFull response) {
                try {
                    tokenManager.saveToken(
                            response.getAccess_token(),
                            response.getUser().getId(),
                            response.getUser().getIdentifiant()
                    );
                        tokenManager.saveUserProfile(response.getUser());
                    Toast.makeText(RegisterActivity.this, "Inscription réussie!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(RegisterActivity.this, ChoiceIdentifyActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    registerButton.setEnabled(true);
                    registerButton.setText("S'inscrire");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
                registerButton.setText("S'inscrire");
            }
        });
    }
}
