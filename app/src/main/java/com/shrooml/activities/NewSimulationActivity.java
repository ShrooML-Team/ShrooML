package com.shrooml.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shrooml.R;
import com.shrooml.models.Mushroom; // Ton modèle de données
import com.shrooml.services.AutoMLModels;
import com.shrooml.services.AutoMLService;
import com.shrooml.services.ShroomLocService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewSimulationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivPreview;
    private Button btnSelectImage, btnAnalyze;
    private TextView tvResult;
    private ProgressBar pbLoader;

    private Bitmap selectedBitmap;

    // Services du projet
    private AutoMLService autoMLService;
    private ShroomLocService shroomLocService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sim);

        initUI();

        // Initialisation des services configurés dans ton projet
        autoMLService = new AutoMLService();
        shroomLocService = new ShroomLocService(this);

        btnSelectImage.setOnClickListener(v -> openGallery());
        btnAnalyze.setOnClickListener(v -> runAnalysis());
    }

    private void initUI() {
        ivPreview = findViewById(R.id.ivPreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        tvResult = findViewById(R.id.tvResult);
        pbLoader = findViewById(R.id.pbLoader);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ivPreview.setImageBitmap(selectedBitmap);
                btnAnalyze.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Toast.makeText(this, "Erreur de chargement d'image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void runAnalysis() {
        if (selectedBitmap == null) return;

        toggleLoading(true);

        // Utilise la méthode predictImage déjà présente dans ton AutoMLService
        // Note: Le token doit idéalement être récupéré via ton service d'auth
        String token = "TON_TOKEN_GOOGLE_CLOUD";

        autoMLService.predictImage(selectedBitmap, token, new Callback<AutoMLModels.PredictResponse>() {
            @Override
            public void onResponse(Call<AutoMLModels.PredictResponse> call, Response<AutoMLModels.PredictResponse> response) {
                toggleLoading(false);

                if (response.isSuccessful() && response.body() != null && !response.body().predictions.isEmpty()) {

                    // 1. Récupérer le résultat de l'IA (le premier est le plus pertinent)
                    AutoMLModels.PredictionResult prediction = response.body().predictions.get(0);
                    String foundName = prediction.displayName;

                    // 2. Exploiter le ShroomLocService pour faire le lien avec la base locale
                    Mushroom mushroomData = shroomLocService.getMushroomByName(foundName);

                    updateUIWithResult(mushroomData, prediction.classification.score);
                } else {
                    tvResult.setText("Champignon inconnu ou erreur API.");
                }
            }

            @Override
            public void onFailure(Call<AutoMLModels.PredictResponse> call, Throwable t) {
                toggleLoading(false);
                tvResult.setText("Erreur réseau : " + t.getMessage());
            }
        });
    }

    private void updateUIWithResult(Mushroom mushroom, float score) {
        if (mushroom != null) {
            // On affiche les vraies infos provenant de ton JSON local (shroomLocService)
            String info = "C'est un(e) : " + mushroom.getName() + "\n" +
                    "Fiabilité : " + String.format("%.1f", score * 100) + "%\n" +
                    "Catégorie : " + mushroom.getCategory();
            tvResult.setText(info);
        } else {
            tvResult.setText("Identifié comme '" + mushroom.getName() + "' mais absent de la base locale.");
        }
    }

    private void toggleLoading(boolean isLoading) {
        pbLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnAnalyze.setEnabled(!isLoading);
        tvResult.setText(isLoading ? "Analyse ShrooML en cours..." : "");
    }
}