package com.shrooml.fragments;

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
import com.shrooml.services.api.AutoMLApi;
import com.shrooml.services.api.AutoMLRetrofitClient;
import com.shrooml.services.api.MushroomAttributes;
import com.shrooml.services.api.Requests.PredictAARequest;
import com.shrooml.services.api.Response.PredictAAResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.*;

public class FormFragment extends Fragment {

    private AutoMLApi autoMLApi;
    private AutoMLRetrofitClient apiClient;
    
    // Spinners correspondant aux 22 caractéristiques
    private Spinner spinnerCapShape, spinnerCapSurface, spinnerCapColor, spinnerBruises,
            spinnerOdor, spinnerGillAttachment, spinnerGillSpacing, spinnerGillSize,
            spinnerGillColor, spinnerStalkShape, spinnerStalkRoot, spinnerStalkSurfaceAbove,
            spinnerStalkSurfaceBelow, spinnerStalkColorAbove, spinnerStalkColorBelow,
            spinnerVeilType, spinnerVeilColor, spinnerRingNumber, spinnerRingType,
            spinnerSporePrintColor, spinnerPopulation, spinnerHabitat;

    private static final Integer Edible = -1;
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
        // Chargement des données des Spinners depuis les ressources ou tableaux en dur
            setSpinnerOptions(spinnerCapShape, getResources().getStringArray(R.array.spinner_cap_shape));
            setSpinnerOptions(spinnerCapSurface, getResources().getStringArray(R.array.spinner_cap_surface));
            setSpinnerOptions(spinnerCapColor, getResources().getStringArray(R.array.spinner_cap_color));
            setSpinnerOptions(spinnerBruises, getResources().getStringArray(R.array.spinner_bruises));
            setSpinnerOptions(spinnerOdor, getResources().getStringArray(R.array.spinner_odor));
            setSpinnerOptions(spinnerGillAttachment, getResources().getStringArray(R.array.spinner_gill_attachment));
            setSpinnerOptions(spinnerGillSpacing, getResources().getStringArray(R.array.spinner_gill_spacing));
            setSpinnerOptions(spinnerGillSize, getResources().getStringArray(R.array.spinner_gill_size));
            setSpinnerOptions(spinnerGillColor, getResources().getStringArray(R.array.spinner_gill_color));
            setSpinnerOptions(spinnerStalkShape, getResources().getStringArray(R.array.spinner_stalk_shape));
            setSpinnerOptions(spinnerStalkRoot, getResources().getStringArray(R.array.spinner_stalk_root));
            setSpinnerOptions(spinnerStalkSurfaceAbove, getResources().getStringArray(R.array.spinner_stalk_surface_above));
            setSpinnerOptions(spinnerStalkSurfaceBelow, getResources().getStringArray(R.array.spinner_stalk_surface_below));
            setSpinnerOptions(spinnerStalkColorAbove, getResources().getStringArray(R.array.spinner_stalk_color_above));
            setSpinnerOptions(spinnerStalkColorBelow, getResources().getStringArray(R.array.spinner_stalk_color_below));
            setSpinnerOptions(spinnerVeilType, getResources().getStringArray(R.array.spinner_veil_type));
            setSpinnerOptions(spinnerVeilColor, getResources().getStringArray(R.array.spinner_veil_color));
            setSpinnerOptions(spinnerRingNumber, getResources().getStringArray(R.array.spinner_ring_number));
            setSpinnerOptions(spinnerRingType, getResources().getStringArray(R.array.spinner_ring_type));
            setSpinnerOptions(spinnerSporePrintColor, getResources().getStringArray(R.array.spinner_spore_print_color));
            setSpinnerOptions(spinnerPopulation, getResources().getStringArray(R.array.spinner_population));
            setSpinnerOptions(spinnerHabitat, getResources().getStringArray(R.array.spinner_habitat));
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
        if (!validateForm()) return;

        if (apiClient == null || !apiClient.isAuthenticated()) {
            Toast.makeText(getContext(), getString(R.string.form_please_login), Toast.LENGTH_SHORT).show();
            return;
        }

