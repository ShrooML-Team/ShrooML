package com.shrooml;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ProfileActivity extends AppCompatActivity {

    private TokenManager tokenManager;

    private ImageView profileImage;
    private TextView userIdText;
    private TextView identifiantText;
    private TextView descriptionText;
    private TextView scoringText;
    private TextView streakText;
    private TextView niveauText;
    private TextView createdAtText;
    private TextView activeText;
    private EditText emailInput;
    private EditText favoriteMushroomInput;

    private ActivityResultLauncher<String> pickProfileImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (!initTokenManager()) {
            finish();
            return;
        }

        bindViews();
        setupImagePicker();
        setupBottomNavigation();
        populateProfile();

        Button changePhotoButton = findViewById(R.id.btnChangePhoto);
        Button saveButton = findViewById(R.id.btnSaveProfile);

        changePhotoButton.setOnClickListener(v -> pickProfileImageLauncher.launch("image/*"));

        saveButton.setOnClickListener(v -> saveProfileChanges());
    }

    private boolean initTokenManager() {
        try {
            tokenManager = TokenManager.getInstance(this);
            return true;
        } catch (GeneralSecurityException | IOException e) {
            Toast.makeText(this, "Erreur d'initialisation du profil", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void bindViews() {
        profileImage = findViewById(R.id.profileImage);
        userIdText = findViewById(R.id.profileUserIdValue);
        identifiantText = findViewById(R.id.profileIdentifiantValue);
        descriptionText = findViewById(R.id.profileDescriptionValue);
        scoringText = findViewById(R.id.profileScoringValue);
        streakText = findViewById(R.id.profileStreakValue);
        niveauText = findViewById(R.id.profileNiveauValue);
        createdAtText = findViewById(R.id.profileCreatedAtValue);
        activeText = findViewById(R.id.profileActiveValue);
        emailInput = findViewById(R.id.profileEmailInput);
        favoriteMushroomInput = findViewById(R.id.profileFavoriteInput);
    }

    private void setupImagePicker() {
        pickProfileImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        tokenManager.setUserLocalPhotoUri(uri.toString());
                        loadProfileImage();
                    }
                }
        );
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                return true;
            }
            if (id == R.id.nav_locate) {
                startActivity(new Intent(ProfileActivity.this, ShroomLocateActivity.class));
                return true;
            }
            if (id == R.id.nav_quiz) {
                startActivity(new Intent(ProfileActivity.this, QuizActivity.class));
                return true;
            }
            if (id == R.id.nav_identify) {
                startActivity(new Intent(ProfileActivity.this, IdentifyActivity.class));
                return true;
            }

            return false;
        });
    }

    private void populateProfile() {
        userIdText.setText("ID : " + tokenManager.getUserId());
        identifiantText.setText("Identifiant : " + valueOrDash(tokenManager.getUserIdentifiant()));
        descriptionText.setText("Description : " + valueOrDash(tokenManager.getUserDescription()));
        scoringText.setText("Score : " + tokenManager.getUserScoring());
        streakText.setText("Streak : " + tokenManager.getUserStreak());
        niveauText.setText("Niveau : " + tokenManager.getUserNiveau());
        createdAtText.setText("Cree le : " + valueOrDash(tokenManager.getUserCreatedAt()));
        activeText.setText("Actif : " + (tokenManager.isUserActive() ? "Oui" : "Non"));

        emailInput.setText(valueOrEmpty(tokenManager.getUserEmail()));
        favoriteMushroomInput.setText(valueOrEmpty(tokenManager.getUserChampignonPrefere()));

        loadProfileImage();
    }

    private void saveProfileChanges() {
        String email = emailInput.getText().toString().trim();
        String champignonPrefere = favoriteMushroomInput.getText().toString().trim();

        if (!email.isEmpty() && !email.contains("@")) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        tokenManager.setUserEmail(email);
        tokenManager.setUserChampignonPrefere(champignonPrefere);

        Toast.makeText(this, "Profil mis a jour localement", Toast.LENGTH_SHORT).show();
    }

    private void loadProfileImage() {
        String localUri = tokenManager.getUserLocalPhotoUri();
        String remotePhoto = tokenManager.getUserPhotoProfil();

        if (localUri != null && !localUri.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(localUri))
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .centerCrop()
                    .into(profileImage);
            return;
        }

        if (remotePhoto != null && !remotePhoto.isEmpty()) {
            Glide.with(this)
                    .load(remotePhoto)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .centerCrop()
                    .into(profileImage);
            return;
        }

        profileImage.setImageResource(R.drawable.ic_profile);
    }

    private String valueOrDash(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value;
    }

    private String valueOrEmpty(String value) {
        return (value == null) ? "" : value;
    }
}
