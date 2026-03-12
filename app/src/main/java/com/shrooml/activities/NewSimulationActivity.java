package com.shrooml.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shrooml.R;
import com.shrooml.models.MushroomFeatures;
import com.shrooml.services.api.AutoMLApi;
import com.shrooml.services.api.AutoMLRetrofitClient;
import com.shrooml.services.api.Requests.PredictRequest;
import com.shrooml.services.api.Response.PredictResponse;
import com.shrooml.services.encoding.MushroomEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NewSimulationActivity - Refactorisée pour les champignons
 *
 * Permet à l'utilisateur de sélectionner les 22 caractéristiques d'un champignon
 * et envoie la requête à l'API AutoML pour obtenir la prédiction
 *
 * MODIFICATION: Ajout de MushroomEncoder pour convertir lettres → nombres
 */
public class NewSimulationActivity extends AppCompatActivity {

    private static final String TAG = "NewSimulationActivity";

    // Spinners pour chaque feature
    private Spinner spinCapShape;
    private Spinner spinCapSurface;
    private Spinner spinCapColor;
    private Spinner spinBruises;
    private Spinner spinOdor;
    private Spinner spinGillAttachment;
    private Spinner spinGillSpacing;
    private Spinner spinGillSize;
    private Spinner spinGillColor;
    private Spinner spinStalkShape;
    private Spinner spinStalkRoot;
    private Spinner spinStalkSurfaceAboveRing;
    private Spinner spinStalkSurfaceBelowRing;
    private Spinner spinStalkColorAboveRing;
    private Spinner spinStalkColorBelowRing;
    private Spinner spinVeilType;
    private Spinner spinVeilColor;
    private Spinner spinRingNumber;
    private Spinner spinRingType;
    private Spinner spinSporePrintColor;
    private Spinner spinPopulation;
    private Spinner spinHabitat;

    private Button btnPredict;
    private Button btnCancel;
    private ScrollView scrollView;

