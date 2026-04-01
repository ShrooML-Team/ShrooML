package com.shrooml.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.shrooml.R;
import com.shrooml.IdentifyDetailsActivity;
import com.shrooml.TokenManager;
import com.shrooml.models.IdentificationEntity;
import com.shrooml.services.KindwiseService;
import com.shrooml.services.UserService;
import com.shrooml.services.api.IdentificationHistoryCreateRequest;
import com.shrooml.services.api.IdentificationHistoryResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Locale;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
public class CameraFragment extends Fragment {

    private ImageButton btnGallery;
    private ImageButton btnTakePhoto;
    private ImageView mushroomImage;
    private File photoFile;
    private ActivityResultLauncher<Uri> takePhotoLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;
    private KindwiseService identify_API;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private String mushroomIdentify;
    private Double accuracyIdentify;
    private SensorManager sensorManager;

    // Variable NON-statique : elle indique si on a déjà vérifié la lumière pour cette visite
    private boolean hasCheckedLight = false;
    private Sensor lightSensor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        initViews(view);
        setupImagePicker();
        setupCameraLauncher();
        setupCameraButton();

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }        identify_API = new KindwiseService();

        return view;
    }

    private void initViews(View view) {
        btnGallery = view.findViewById(R.id.btnGallery);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        mushroomImage = view.findViewById(R.id.mushroomImage);

        btnGallery.setOnClickListener(v -> pickImageLauncher.launch("image/*"));


    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && getContext() != null) {
                        mushroomImage.setImageURI(uri);
                        try {
                            File tempFile = uriToFile(uri);
                            callApi(tempFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                                Toast.makeText(getContext(), getString(R.string.camera_file_read_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void setupCameraLauncher() {
        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result && getContext() != null) {
                        Glide.with(getContext())
                                .load(photoFile)
                                .centerCrop()
                                .into(mushroomImage);
                        callApi(photoFile);
                    }
                }
        );
    }

    private void setupCameraButton() {
        btnTakePhoto.setOnClickListener(v -> {
            if (getContext() == null) return;

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                launchCamera();
            }
        });
    }

    private void launchCamera() {
        try {
            photoFile = createImageFile();
            if (getActivity() == null) return;

            Uri photoUri = FileProvider.getUriForFile(
                    getActivity(),
                    getActivity().getPackageName() + ".fileprovider",
                    photoFile
            );
            takePhotoLauncher.launch(photoUri);
        } catch (IOException e) {
            e.printStackTrace();
              Toast.makeText(getContext(), getString(R.string.camera_create_photo_error), Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        return File.createTempFile("mushroom_photo_", ".jpg", requireContext().getCacheDir());
    }

    private File uriToFile(Uri uri) throws IOException {
        if (getContext() == null) throw new IOException("Context is null");

        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("mushroom_", ".jpg", requireContext().getCacheDir());
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

    private void callApi(File file) {
        identify_API.identificationImg(file.getAbsolutePath(), new KindwiseService.IdentificationCallback() {
            @Override
            public void onSuccess(IdentificationEntity identification) {
                if (identification.getResult().getIs_mushroom().getBinary().equals("true")) {
                    mushroomIdentify = identification.getResult().getClassification()
                            .getSuggestions().get(0).getName();
                    accuracyIdentify = identification.getResult().getClassification()
                            .getSuggestions().get(0).getProbability();
                    saveIdentificationToHistory(mushroomIdentify, accuracyIdentify);
                    Intent intent_id = new Intent(getContext(), IdentifyDetailsActivity.class);
                    intent_id.putExtra("scientificName", mushroomIdentify);
                    intent_id.putExtra("accuracy", accuracyIdentify);
                    file.delete();
                    startActivity(intent_id);
                } else {
                        Toast.makeText(getContext(), getString(R.string.camera_not_mushroom), LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, LENGTH_SHORT).show();
                file.delete();
            }
        });
    }

    private void saveIdentificationToHistory(String mushroomName, Double probability) {
        if (getContext() == null || mushroomName == null || mushroomName.trim().isEmpty()) {
            return;
        }

        try {
            TokenManager tokenManager = TokenManager.getInstance(getContext());
            String authToken = tokenManager.getToken();
            if (authToken == null || authToken.isEmpty()) {
                return;
            }

            float score = 0f;
            if (probability != null && !probability.isNaN() && !probability.isInfinite()) {
                score = (float) Math.max(0d, Math.min(100d, probability * 100d));
            }

            IdentificationHistoryCreateRequest request = new IdentificationHistoryCreateRequest(
                    mushroomName,
                    score,
                    null,
                    null,
                    null,
                    null,
                    String.format(Locale.US, "Identification camera (%.1f%%)", score)
            );

            UserService userService = new UserService(authToken);
            userService.createIdentificationHistory(request, new UserService.HistoryEntryCallback() {
                @Override
                public void onSuccess(IdentificationHistoryResponse entry) {
                    // Enregistrement réussi, aucune action UI requise ici.
                }

                @Override
                public void onError(String errorMessage) {
                    android.util.Log.w("CameraFragment", "Historique non enregistre: " + errorMessage);
                }
            });
        } catch (GeneralSecurityException | IOException e) {
            android.util.Log.e("CameraFragment", "Impossible d'initialiser TokenManager pour l'historique", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                 Toast.makeText(getContext(), getString(R.string.camera_permission_required), Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        // 1. L'écran s'affiche : on remet notre sécurité à zéro
        hasCheckedLight = false;

        if (lightSensor != null) {
            // 2. On attend 500ms pour que le capteur de lumière se calibre (évite le bug des 0 lux)
            new android.os.Handler().postDelayed(() -> {

                if (!isAdded() || isDetached() || isRemoving() || getActivity() == null || getActivity().isFinishing()) return;

                sensorManager.registerListener(new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        // Si on a déjà vérifié, on ignore les valeurs suivantes
                        if (hasCheckedLight) return;

                        float lux = event.values[0];

                        // 3. Si la lumière est vraiment basse (< 15 lux), on affiche le Toast
                        if (lux < 15) {
                            Toast.makeText(getContext(),
                                        getString(R.string.camera_low_light),
                                    Toast.LENGTH_LONG).show();
                        }

                        // 4. On a notre réponse ! On valide et on coupe le capteur pour économiser la batterie
                        hasCheckedLight = true;
                        sensorManager.unregisterListener(this);
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
                }, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

            }, 500); // 500 ms de délai au réveil
        }
    }
}