        // Construction de la map en utilisant les constantes de MushroomAttributes
        Map<String, Integer> mushroomFeatures = new LinkedHashMap<>();
        mushroomFeatures.put(MushroomAttributes.CAP_SHAPE, spinnerCapShape.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.CAP_SURFACE, spinnerCapSurface.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.CAP_COLOR, spinnerCapColor.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.BRUISES, spinnerBruises.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.ODOR, spinnerOdor.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.GILL_ATTACHMENT, spinnerGillAttachment.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.GILL_SPACING, spinnerGillSpacing.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.GILL_SIZE, spinnerGillSize.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.GILL_COLOR, spinnerGillColor.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.STALK_SHAPE, spinnerStalkShape.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.STALK_ROOT, spinnerStalkRoot.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.STALK_SURFACE_ABOVE_RING, spinnerStalkSurfaceAbove.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.STALK_SURFACE_BELOW_RING, spinnerStalkSurfaceBelow.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.STALK_COLOR_ABOVE_RING, spinnerStalkColorAbove.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.STALK_COLOR_BELOW_RING, spinnerStalkColorBelow.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.VEIL_TYPE, spinnerVeilType.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.VEIL_COLOR, spinnerVeilColor.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.RING_NUMBER, spinnerRingNumber.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.RING_TYPE, spinnerRingType.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.SPORE_PRINT_COLOR, spinnerSporePrintColor.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.POPULATION, spinnerPopulation.getSelectedItemPosition());
        mushroomFeatures.put(MushroomAttributes.HABITAT, spinnerHabitat.getSelectedItemPosition());

        List<Map<String, Integer>> samples = new ArrayList<>();
        samples.add(mushroomFeatures);
        PredictAARequest request = new PredictAARequest(samples);

        showLoading(true);

        Call<PredictAAResponse> call = autoMLApi.predict(request);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PredictAAResponse> call, Response<PredictAAResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Integer> predictions = response.body().getPredictions();
                    if (!predictions.isEmpty()) {
                        boolean isEdible = (predictions.get(0) == Edible);
                        if (getActivity() != null) {
                            ((IdentifyActivity) getActivity()).showResult(isEdible);
                        }
                    } else {
                            Toast.makeText(getContext(), getString(R.string.form_no_prediction), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<PredictAAResponse> call, Throwable t) {
                showLoading(false);
                 Toast.makeText(getContext(), getString(R.string.form_network_error, t.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateForm() {
        Spinner[] allSpinners = {
            spinnerCapShape, spinnerCapSurface, spinnerCapColor, spinnerBruises,
            spinnerOdor, spinnerGillAttachment, spinnerGillSpacing, spinnerGillSize,
            spinnerGillColor, spinnerStalkShape, spinnerStalkRoot, spinnerStalkSurfaceAbove,
            spinnerStalkSurfaceBelow, spinnerStalkColorAbove, spinnerStalkColorBelow,
            spinnerVeilType, spinnerVeilColor, spinnerRingNumber, spinnerRingType,
            spinnerSporePrintColor, spinnerPopulation, spinnerHabitat
        };

        for (Spinner s : allSpinners) {
            if (s == null) {
                 Toast.makeText(getContext(), getString(R.string.form_view_not_initialized), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (s.getSelectedItemPosition() == 0) {
                 Toast.makeText(getContext(), getString(R.string.form_fill_all_fields), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void handleApiError(Response<PredictAAResponse> response) {
        String errorMsg = getString(R.string.form_prediction_failed, response.code());
        try {
            if (response.errorBody() != null) {
                errorMsg += " - " + response.errorBody().string();
            }
        } catch (Exception e) {
            errorMsg += " - " + e.getMessage();
        }
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
    }

    private void showLoading(boolean show) {
        if (btnIdentifyForm != null) {
            btnIdentifyForm.setEnabled(!show);
              btnIdentifyForm.setText(show ? getString(R.string.form_identifying) : getString(R.string.form_identify));
        }
        if (progressText != null) {
            progressText.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
