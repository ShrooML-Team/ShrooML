package com.shrooml;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.models.IdentificationEntity;
import com.shrooml.services.KindwiseService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IdentifyActivity extends AppCompatActivity {
    private ImageButton btnGallery;

    private ImageButton btnTakePhoto;
    private ImageView mushroomImage;

    private File photoFile;

    private ActivityResultLauncher<Uri> takePhotoLauncher;

    private KindwiseService identify_API;

    private ActivityResultLauncher<String> pickImageLauncher;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private String mushroomIdentify;

    private Double accuracyIdentify;

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        mushroomImage.setImageURI(uri);
                        try{
                            File tempFile = uriToFile(uri);
                            callApi(tempFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(IdentifyActivity.this, "Erreur lecture fichier", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        setupCameraLauncher();
        setupCameraButton();

        identify_API = new KindwiseService();

        setupImagePicker();

        btnGallery = findViewById(R.id.btnGallery);
        mushroomImage = findViewById(R.id.mushroomImage);

        btnGallery.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_locate);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.nav_identify) {
                    startActivity(new Intent(IdentifyActivity.this, ChoiceIdentifyActivity.class));
                    return true;
                }
                if (id == R.id.nav_locate) {
                    startActivity(new Intent(IdentifyActivity.this, ShroomLocateActivity.class));
                    return true;
                }
                if (id == R.id.nav_quiz) {
                    startActivity(new Intent(IdentifyActivity.this, QuizActivity.class));
                    return true;
                }
                if (id == R.id.nav_home) {
                    startActivity(new Intent(IdentifyActivity.this, SplashActivity.class));
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée → lancer la caméra
                launchCamera();
            } else {
                // Permission refusée
                Toast.makeText(this, "Permission caméra requise pour prendre une photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File uriToFile(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("mushroom_", ".jpg", getCacheDir());
        OutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();
        return tempFile;
    }

    // Initialiser le launcher dans onCreate ou dans une fonction setup
    private void setupCameraLauncher() {
        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        Glide.with(IdentifyActivity.this)
                                .load(photoFile)
                                .centerCrop()
                                .into(mushroomImage);

                        // Envoyer à ton API d'identification
                        callApi(photoFile);
                    }
                }
        );
    }

    //Créer un fichier temporaire pour la caméra
    private File createImageFile() throws IOException {
        return File.createTempFile("mushroom_photo_", ".jpg", getCacheDir());
    }

    //Bouton caméra
    private void setupCameraButton() {
        btnTakePhoto = findViewById(R.id.btnTakePhoto);

        btnTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission non accordée → demander
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                // Permission déjà accordée → lancer la caméra
                launchCamera();
            }
        });
    }

    private void callApi(File file) {
        identify_API.identificationImg(file.getAbsolutePath(), new KindwiseService.IdentificationCallback() {
            @Override
            public void onSuccess(IdentificationEntity identification) {
                if (identification.getResult().getIs_mushroom().getBinary().equals("true")) {
                    IdentifyActivity.this.mushroomIdentify = identification.getResult().
                            getClassification().
                            getSuggestions().get(0).getName();
                    IdentifyActivity.this.accuracyIdentify = identification.getResult().
                            getClassification().
                            getSuggestions().get(0).getProbability();
                    Intent intent_id = new Intent(IdentifyActivity.this, IdentifyDetailsActivity.class);
                    intent_id.putExtra("scientificName", IdentifyActivity.this.mushroomIdentify);
                    intent_id.putExtra("accuracy", IdentifyActivity.this.accuracyIdentify);
                    file.delete();
                    startActivity(intent_id);
                } else {
                    Toast.makeText(IdentifyActivity.this, "Cette photo n'est pas un champignon", LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(IdentifyActivity.this, errorMessage, LENGTH_SHORT).show();
                file.delete();
            }
        });
    }

    private void launchCamera() {
        try {
            photoFile = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );
            takePhotoLauncher.launch(photoUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Impossible de créer le fichier photo", Toast.LENGTH_SHORT).show();
        }
    }
}
