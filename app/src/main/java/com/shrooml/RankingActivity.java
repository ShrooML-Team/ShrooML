package com.shrooml;

import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shrooml.adapters.RankingAdapter;
import com.shrooml.services.UserService;
import com.shrooml.services.api.UserResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    private static final String TAG = "RankingActivity";

    private RecyclerView rankingRecyclerView;
    private ProgressBar loadingSpinner;
    private RankingAdapter adapter;
    private TokenManager tokenManager;
    private TextView currentUserRank;
    private TextView currentUserName;
    private TextView currentUserScore;
    private ImageView currentUserImage;
    private View currentUserContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_ranking);
        } catch (RuntimeException exception) {
            Log.e(TAG, "Erreur pendant setContentView(activity_ranking)", exception);
            Toast.makeText(this, "Erreur d'ouverture du classement", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        loadingSpinner = findViewById(R.id.loadingSpinner);
        currentUserRank = findViewById(R.id.currentUserRank);
        currentUserName = findViewById(R.id.currentUserName);
        currentUserScore = findViewById(R.id.currentUserScore);
        currentUserImage = findViewById(R.id.currentUserImage);
        currentUserContainer = findViewById(R.id.currentUserContainer);

        if (currentUserContainer != null) {
            currentUserContainer.setOnClickListener(v -> openProfile());
        }

        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (!initTokenManager()) {
            finish();
            return;
        }

        String authToken = tokenManager.getToken();
        int userId = tokenManager.getUserId();

        if (authToken == null || authToken.isEmpty() || userId <= 0) {
            Toast.makeText(this, "Session invalide, reconnectez-vous", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRanking(authToken, userId);
        loadCurrentUser(authToken);
    }

    private boolean initTokenManager() {
        try {
            tokenManager = TokenManager.getInstance(this);
            return true;
        } catch (GeneralSecurityException | IOException e) {
            Toast.makeText(this, "Erreur d'initialisation du classement", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void loadRanking(String authToken, int userId) {
        loadingSpinner.setVisibility(android.view.View.VISIBLE);
        rankingRecyclerView.setVisibility(android.view.View.GONE);

        try {
            UserService userService = new UserService(authToken);
            userService.getTopRanking(20, new UserService.UsersListCallback() {
                @Override
                public void onSuccess(List<UserResponse> users) {
                    loadingSpinner.setVisibility(android.view.View.GONE);
                    rankingRecyclerView.setVisibility(android.view.View.VISIBLE);

                    adapter = new RankingAdapter(users, userId);
                    rankingRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onError(String errorMessage) {
                    loadingSpinner.setVisibility(android.view.View.GONE);
                    Toast.makeText(RankingActivity.this, "Erreur: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (RuntimeException exception) {
            Log.e(TAG, "Erreur pendant loadRanking", exception);
            loadingSpinner.setVisibility(android.view.View.GONE);
            Toast.makeText(this, "Erreur lors du chargement du classement", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCurrentUser(String authToken) {
        UserService userService = new UserService(authToken);
        userService.getCurrentUserProfile(new UserService.UserProfileCallback() {
            @Override
            public void onSuccess(UserResponse user) {
                if (user == null) {
                    return;
                }

                currentUserRank.setText("#" + user.getRang());
                currentUserName.setText(user.getIdentifiant());
                currentUserScore.setText(String.valueOf(user.getScoring()));

                String photo = user.getPhoto_profil();
                if (photo != null && !photo.isEmpty()) {
                    Glide.with(RankingActivity.this)
                            .load(photo)
                            .circleCrop()
                            .into(currentUserImage);
                } else {
                    currentUserImage.setImageResource(R.drawable.ic_profile);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.w(TAG, "Impossible de charger le profil courant: " + errorMessage);
            }
        });
    }

    private void openProfile() {
        Intent intent = new Intent(RankingActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}
