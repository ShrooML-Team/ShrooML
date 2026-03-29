package com.shrooml;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.models.MushroomCompleteEntity;
import com.shrooml.models.RecipeEntity;
import com.shrooml.services.ShroomLocService;

import java.net.URLEncoder;

public class IdentifyDetailsActivity extends BackgroundActivity {

    private TextView detailsTitle, detailsBadge, detailsGeneral, detailsEcology;
    private ImageView detailsImage;

    private LinearLayout recipeSection;
    private ImageView recipeImage;
    private TextView recipeTitle, recipeIngredients, recipeInstructions;

    private ShroomLocService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_details);

        // UI
        detailsTitle = findViewById(R.id.detailsTitle);
        detailsBadge = findViewById(R.id.detailsBadge);
        detailsGeneral = findViewById(R.id.detailsGeneral);
        detailsEcology = findViewById(R.id.detailsEcology);
        detailsImage = findViewById(R.id.detailsImage);

        recipeSection = findViewById(R.id.recipeSection);
        recipeImage = findViewById(R.id.recipeImage);
        recipeTitle = findViewById(R.id.recipeTitle);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeInstructions = findViewById(R.id.recipeInstructions);

        TextView accuracyText = findViewById(R.id.accuracyText);
        ProgressBar accuracyBar = findViewById(R.id.accuracyBar);

        // Dans onCreate(), après findViewById :
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(m -> {
            Intent intent = new Intent(IdentifyDetailsActivity.this, IdentifyActivity.class);
            startActivity(intent);
            finish();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_identify);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_identify) {
                    startActivity(new Intent(IdentifyDetailsActivity.this, IdentifyActivity.class));
                    finish();
                    return true;
                }
                if (id == R.id.nav_locate) {
                    startActivity(new Intent(IdentifyDetailsActivity.this, ShroomLocateActivity.class));
                    finish();
                    return true;
                }
                if (id == R.id.nav_quiz) {
                    startActivity(new Intent(IdentifyDetailsActivity.this, QuizActivity.class));
                    finish();
                    return true;
                }
                if (id == R.id.nav_profile) {
                    startActivity(new Intent(IdentifyDetailsActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }

                return false;
            }
        });

        api = new ShroomLocService();

        // Nom scientifique original
        String scientificName = getIntent().getStringExtra("scientificName");
        Double accuracy = getIntent().getDoubleExtra("accuracy", 0.0);

        int percent = (int)(accuracy * 100);

        accuracyText.setText(percent + "% " + getString(R.string.accu));
        accuracyBar.setProgress(percent);

        int color;

        if (percent < 40) {
            color = Color.RED;
        } else if (percent < 70) {
            color = Color.parseColor("#FFA500"); // orange
        } else {
            color = Color.GREEN;
        }

        accuracyBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        if (scientificName == null) {
            Toast.makeText(this, "Erreur : aucun champignon reçu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        detailsTitle.setText(scientificName);

        // Encoder pour l’API
        String encodedName = scientificName;
        try {
            encodedName = URLEncoder.encode(scientificName, "UTF-8").replace("+", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Appel API
        api.getMushroomDetailsByName(encodedName, new ShroomLocService.MushroomDetailsCallback() {
            @Override
            public void onSuccess(MushroomCompleteEntity m) {

                // Image
                Glide.with(IdentifyDetailsActivity.this)
                        .load(m.getImageUrl())
                        .into(detailsImage);

                // Badge toxicité
                switch (m.getEdibility()) {
                    case "inedible":
                        detailsBadge.setText("Toxique");
                        detailsBadge.setBackgroundColor(0xFFD32F2F);
                        break;

                    case "medicinal":
                        detailsBadge.setText("Médicinal");
                        detailsBadge.setBackgroundColor(0xFF1976D2);
                        break;

                    default:
                        detailsBadge.setText("Comestible");
                        detailsBadge.setBackgroundColor(0xFF388E3C);
                        break;
                }

                // Informations générales
                String general =
                        "Nom commun : " + m.getCommonName() + "\n" +
                                "Comestibilité : " + m.getEdibility() + "\n" +
                                "Psychoactif : " + (m.isPsychoactive() ? "Oui" : "Non");

                detailsGeneral.setText(general);

                // Écologie
                String ecology =
                        "Saison : " + String.join(", ", m.getSeason()) + "\n" +
                                "Habitat : " + String.join(", ", m.getHabitat()) + "\n" +
                                "Température : " + m.getMinTemp() + "°C à " + m.getMaxTemp() + "°C\n" +
                                "Humidité minimale : " + m.getMinHumidity() + "%\n" +
                                "Notes : " + m.getNotes();

                detailsEcology.setText(ecology);

                // Recette
                RecipeEntity r = m.getRecipe();
                if (r != null) {
                    recipeSection.setVisibility(LinearLayout.VISIBLE);

                    recipeTitle.setText(r.getName());

                    Glide.with(IdentifyDetailsActivity.this)
                            .load(r.getImage())
                            .into(recipeImage);

                    recipeIngredients.setText("Ingrédients :\n- " + String.join("\n- ", r.getIngredients()));
                    recipeInstructions.setText("Instructions :\n" + r.getInstructions());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(IdentifyDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
