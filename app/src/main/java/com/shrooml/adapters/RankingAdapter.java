package com.shrooml.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shrooml.R;
import com.shrooml.services.api.UserResponse;

import java.text.DecimalFormat;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<UserResponse> users;
    private int currentUserId;
    private DecimalFormat scoreFormat = new DecimalFormat("0.##");

    public RankingAdapter(List<UserResponse> users, int currentUserId) {
        this.users = users;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        UserResponse user = users.get(position);
        int rank = (user.getRang() > 0) ? user.getRang() : position + 1;

        holder.rankNumber.setText("#" + rank);
        holder.userName.setText(user.getIdentifiant());
        
        // Use favorite mushroom as description, or empty if not available
        String description = user.getChampignon_prefere();
        if (description != null && !description.isEmpty()) {
            holder.userDescription.setText(description);
        } else {
            holder.userDescription.setText("No description yet");
        }

        holder.userScore.setText(scoreFormat.format(user.getScoring()));

        // Load profile image
        String profilePicture = user.getPhoto_profil();
        if (profilePicture != null && !profilePicture.isEmpty()) {
            Glide.with(holder.userProfileImage.getContext())
                    .load(profilePicture)
                    .circleCrop()
                    .into(holder.userProfileImage);
        } else {
            holder.userProfileImage.setImageResource(R.drawable.ic_profile);
        }

        // Highlight current user
        if (currentUserId > 0 && user.getId() == currentUserId) {
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.itemView.setAlpha(0.9f);
        }

        SharedPreferences prefs = holder.itemView.getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        boolean corrupted = prefs.getBoolean("corrupted", false);

        int itemColor = corrupted ? Color.parseColor("#4f004f") : Color.parseColor("#A06A42");

        android.graphics.drawable.Drawable background = holder.itemView.getBackground();
        if (background instanceof android.graphics.drawable.GradientDrawable) {
            ((android.graphics.drawable.GradientDrawable) background.mutate()).setColor(itemColor);
        }
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public void updateUsers(List<UserResponse> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView rankNumber;
        ImageView userProfileImage;
        TextView userName;
        TextView userDescription;
        TextView userScore;

        public RankingViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            rankNumber = itemView.findViewById(R.id.rankNumber);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            userName = itemView.findViewById(R.id.userName);
            userDescription = itemView.findViewById(R.id.userDescription);
            userScore = itemView.findViewById(R.id.userScore);
        }
    }
}
