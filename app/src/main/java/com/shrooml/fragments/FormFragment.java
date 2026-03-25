package com.shrooml.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.shrooml.IdentifyActivity;
import com.shrooml.R;
import com.shrooml.activities.*;
import com.shrooml.services.api.AutoMLApi;
import com.shrooml.services.api.AutoMLRetrofitClient;
import com.shrooml.services.api.Requests.LoginRequest;
import com.shrooml.services.api.Requests.PredictRequest;
import com.shrooml.services.api.Response.LoginResponse;
import com.shrooml.services.api.Response.PredictResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.*;

public class FormFragment extends Fragment {

    private AutoMLApi autoMLApi;
    private AutoMLRetrofitClient apiClient;
    private static final String TEST_USERNAME = "admin";
    private static final String TEST_PASSWORD = "admin123";
    // 22 spinners
    private Spinner spinnerCapShape, spinnerCapSurface, spinnerCapColor, spinnerBruises,
            spinnerOdor, spinnerGillAttachment, spinnerGillSpacing, spinnerGillSize,
            spinnerGillColor, spinnerStalkShape, spinnerStalkRoot, spinnerStalkSurfaceAbove,
            spinnerStalkSurfaceBelow, spinnerStalkColorAbove, spinnerStalkColorBelow,
            spinnerVeilType, spinnerVeilColor, spinnerRingNumber, spinnerRingType,
            spinnerSporePrintColor, spinnerPopulation,spinnerHabitat;
    private boolean isAuthenticated = false;

