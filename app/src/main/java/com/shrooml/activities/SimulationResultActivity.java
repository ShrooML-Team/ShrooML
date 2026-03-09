package com.shrooml.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shrooml.R;

/**
 * SimulationResultActivity - Résultat de la simulation de crédit
 */
public class SimulationResultActivity extends AppCompatActivity {

    private TextView tvResult;
    private TextView tvProbability;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_result);

        // Initialiser les vues
        tvResult = findViewById(R.id.tv_result);
        tvProbability = findViewById(R.id.tv_probability);
        btnBack = findViewById(R.id.btn_back);

        // Récupérer les paramètres
        String age = getIntent().getStringExtra("age");
        String income = getIntent().getStringExtra("income");
        String creditAmount = getIntent().getStringExtra("creditAmount");
        String duration = getIntent().getStringExtra("duration");

        // TODO: Afficher les résultats de la prédiction
        tvResult.setText("APPROVED");
        tvProbability.setText("Probabilité: 85%");

        // Listener
        btnBack.setOnClickListener(v -> finish());
    }
}