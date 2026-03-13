package com.shrooml;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChoiceIdentifyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_identify);
        ImageButton btnIdentifyImg = findViewById(R.id.buttonPhoto);
        ImageButton btnIdentifyCriteria = findViewById(R.id.buttonCheck);

        btnIdentifyImg.setOnClickListener(v ->
                startActivity(new Intent(ChoiceIdentifyActivity.this, IdentifyActivity.class)));

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_locate);


        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.nav_identify) {
                    return true;
                }
                if (id == R.id.nav_locate) {
                    startActivity(new Intent(ChoiceIdentifyActivity.this, ShroomLocateActivity.class));
                    return true;
                }
                if (id == R.id.nav_quiz) {
                    startActivity(new Intent(ChoiceIdentifyActivity.this, QuizActivity.class));
                    return true;
                }
                if (id == R.id.nav_home) {
                    startActivity(new Intent(ChoiceIdentifyActivity.this, SplashActivity.class));
                    return true;
                }

                return false;
            }
        });

    }

}
