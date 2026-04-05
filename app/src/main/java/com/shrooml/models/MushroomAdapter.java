package com.shrooml.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shrooml.R;

import java.util.List;

public class MushroomAdapter extends RecyclerView.Adapter<MushroomAdapter.ViewHolder> {

    private final List<MushroomCompleteEntity> mushrooms;
    private final Context context;

    private OnMushroomClickListener listener;

    public void setOnMushroomClickListener(OnMushroomClickListener listener) {
        this.listener = listener;
    }

    public interface OnMushroomClickListener {
        void onMushroomClick(MushroomCompleteEntity mushroom);
    }

    public MushroomAdapter(Context context, List<MushroomCompleteEntity> mushrooms) {
        this.context = context;
        this.mushrooms = mushrooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_mushroom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MushroomCompleteEntity m = mushrooms.get(position);

        holder.commonName.setText(m.getCommonName());
        holder.scientificName.setText(m.getScientificName());

        // Charger l’image
        Glide.with(context)
                .load(m.getImageUrl())
                .into(holder.image);

        // Icône selon comestibilité
        String ed = m.getEdibility().toLowerCase();
        if (ed.equals("inedible")) {
            holder.statusIcon.setImageResource(R.drawable.ic_skull);
        } else if (ed.equals("medicinal")) {
            holder.statusIcon.setImageResource(R.drawable.ic_medicinal);
        } else {
            holder.statusIcon.setImageResource(R.drawable.ic_check);
        }


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMushroomClick(m);
            }
        });

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
        return mushrooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, statusIcon;
        TextView commonName, scientificName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.mushroomImage);
            statusIcon = itemView.findViewById(R.id.statusIcon);
            commonName = itemView.findViewById(R.id.commonName);
            scientificName = itemView.findViewById(R.id.scientificName);
        }
    }
}
