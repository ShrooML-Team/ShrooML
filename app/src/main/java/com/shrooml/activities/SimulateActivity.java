package com.shrooml.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.shrooml.IdentifyActivity;
import com.shrooml.R;
import com.shrooml.services.api.AutoMLApi;
import com.shrooml.services.api.AutoMLRetrofitClient;
import com.shrooml.services.api.Requests.PredictRequest;
import com.shrooml.services.api.Response.PredictResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.*;

public class SimulateActivity extends AppCompatActivity {

    private AutoMLApi autoMLApi;
    private AutoMLRetrofitClient apiClient;
    private ImageButton btnToggleMode;

    private Spinner spinnerCapColor, spinnerFootColor, spinnerVeilColor, spinnerOdor, spinnerBruises;
    private TextView btnIdentifyForm;
    private TextView btnRanking;
    private TextView btnGames;
    private TextView btnIdentify;  // Le bouton "Identify" dans la barre de navigation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sim);

        initApiClient();
        initViews();
        setupSpinners();
        setupListeners();
        setupToggleButton();
    }

    private void initApiClient() {
        try {
            apiClient = AutoMLRetrofitClient.getInstance(this);
            autoMLApi = apiClient.getAutoMLApi();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing API: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        // Bouton toggle
        btnToggleMode = findViewById(R.id.btn_toggle_mode);

        // Spinners
        spinnerCapColor = findViewById(R.id.spinner_cap_color);
        spinnerFootColor = findViewById(R.id.spinner_foot_color);
        spinnerVeilColor = findViewById(R.id.spinner_veil_color);
        spinnerOdor = findViewById(R.id.spinner_odor);
        spinnerBruises = findViewById(R.id.spinner_bruises);

        // Boutons de navigation (bas de l'écran)
        btnRanking = findViewById(R.id.btn_ranking);
        btnIdentify = findViewById(R.id.btn_identify);
        btnGames = findViewById(R.id.btn_games);

        // Bouton Identify du formulaire
        btnIdentifyForm = findViewById(R.id.btn_identify_form);
    }

    private void setupToggleButton() {
        if (btnToggleMode != null) {
            btnToggleMode.setOnClickListener(v -> {
                Intent intent = new Intent(SimulateActivity.this, IdentifyActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }
    }

    private void setupSpinners() {
        String[] colors = {"Choose...", "Brown", "Buff", "Cinnamon", "Gray", "Green", "Pink", "Purple", "Red", "White", "Yellow"};
        String[] odors = {"Choose...", "Almond", "Anise", "Creosote", "Fishy", "Foul", "Musty", "None", "Pungent", "Spicy"};
        String[] bruises = {"Choose...", "No", "Yes"};

        setSpinnerOptions(spinnerCapColor, colors);
        setSpinnerOptions(spinnerFootColor, colors);
        setSpinnerOptions(spinnerVeilColor, colors);
        setSpinnerOptions(spinnerOdor, odors);
        setSpinnerOptions(spinnerBruises, bruises);
    }

    private void setSpinnerOptions(Spinner spinner, String[] options) {
        if (spinner != null) {
            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, options);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        // Bouton Identify du formulaire
        if (btnIdentifyForm != null) {
            btnIdentifyForm.setOnClickListener(v -> performPrediction());
        }

        // Navigation
        if (btnRanking != null) {
            btnRanking.setOnClickListener(v -> {
                Toast.makeText(this, "Ranking feature coming soon", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnGames != null) {
            btnGames.setOnClickListener(v -> {
                Toast.makeText(this, "Games feature coming soon", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnIdentify != null) {
            btnIdentify.setOnClickListener(v -> {
                // Déjà sur la page Identify, ne rien faire
            });
        }
    }

    private void performPrediction() {
        // Vérifier que tous les champs sont remplis
        if (spinnerCapColor.getSelectedItemPosition() == 0 ||
                spinnerFootColor.getSelectedItemPosition() == 0 ||
                spinnerVeilColor.getSelectedItemPosition() == 0 ||
                spinnerOdor.getSelectedItemPosition() == 0 ||
                spinnerBruises.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construction des caractéristiques pour l'API AutoML
        Map<String, Integer> mushroomFeatures = new LinkedHashMap<>();
        // TODO: Construire les 22 caractéristiques complètes

        // Pour l'instant, simulation
        boolean isEdible = Math.random() > 0.5;

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("is_edible", isEdible);
        startActivity(intent);
    }
}