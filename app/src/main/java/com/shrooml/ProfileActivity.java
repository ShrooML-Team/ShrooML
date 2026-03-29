package com.shrooml;

import android.content.Intent;
import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.models.MushroomEntity;
import com.shrooml.services.InaturalistService;
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
import java.text.DecimalFormat;
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

public class ProfileActivity extends BackgroundActivity {

    private static final String TAG = "ProfileActivity";
    private static final long TOKEN_REFRESH_THRESHOLD_SECONDS = 120L;
    private static final DecimalFormat SCORE_FORMAT = new DecimalFormat("0.##");

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
    private ImageView favoriteMushroomImage;
    private ImageView favoriteMushroomStatusIcon;
    private View favoriteMushroomCard;
    private TextView identifiantText;
    private TextView rangText;
    private TextView favoriteMushroomCommonText;
    private TextView favoriteMushroomScientificText;
    private TextView descriptionText;
    private TextView scoringText;
    private TextView streakText;
    private TextView niveauText;
    private TextView createdAtText;
    private EditText emailInput;
    private AutoCompleteTextView favoriteMushroomInput;
    private Button saveButton;
    private List<MushroomEntity> availableMushrooms = new ArrayList<>();

    private InaturalistService inaturalistService;

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
        inaturalistService = new InaturalistService();
        setupFavoriteMushroomSuggestions();
        populateProfile();

        saveButton = findViewById(R.id.btnSaveProfile);

