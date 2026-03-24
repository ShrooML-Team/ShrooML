package com.shrooml.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.shrooml.R;

public class ResultActivity extends AppCompatActivity {

    private TextView resultText;
    private TextView btnIdentify, btnRanking, btnGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_result);

        resultText = findViewById(R.id.result_text);
        btnIdentify = findViewById(R.id.btn_identify);
        btnRanking = findViewById(R.id.btn_ranking);
        btnGames = findViewById(R.id.btn_games);

        boolean isEdible = getIntent().getBooleanExtra("is_edible", false);

        if (isEdible) {
            resultText.setText("🍄 Edible");
            resultText.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else {
            resultText.setText("☠️ Inedible");
            resultText.setTextColor(ContextCompat.getColor(this, R.color.error));
        }

        btnIdentify.setOnClickListener(v -> finish());

        btnRanking.setOnClickListener(v -> {
            Toast.makeText(this, "Ranking feature coming soon", Toast.LENGTH_SHORT).show();
        });

        btnGames.setOnClickListener(v -> {
            Toast.makeText(this, "Games feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }
}