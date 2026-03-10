package com.shrooml.models;

import android.content.Context;
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

        // Icône selon toxicité
        if ("toxic".equalsIgnoreCase(m.getToxicity())) {
            holder.statusIcon.setImageResource(R.drawable.ic_skull);
        } else {
            holder.statusIcon.setImageResource(R.drawable.ic_check);
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
