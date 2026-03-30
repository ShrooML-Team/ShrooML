package com.shrooml;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChoiceIdentifyActivity extends BackgroundActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_identify);
        ImageButton btnIdentifyImg = findViewById(R.id.buttonPhoto);
        ImageButton btnIdentifyCriteria = findViewById(R.id.buttonCheck);

        btnIdentifyImg.setOnClickListener(v -> {
                    Intent intent = new Intent(ChoiceIdentifyActivity.this, IdentifyActivity.class);
                    intent.putExtra("default_mode", "camera");
                    startActivity(intent);
                    finish();
                });
        btnIdentifyCriteria.setOnClickListener(v ->
        {
            Intent intent = new Intent(ChoiceIdentifyActivity.this, IdentifyActivity.class);
            intent.putExtra("default_mode", "form");
            startActivity(intent);
            finish();
        });

        activityId = 3;
        bottomNav = findViewById(R.id.bottomNav);
        if(bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_identify);
            initNavBar(ChoiceIdentifyActivity.this);
        }
    }

}