    private TextView btnIdentifyForm;
    private TextView progressText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form, container, false);

        initApiClient();
        initViews(view);
        setupSpinners();
        setupListeners();
        autoLogin();

        return view;
    }

    private void initApiClient() {
        try {
            if (getActivity() != null) {
                apiClient = AutoMLRetrofitClient.getInstance(getActivity());
                autoMLApi = apiClient.getAutoMLApi();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews(View view) {
        spinnerCapShape = view.findViewById(R.id.spinner_cap_shape);
        spinnerCapSurface = view.findViewById(R.id.spinner_cap_surface);
        spinnerCapColor = view.findViewById(R.id.spinner_cap_color);
        spinnerBruises = view.findViewById(R.id.spinner_bruises);
        spinnerOdor = view.findViewById(R.id.spinner_odor);
        spinnerGillAttachment = view.findViewById(R.id.spinner_gill_attachment);
        spinnerGillSpacing = view.findViewById(R.id.spinner_gill_spacing);
        spinnerGillSize = view.findViewById(R.id.spinner_gill_size);
        spinnerGillColor = view.findViewById(R.id.spinner_gill_color);
        spinnerStalkShape = view.findViewById(R.id.spinner_stalk_shape);
        spinnerStalkRoot = view.findViewById(R.id.spinner_stalk_root);
        spinnerStalkSurfaceAbove = view.findViewById(R.id.spinner_stalk_surface_above);
        spinnerStalkSurfaceBelow = view.findViewById(R.id.spinner_stalk_surface_below);
        spinnerStalkColorAbove = view.findViewById(R.id.spinner_stalk_color_above);
        spinnerStalkColorBelow = view.findViewById(R.id.spinner_stalk_color_below);
        spinnerVeilType = view.findViewById(R.id.spinner_veil_type);
        spinnerVeilColor = view.findViewById(R.id.spinner_veil_color);
        spinnerRingNumber = view.findViewById(R.id.spinner_ring_number);
        spinnerRingType = view.findViewById(R.id.spinner_ring_type);
        spinnerSporePrintColor = view.findViewById(R.id.spinner_spore_print_color);
        spinnerPopulation = view.findViewById(R.id.spinner_population);
        spinnerHabitat = view.findViewById(R.id.spinner_habitat);

        btnIdentifyForm = view.findViewById(R.id.btn_identify_form);
        progressText = view.findViewById(R.id.progress_text);
    }

    private void setupSpinners() {
        // Options pour chaque spinner
        String[] capShapes = {"Choose...", "Bell", "Conical", "Convex", "Flat", "Knobbed", "Sunken"};
        String[] capSurfaces = {"Choose...", "Fibrous", "Grooves", "Scaly", "Smooth"};
        String[] colors = {"Choose...", "Brown", "Buff", "Cinnamon", "Gray", "Green", "Pink", "Purple", "Red", "White", "Yellow"};
        String[] bruises = {"Choose...", "No", "Yes"};
        String[] odors = {"Choose...", "Almond", "Anise", "Creosote", "Fishy", "Foul", "Musty", "None", "Pungent", "Spicy"};
        String[] gillAttachments = {"Choose...", "Attached", "Descending", "Free", "Notched"};
        String[] gillSpacings = {"Choose...", "Close", "Crowded", "Distant"};
        String[] gillSizes = {"Choose...", "Broad", "Narrow"};
        String[] gillColors = {"Choose...", "Black", "Brown", "Buff", "Chocolate", "Gray", "Green", "Orange", "Pink", "Purple", "Red", "White", "Yellow"};
        String[] stalkShapes = {"Choose...", "Enlarging", "Tapering"};
        String[] stalkRoots = {"Choose...", "Bulbous", "Club", "Cup", "Equal", "Rhizomorphs", "Rooted"};
        String[] stalkSurfaces = {"Choose...", "Fibrous", "Scaly", "Silky", "Smooth"};
        String[] stalkColors = {"Choose...", "Brown", "Buff", "Cinnamon", "Gray", "Orange", "Pink", "Red", "White", "Yellow"};
        String[] veilTypes = {"Choose...", "Partial", "Universal"};
        String[] veilColors = {"Choose...", "Brown", "Orange", "White", "Yellow"};
        String[] ringNumbers = {"Choose...", "None", "One", "Two"};
        String[] ringTypes = {"Choose...", "Cobwebby", "Evanescent", "Flaring", "Large", "None", "Pendant"};
        String[] sporePrintColors = {"Choose...", "Black", "Brown", "Buff", "Chocolate", "Green", "Orange", "Purple", "White", "Yellow"};
        String[] populations = {"Choose...", "Abundant", "Clustered", "Numerous", "Scattered", "Several", "Solitary"};
        String[] habitats = {"Choose...", "Grasses", "Leaves", "Meadows", "Paths", "Urban", "Waste", "Woods"};

        // Appliquer les options
        setSpinnerOptions(spinnerCapShape, capShapes);
        setSpinnerOptions(spinnerCapSurface, capSurfaces);
        setSpinnerOptions(spinnerCapColor, colors);
        setSpinnerOptions(spinnerBruises, bruises);
        setSpinnerOptions(spinnerOdor, odors);
        setSpinnerOptions(spinnerGillAttachment, gillAttachments);
        setSpinnerOptions(spinnerGillSpacing, gillSpacings);
        setSpinnerOptions(spinnerGillSize, gillSizes);
        setSpinnerOptions(spinnerGillColor, gillColors);
        setSpinnerOptions(spinnerStalkShape, stalkShapes);
        setSpinnerOptions(spinnerStalkRoot, stalkRoots);
        setSpinnerOptions(spinnerStalkSurfaceAbove, stalkSurfaces);
        setSpinnerOptions(spinnerStalkSurfaceBelow, stalkSurfaces);
        setSpinnerOptions(spinnerStalkColorAbove, stalkColors);
        setSpinnerOptions(spinnerStalkColorBelow, stalkColors);
        setSpinnerOptions(spinnerVeilType, veilTypes);
        setSpinnerOptions(spinnerVeilColor, veilColors);
        setSpinnerOptions(spinnerRingNumber, ringNumbers);
        setSpinnerOptions(spinnerRingType, ringTypes);
        setSpinnerOptions(spinnerSporePrintColor, sporePrintColors);
        setSpinnerOptions(spinnerPopulation, populations);
        setSpinnerOptions(spinnerHabitat, habitats);
    }

    private void setSpinnerOptions(Spinner spinner, String[] options) {
        if (spinner != null && getContext() != null) {
            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                    getContext(), android.R.layout.simple_spinner_item, options);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        if (btnIdentifyForm != null) {
            btnIdentifyForm.setOnClickListener(v -> performPrediction());
        }
    }

    private void performPrediction() {
        // Vérifier que tous les spinners sont initialisés
        if (spinnerCapShape == null || spinnerCapSurface == null || spinnerCapColor == null ||
                spinnerBruises == null || spinnerOdor == null || spinnerGillAttachment == null ||
                spinnerGillSpacing == null || spinnerGillSize == null || spinnerGillColor == null ||
                spinnerStalkShape == null || spinnerStalkRoot == null || spinnerStalkSurfaceAbove == null ||
                spinnerStalkSurfaceBelow == null || spinnerStalkColorAbove == null || spinnerStalkColorBelow == null ||
                spinnerVeilType == null || spinnerVeilColor == null || spinnerRingNumber == null ||
                spinnerRingType == null || spinnerSporePrintColor == null || spinnerPopulation == null ||
                spinnerHabitat == null) {
            Toast.makeText(getContext(), "Error loading form", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier que tous les champs sont remplis (position > 0)
        if (spinnerCapShape.getSelectedItemPosition() == 0 ||
                spinnerCapSurface.getSelectedItemPosition() == 0 ||
                spinnerCapColor.getSelectedItemPosition() == 0 ||
                spinnerBruises.getSelectedItemPosition() == 0 ||
                spinnerOdor.getSelectedItemPosition() == 0 ||
                spinnerGillAttachment.getSelectedItemPosition() == 0 ||
                spinnerGillSpacing.getSelectedItemPosition() == 0 ||
                spinnerGillSize.getSelectedItemPosition() == 0 ||
                spinnerGillColor.getSelectedItemPosition() == 0 ||
                spinnerStalkShape.getSelectedItemPosition() == 0 ||
                spinnerStalkRoot.getSelectedItemPosition() == 0 ||
                spinnerStalkSurfaceAbove.getSelectedItemPosition() == 0 ||
                spinnerStalkSurfaceBelow.getSelectedItemPosition() == 0 ||
                spinnerStalkColorAbove.getSelectedItemPosition() == 0 ||
                spinnerStalkColorBelow.getSelectedItemPosition() == 0 ||
                spinnerVeilType.getSelectedItemPosition() == 0 ||
                spinnerVeilColor.getSelectedItemPosition() == 0 ||
                spinnerRingNumber.getSelectedItemPosition() == 0 ||
                spinnerRingType.getSelectedItemPosition() == 0 ||
                spinnerSporePrintColor.getSelectedItemPosition() == 0 ||
                spinnerPopulation.getSelectedItemPosition() == 0 ||
                spinnerHabitat.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier l'authentification
        if (apiClient == null || !apiClient.isAuthenticated()) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            autoLogin();
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

        List<Map<String, Integer>> samples = new ArrayList<>();
        samples.add(mushroomFeatures);
        PredictRequest request = new PredictRequest(samples);

        showLoading(true);

        Call<PredictResponse> call = autoMLApi.predict(request);
        call.enqueue(new Callback<PredictResponse>() {
            @Override
            public void onResponse(Call<PredictResponse> call, Response<PredictResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Integer> predictions = response.body().getPredictions();
                    if (!predictions.isEmpty()) {
                        boolean isEdible = (predictions.get(0) == 0);
                        if (getActivity() != null) {
                            ((IdentifyActivity) getActivity()).showResult(isEdible);
                        }
                    } else {
                        Toast.makeText(getContext(), "No prediction received", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PredictResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (btnIdentifyForm != null) {
            btnIdentifyForm.setEnabled(!show);
            btnIdentifyForm.setText(show ? "Identifying..." : "Identify");
        }
        if (progressText != null) {
            progressText.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    private void autoLogin() {
        if (getContext() == null) return;

        Toast.makeText(getContext(), "Connecting to AutoML...", Toast.LENGTH_SHORT).show();

        // Utiliser la méthode avec FormUrlEncoded
        Call<LoginResponse> call = autoMLApi.login(
                "password",           // grant_type
                TEST_USERNAME,        // username
                TEST_PASSWORD,        // password
                "",                   // scope
                "",                   // client_id
                ""                    // client_secret
        );

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    apiClient.setAuthToken(token);
                    isAuthenticated = true;
                    Toast.makeText(getContext(), "✓ AutoML connected", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "AutoML login failed: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMsg += " - " + e.getMessage();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getContext(), "AutoML connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
