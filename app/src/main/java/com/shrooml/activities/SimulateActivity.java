package com.shrooml.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
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
    private ProgressBar progressBar;
    private Button btnIdentify;
    private Button btnRanking;
    private Button btnGames;

    // Spinners pour tous les attributs
    private Spinner spinnerCapShape, spinnerCapSurface, spinnerCapColor, spinnerBruises,
            spinnerOdor, spinnerGillAttachment, spinnerGillSpacing, spinnerGillSize,
            spinnerGillColor, spinnerStalkShape, spinnerStalkRoot, spinnerStalkSurfaceAbove,
            spinnerStalkSurfaceBelow, spinnerStalkColorAbove, spinnerStalkColorBelow,
            spinnerVeilType, spinnerVeilColor, spinnerRingNumber, spinnerRingType,
            spinnerSporePrintColor, spinnerPopulation, spinnerHabitat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sim);

        initApiClient();
        initViews();
        initSpinners();
        setupListeners();
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
        progressBar = findViewById(R.id.progress_bar);
        btnIdentify = findViewById(R.id.btn_identify);
        btnRanking = findViewById(R.id.btn_ranking);
        btnGames = findViewById(R.id.btn_games);

        // Initialiser tous les spinners
        spinnerCapShape = findViewById(R.id.spinner_cap_shape);
        spinnerCapSurface = findViewById(R.id.spinner_cap_surface);
        spinnerCapColor = findViewById(R.id.spinner_cap_color);
        spinnerBruises = findViewById(R.id.spinner_bruises);
        spinnerOdor = findViewById(R.id.spinner_odor);
        spinnerGillAttachment = findViewById(R.id.spinner_gill_attachment);
        spinnerGillSpacing = findViewById(R.id.spinner_gill_spacing);
        spinnerGillSize = findViewById(R.id.spinner_gill_size);
        spinnerGillColor = findViewById(R.id.spinner_gill_color);
        spinnerStalkShape = findViewById(R.id.spinner_stalk_shape);
        spinnerStalkRoot = findViewById(R.id.spinner_stalk_root);
        spinnerStalkSurfaceAbove = findViewById(R.id.spinner_stalk_surface_above);
        spinnerStalkSurfaceBelow = findViewById(R.id.spinner_stalk_surface_below);
        spinnerStalkColorAbove = findViewById(R.id.spinner_stalk_color_above);
        spinnerStalkColorBelow = findViewById(R.id.spinner_stalk_color_below);
        spinnerVeilType = findViewById(R.id.spinner_veil_type);
        spinnerVeilColor = findViewById(R.id.spinner_veil_color);
        spinnerRingNumber = findViewById(R.id.spinner_ring_number);
        spinnerRingType = findViewById(R.id.spinner_ring_type);
        spinnerSporePrintColor = findViewById(R.id.spinner_spore_print_color);
        spinnerPopulation = findViewById(R.id.spinner_population);
        spinnerHabitat = findViewById(R.id.spinner_habitat);
    }

    private void initSpinners() {
        // Définir les options pour chaque spinner
        setSpinnerOptions(spinnerCapShape, new String[]{"Bell", "Conical", "Convex", "Flat", "Knobbed", "Sunken"});
        setSpinnerOptions(spinnerCapSurface, new String[]{"Fibrous", "Grooves", "Scaly", "Smooth"});
        setSpinnerOptions(spinnerCapColor, new String[]{"Brown", "Buff", "Cinnamon", "Gray", "Green", "Pink", "Purple", "Red", "White", "Yellow"});
        setSpinnerOptions(spinnerBruises, new String[]{"No", "Yes"});
        setSpinnerOptions(spinnerOdor, new String[]{"Almond", "Anise", "Creosote", "Fishy", "Foul", "Musty", "None", "Pungent", "Spicy"});
        setSpinnerOptions(spinnerGillAttachment, new String[]{"Attached", "Descending", "Free", "Notched"});
        setSpinnerOptions(spinnerGillSpacing, new String[]{"Close", "Crowded", "Distant"});
        setSpinnerOptions(spinnerGillSize, new String[]{"Broad", "Narrow"});
        setSpinnerOptions(spinnerGillColor, new String[]{"Black", "Brown", "Buff", "Chocolate", "Gray", "Green", "Orange", "Pink", "Purple", "Red", "White", "Yellow"});
        setSpinnerOptions(spinnerStalkShape, new String[]{"Enlarging", "Tapering"});
        setSpinnerOptions(spinnerStalkRoot, new String[]{"Bulbous", "Club", "Cup", "Equal", "Rhizomorphs", "Rooted"});
        setSpinnerOptions(spinnerStalkSurfaceAbove, new String[]{"Fibrous", "Scaly", "Silky", "Smooth"});
        setSpinnerOptions(spinnerStalkSurfaceBelow, new String[]{"Fibrous", "Scaly", "Silky", "Smooth"});
        setSpinnerOptions(spinnerStalkColorAbove, new String[]{"Brown", "Buff", "Cinnamon", "Gray", "Orange", "Pink", "Red", "White", "Yellow"});
        setSpinnerOptions(spinnerStalkColorBelow, new String[]{"Brown", "Buff", "Cinnamon", "Gray", "Orange", "Pink", "Red", "White", "Yellow"});
        setSpinnerOptions(spinnerVeilType, new String[]{"Partial", "Universal"});
        setSpinnerOptions(spinnerVeilColor, new String[]{"Brown", "Orange", "White", "Yellow"});
        setSpinnerOptions(spinnerRingNumber, new String[]{"None", "One", "Two"});
        setSpinnerOptions(spinnerRingType, new String[]{"Cobwebby", "Evanescent", "Flaring", "Large", "None", "Pendant"});
        setSpinnerOptions(spinnerSporePrintColor, new String[]{"Black", "Brown", "Buff", "Chocolate", "Green", "Orange", "Purple", "White", "Yellow"});
        setSpinnerOptions(spinnerPopulation, new String[]{"Abundant", "Clustered", "Numerous", "Scattered", "Several", "Solitary"});
        setSpinnerOptions(spinnerHabitat, new String[]{"Grasses", "Leaves", "Meadows", "Paths", "Urban", "Waste", "Woods"});
    }

    private void setSpinnerOptions(Spinner spinner, String[] options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupListeners() {
        btnIdentify.setOnClickListener(v -> performPrediction());

        // Navigation
        btnRanking.setOnClickListener(v -> {
            Toast.makeText(this, "Ranking feature coming soon", Toast.LENGTH_SHORT).show();
        });

        btnGames.setOnClickListener(v -> {
            Toast.makeText(this, "Games feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void performPrediction() {
        if (apiClient == null || !apiClient.isAuthenticated()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construire la map des caractéristiques
        Map<String, Integer> mushroomFeatures = new LinkedHashMap<>();
        mushroomFeatures.put("cap-shape", spinnerCapShape.getSelectedItemPosition());
        mushroomFeatures.put("cap-surface", spinnerCapSurface.getSelectedItemPosition());
        mushroomFeatures.put("cap-color", spinnerCapColor.getSelectedItemPosition());
        mushroomFeatures.put("bruises", spinnerBruises.getSelectedItemPosition());
        mushroomFeatures.put("odor", spinnerOdor.getSelectedItemPosition());
        mushroomFeatures.put("gill-attachment", spinnerGillAttachment.getSelectedItemPosition());
        mushroomFeatures.put("gill-spacing", spinnerGillSpacing.getSelectedItemPosition());
        mushroomFeatures.put("gill-size", spinnerGillSize.getSelectedItemPosition());
        mushroomFeatures.put("gill-color", spinnerGillColor.getSelectedItemPosition());
        mushroomFeatures.put("stalk-shape", spinnerStalkShape.getSelectedItemPosition());
        mushroomFeatures.put("stalk-root", spinnerStalkRoot.getSelectedItemPosition());
        mushroomFeatures.put("stalk-surface-above-ring", spinnerStalkSurfaceAbove.getSelectedItemPosition());
        mushroomFeatures.put("stalk-surface-below-ring", spinnerStalkSurfaceBelow.getSelectedItemPosition());
        mushroomFeatures.put("stalk-color-above-ring", spinnerStalkColorAbove.getSelectedItemPosition());
        mushroomFeatures.put("stalk-color-below-ring", spinnerStalkColorBelow.getSelectedItemPosition());
        mushroomFeatures.put("veil-type", spinnerVeilType.getSelectedItemPosition());
        mushroomFeatures.put("veil-color", spinnerVeilColor.getSelectedItemPosition());
        mushroomFeatures.put("ring-number", spinnerRingNumber.getSelectedItemPosition());
        mushroomFeatures.put("ring-type", spinnerRingType.getSelectedItemPosition());
        mushroomFeatures.put("spore-print-color", spinnerSporePrintColor.getSelectedItemPosition());
        mushroomFeatures.put("population", spinnerPopulation.getSelectedItemPosition());
        mushroomFeatures.put("habitat", spinnerHabitat.getSelectedItemPosition());

        // Créer la requête
        List<Map<String, Integer>> samples = new ArrayList<>();
        samples.add(mushroomFeatures);
        PredictRequest request = new PredictRequest(samples);

        // Afficher le loading
        showLoading(true);

        // Appel API
        Call<PredictResponse> call = autoMLApi.predict(request);
        call.enqueue(new Callback<PredictResponse>() {
            @Override
            public void onResponse(Call<PredictResponse> call, Response<PredictResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Integer> predictions = response.body().getPredictions();
                    if (!predictions.isEmpty()) {
                        int prediction = predictions.get(0);
                        // 0 = Edible, 1 = Poisonous
                        showResult(prediction == 0);
                    } else {
                        Toast.makeText(SimulateActivity.this,
                                "No prediction received", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Prediction failed: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMsg += " - " + e.getMessage();
                    }
                    Toast.makeText(SimulateActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PredictResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(SimulateActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showResult(boolean isEdible) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("is_edible", isEdible);
        startActivity(intent);
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (btnIdentify != null) {
            btnIdentify.setEnabled(!show);
        }
    }
}