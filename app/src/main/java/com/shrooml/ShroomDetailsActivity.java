package com.shrooml;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.models.MushroomCompleteEntity;
import com.shrooml.models.RecipeEntity;
import com.shrooml.services.ShroomLocService;

import java.net.URLEncoder;

public class ShroomDetailsActivity extends BackgroundActivity {

    private TextView detailsTitle, detailsBadge, detailsGeneral, detailsEcology;
    private ImageView detailsImage;

    private LinearLayout recipeSection;
    private ImageView recipeImage;
    private TextView recipeTitle, recipeIngredients, recipeInstructions;

    private ShroomLocService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shroom_details);

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

        // Dans onCreate(), après findViewById :
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        activityId = -1;
        initNavBar(ShroomDetailsActivity.this);

        api = new ShroomLocService();

        // Nom scientifique original
        String scientificName = getIntent().getStringExtra("scientificName");

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
                Glide.with(ShroomDetailsActivity.this)
                        .load(m.getImageUrl())
                        .into(detailsImage);

                // Badge toxicité
                switch (m.getEdibility()) {
                    case "inedible":
                        translateDynamicText("Toxique",detailsBadge);
                        detailsBadge.setBackgroundColor(0xFFD32F2F);
                        break;

                    case "medicinal":
                        translateDynamicText("Médicinal",detailsBadge);
                        detailsBadge.setBackgroundColor(0xFF1976D2);
                        break;

                    default:
                        translateDynamicText("Comestible",detailsBadge);
                        detailsBadge.setBackgroundColor(0xFF388E3C);
                        break;
                }


                // Informations générales
                String general =
                        "Nom commun : " + m.getCommonName() + "\n" +
                                "Comestibilité : " + m.getEdibility() + "\n" +
                                "Psychoactif : " + (m.isPsychoactive() ? "Oui" : "Non");
                translateDynamicText(general, detailsGeneral);

                // Écologie
                String ecology =
                        "Saison : " + String.join(", ", m.getSeason()) + "\n" +
                                "Habitat : " + String.join(", ", m.getHabitat()) + "\n" +
                                "Température : " + m.getMinTemp() + "°C à " + m.getMaxTemp() + "°C\n" +
                                "Humidité minimale : " + m.getMinHumidity() + "%\n" +
                                "Notes : " + m.getNotes();
                translateDynamicText(ecology, detailsEcology);

                // Recette
                RecipeEntity r = m.getRecipe();
                if (r != null) {
                    recipeSection.setVisibility(LinearLayout.VISIBLE);
                    translateDynamicText(r.getName(), recipeTitle);

                    Glide.with(ShroomDetailsActivity.this)
                            .load(r.getImage())
                            .into(recipeImage);

                    String txt1 = "Ingrédients :\n- " + String.join("\n- ", r.getIngredients());
                    String txt2 = "Instructions :\n" + r.getInstructions();
                    recipeIngredients.setText(txt1);
                    recipeInstructions.setText(txt2);

                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ShroomDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
