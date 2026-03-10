package com.shrooml.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shrooml.R;

/**
 * SimulationResultActivity - Refactorisée pour les champignons
 *
 * Affiche le résultat de la prédiction (Edible ou Poisonous)
 */
public class SimulationResultActivity extends AppCompatActivity {

    private TextView tvResult;
    private TextView tvResultLabel;
    private ImageView ivResultImage;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_result);

        // Initialiser les vues
        tvResult = findViewById(R.id.tv_result);
        tvResultLabel = findViewById(R.id.tv_result_label);
        ivResultImage = findViewById(R.id.iv_result_image);
        btnBack = findViewById(R.id.btn_back);

        // Récupérer les paramètres
        String prediction = getIntent().getStringExtra("prediction");
        double confidence = getIntent().getDoubleExtra("confidence", 0.0);

        // Afficher le résultat
        displayResult(prediction, confidence);

        // Listener
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Afficher le résultat de la prédiction
     */
    private void displayResult(String prediction, double confidence) {
        if (prediction != null) {
            // Déterminer le résultat et la couleur
            boolean isEdible = prediction.equalsIgnoreCase("e") || prediction.equalsIgnoreCase("edible");

            if (isEdible) {
                tvResult.setText("EDIBLE");
                tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                tvResultLabel.setText("This mushroom is SAFE to eat!");
                ivResultImage.setImageResource(android.R.drawable.ic_dialog_info); // Utiliser une icône disponible
            } else {
                tvResult.setText("POISONOUS");
                tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvResultLabel.setText("This mushroom is DANGEROUS! Do not eat!");
                ivResultImage.setImageResource(android.R.drawable.ic_dialog_alert);
            }

            // Afficher la confiance si disponible
            if (confidence > 0) {
                tvResultLabel.setText(tvResultLabel.getText() + "\n\nConfidence: " + String.format("%.2f%%", confidence * 100));
            }
        } else {
            tvResult.setText("ERROR");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            tvResultLabel.setText("Could not determine prediction");
        }
    }
}