        profileImage.setOnClickListener(v -> pickProfileImageLauncher.launch("image/*"));
        descriptionText.setOnClickListener(v -> showDescriptionDialog());
        favoriteMushroomCard.setOnClickListener(v -> showFavoriteMushroomDialog());
        rangText.setOnClickListener(v -> openRanking());

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
        favoriteMushroomImage = findViewById(R.id.profileFavoriteImage);
        favoriteMushroomStatusIcon = findViewById(R.id.profileFavoriteStatusIcon);
        favoriteMushroomCard = findViewById(R.id.profileFavoriteCard);
        identifiantText = findViewById(R.id.profileIdentifiantValue);
        rangText = findViewById(R.id.profileRangValue);
        favoriteMushroomCommonText = findViewById(R.id.profileFavoriteCommonValue);
        favoriteMushroomScientificText = findViewById(R.id.profileFavoriteScientificValue);
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
                availableMushrooms = (mushrooms == null) ? new ArrayList<>() : mushrooms;

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        ProfileActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        buildFavoriteMushroomNames()
                );

                favoriteMushroomInput.setAdapter(adapter);
                favoriteMushroomInput.setOnClickListener(v -> favoriteMushroomInput.showDropDown());
                favoriteMushroomInput.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        favoriteMushroomInput.showDropDown();
                    }
                });

                updateFavoriteMushroomSection(tokenManager.getUserChampignonPrefere());
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
                startActivity(new Intent(ProfileActivity.this, ChoiceIdentifyActivity.class));
                return true;
            }

            return false;
        });
    }

    private void populateProfile() {
        identifiantText.setText(valueOrDash(tokenManager.getUserIdentifiant()));
        updateRangDisplay(tokenManager.getUserRang());
        displayRandomDescription();
        scoringText.setText(String.format(getString(R.string.profile_score), formatScore(tokenManager.getUserScoring())));
        streakText.setText(String.format(getString(R.string.profile_streak), String.valueOf(tokenManager.getUserStreak())));
        niveauText.setText(String.format(getString(R.string.profile_niveau), String.valueOf(tokenManager.getUserNiveau())));
        createdAtText.setText(String.format(getString(R.string.profile_created_at), formatCreatedAt(tokenManager.getUserCreatedAt())));
        emailInput.setText(valueOrEmpty(tokenManager.getUserEmail()));
        String preferredMushroom = valueOrEmpty(tokenManager.getUserChampignonPrefere());
        favoriteMushroomInput.setText(preferredMushroom);
        updateFavoriteMushroomSection(preferredMushroom);

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
                        champignonPrefere,
                    null,
                    null
                );

                userService.updateCurrentUserProfile(request, new UserService.UserProfileCallback() {
                    @Override
                    public void onSuccess(UserResponse user) {
                            // Sauvegarder l'URL photo actuelle avant que saveUserProfile ne la remplace.
                            // Si le serveur retourne photo_profil=null (ex: mauvaise URL interne), on la restaure.
                            String existingPhotoUrl = tokenManager.getUserPhotoProfil();
                            tokenManager.saveUserProfile(user);
                            if ((user.getPhoto_profil() == null || user.getPhoto_profil().isEmpty())
                                    && existingPhotoUrl != null && !existingPhotoUrl.isEmpty()) {
                                tokenManager.setUserPhotoProfil(existingPhotoUrl);
                            }
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

    private void updateFavoriteMushroomSection(String preferredMushroomName) {
        String safeName = valueOrDash(preferredMushroomName);
        MushroomEntity matchedMushroom = findMushroomByCommonName(preferredMushroomName);

        if (matchedMushroom == null) {
            favoriteMushroomCommonText.setText(String.format(getString(R.string.profile_common_name), safeName));
            favoriteMushroomScientificText.setText(String.format(getString(R.string.profile_scientific_name), "-"));
            favoriteMushroomImage.setImageResource(R.drawable.ic_mushroom_placeholder);
            favoriteMushroomStatusIcon.setImageResource(R.drawable.ic_check);
            return;
        }

        favoriteMushroomCommonText.setText(String.format(getString(R.string.profile_common_name), valueOrDash(matchedMushroom.getCommon_name())));
        favoriteMushroomScientificText.setText(String.format(getString(R.string.profile_scientific_name), valueOrDash(matchedMushroom.getScientific_name())));
        updateFavoriteMushroomStatusIcon(matchedMushroom.getEdibility());

        // Charger l'image via iNaturalis
        String scientificName = matchedMushroom.getScientific_name();
        if (scientificName != null && !scientificName.trim().isEmpty()) {
            inaturalistService.getMushroomImage(scientificName, new InaturalistService.ImageCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    Glide.with(ProfileActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_mushroom_placeholder)
                            .error(R.drawable.ic_mushroom_placeholder)
                            .centerCrop()
                            .into(favoriteMushroomImage);
                }

                @Override
                public void onError(String errorMessage) {
                    favoriteMushroomImage.setImageResource(R.drawable.ic_mushroom_placeholder);
                }
            });
        } else {
            favoriteMushroomImage.setImageResource(R.drawable.ic_mushroom_placeholder);
        }
    }

    private void updateFavoriteMushroomStatusIcon(String edibility) {
        if (edibility == null) {
            favoriteMushroomStatusIcon.setImageResource(R.drawable.ic_check);
            return;
        }

        String normalizedEdibility = edibility.trim().toLowerCase();
        if ("inedible".equals(normalizedEdibility)) {
            favoriteMushroomStatusIcon.setImageResource(R.drawable.ic_skull);
        } else if ("medicinal".equals(normalizedEdibility)) {
            favoriteMushroomStatusIcon.setImageResource(R.drawable.ic_medicinal);
        } else {
            favoriteMushroomStatusIcon.setImageResource(R.drawable.ic_check);
        }
    }

    private MushroomEntity findMushroomByCommonName(String commonName) {
        if (commonName == null || commonName.trim().isEmpty()) {
            return null;
        }

        String normalizedName = commonName.trim();
        for (MushroomEntity mushroom : availableMushrooms) {
            if (mushroom == null || mushroom.getCommon_name() == null) {
                continue;
            }

            if (normalizedName.equalsIgnoreCase(mushroom.getCommon_name().trim())) {
                return mushroom;
            }
        }

        return null;
    }

    private List<String> buildFavoriteMushroomNames() {
        Set<String> commonNames = new LinkedHashSet<>();
        for (MushroomEntity mushroom : availableMushrooms) {
            if (mushroom == null || mushroom.getCommon_name() == null) {
                continue;
            }

            String commonName = mushroom.getCommon_name().trim();
            if (!commonName.isEmpty()) {
                commonNames.add(commonName);
            }
        }
        return new ArrayList<>(commonNames);
    }

    private void showFavoriteMushroomDialog() {
        AutoCompleteTextView input = new AutoCompleteTextView(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Nom du champignon");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(0xCCFFFFFF);
        input.setBackgroundResource(R.drawable.input_white_outline);
        input.setText(favoriteMushroomInput.getText());
        input.setSelectAllOnFocus(true);
        input.setThreshold(1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                buildFavoriteMushroomNames()
        );
        input.setAdapter(adapter);
        input.setOnClickListener(v -> input.showDropDown());
        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                input.showDropDown();
            }
        });

        int horizontalPadding = (int) (20 * getResources().getDisplayMetrics().density);
        int verticalPadding = (int) (10 * getResources().getDisplayMetrics().density);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        container.addView(input, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Changer le champignon prefere")
                .setView(container)
            .setPositiveButton("Valider", (dialogInterface, which) -> {
                    String value = input.getText().toString().trim();
                    favoriteMushroomInput.setText(value);
                    updateFavoriteMushroomSection(value);
                    saveProfileChanges();
                })
                .setNegativeButton("Annuler", null)
                .show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.item_bg);
        }

        input.post(input::showDropDown);
    }

    private void displayRandomDescription() {
        int niveau = tokenManager.getUserNiveau();
        String[] availableDescriptions = getAvailableDescriptions(niveau);
        
        if (availableDescriptions.length > 0) {
            int savedIndex = tokenManager.getUserDescriptionIndex();
            // Vérifier que l'indice sauvegardé est valide pour le niveau actuel
            if (savedIndex >= 0 && savedIndex < availableDescriptions.length) {
                descriptionText.setText(availableDescriptions[savedIndex]);
            } else {
                // Si l'indice n'est pas valide (ex: utilisateur a baissé de niveau), afficher le premier
                descriptionText.setText(availableDescriptions[0]);
            }
        }
    }

    private String[] getAvailableDescriptions(int niveau) {
        String[] allDescriptions = getResources().getStringArray(R.array.profile_descriptions);
        
        // Niveau 1-2: 5 descriptions
        // Niveau 3-4: 10 descriptions
        // Niveau 5-6: toutes (15 descriptions)
        int maxIndex;
        if (niveau <= 2) {
            maxIndex = Math.min(5, allDescriptions.length);
        } else if (niveau <= 4) {
            maxIndex = Math.min(10, allDescriptions.length);
        } else {
            maxIndex = allDescriptions.length;
        }
        
        String[] filtered = new String[maxIndex];
        System.arraycopy(allDescriptions, 0, filtered, 0, maxIndex);
        return filtered;
    }

    private void showDescriptionDialog() {
        int niveau = tokenManager.getUserNiveau();
        String[] availableDescriptions = getAvailableDescriptions(niveau);
        
        if (availableDescriptions.length == 0) {
            Toast.makeText(this, "Aucune description disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Changer de description (Niveau " + niveau + ")");
        builder.setItems(availableDescriptions, (dialog, which) -> {
            descriptionText.setText(availableDescriptions[which]);
            
            // Sauvegarder l'indice localement et sur l'API
            // L'indice 'which' correspond exactement à l'indice dans le tableau filtré
            // qui commence à 0, donc c'est aussi l'indice dans le tableau complet
            tokenManager.saveDescriptionIndex(which);
            saveDescriptionIndexToAPI(which);
            
            Toast.makeText(ProfileActivity.this, "Description changée !", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void saveDescriptionIndexToAPI(int descriptionIndex) {
        String authToken = tokenManager.getToken();
        if (authToken == null || authToken.isEmpty()) {
            return;
        }

        UserService userService = new UserService(authToken);
        UpdateUserRequest request = new UpdateUserRequest(
                null,
                null,
                null,
                null,
                descriptionIndex
        );

        userService.updateCurrentUserProfile(request, new UserService.UserProfileCallback() {
            @Override
            public void onSuccess(UserResponse user) {
                tokenManager.saveUserProfile(user);
                Log.d(TAG, "Description index sauvegardée sur l'API: " + descriptionIndex);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Erreur lors de la sauvegarde de description_index: " + errorMessage);
            }
        });
    }

    private String valueOrDash(String value) {
        return (value == null || value.trim().isEmpty()) ? "-" : value;
    }

    private String buildDisplayIdentifiant(String identifiant, int rang) {
        String safeIdentifiant = valueOrDash(identifiant);
        if (rang > 0) {
            return safeIdentifiant + "  |  Rang " + rang;
        }
        return safeIdentifiant;
    }

    private void updateRangDisplay(int rang) {
        if (rang > 0) {
            rangText.setText("#" + rang);
        } else {
            rangText.setText("-");
        }
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

    private String formatScore(float score) {
        if (Float.isNaN(score) || Float.isInfinite(score)) {
            return "0";
        }
        return SCORE_FORMAT.format(score);
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

    private void openRanking() {
        Intent intent = new Intent(ProfileActivity.this, RankingActivity.class);
        startActivity(intent);
    }
}
