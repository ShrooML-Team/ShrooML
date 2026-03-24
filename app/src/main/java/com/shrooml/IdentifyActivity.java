package com.shrooml;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.R;
import com.shrooml.fragments.CameraFragment;
import com.shrooml.fragments.FormFragment;

public class IdentifyActivity extends AppCompatActivity {

    private ImageButton btnToggleMode;
    private boolean isCameraMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        btnToggleMode = findViewById(R.id.btn_toggle_mode);

        // Charger le fragment Camera par défaut
        loadFragment(new CameraFragment());

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

    private void loadFragment(Fragment fragment) {
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
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_identify);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_identify) {
                    return true;
                }
                if (id == R.id.nav_locate) {
                    startActivity(new Intent(IdentifyActivity.this, ShroomLocateActivity.class));
                    return true;
                }
                if (id == R.id.nav_quiz) {
                    startActivity(new Intent(IdentifyActivity.this, QuizActivity.class));
                    return true;
                }
                if (id == R.id.nav_home) {
                    startActivity(new Intent(IdentifyActivity.this, SplashActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}