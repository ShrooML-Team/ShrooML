package com.shrooml.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.shrooml.IdentifyActivity;
import com.shrooml.R;

public class ResultFragment extends Fragment {

    private TextView resultText;
    private TextView btnNewPrediction;
    private boolean isEdible;

    public static ResultFragment newInstance(boolean isEdible) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putBoolean("is_edible", isEdible);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isEdible = getArguments().getBoolean("is_edible");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_simulation_result, container, false);

        resultText = view.findViewById(R.id.result_text);
        btnNewPrediction = view.findViewById(R.id.btn_new_prediction);

        setupResult();
        setupListeners();

        return view;
    }

    private void setupResult() {
        if (isEdible) {
            resultText.setText("🍄 Edible");
            resultText.setTextColor(ContextCompat.getColor(requireContext(), R.color.success));
        } else {
            resultText.setText("☠️ Inedible");
            resultText.setTextColor(ContextCompat.getColor(requireContext(), R.color.error));
        }
    }

    private void setupListeners() {
        btnNewPrediction.setOnClickListener(v -> {
            // Retour au formulaire
            if (getActivity() != null) {
                FormFragment formFragment = new FormFragment();
                ((IdentifyActivity) getActivity()).loadFragment(formFragment);
                // Mettre à jour l'icône du toggle
                ((IdentifyActivity) getActivity()).setToggleIconToForm();
                ((IdentifyActivity) getActivity()).setCameraMode(false);
            }
        });
    }
}