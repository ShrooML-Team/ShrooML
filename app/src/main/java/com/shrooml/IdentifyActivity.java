package com.shrooml;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.fragments.CameraFragment;
import com.shrooml.fragments.FormFragment;
import com.shrooml.fragments.ResultFragment;

public class IdentifyActivity extends BackgroundActivity {

    private ImageButton btnToggleMode;

    private boolean isCameraMode = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        btnToggleMode = findViewById(R.id.btn_toggle_mode);
        String defaultMode = getIntent().getStringExtra("default_mode");
        if("form".equals(defaultMode)) {
            loadFragment(new FormFragment());
            btnToggleMode.setImageResource(R.drawable.ic_camera);
            isCameraMode = false;
        } else {
            loadFragment(new CameraFragment());
            btnToggleMode.setImageResource(R.drawable.ic_edit);
            isCameraMode = true;

        }

        setupToggleButton();
        setupBottomNav();
    }

    private void setupToggleButton() {
        btnToggleMode.setOnClickListener(v -> {
            if (isCameraMode) {
                // Passer en mode Formulaire
                loadFragment(new FormFragment());
                btnToggleMode.setImageResource(R.drawable.ic_camera);
                isCameraMode = false;
            } else {
                // Passer en mode Caméra
                loadFragment(new CameraFragment());
                btnToggleMode.setImageResource(R.drawable.ic_edit);
                isCameraMode = true;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void setupBottomNav() {
        activityId = -1;
        initNavBar(IdentifyActivity.this);
    }


    public void setToggleIconToForm() {
        if (btnToggleMode != null) {
            btnToggleMode.setImageResource(R.drawable.ic_camera);
        }
    }

    public void setCameraMode(boolean isCamera) {
        this.isCameraMode = isCamera;
    }

    // Méthode pour afficher le résultat
    public void showResult(boolean isEdible) {
        ResultFragment resultFragment = ResultFragment.newInstance(isEdible);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.fragment_container, resultFragment)
                .commit();
    }

}

