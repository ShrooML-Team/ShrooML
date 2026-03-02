package com.shrooml;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shrooml.games.QuizGame;

public class QuizActivity extends Activity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        imageView = findViewById(R.id.imageView); // ⚠️ id à adapter
        TextView questionTextView = findViewById(R.id.questionTextView);

        final QuizGame[] quizGameHolder = new QuizGame[1]; // conteneur pour Java
        quizGameHolder[0] = new QuizGame(this, "admin", "password123", new QuizGame.QuizCallback() {
            @Override
            public void onQuizReady() {
                questionTextView.setText(quizGameHolder[0].getQuestion(0));
                Glide.with(QuizActivity.this)
                        .load(quizGameHolder[0].getImg())
                        .into(imageView);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(QuizActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_hello_world, menu);
        return true;
    }
}