    private AutoMLApi api;
    private MushroomFeatures mushroomFeatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sim);

        // Initialiser l'API
        api = AutoMLRetrofitClient.getInstance().getAutoMLApi();

        // Initialiser les vues
        initializeSpinners();
        initializeButtons();

        // Initialiser les adapters
        setupSpinnersAdapters();
    }

    /**
     * Initialiser les références des Spinners
     */
    private void initializeSpinners() {
        scrollView = findViewById(R.id.scroll_view);
        spinCapShape = findViewById(R.id.spin_cap_shape);
        spinCapSurface = findViewById(R.id.spin_cap_surface);
        spinCapColor = findViewById(R.id.spin_cap_color);
        spinBruises = findViewById(R.id.spin_bruises);
        spinOdor = findViewById(R.id.spin_odor);
        spinGillAttachment = findViewById(R.id.spin_gill_attachment);
        spinGillSpacing = findViewById(R.id.spin_gill_spacing);
        spinGillSize = findViewById(R.id.spin_gill_size);
        spinGillColor = findViewById(R.id.spin_gill_color);
        spinStalkShape = findViewById(R.id.spin_stalk_shape);
        spinStalkRoot = findViewById(R.id.spin_stalk_root);
        spinStalkSurfaceAboveRing = findViewById(R.id.spin_stalk_surface_above_ring);
        spinStalkSurfaceBelowRing = findViewById(R.id.spin_stalk_surface_below_ring);
        spinStalkColorAboveRing = findViewById(R.id.spin_stalk_color_above_ring);
        spinStalkColorBelowRing = findViewById(R.id.spin_stalk_color_below_ring);
        spinVeilType = findViewById(R.id.spin_veil_type);
        spinVeilColor = findViewById(R.id.spin_veil_color);
        spinRingNumber = findViewById(R.id.spin_ring_number);
        spinRingType = findViewById(R.id.spin_ring_type);
        spinSporePrintColor = findViewById(R.id.spin_spore_print_color);
        spinPopulation = findViewById(R.id.spin_population);
        spinHabitat = findViewById(R.id.spin_habitat);
    }

    /**
     * Initialiser les boutons
     */
    private void initializeButtons() {
        btnPredict = findViewById(R.id.btn_predict);
        btnCancel = findViewById(R.id.btn_cancel);

        btnPredict.setOnClickListener(v -> predict());
        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * Configurer les adapters pour tous les spinners
     */
    private void setupSpinnersAdapters() {
        // Cap Shape
        setSpinnerAdapter(spinCapShape, new String[]{"Select...", "x", "b", "f", "k", "s"});
        // Cap Surface
        setSpinnerAdapter(spinCapSurface, new String[]{"Select...", "f", "g", "y", "s"});
        // Cap Color
        setSpinnerAdapter(spinCapColor, new String[]{"Select...", "n", "b", "c", "g", "r", "p", "u", "e", "w", "y"});
        // Bruises
        setSpinnerAdapter(spinBruises, new String[]{"Select...", "t", "f"});
        // Odor
        setSpinnerAdapter(spinOdor, new String[]{"Select...", "a", "l", "c", "y", "f", "m", "n", "p", "s"});
        // Gill Attachment
        setSpinnerAdapter(spinGillAttachment, new String[]{"Select...", "a", "d", "f", "n"});
        // Gill Spacing
        setSpinnerAdapter(spinGillSpacing, new String[]{"Select...", "c", "w", "d"});
        // Gill Size
        setSpinnerAdapter(spinGillSize, new String[]{"Select...", "b", "n"});
        // Gill Color
        setSpinnerAdapter(spinGillColor, new String[]{"Select...", "k", "n", "b", "h", "o", "p", "u", "e", "w", "y", "g", "r"});
        // Stalk Shape
        setSpinnerAdapter(spinStalkShape, new String[]{"Select...", "e", "t"});
        // Stalk Root
        setSpinnerAdapter(spinStalkRoot, new String[]{"Select...", "b", "c", "u", "e", "z", "r"});
        // Stalk Surface Above Ring
        setSpinnerAdapter(spinStalkSurfaceAboveRing, new String[]{"Select...", "f", "y", "k", "s"});
        // Stalk Surface Below Ring
        setSpinnerAdapter(spinStalkSurfaceBelowRing, new String[]{"Select...", "f", "y", "k", "s"});
        // Stalk Color Above Ring
        setSpinnerAdapter(spinStalkColorAboveRing, new String[]{"Select...", "b", "c", "e", "g", "n", "o", "p", "w", "y"});
        // Stalk Color Below Ring
        setSpinnerAdapter(spinStalkColorBelowRing, new String[]{"Select...", "b", "c", "e", "g", "n", "o", "p", "w", "y"});
        // Veil Type
        setSpinnerAdapter(spinVeilType, new String[]{"Select...", "p", "u"});
        // Veil Color
        setSpinnerAdapter(spinVeilColor, new String[]{"Select...", "n", "o", "w", "y"});
        // Ring Number
        setSpinnerAdapter(spinRingNumber, new String[]{"Select...", "n", "o", "t"});
        // Ring Type
        setSpinnerAdapter(spinRingType, new String[]{"Select...", "c", "e", "f", "l", "n", "p", "s", "z"});
        // Spore Print Color
        setSpinnerAdapter(spinSporePrintColor, new String[]{"Select...", "k", "n", "b", "h", "o", "r", "u", "w", "y", "g"});
        // Population
        setSpinnerAdapter(spinPopulation, new String[]{"Select...", "a", "c", "n", "s", "v", "y"});
        // Habitat
        setSpinnerAdapter(spinHabitat, new String[]{"Select...", "g", "l", "m", "p", "u", "w", "d"});
    }

    /**
     * Configurer un spinner avec un array adapter
     */
    private void setSpinnerAdapter(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Collecter les données et faire une prédiction
     */
    private void predict() {
        // Récupérer les valeurs des spinners (les lettres)
        String capShape = getSpinnerValue(spinCapShape);
        String capSurface = getSpinnerValue(spinCapSurface);
        String capColor = getSpinnerValue(spinCapColor);
        String bruises = getSpinnerValue(spinBruises);
        String odor = getSpinnerValue(spinOdor);
        String gillAttachment = getSpinnerValue(spinGillAttachment);
        String gillSpacing = getSpinnerValue(spinGillSpacing);
        String gillSize = getSpinnerValue(spinGillSize);
        String gillColor = getSpinnerValue(spinGillColor);
        String stalkShape = getSpinnerValue(spinStalkShape);
        String stalkRoot = getSpinnerValue(spinStalkRoot);
        String stalkSurfaceAboveRing = getSpinnerValue(spinStalkSurfaceAboveRing);
        String stalkSurfaceBelowRing = getSpinnerValue(spinStalkSurfaceBelowRing);
        String stalkColorAboveRing = getSpinnerValue(spinStalkColorAboveRing);
        String stalkColorBelowRing = getSpinnerValue(spinStalkColorBelowRing);
        String veilType = getSpinnerValue(spinVeilType);
        String veilColor = getSpinnerValue(spinVeilColor);
        String ringNumber = getSpinnerValue(spinRingNumber);
        String ringType = getSpinnerValue(spinRingType);
        String sporePrintColor = getSpinnerValue(spinSporePrintColor);
        String population = getSpinnerValue(spinPopulation);
        String habitat = getSpinnerValue(spinHabitat);

        // Créer une liste de valeurs pour vérification
        List<String> values = new ArrayList<>();
        values.add(capShape); values.add(capSurface); values.add(capColor);
        values.add(bruises); values.add(odor); values.add(gillAttachment);
        values.add(gillSpacing); values.add(gillSize); values.add(gillColor);
        values.add(stalkShape); values.add(stalkRoot); values.add(stalkSurfaceAboveRing);
        values.add(stalkSurfaceBelowRing); values.add(stalkColorAboveRing); values.add(stalkColorBelowRing);
        values.add(veilType); values.add(veilColor); values.add(ringNumber);
        values.add(ringType); values.add(sporePrintColor); values.add(population);
        values.add(habitat);

        // Vérifier que tous les champs sont remplis
        if (values.contains("Select...")) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Afficher un toast de chargement
        Toast.makeText(this, "Prédiction en cours...", Toast.LENGTH_SHORT).show();

        // 🔧 CRÉER UN MAP AVEC LES ENCODAGES (LETTRES → NOMBRES)
        Map<String, Object> sample = new HashMap<>();

        sample.put("cap-shape", MushroomEncoder.encodeCapShape(capShape));
        sample.put("cap-surface", MushroomEncoder.encodeCapSurface(capSurface));
        sample.put("cap-color", MushroomEncoder.encodeCapColor(capColor));
        sample.put("bruises", MushroomEncoder.encodeBruises(bruises));
        sample.put("odor", MushroomEncoder.encodeOdor(odor));
        sample.put("gill-attachment", MushroomEncoder.encodeGillAttachment(gillAttachment));
        sample.put("gill-spacing", MushroomEncoder.encodeGillSpacing(gillSpacing));
        sample.put("gill-size", MushroomEncoder.encodeGillSize(gillSize));
        sample.put("gill-color", MushroomEncoder.encodeGillColor(gillColor));
        sample.put("stalk-shape", MushroomEncoder.encodeStalkShape(stalkShape));
        sample.put("stalk-root", MushroomEncoder.encodeStalkRoot(stalkRoot));
        sample.put("stalk-surface-above-ring", MushroomEncoder.encodeStalkSurfaceAboveRing(stalkSurfaceAboveRing));
        sample.put("stalk-surface-below-ring", MushroomEncoder.encodeStalkSurfaceBelowRing(stalkSurfaceBelowRing));
        sample.put("stalk-color-above-ring", MushroomEncoder.encodeStalkColorAboveRing(stalkColorAboveRing));
        sample.put("stalk-color-below-ring", MushroomEncoder.encodeStalkColorBelowRing(stalkColorBelowRing));
        sample.put("veil-type", MushroomEncoder.encodeVeilType(veilType));
        sample.put("veil-color", MushroomEncoder.encodeVeilColor(veilColor));
        sample.put("ring-number", MushroomEncoder.encodeRingNumber(ringNumber));
        sample.put("ring-type", MushroomEncoder.encodeRingType(ringType));
        sample.put("spore-print-color", MushroomEncoder.encodeSporeColor(sporePrintColor));
        sample.put("population", MushroomEncoder.encodePopulation(population));
        sample.put("habitat", MushroomEncoder.encodeHabitat(habitat));

        // Créer la liste de features pour l'API
        List<Map<String, Object>> X = new ArrayList<>();
        X.add(sample);

        // Faire l'appel à l'API avec les données encodées
        PredictRequest request = new PredictRequest(X);

        api.predict(request).enqueue(new Callback<PredictResponse>() {
            @Override
            public void onResponse(Call<PredictResponse> call, Response<PredictResponse> response) {
                Log.d(TAG, "Réponse API: Code " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<String> predictions = response.body().getPredictions();

                    if (predictions != null && !predictions.isEmpty()) {
                        String prediction = predictions.get(0); // Première prédiction
                        Log.d(TAG, "Prédiction: " + prediction);

                        // Rediriger vers SimulationResultActivity avec le résultat
                        Intent intent = new Intent(NewSimulationActivity.this, SimulationResultActivity.class);
                        intent.putExtra("prediction", prediction);
                        intent.putExtra("confidence", 0.85); // À récupérer de l'API si disponible
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.e(TAG, "Erreur API: " + response.code());
                    Toast.makeText(NewSimulationActivity.this, "Erreur de prédiction (Code " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PredictResponse> call, Throwable t) {
                Log.e(TAG, "Erreur réseau: " + t.getMessage(), t);
                Toast.makeText(NewSimulationActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Récupérer la valeur sélectionnée d'un spinner
     */
    private String getSpinnerValue(Spinner spinner) {
        return spinner.getSelectedItem().toString();
    }
}