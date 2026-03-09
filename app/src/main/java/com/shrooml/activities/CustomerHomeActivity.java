package com.shrooml.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shrooml.NewSimulationActivity;
import com.shrooml.R;

/**
 * CustomerHomeActivity - Accueil client
 */
public class CustomerHomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnNewSimulation;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        // Initialiser les vues
        tvWelcome = findViewById(R.id.tv_welcome);
        btnNewSimulation = findViewById(R.id.btn_new_simulation);
        btnLogout = findViewById(R.id.btn_logout);

        // TODO: Récupérer le nom d'utilisateur
        tvWelcome.setText("Bienvenue!");

        // Listeners
        btnNewSimulation.setOnClickListener(v -> newSimulation());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void newSimulation() {
        Intent intent = new Intent(this, NewSimulationActivity.class);
        startActivity(intent);
    }

    private void logout() {
        Toast.makeText(this, "Déconnexion", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}