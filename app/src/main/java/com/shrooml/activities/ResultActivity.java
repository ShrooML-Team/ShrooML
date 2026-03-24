package com.shrooml.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.shrooml.R;

public class ResultActivity extends AppCompatActivity {

    private TextView resultText;
    private Button btnNewPrediction;
    private Button btnIdentify;
    private Button btnRanking;
    private Button btnGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_result);

        initViews();
        setupResult();
        setupListeners();
    }

    private void initViews() {
        resultText = findViewById(R.id.result_text);
        btnNewPrediction = findViewById(R.id.btn_new_prediction);
        btnIdentify = findViewById(R.id.btn_identify);
        btnRanking = findViewById(R.id.btn_ranking);
        btnGames = findViewById(R.id.btn_games);
    }

    private void setupResult() {
        boolean isEdible = getIntent().getBooleanExtra("is_edible", false);

        if (isEdible) {
            resultText.setText("🍄 Edible");
            resultText.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else {
            resultText.setText("☠️ Inedible");
            resultText.setTextColor(ContextCompat.getColor(this, R.color.error));
        }
    }

    private void setupListeners() {
        // Nouvelle prédiction - retour à l'écran de saisie
        btnNewPrediction.setOnClickListener(v -> finish());

        // Navigation - Retour à l'identification
        btnIdentify.setOnClickListener(v -> finish());

        // Navigation vers Ranking
        btnRanking.setOnClickListener(v -> {
            Toast.makeText(this, "Ranking feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Navigation vers Games
        btnGames.setOnClickListener(v -> {
            Toast.makeText(this, "Games feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }
}