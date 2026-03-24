package com.shrooml;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.R;
import com.shrooml.fragments.CameraFragment;
import com.shrooml.fragments.FormFragment;

public class IdentifyActivity extends AppCompatActivity {

    private ImageButton btnToggleMode;
    private boolean isCameraMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        btnToggleMode = findViewById(R.id.btn_toggle_mode);

        // Charger le fragment Camera par défaut
        loadFragment(new CameraFragment());

        setupToggleButton();
        setupBottomNav();
    }

    private void setupToggleButton() {
        btnToggleMode.setOnClickListener(v -> {
            if (isCameraMode) {
                // Passer en mode Formulaire
                loadFragment(new FormFragment());
                btnToggleMode.setImageResource(R.drawable.ic_camera);
                isCameraMode = false;
            } else {
                // Passer en mode Caméra
                loadFragment(new CameraFragment());
                btnToggleMode.setImageResource(R.drawable.ic_edit);
                isCameraMode = true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_identify);

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
                if (id == R.id.nav_profile) {
                    startActivity(new Intent(IdentifyActivity.this, ProfileActivity.class));
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

}