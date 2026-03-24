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
import com.shrooml.R;
import com.shrooml.activities.ResultActivity;

public class FormFragment extends Fragment {

    private Spinner spinnerCapColor, spinnerFootColor, spinnerVeilColor, spinnerOdor, spinnerBruises;
    private TextView btnIdentifyForm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form, container, false);

        initViews(view);
        setupSpinners();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        spinnerCapColor = view.findViewById(R.id.spinner_cap_color);
        spinnerFootColor = view.findViewById(R.id.spinner_foot_color);
        spinnerVeilColor = view.findViewById(R.id.spinner_veil_color);
        spinnerOdor = view.findViewById(R.id.spinner_odor);
        spinnerBruises = view.findViewById(R.id.spinner_bruises);
        btnIdentifyForm = view.findViewById(R.id.btn_identify_form);
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
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupListeners() {
        btnIdentifyForm.setOnClickListener(v -> {
            if (spinnerCapColor.getSelectedItemPosition() == 0 ||
                    spinnerFootColor.getSelectedItemPosition() == 0 ||
                    spinnerVeilColor.getSelectedItemPosition() == 0 ||
                    spinnerOdor.getSelectedItemPosition() == 0 ||
                    spinnerBruises.getSelectedItemPosition() == 0) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Appeler votre API AutoML
            boolean isEdible = Math.random() > 0.5;

            Intent intent = new Intent(getActivity(), ResultActivity.class);
            intent.putExtra("is_edible", isEdible);
            startActivity(intent);
        });
    }
}