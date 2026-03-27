package com.shrooml.fragments;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.shrooml.R;
import com.shrooml.IdentifyDetailsActivity;
import com.shrooml.models.IdentificationEntity;
import com.shrooml.services.KindwiseService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CameraFragment extends Fragment {

    private ImageButton btnGallery;
    private ImageButton btnTakePhoto;
    private ImageButton btnZoom;
    private ImageView mushroomImage;
    private File photoFile;
    private ActivityResultLauncher<Uri> takePhotoLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;
    private KindwiseService identify_API;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private String mushroomIdentify;
    private Double accuracyIdentify;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        initViews(view);
        setupImagePicker();
        setupCameraLauncher();
        setupCameraButton();
        identify_API = new KindwiseService();

        return view;
    }

    private void initViews(View view) {
        btnGallery = view.findViewById(R.id.btnGallery);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnZoom = view.findViewById(R.id.btnZoom);
        mushroomImage = view.findViewById(R.id.mushroomImage);

        btnGallery.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnZoom.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), IdentifyDetailsActivity.class);
            intent.putExtra("scientificName", mushroomIdentify);
            intent.putExtra("accuracy", accuracyIdentify);
            startActivity(intent);
        });
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
                            Toast.makeText(getContext(), "Erreur lecture fichier", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Impossible de créer le fichier photo", Toast.LENGTH_SHORT).show();
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
        if (identify_API == null) return;

        identify_API.identificationImg(file.getAbsolutePath(), new KindwiseService.IdentificationCallback() {
            @Override
            public void onSuccess(IdentificationEntity identification) {
                if (getContext() == null) return;

                if (identification.getResult().getIs_mushroom().getBinary().equals("true")) {
                    mushroomIdentify = identification.getResult()
                            .getClassification()
                            .getSuggestions().get(0).getName();
                    accuracyIdentify = identification.getResult()
                            .getClassification()
                            .getSuggestions().get(0).getProbability();
                    Toast.makeText(getContext(),
                            "Mushroom: " + mushroomIdentify + " | " + accuracyIdentify + "%",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Cette photo n'est pas un champignon", Toast.LENGTH_LONG).show();
                }
                file.delete();
            }

            @Override
            public void onError(String errorMessage) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
                file.delete();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(getContext(), "Permission caméra requise", Toast.LENGTH_SHORT).show();
            }
        }
    }
}