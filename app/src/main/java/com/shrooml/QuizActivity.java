package com.shrooml;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shrooml.games.QuizGame;
import com.shrooml.services.InaturalistService;
import com.shrooml.services.OAuthService;
import com.shrooml.services.UserService;
import com.shrooml.services.api.ShroomLocRetrofitClient;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class QuizActivity extends Activity {

    private static final long TOKEN_REFRESH_THRESHOLD_SECONDS = 120L;

    private ImageView imageView;

    private int currentQuestion = 0;
    private QuizGame quizGame;

    private Button nextButton;
    private ProgressBar progressBar;
    private TextView progressText;

    private AutoCompleteTextView answerInput;
    private RadioGroup edibleGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        OAuthService auth_api = new OAuthService();

        imageView = findViewById(R.id.imageView); // id à adapter
        nextButton = findViewById(R.id.nextButton); // <-- ajouter
        progressBar = findViewById(R.id.progressBar); // <-- ajouter
        progressText = findViewById(R.id.progressText); // <-- ajouter
        answerInput = findViewById(R.id.answerInput); // <-- ajouter
        edibleGroup = findViewById(R.id.edibleGroup);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_quiz);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_quiz) {
                    return true;
                }
                if (id == R.id.nav_locate) {
                    startActivity(new Intent(QuizActivity.this, ShroomLocateActivity.class));
                    return true;
                }
                if (id == R.id.nav_profile) {
                    startActivity(new Intent(QuizActivity.this, ProfileActivity.class));
                    return true;
                }
                if(id == R.id.nav_identify) {
                    startActivity(new Intent(QuizActivity.this, IdentifyActivity.class));
                    return true;
                }

                return false;
            }
        });

        answerInput.setOnItemClickListener((parent, view, position, id) -> {
            checkAnswer();
        });

        answerInput.setOnEditorActionListener((v, actionId, event) -> {
            checkAnswer();
            return true;
        });

        edibleGroup.setOnCheckedChangeListener((group,id)->{
            checkAnswer();
        });

        auth_api.login("admin", "password123", new OAuthService.OAuthCallback(){
            @Override
            public void onSuccess(String token) {
                ShroomLocRetrofitClient.setToken(token);
                final QuizGame[] quizGameHolder = new QuizGame[1]; // conteneur pour Java
                quizGameHolder[0] = new QuizGame(QuizActivity.this, new QuizGame.QuizCallback() {
                    @Override
                    public void onQuizReady() {
                        quizGame = quizGameHolder[0];

                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(QuizActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        quizGame.getAllCommonName());

                        answerInput.setAdapter(adapter);

                        updateQuestion();
                        nextButton.setOnClickListener(v -> {
                            currentQuestion++;
                            answerInput.setText("");
                            edibleGroup.clearCheck();
                            ArrayAdapter<String> adapt = null;
                            switch (currentQuestion){
                                case 1:adapt = new ArrayAdapter<>(QuizActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        quizGame.getAllScientName());break;
                                case 3: adapt = new ArrayAdapter<>(QuizActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        quizGame.getAllHabitat());break;
                                case 4:adapt = new ArrayAdapter<>(QuizActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        quizGame.getAllSeason());
                                        nextButton.setText("Terminer le quiz");break;

                            }
                            answerInput.setAdapter(adapt);

                            if(currentQuestion < 5){
                                updateQuestion();
                            } else {
                                answerInput.setVisibility(View.GONE);
                                edibleGroup.setVisibility(View.GONE);
                                nextButton.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                progressText.setVisibility(View.GONE);
                                imageView.setVisibility(View.GONE);
                                findViewById(R.id.questionTextView).setVisibility(View.GONE);

                                // afficher le layout de fin
                                LinearLayout quizEndLayout = findViewById(R.id.quizEndLayout);
                                LinearLayout questionCard = findViewById(R.id.questionCard);
                                questionCard.setVisibility(View.GONE);

                                quizEndLayout.setVisibility(View.VISIBLE);

                                TextView titleEnd = findViewById(R.id.quizTitleEnd);
                                TextView scoreEnd = findViewById(R.id.quizScoreEnd);
                                int score = quizGame.getScore();
                                titleEnd.setText(quizGame.getTitre(score));
                                scoreEnd.setText("Score : " + score);
                                persistScoreToApi(score);
                            }
                            answerInput.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        });

                        answerInput.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                // rien à faire ici
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                // rien à faire ici
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                // active le bouton suivant seulement si quelque chose est tapé
                                nextButton.setEnabled(!s.toString().trim().isEmpty());
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(QuizActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void updateQuestion(){
        TextView questionTextView = findViewById(R.id.questionTextView);

        questionTextView.setText(quizGame.getQuestion(currentQuestion));

        progressText.setText((currentQuestion + 1) + " / 5");
        progressBar.setProgress((currentQuestion + 1) * 20);

        if(currentQuestion == 0){
            Glide.with(QuizActivity.this)
                    .load(quizGame.getImg())
                    .into(imageView);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        if(currentQuestion == 2){
            edibleGroup.setVisibility(View.VISIBLE);
            answerInput.setVisibility(View.GONE);
            answerInput.setEnabled(false);
            edibleGroup.setEnabled(true);
            for (int i = 0; i < edibleGroup.getChildCount(); i++) {
                edibleGroup.getChildAt(i).setEnabled(true);
                edibleGroup.getChildAt(i).setVisibility(View.VISIBLE);
                ((android.widget.RadioButton) edibleGroup.getChildAt(i)).setTextColor(Color.BLACK);
            }
        } else {
            edibleGroup.setVisibility(View.GONE);
            answerInput.setVisibility(View.VISIBLE);
            answerInput.setEnabled(true);
            edibleGroup.setEnabled(false);
            for (int i = 0; i < edibleGroup.getChildCount(); i++) {
                edibleGroup.getChildAt(i).setEnabled(false);
                edibleGroup.getChildAt(i).setVisibility(View.GONE);
                ((android.widget.RadioButton) edibleGroup.getChildAt(i)).setTextColor(Color.BLACK);
            }
        }

        nextButton.setEnabled(false);
    }

    private void checkAnswer() {

        String answer;

        if(currentQuestion == 2){ // question comestible
            int selectedId = edibleGroup.getCheckedRadioButtonId();

            if(selectedId == R.id.trueButton){
                answer = "true";
            } else {
                answer = "false";
            }

        } else {
            answer = answerInput.getText().toString().trim();
        }

        boolean result = quizGame.checkAnswer(currentQuestion, answer);

        if(result){
            answerInput.setBackgroundColor(Color.parseColor("#A5D6A7")); // vert
            quizGame.upScore();
        } else {
            answerInput.setBackgroundColor(Color.parseColor("#EF9A9A")); // rouge
        }

        nextButton.setEnabled(true);
    }

    private void persistScoreToApi(int scoreToAdd) {
        if (scoreToAdd <= 0) {
            return;
        }

        try {
            TokenManager tokenManager = TokenManager.getInstance(this);
            String authToken = tokenManager.getToken();

            if (authToken == null || authToken.isEmpty()) {
                return;
            }

            if (tokenManager.isTokenExpired()) {
                redirectToLogin(tokenManager);
                return;
            }

            if (tokenManager.isTokenExpiringSoon(TOKEN_REFRESH_THRESHOLD_SECONDS)) {
                OAuthService authService = new OAuthService(true);
                authService.refreshUserSession(authToken, new OAuthService.OAuthUserCallback() {
                    @Override
                    public void onSuccess(com.shrooml.services.api.TokenResponseFull response) {
                        tokenManager.updateToken(response.getAccess_token());
                        tokenManager.saveUserProfile(response.getUser());
                        persistScoreToApiWithToken(tokenManager, scoreToAdd, response.getAccess_token());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (errorMessage.contains("401")) {
                            redirectToLogin(tokenManager);
                            return;
                        }

                        runOnUiThread(() -> Toast.makeText(QuizActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                    }
                });
                return;
            }

            persistScoreToApiWithToken(tokenManager, scoreToAdd, authToken);
        } catch (GeneralSecurityException | IOException e) {
            Toast.makeText(this, "Impossible d'enregistrer les points", Toast.LENGTH_SHORT).show();
        }
    }

    private void persistScoreToApiWithToken(TokenManager tokenManager, int scoreToAdd, String authToken) {
        UserService userService = new UserService(authToken);
        userService.addPoints(scoreToAdd, new UserService.UserProfileCallback() {
            @Override
            public void onSuccess(com.shrooml.services.api.UserResponse user) {
                tokenManager.saveUserProfile(user);
            }

            @Override
            public void onError(String errorMessage) {
                if ("Session expirée, reconnectez-vous".equals(errorMessage)) {
                    redirectToLogin(tokenManager);
                    return;
                }

                runOnUiThread(() -> Toast.makeText(QuizActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void redirectToLogin(TokenManager tokenManager) {
        tokenManager.logout();
        runOnUiThread(() -> {
            Toast.makeText(QuizActivity.this, "Session expirée, reconnectez-vous", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(QuizActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}