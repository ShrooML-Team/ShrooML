package com.shrooml;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.shrooml.models.MushroomEntity;
import com.shrooml.services.OAuthService;
import com.shrooml.services.ShroomLocService;
import com.shrooml.services.UserService;
import com.shrooml.services.api.ShroomLocRetrofitClient;
import com.shrooml.services.api.UpdateUserRequest;
import com.shrooml.services.api.UserPhotoUploadResponse;
import com.shrooml.services.api.UserResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileActivity extends AppCompatActivity {

    private static final long TOKEN_REFRESH_THRESHOLD_SECONDS = 120L;

    private static final String[] CREATED_AT_PATTERNS = new String[] {
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
    };

    private TokenManager tokenManager;

    private ImageView profileImage;
    private TextView identifiantText;
    private TextView descriptionText;
    private TextView scoringText;
    private TextView streakText;
    private TextView niveauText;
    private TextView createdAtText;
    private EditText emailInput;
    private AutoCompleteTextView favoriteMushroomInput;
    private Button saveButton;

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
        setupFavoriteMushroomSuggestions();
        populateProfile();

        Button changePhotoButton = findViewById(R.id.btnChangePhoto);
        saveButton = findViewById(R.id.btnSaveProfile);

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
        identifiantText = findViewById(R.id.profileIdentifiantValue);
        descriptionText = findViewById(R.id.profileDescriptionValue);
        scoringText = findViewById(R.id.profileScoringValue);
        streakText = findViewById(R.id.profileStreakValue);
        niveauText = findViewById(R.id.profileNiveauValue);
        createdAtText = findViewById(R.id.profileCreatedAtValue);
        emailInput = findViewById(R.id.profileEmailInput);
        favoriteMushroomInput = findViewById(R.id.profileFavoriteInput);
    }

    private void setupFavoriteMushroomSuggestions() {
        OAuthService authService = new OAuthService();
        authService.login("admin", "password123", new OAuthService.OAuthCallback() {
            @Override
            public void onSuccess(String token) {
                ShroomLocRetrofitClient.setToken(token);
                loadFavoriteMushroomSuggestions();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProfileActivity.this, "Connexion a l'API des champignons impossible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFavoriteMushroomSuggestions() {
        ShroomLocService shroomLocService = new ShroomLocService();
        shroomLocService.getAll(new ShroomLocService.MushroomsCallback() {
            @Override
            public void onSuccess(List<MushroomEntity> mushrooms) {
                Set<String> commonNames = new LinkedHashSet<>();

                for (MushroomEntity mushroom : mushrooms) {
                    if (mushroom == null || mushroom.getCommon_name() == null) {
                        continue;
                    }

                    String commonName = mushroom.getCommon_name().trim();
                    if (!commonName.isEmpty()) {
                        commonNames.add(commonName);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        ProfileActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        new ArrayList<>(commonNames)
                );

                favoriteMushroomInput.setAdapter(adapter);
                favoriteMushroomInput.setOnClickListener(v -> favoriteMushroomInput.showDropDown());
                favoriteMushroomInput.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        favoriteMushroomInput.showDropDown();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProfileActivity.this, "Liste des champignons indisponible", Toast.LENGTH_SHORT).show();
            }
        });
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
        identifiantText.setText("Identifiant : " + valueOrDash(tokenManager.getUserIdentifiant()));
        descriptionText.setText("Description : " + valueOrDash(tokenManager.getUserDescription()));
        scoringText.setText("Score : " + tokenManager.getUserScoring());
        streakText.setText("Streak : " + tokenManager.getUserStreak());
        niveauText.setText("Niveau : " + tokenManager.getUserNiveau());
        createdAtText.setText("Cree le : " + formatCreatedAt(tokenManager.getUserCreatedAt()));
        emailInput.setText(valueOrEmpty(tokenManager.getUserEmail()));
        favoriteMushroomInput.setText(valueOrEmpty(tokenManager.getUserChampignonPrefere()));

        loadProfileImage();
    }

    private void saveProfileChanges() {
        String authToken = tokenManager.getToken();
        String email = emailInput.getText().toString().trim();
        String champignonPrefere = favoriteMushroomInput.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Email requis", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(this, "Session invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        saveButton.setEnabled(false);
        saveButton.setText("Sauvegarde...");

        if (tokenManager.isTokenExpired()) {
            redirectToLogin();
            return;
        }

        if (tokenManager.isTokenExpiringSoon(TOKEN_REFRESH_THRESHOLD_SECONDS)) {
            OAuthService authService = new OAuthService(true);
            authService.refreshUserSession(authToken, new OAuthService.OAuthUserCallback() {
                @Override
                public void onSuccess(com.shrooml.services.api.TokenResponseFull response) {
                    tokenManager.updateToken(response.getAccess_token());
                    tokenManager.saveUserProfile(response.getUser());
                    continueProfileSave(response.getAccess_token(), email, champignonPrefere);
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        saveButton.setEnabled(true);
                        saveButton.setText("Sauvegarder");
                    });

                    if (errorMessage.contains("401")) {
                        redirectToLogin();
                        return;
                    }

                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
            return;
        }

        continueProfileSave(authToken, email, champignonPrefere);
    }

    private void continueProfileSave(String authToken, String email, String champignonPrefere) {
        UserService userService = new UserService(authToken);

        uploadProfilePhotoIfNeeded(userService, new Runnable() {
            @Override
            public void run() {
                UpdateUserRequest request = new UpdateUserRequest(
                        email,
                        null,
                        champignonPrefere,
                    null,
                    null
                );

                userService.updateCurrentUserProfile(request, new UserService.UserProfileCallback() {
                    @Override
                    public void onSuccess(UserResponse user) {
                        tokenManager.saveUserProfile(user);
                        tokenManager.setUserEmail(user.getEmail());
                        tokenManager.setUserChampignonPrefere(user.getChampignon_prefere());

                        runOnUiThread(() -> {
                            populateProfile();
                            saveButton.setEnabled(true);
                            saveButton.setText("Sauvegarder");
                            Toast.makeText(ProfileActivity.this, "Profil mis a jour", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if ("Session expirée, reconnectez-vous".equals(errorMessage)) {
                            redirectToLogin();
                            return;
                        }

                        runOnUiThread(() -> {
                            saveButton.setEnabled(true);
                            saveButton.setText("Sauvegarder");
                            Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }

    private void uploadProfilePhotoIfNeeded(UserService userService, Runnable onSuccess) {
        String localUri = tokenManager.getUserLocalPhotoUri();
        if (localUri == null || localUri.isEmpty()) {
            onSuccess.run();
            return;
        }

        try {
            MultipartBody.Part photoPart = buildPhotoPart(Uri.parse(localUri));
            userService.uploadCurrentUserProfilePhoto(photoPart, new UserService.UserPhotoCallback() {
                @Override
                public void onSuccess(UserPhotoUploadResponse response) {
                    tokenManager.setUserPhotoProfil(response.getPhoto_profil());
                    tokenManager.clearUserLocalPhotoUri();
                    runOnUiThread(() -> {
                        loadProfileImage();
                        onSuccess.run();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    if ("Session expirée, reconnectez-vous".equals(errorMessage)) {
                        redirectToLogin();
                        return;
                    }

                    runOnUiThread(() -> {
                        saveButton.setEnabled(true);
                        saveButton.setText("Sauvegarder");
                        Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (IOException e) {
            saveButton.setEnabled(true);
            saveButton.setText("Sauvegarder");
            Toast.makeText(this, "Impossible de lire la photo selectionnee", Toast.LENGTH_SHORT).show();
        }
    }

    private MultipartBody.Part buildPhotoPart(Uri photoUri) throws IOException {
        String mimeType = getContentResolver().getType(photoUri);
        if (mimeType == null || mimeType.isEmpty()) {
            mimeType = "image/*";
        }

        String fileName = getFileName(photoUri);
        File tempFile = new File(getCacheDir(), fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(photoUri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            if (inputStream == null) {
                throw new IOException("Flux image indisponible");
            }

            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }

        RequestBody requestBody = RequestBody.create(tempFile, MediaType.parse(mimeType));
        return MultipartBody.Part.createFormData("photo", fileName, requestBody);
    }

    private String getFileName(Uri uri) {
        String fallbackName = "profile_photo.jpg";

        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    String displayName = cursor.getString(nameIndex);
                    if (displayName != null && !displayName.trim().isEmpty()) {
                        return displayName;
                    }
                }
            }
        }

        return fallbackName;
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

    private String formatCreatedAt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        String trimmedValue = value.trim();
        for (String pattern : CREATED_AT_PATTERNS) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(pattern, Locale.US);
                inputFormat.setLenient(false);
                Date parsedDate = inputFormat.parse(trimmedValue);

                if (parsedDate != null) {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", Locale.FRENCH);
                    return outputFormat.format(parsedDate);
                }
            } catch (ParseException ignored) {
                // Essaie le format suivant.
            }
        }

        return trimmedValue;
    }

    private String valueOrEmpty(String value) {
        return (value == null) ? "" : value;
    }

    private void redirectToLogin() {
        tokenManager.logout();
        runOnUiThread(() -> {
            saveButton.setEnabled(true);
            saveButton.setText("Sauvegarder");
            Toast.makeText(ProfileActivity.this, "Session expirée, reconnectez-vous", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
